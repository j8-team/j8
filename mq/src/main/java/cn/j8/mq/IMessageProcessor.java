package cn.j8.mq;

import cn.j8.mq.msg.Message;
import cn.j8.mq.msg.Metadata;

public interface IMessageProcessor {
    public void process(MessageChannel channel, Metadata meta, Message message);
}
