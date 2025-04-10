package model;

public class HistoryItem {
    private String id;
    private String actionType;
    private String message;
    private String timestamp;

    public HistoryItem() {}

    public HistoryItem(String id, String actionType, String message, String timestamp) {
        this.id = id;
        this.actionType = actionType;
        this.message = message;
        this.timestamp = timestamp;
    }

    public String getId() { return id; }
    public String getActionType() { return actionType; }
    public String getMessage() { return message; }
    public String getTimestamp() { return timestamp; }

    public void setId(String id) { this.id = id; }
    public void setActionType(String actionType) { this.actionType = actionType; }
    public void setMessage(String message) { this.message = message; }
    public void setTimestamp(String timestamp) { this.timestamp = timestamp; }
}
