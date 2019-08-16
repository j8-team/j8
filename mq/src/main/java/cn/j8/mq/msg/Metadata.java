package cn.j8.mq.msg;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * 消息元信息
 */
public class Metadata {
    private String producer; //生产者名称
    private List<String> consumers; //消费者的名称
    private String msgId;

    public String getProducer() {
        return producer;
    }

    public void setProducer(String producer) {
        this.producer = producer;
    }

    public List<String> getConsumers() {
        return consumers;
    }

    public void setConsumers(List<String> consumers) {
        this.consumers = consumers;
    }

    public String getMsgId() {
        return msgId;
    }

    public void setMsgId(String msgId) {
        this.msgId = msgId;
    }

    public String toJson(){
        JSONObject json = new JSONObject();
        json.put("msg_id", msgId);
        json.put("producer", producer);
        JSONArray ar = new JSONArray();
        for(String c : consumers){
            ar.add(c);
        }
        json.put("consumer", ar);
        return json.toString();
    }

    public static Metadata fromJson(String s){
        JSONObject json = JSONObject.parseObject(s);
        String msgId = json.getString("msg_id");
        String producer = json.getString("producer");

        List<String> consumers = new ArrayList<>();
        JSONArray ar = json.getJSONArray("consumer");
        if(ar != null) {
            for (int i = 0; i < ar.size(); ++i) {
                consumers.add(ar.getString(i));
            }
        }
        Metadata meta = new Metadata();
        meta.setMsgId(msgId);
        meta.setProducer(producer);
        meta.setConsumers(consumers);
        return meta;
    }

    @Override
    public String toString() {
        return this.toJson().toString();
    }
}
