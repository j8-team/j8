package cn.j8.consul;

import cn.j8.json.Json;
import org.apache.commons.lang3.StringUtils;

import java.util.*;

public class KvStore {
    private String consulHost;
    public KvStore(String consulHost){
        this.consulHost = consulHost;
    }

    public Map<String, String> get(String key){
        String url = "http://" + consulHost + "/v1/kv/" + key;
        String r = HttpClient.instance.get(url);
        Json j = Json.parse(r);
        Map<String, String> map = new LinkedHashMap<>();
        if(j == null)
            return map;

        for(int i=0; i<j.size(); ++i){
            Json el = j.asArray().val(i);
            byte[] bs = Base64.getDecoder().decode(el.asObj().strVal("Value"));
            try {
                map.put(el.asObj().strVal("Key"), new String(bs, "UTF-8"));
            }catch(Exception e){
                //...
            }
        }
        return map;
    }

    public String getOne(String key){
        Map<String, String> map = get(key);
        return map.size() == 0 ? null : map.values().iterator().next();
    }

    public boolean set(String key, String val){
        String url = "http://" + consulHost + "/v1/kv/" + key;
        String r = HttpClient.instance.put(url, "text/plain", val);
        System.out.println(r);
        return "true".equalsIgnoreCase(r);
    }

    public static void main(String[] args) {
        KvStore kv = new KvStore("192.168.0.162:8500");
    }
}
