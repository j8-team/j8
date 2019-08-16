package cn.j8.mq;

public class MessageChannelConfig {
    /**
     * 消费模式：严格消费，只有当消息成功消费完成，才更新kafka偏移量，若消息消费耗时，则将容易导致消息淤积。
     */
    public final static int CONSUME_MODE_STRICT = 1;

    /**
     * 消费模式：快速消费，在成功将消息放入消费队列之后，即更新kafka的topic偏移量。
     */
    public final static int CONSUME_MODE_FAST = 2;

    private String user;
    private String channel;
    private String server;
    private int workerCount = 1;
    private int level = CONSUME_MODE_STRICT;

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public String getChannel() {
        return channel;
    }

    public void setChannel(String channel) {
        this.channel = channel;
    }

    public String getServer() {
        return server;
    }

    public void setServer(String server) {
        this.server = server;
    }

    public int getWorkerCount() {
        return workerCount;
    }

    public void setWorkerCount(int workerCount) {
        this.workerCount = workerCount;
    }

    public int getLevel() {
        return level;
    }

    public void setLevel(int level) {
        this.level = level;
    }
}
