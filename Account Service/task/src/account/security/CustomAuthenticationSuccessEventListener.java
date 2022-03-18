package account.security;

import account.business.EmployeeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.security.authentication.event.AuthenticationSuccessEvent;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

@Component
public class CustomAuthenticationSuccessEventListener implements
        ApplicationListener<AuthenticationSuccessEvent> {

    @Autowired
    EmployeeService employeeService;

    @Override
    public void onApplicationEvent(AuthenticationSuccessEvent e) {
        UserDetails userName = (UserDetails) e.getAuthentication().getPrincipal();
        String login = userName.getUsername();
        employeeService.clearFailedAttempts(login);
    }
}