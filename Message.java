public class Message {
    public enum Type {
        ADD_CONTACT,
        UPDATE_CONTACT,
        DELETE_CONTACT,
        GET_CONTACTS,
        SEARCH_CONTACTS,
        RESPONSE,
        ERROR
    }

    private final Type type;
    private final String content;

    public Message(Type type, String content) {
        this.type = type;
        this.content = content;
    }

    public Type getType() { return type; }
    public String getContent() { return content; }

    @Override
    public String toString() {
        return type + ":" + content;
    }

    public static Message fromString(String str) {
        String[] parts = str.split(":", 2);
        return new Message(Type.valueOf(parts[0]), parts[1]);
    }
}


