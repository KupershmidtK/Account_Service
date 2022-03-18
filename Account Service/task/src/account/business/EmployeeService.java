package account.business;

import account.business.request.ChangeRoleRequest;
import account.business.request.UnlockUserRequest;
import account.business.response.EmployeeResponseView;
import account.database.model.EmployeeEntity;
import account.database.repository.EmployeeRepository;
import account.database.model.RoleEntity;
import account.database.repository.RoleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.server.ResponseStatusException;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

@ResponseStatus(code = HttpStatus.BAD_REQUEST, reason = "User exist!")
class UserExistException extends RuntimeException {}

@ResponseStatus(code = HttpStatus.BAD_REQUEST, reason = "Password length must be 12 chars minimum!")
class PasswordLengthException extends RuntimeException {}

@ResponseStatus(code = HttpStatus.BAD_REQUEST, reason = "The password is in the hacker's database!")
class PasswordBreachedException extends RuntimeException {}

class BreachedPasswords {
    private final static Set<String> passwords = Set.of("PasswordForJanuary", "PasswordForFebruary", "PasswordForMarch", "PasswordForApril",
            "PasswordForMay", "PasswordForJune", "PasswordForJuly", "PasswordForAugust",
            "PasswordForSeptember", "PasswordForOctober", "PasswordForNovember", "PasswordForDecember");

    public static boolean check(String password) {
        return passwords.contains(password);
    }
}

@Service
public class EmployeeService {
    private final int MAX_FAILED_ATTEMPTS = 5;

    @Autowired
    private EmployeeRepository employeeRepository;
    @Autowired
    private RoleRepository roleRepository;
    @Autowired
    EventService eventLogger;
    @Autowired
    private PasswordEncoder passwordEncoder;

    public List<EmployeeResponseView> findAll() {
        return StreamSupport.stream(employeeRepository.findAll().spliterator(), false)
                .map(EmployeeResponseView::new)
                .sorted(Comparator.comparingLong(EmployeeResponseView::getId))
                .collect(Collectors.toList());
    }

    public EmployeeResponseView createUser(Map<String, String> request) {
        // If user already exist throw Exception
        if (isEmployeeExist(request.get("email"))) {
            throw new UserExistException();
        }

        // First user must have Administrator role. Next users have User role
        List<RoleEntity> role = employeeRepository.count() == 0
                ? roleRepository.findByName("ROLE_ADMINISTRATOR")
                : roleRepository.findByName("ROLE_USER");

        EmployeeEntity employee = new EmployeeEntity(
                request.get("name"),
                request.get("lastname"),
                request.get("email"),
                passwordEncoder.encode(request.get("password")),
                new HashSet<>(role));
        return new EmployeeResponseView(employeeRepository.save(employee));
    }

    public Map<String, String> deleteUser(String email) {
        EmployeeEntity employee = findEmployee(email);
        if (employee == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found!");
        }

        if (employee.getRoles().stream()
                .anyMatch(r -> "ROLE_ADMINISTRATOR".equals(r.getName()))) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "Can't remove ADMINISTRATOR role!");
        }
        employeeRepository.delete(employee);
        return Map.of("user", employee.getEmail(),
                "status", "Deleted successfully!");
    }

    public Map<String, String> changePassword(String userEmail, Map<String, String> request) {
        String newPassword = request.get("new_password");
        if (newPassword == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST);
        }
        if (!EmployeeRequestValidator.isValidPasswordLen(newPassword)) {
            throw new PasswordLengthException();
        }
        if(BreachedPasswords.check(newPassword)) {
            throw new PasswordBreachedException();
        }

        EmployeeEntity user = findEmployee(userEmail);
        if (user == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "User " + userEmail + " not found");
        }
        String oldPassword = user.getPassword();
        if (passwordEncoder.matches(newPassword, oldPassword)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "The passwords must be different!");
        }

        user.setPassword(passwordEncoder.encode(newPassword));
        employeeRepository.save(user);
        return Map.of(
                "email", userEmail,
                "status", "The password has been updated successfully");
    }

    public EmployeeResponseView changeRole(ChangeRoleRequest request) {
        EmployeeEntity employee = findEmployee(request.getUser());
        if (employee == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found!");
        }

        List<RoleEntity> roles = roleRepository.findByName("ROLE_" + request.getRole());
        if (roles.size() == 0) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND,
                    "Role not found!");
        } // else
        RoleEntity role = roles.get(0);

        employee = executeOperation(employee, role, request.getOperation());
        return new EmployeeResponseView(employee);
    }

    public Map<String, String> lockUnlockEmployee(UnlockUserRequest request) {
        EmployeeEntity employee = findEmployee(request.getUser());
        if (employee == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found!");
        }

        String retString = "User " + request.getUser().toLowerCase();
        if ("LOCK".equals(request.getOperation().toUpperCase())) {
            if (hasAdministrativeRole(employee)) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Can't lock the ADMINISTRATOR!");
            }

            employee.lockUser();
            retString += " locked!";
        } else {
            employee.unlockUser();
            retString += " unlocked!";
        }
        employeeRepository.save(employee);
        return Map.of("status", retString);
    }

    public boolean increaseFailedAttempt(String email) {
        EmployeeEntity employee = findEmployee(email);
        if (employee == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found!");
        }

        boolean isBruteForceAttack = false;
        if (!employee.isLocked() && !hasAdministrativeRole(employee)) {
            if (employee.getFailedAttemptCnt() < MAX_FAILED_ATTEMPTS - 1) {
                employee.increaseFailedAttemptCnt();
            } else {
                employee.lockUser();
                isBruteForceAttack = true;
            }
            employeeRepository.save(employee);
        }
        return isBruteForceAttack;
    }

    public void clearFailedAttempts(String email) {
        EmployeeEntity employee = findEmployee(email);
        if (employee == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found!");
        }

        if (!employee.isLocked() && employee.getFailedAttemptCnt() > 0) {
            employee.clearFailedAttemptCnt();
            employeeRepository.save(employee);
        }
    }
/////////////////////////////////////////////////////////////////////////////////////////////
    public void checkRequest(Map<String, String> request) {
        if (!EmployeeRequestValidator.isValidRequest(request)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST);
        }
        if (!EmployeeRequestValidator.isValidPasswordLen(request.get("password"))) {
            throw new PasswordLengthException();
        }
        if(BreachedPasswords.check(request.get("password"))) {
            throw new PasswordBreachedException();
        }
    }

    private EmployeeEntity executeOperation(EmployeeEntity employee, RoleEntity role, String operation) {
        boolean isAdmin = hasAdministrativeRole(employee);

        if ("GRANT".equals(operation)) { // GRANT role
            if(
                    isAdmin && !"ROLE_ADMINISTRATOR".equals(role.getName()) ||
                    !isAdmin && "ROLE_ADMINISTRATOR".equals(role.getName())) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                        "The user cannot combine administrative and business roles!");
            }
            employee.addRole(role);
        } else if ("REMOVE".equals(operation)) { // REMOVE role
            if ("ROLE_ADMINISTRATOR".equals(role.getName())) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                        "Can't remove ADMINISTRATOR role!");
            }

            if (!employee.removeRole(role)) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                        "The user does not have a role!");
            }

            if (employee.getRoles().size() == 0) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                        "The user must have at least one role!");
            }
        }
        return employeeRepository.save(employee);
    }

    private boolean hasAdministrativeRole(EmployeeEntity employee) {
        RoleEntity role = roleRepository.findByName("ROLE_ADMINISTRATOR").get(0);
        boolean hasRole = employee.hasRole(role);
        return hasRole;
    }

    private EmployeeEntity findEmployee(String email) {
        List<EmployeeEntity> userList = employeeRepository.findByEmail(email.toLowerCase());
        return userList.size() == 0 ? null : userList.get(0);
    }

    private boolean isEmployeeExist(String email) {
        return findEmployee(email) != null;
    }
}
