package cn.j8.mq;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

/**
 * 消息通道<br/>
 * 内部组合了Producer和Consumer，且它们将同时操作同一个topic。<br/>
 * 使用上是这样子的：<br/>
 * <p>消息通道的每个端需要指定自己的user（MessageChannelConfig.user），在连接kafka时将使用这个作为groupId。</p>
 * <p>比如有A和B两个端同时连入topic-X，A发送消息给B时，因为指定了接收端是B，且两者groupId不一样，消息将同时被A和B接收，
 * 但是A会忽略掉这条消息，而B会消费掉。</p>
 * <p>这种设计方式是因为我厂大部分业务都是点对点的通信方式，比如订单通知支付，支付完成后通知订单，若走两个topic的方式则
 * 对topic的管理会比较麻烦且混乱，而本来每个业务都应该具备自己的名称，通过groupId来进行消息消费的隔离也变得必要和方便。</p>
 */
public class MessageChannel {
    private Logger logger = LoggerFactory.getLogger(this.getClass());

    private Producer producer;
    private Consumer consumer;
    private MessageChannelConfig conf;
    private IMessageProcessor processor;
    private ExecutorService service;

    public MessageChannel(MessageChannelConfig conf, IMessageProcessor processor){
        this.conf = conf;
        this.processor = processor;

        ConnectConfig connectConfig = new ConnectConfig(conf.getUser());
        connectConfig.setTopic(conf.getChannel());
        connectConfig.setGroupId(conf.getUser());
        connectConfig.setServer(conf.getServer());

        this.producer = new Producer(connectConfig);
        this.consumer = new Consumer(connectConfig);

        service = Executors.newFixedThreadPool(conf.getWorkerCount());
    }

    /**
     * 发送消息到消息通道中
     * @param data
     * @param consumers
     * @return
     * @throws InterruptedException
     */
    public boolean send(String data, List<String> consumers) throws InterruptedException{
        return this.producer.send(data, consumers);
    }

    public IMessageProcessor getProcessor() {
        return processor;
    }

    /**
     * 从消息通道中获取消息（注意：只有接收者是自己的消息才会被消费！）<br/>
     * 本方法底层调用Consumer.startAsService，会导致阻塞，且只有在异常时才会退出。
     */
    public void watch(){
        Consumer.startAsService(this.consumer, (meta, msg) -> {
            //消息不是自己的，扔掉
            if (!meta.getConsumers().contains(conf.getUser())) {
                return;
            }

            //收到的消息是自己的，则加入队列进行处理
            logger.debug("收到消息，meta=>" + meta + ", message=>" + msg);
            if(conf.getLevel() == MessageChannelConfig.CONSUME_MODE_FAST) {
                service.execute(new MessageChannelWorker(MessageChannel.this, meta, msg));
            }else{
                if(processor != null) {
                    try {
                        processor.process(MessageChannel.this, meta, msg);
                    } catch (Throwable e) {
                        logger.error("worker异常，meta=>" + meta + ", message=>" + msg, e);
                    }
                }
            }
        });
    }

    public void close(){
        this.producer.close();
        this.consumer.close();

        service.shutdown();
        try {
            service.awaitTermination(Long.MAX_VALUE, TimeUnit.DAYS);
        }catch(Exception e){
            logger.error("error", e);
        }
    }
}
