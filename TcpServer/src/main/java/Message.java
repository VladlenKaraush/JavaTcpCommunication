import java.util.List;

public class Message {

    private List<String> stringList;
    private MessageType messageType;
    private MessageCode messageCode;

    public Message(List<String> stringList, MessageType messageType, MessageCode messageCode) {
        this.stringList = stringList;
        this.messageType = messageType;
        this.messageCode = messageCode;
    }

    public MessageCode getMessageCode() {
        return messageCode;
    }

    public void setMessageCode(MessageCode messageCode) {
        this.messageCode = messageCode;
    }

    public Message() {
    }

    public List<String> getStringList() {
        return stringList;
    }

    public void setStringList(List<String> stringList) {
        this.stringList = stringList;
    }

    public MessageType getMessageType() {
        return messageType;
    }

    public void setMessageType(MessageType messageType) {
        this.messageType = messageType;
    }
}
