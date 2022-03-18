package account.security;

import account.business.EventAction;
import account.business.EventService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class CustomAccessDeniedHandler implements AccessDeniedHandler {
    @Autowired
    EventService eventLogger;

    @Override
    public void handle(HttpServletRequest request, HttpServletResponse response, AccessDeniedException accessDeniedException) throws IOException, ServletException {
        String path = request.getRequestURI();
        String login = request.getUserPrincipal().getName();
        eventLogger.write(EventAction.ACCESS_DENIED, login, path, path);

        response.sendError(HttpServletResponse.SC_FORBIDDEN, "Access Denied!");
    }
}
