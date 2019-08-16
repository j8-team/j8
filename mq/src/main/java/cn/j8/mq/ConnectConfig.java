package cn.j8.mq;

public class ConnectConfig {
    private String name; //端的名称
    private String server;
    private String groupId;
    private String topic;

    public ConnectConfig(String name){
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public String getServer() {
        return server;
    }

    public void setServer(String server) {
        this.server = server;
    }

    public String getGroupId() {
        return groupId;
    }

    public void setGroupId(String groupId) {
        this.groupId = groupId;
    }

    public String getTopic() {
        return topic;
    }

    public void setTopic(String topic) {
        this.topic = topic;
    }
}
