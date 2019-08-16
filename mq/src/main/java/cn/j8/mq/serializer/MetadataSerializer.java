package cn.j8.mq.serializer;

import cn.j8.mq.msg.Metadata;
import cn.j8.mq.utils.Gzips;
import org.apache.kafka.common.errors.SerializationException;
import org.apache.kafka.common.serialization.Serializer;

import java.io.UnsupportedEncodingException;
import java.util.Map;

public class MetadataSerializer implements Serializer<Metadata> {
    @Override
    public void configure(Map<String, ?> configs, boolean isKey) {
    }

    @Override
    public byte[] serialize(String topic, Metadata data) {
        if(data == null)
            return null;

        try{
            byte[] bytes = data.toJson().getBytes("UTF-8");
            return Gzips.gzencode(bytes);
        }catch(UnsupportedEncodingException e){
            throw new SerializationException("消息Metadata数据序列化失败!");
        }
    }

    @Override
    public void close() {
    }
}
