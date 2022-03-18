package account.security;

import account.business.EmployeeService;
import account.business.EventAction;
import account.business.EventService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.security.authentication.event.AuthenticationFailureBadCredentialsEvent;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import javax.servlet.http.HttpServletRequest;

@Component
public class CustomAuthenticationFailureListener implements
        ApplicationListener<AuthenticationFailureBadCredentialsEvent> {

    @Autowired
    private HttpServletRequest request;

    @Autowired
    EmployeeService employeeService;
    @Autowired
    EventService eventLogger;

    @Override
    @Transactional
    public void onApplicationEvent(AuthenticationFailureBadCredentialsEvent e) {
        String path = request.getRequestURI();
        String login = (String) e.getAuthentication().getPrincipal();
        eventLogger.write(EventAction.LOGIN_FAILED, login, path, path);
        try {
            boolean isBruteForceAttack = employeeService.increaseFailedAttempt(login);
            if (isBruteForceAttack) {
                eventLogger.write(EventAction.BRUTE_FORCE, login, path, path);
                eventLogger.write(EventAction.LOCK_USER, login,
                        "Lock user " + login.toLowerCase(),
                        "/api/admin/user/access");
            }
        } catch (Exception ignore) {}
    }
}
