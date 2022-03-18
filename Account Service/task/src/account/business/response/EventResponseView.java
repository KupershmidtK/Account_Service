package account.business.response;

import account.database.model.EventEntity;

public class EventResponseView {
    private String date;
    private String action;
    private String subject;
    private String object;
    private String path;

    public EventResponseView(EventEntity event) {
        this.date = event.getDate().toString();
        this.action = event.getAction();
        this.subject = event.getSubject();
        this.object = event.getObject();
        this.path = event.getPath();
    }

    public String getDate() {
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
