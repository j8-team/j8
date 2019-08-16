package cn.j8.mq;

import cn.j8.mq.deserializer.MessageDeserializer;
import cn.j8.mq.deserializer.MetadataDeserialier;
import cn.j8.mq.msg.Message;
import cn.j8.mq.msg.Metadata;
import org.apache.kafka.clients.consumer.*;
import org.apache.kafka.common.TopicPartition;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Duration;
import java.util.*;

public class Consumer {

    private Logger logger = LoggerFactory.getLogger(this.getClass());

    private Map<TopicPartition, OffsetAndMetadata> currentOffsets = new HashMap<>();// 用于跟踪偏移量的map

    private ConnectConfig conf;

    private KafkaConsumer<Metadata, Message> consumer;

    private Throwable e;

    public Consumer(ConnectConfig conf, final boolean fromBeginning) {
        this.conf = conf;

        // 定义消费者
        Properties props = new Properties();
        props.put("bootstrap.servers", conf.getServer());
        props.put("group.id", conf.getGroupId());
        props.put("enable.auto.commit", "false");
        props.put("session.timeout.ms", "30000");
        props.put("key.deserializer", MetadataDeserialier.class.getName());
        props.put("value.deserializer", MessageDeserializer.class.getName());
        consumer = new KafkaConsumer<>(props);

        // 订阅主题
        consumer.subscribe(Arrays.asList(conf.getTopic()), new ConsumerRebalanceListener() {
            @Override
            public void onPartitionsRevoked(Collection<TopicPartition> collection) {
                // 如果发生rebalance，要在即将失去partition所有权时提交偏移量。
                // 注意：
                // (1)提交的是最近处理过的偏移量，而不是批次中还在处理的最后一个偏移量。因为partition有可能在我们还在处理消息时被撤回。
                // (2)我们要提交所有分区的偏移量，而不只是即将市区所有权的分区的偏移量。因为提交的偏移量是已经处理过的，所以不会有什么问题。
                // (3)调用commitSync方法，确保在再均衡发生之前提交偏移量
                consumer.commitSync(currentOffsets);

                // 同时将对应的topicpartition记录清空掉
                for (TopicPartition tp : collection) {
                    currentOffsets.remove(tp);
                }
            }

            @Override
            public void onPartitionsAssigned(Collection<TopicPartition> collection) {
                Map<TopicPartition, Long> beginningOffset = consumer.beginningOffsets(collection);
                if (fromBeginning) {
                    // 基于seekToBeginning方法
                    consumer.seekToBeginning(collection);
                } else {
                    for (Map.Entry<TopicPartition, Long> entry : beginningOffset.entrySet()) {
                        currentOffsets.put(entry.getKey(), new OffsetAndMetadata(entry.getValue(), "no matadata"));
                    }
                }
            }
        });
}

    public Consumer(ConnectConfig conf) {
        this(conf, false);
    }

    public void close(){
        consumer.close();
    }

    public boolean poll(IConsumerWorker worker){
        try {
            ConsumerRecords<Metadata, Message> records = consumer.poll(Duration.ofMillis(1000));
            for (ConsumerRecord<Metadata, Message> record : records) {
                Metadata meta = record.key();
                Message message = record.value();

                worker.work(meta, message);

                // 更新偏移量
                currentOffsets.put(new TopicPartition(record.topic(), record.partition()),
                        new OffsetAndMetadata(record.offset() + 1, "no matadata"));
                consumer.commitAsync(currentOffsets, null);
            }
            return true;
        } catch (Throwable e) {
            logger.error("error", e);
            this.e = e;
            return false;
        } finally {
            try {
                // 总是提交一次偏移量
                consumer.commitSync(currentOffsets);
            } catch (Exception e) {
                logger.error("consumer.commitSync fail", e);
            }
        }
    }

    public Throwable lastException(){
        return e;
    }

    /**
     * 以服务形式常驻运行（死循环），但是若发生异常将会导致退出
     * @param consumer
     * @param worker
     */
    public static void startAsService(Consumer consumer, IConsumerWorker worker){
        if(consumer == null)
            return;
        while(consumer.poll(worker));
        consumer.close();
    }
}
