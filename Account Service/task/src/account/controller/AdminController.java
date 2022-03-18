package account.controller;

import account.business.EventAction;
import account.business.EventService;
import account.business.request.ChangeRoleRequest;
import account.business.request.UnlockUserRequest;
import account.business.response.EmployeeResponseView;
import account.business.EmployeeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import javax.validation.constraints.Email;
import java.util.List;
import java.util.Map;

@RestController
@Validated
public class AdminController {
    @Autowired
    EmployeeService employeeService;
    @Autowired
    EventService eventLog;

    @GetMapping("/api/admin/user")
    public List<EmployeeResponseView> getEmployeeList() {
        return employeeService.findAll();
    }

    @DeleteMapping("/api/admin/user/{email}")
    @Transactional
    public Map<String, String> deleteUser(@PathVariable @Email String email,
                                          @AuthenticationPrincipal UserDetails details) {
        String login = details.getUsername();
        Map<String, String> response = employeeService.deleteUser(email);
        eventLog.write(EventAction.DELETE_USER, login, response.get("user"), "/api/admin/user");
        return response;
    }

    @PutMapping("/api/admin/user/role")
    @Transactional
    public EmployeeResponseView changeRole(@RequestBody @Valid ChangeRoleRequest request,
                                           @AuthenticationPrincipal UserDetails details) {
        String login = details.getUsername();
        EmployeeResponseView response = employeeService.changeRole(request);
        if ("GRANT".equals(request.getOperation())) {
            eventLog.write(EventAction.GRANT_ROLE, login,
                    "Grant role " + request.getRole() + " to " + request.getUser().toLowerCase(),
                    "/api/admin/user/role");
        } else {
            eventLog.write(EventAction.REMOVE_ROLE, login,
                    "Remove role " + request.getRole() + " from " + request.getUser().toLowerCase(),
                    "/api/admin/user/role");
        }
        return response;
    }

    @PutMapping("/api/admin/user/access")
    @Transactional
    public Map<String, String> unlockUser(@RequestBody @Valid UnlockUserRequest request,
                             @AuthenticationPrincipal UserDetails details) {
        String login = details.getUsername();
        Map<String, String> response = employeeService.lockUnlockEmployee(request);
        if("LOCK".equals(request.getOperation())) {
            eventLog.write(EventAction.LOCK_USER,
                    request.getUser().toLowerCase(),
                    "Lock user " + request.getUser().toLowerCase(),
                    "/api/admin/user/access");
        } else {
            eventLog.write(EventAction.UNLOCK_USER, login,
                    "Unlock user " + request.getUser().toLowerCase(),
                    "/api/admin/user/access");
        }
        return response;
    }
}
