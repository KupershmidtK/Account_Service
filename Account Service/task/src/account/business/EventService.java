package account.business;

import account.business.response.EventResponseView;
import account.database.model.EventEntity;
import account.database.repository.EventRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class EventService {
    @Autowired
    private EventRepository eventRepository;

    public void write(EventAction type, String subject, String object, String path) {
        EventEntity event = new EventEntity(type, subject, object, path);
        saveEvent(event);
    }

    public List<EventResponseView> readAll() {
        List<EventResponseView> list = new ArrayList<>();
        eventRepository.findAll().forEach(event -> list.add(new EventResponseView(event)));
        return list;
    }
////////////////////////////////////////////////////////////////////
    private void saveEvent(EventEntity event) {
        eventRepository.save(event);
    }



}
