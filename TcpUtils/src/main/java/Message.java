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

    MessageCode getMessageCode() {
        return messageCode;
    }

    void setMessageCode(MessageCode messageCode) {
        this.messageCode = messageCode;
    }

    Message() {
    }

    List<String> getStringList() {
        return stringList;
    }

    void setStringList(List<String> stringList) {
        this.stringList = stringList;
    }

    MessageType getMessageType() {
        return messageType;
    }

    void setMessageType(MessageType messageType) {
        this.messageType = messageType;
    }
}
