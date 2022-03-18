package account.controller;

import account.business.EmployeeService;
import account.business.EventAction;
import account.business.EventService;
import account.business.response.EmployeeResponseView;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
public class AuthController {
    @Autowired
    EmployeeService employeeService;
    @Autowired
    EventService eventLogger;

    @PostMapping("/api/auth/signup")
    @Transactional
    public EmployeeResponseView createEmployee(@RequestBody Map<String, String> request) {
        employeeService.checkRequest(request);
        EmployeeResponseView response =  employeeService.createUser(request);
        String login =  response.getEmail();
        eventLogger.write(EventAction.CREATE_USER,"Anonymous", login, "/api/auth/signup");
        return response;
    }

    @PostMapping("/api/auth/changepass")
    @Transactional
    public Map<String, String> changePassword(@RequestBody Map<String, String> request,
                                              @AuthenticationPrincipal UserDetails details) {
        String email = details.getUsername();
        Map<String, String> response =  employeeService.changePassword(email, request);
        eventLogger.write(EventAction.CHANGE_PASSWORD, email, email, "/api/auth/changepass");
        return response;
    }
}