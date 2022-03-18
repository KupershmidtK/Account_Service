package account.database.model;

import account.business.EventAction;

import javax.persistence.*;
import java.time.LocalDate;

@Entity
@Table(name = "event_log")
public class EventEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private LocalDate date;
    private String action;
    private String subject;
    private String object;
    private String path;

    public EventEntity() { }

    public EventEntity(EventAction action, String subject, String object, String path) {
        this.action = action.toString();
        this.subject = subject;
        this.object = object;
        this.path = path;
        this.date = LocalDate.now();
    }

    public LocalDate getDate() {
        return date;
    }

    public String getAction() {
        return action;
    }

    public String getSubject() {
        return subject;
    }

    public String getObject() {
        return object;
    }

    public String getPath() {
        return path;
    }
}
