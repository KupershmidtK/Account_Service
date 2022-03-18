package account.controller;

import account.business.EventService;
import account.business.response.EventResponseView;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class AuditorController {
    @Autowired
    EventService eventLog;

    @GetMapping("/api/security/events")
    public List<EventResponseView> getAllEvents(@AuthenticationPrincipal UserDetails details) {
        return eventLog.readAll();
    }
}
