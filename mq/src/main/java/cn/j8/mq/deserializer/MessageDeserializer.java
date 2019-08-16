package cn.j8.mq.deserializer;

import cn.j8.mq.msg.Message;
import cn.j8.mq.utils.Gzips;
import org.apache.kafka.common.errors.SerializationException;
import org.apache.kafka.common.serialization.Deserializer;

import java.util.Map;

public class MessageDeserializer implements Deserializer<Message> {
    @Override
    public void configure(Map<String, ?> configs, boolean isKey) {
    }

    @Override
    public Message deserialize(String topic, byte[] data) {
        if(data == null)
            return null;

        data = Gzips.gzdecode(data);
        if(data == null)
            throw new SerializationException("消息Metadata消息格式错误：非gzip格式。");

        try {
            return new Message(new String(data, "UTF-8"));
        }catch(Exception e){
            throw new SerializationException("消息Metadata消息格式错误：非JSON数据。");
        }
    }

    @Override
    public void close() {
    }
}
