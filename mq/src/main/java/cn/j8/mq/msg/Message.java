package cn.j8.mq.msg;

/**
 * 消息体
 */
public class Message {
    private String data;

    public Message(String data){
        this.data = data;
    }

    public String getData() {
        return data;
    }

    @Override
    public String toString() {
        return data;
    }
}
