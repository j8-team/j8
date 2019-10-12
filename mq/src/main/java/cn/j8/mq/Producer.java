package cn.j8.mq;

import cn.j8.mq.msg.Message;
import cn.j8.mq.msg.Metadata;
import cn.j8.mq.serializer.MessageSerializer;
import cn.j8.mq.serializer.MetadataSerializer;
import org.apache.commons.lang3.StringUtils;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.clients.producer.RecordMetadata;
import org.apache.kafka.common.PartitionInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Properties;
import java.util.UUID;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

public class Producer{
    private Logger logger = LoggerFactory.getLogger(this.getClass());

    private KafkaProducer<Metadata, Message> kafkaProducer;
    private ConnectConfig conf;

    public Producer(ConnectConfig conf){
        this.conf = conf;

        Properties props = new Properties();
        props.put("bootstrap.servers", conf.getServer());
        props.put("acks", "all");
        props.put("retries", 0);
        props.put("batch.size", 16384);
        props.put("linger.ms", 100);
        props.put("buffer.memory", 33554432);
        props.put("key.serializer", MetadataSerializer.class.getName());
        props.put("value.serializer", MessageSerializer.class.getName());
        kafkaProducer = new KafkaProducer<>(props);
    }

    /**
     * 获取topic对应的所有分片索引
     * @return
     */
    public int[] partitions(){
        List<PartitionInfo> pars = kafkaProducer.partitionsFor(conf.getTopic());
        int[] iPars = new int[pars.size()];
        for(int i=0; i<pars.size(); ++i){
            iPars[i] = pars.get(i).partition();
        }
        return iPars;
    }

    public void close(){
        kafkaProducer.close();
    }

    /**
     * 发送消息，必须指定消费者是谁
     * @param data  消息数据
     * @param consumers  消费者名称列表，必须指定，否则消息将被丢弃，同时返回false
     * @throws InterruptedException
     */
    public boolean send(String data, List<String> consumers) throws InterruptedException{
        return send(data, consumers, -1);
    }

    /**
     * 发送消息，必须指定消费者是谁
     * @param data  消息数据
     * @param consumers  消费者名称列表，必须指定，否则消息将被丢弃，同时返回false
     * @param partition 分区
     * @throws InterruptedException
     */
    public boolean send(String data, List<String> consumers, int partition) throws InterruptedException{
        if(consumers == null || consumers.size() == 0)
            return false;
        if(StringUtils.isBlank(data))
            return false;

        Metadata meta = new Metadata();
        meta.setMsgId(UUID.randomUUID().toString());
        meta.setProducer(conf.getName());
        meta.setConsumers(consumers);
        Message msg = new Message(data);
        //发送消息
        Future<RecordMetadata> f;
        if(partition >= 0){
            f = kafkaProducer.send(new ProducerRecord<>(conf.getTopic(), partition, meta, msg));
        }else{
            f = kafkaProducer.send(new ProducerRecord<>(conf.getTopic(), meta, msg));
        }
        try{
            RecordMetadata recordMeta = f.get(5000, TimeUnit.SECONDS);
            logger.info("发送消息成功，meta=>" + meta + ", topic=>" + conf.getTopic() + ", partition=>" + recordMeta.partition() + ", message=>" + msg);
            return true;
        }catch(InterruptedException e){
            throw e;
        }catch(Exception e){
            logger.error("producer.send error", e);
            return false;
        }
    }

}
