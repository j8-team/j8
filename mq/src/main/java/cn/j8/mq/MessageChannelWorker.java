package cn.j8.mq;

import cn.j8.mq.msg.Message;
import cn.j8.mq.msg.Metadata;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MessageChannelWorker implements Runnable{
    private Logger logger = LoggerFactory.getLogger(this.getClass());

    private Metadata meta;
    private Message msg;
    private MessageChannel channel;
    public MessageChannelWorker(MessageChannel channel, Metadata meta, Message msg){
        this.meta = meta;
        this.msg = msg;
        this.channel = channel;
    }

    @Override
    public void run() {
        if(channel == null || channel.getProcessor() == null)
            return;

        try {
            channel.getProcessor().process(channel, meta, msg);
        } catch (Throwable e) {
            logger.error("MessageChannelWorker.run error, meta=>" + meta + ", message=>" + msg, e);
        }
    }
}
