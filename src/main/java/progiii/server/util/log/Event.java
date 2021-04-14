package progiii.server.util.log;

import progiii.common.network.RequestType;
import progiii.common.network.request.Request;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

public class Event implements Comparable<Event>{
    private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
    private String client = null;
    private RequestType requestType;
    private EventType type;
    private List<Action> actions;
    private Date date;
    private boolean concluded = false;

    public Event() {
        actions = new ArrayList<>();
        type = EventType.UNDEFINED;
        this.date = new Date();
    }

    private void concludedException() {
        if (concluded)
            throw new IllegalStateException("Event has been closed");
    }

    public void setRequest(Request request) {
        if (client == null) {
            client = request.getEmail();
            requestType = request.getType();
            addNormalMsg(String.format("Request type: %s from %s", requestType.toString(), client));
        } else
            throw new IllegalStateException("Event has already been set");
    }

    public String getClient() {
        return client;
    }

    public RequestType getRequestType() {
        return requestType;
    }

    public EventType getType() {
        return type;
    }

    public List<Action> getActions() {
        return Collections.unmodifiableList(actions);
    }

    public Date getDate() {
        return date;
    }

    public boolean isConcluded() {
        return concluded;
    }

    public synchronized void conclude() {
        if (!concluded)
            if (type == EventType.UNDEFINED)
                type = EventType.SUCCESS;
        concluded = true;
    }

    public void addNormalMsg(String message) {
        concludedException();
        actions.add(new Action(message, MsgType.NORMAL));
    }

    public void addWarning(String message) {
        concludedException();
        actions.add(new Action(message, MsgType.WARNING));
    }

    public void addError(String message) {
        concludedException();
        actions.add(new Action(message, MsgType.ERROR));
    }

    public void addException(Exception e) {
        concludedException();
        StringWriter stringWriter = new StringWriter();
        PrintWriter printWriter = new PrintWriter(stringWriter);
        e.printStackTrace(printWriter);

        actions.add(new Action(stringWriter.toString(), MsgType.EXCEPTION));
        type = EventType.EXCEPTION;
    }

    @Override
    public String toString() {
        return String.format("%s %s %s", DATE_FORMAT.format(date), client, requestType.toString());
    }

    @Override
    public int compareTo(Event o) {
        return date.compareTo(o.date);
    }
}
