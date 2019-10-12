package cn.j8.consul;

import cn.j8.json.Json;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.URLEncoder;
import java.util.List;
import java.util.Map;

/**
 * 只支持json方式传递数据的服务调用
 */
public class ServiceCaller {
    private Logger logger = LoggerFactory.getLogger(this.getClass());
    private ServiceDetector detector;
    private String serviceName;
    public ServiceCaller(String consulHost, String serviceName){
        detector = new ServiceDetector(consulHost);
        this.serviceName = serviceName;
    }

    private int idx = 0;

    /**
     * 通过json格式的数据请求服务，content-type=application/json
     * @param path
     * @param param
     * @return
     */
    public Json call(String path, Json param){
        List<String> hosts = detector.detect(serviceName);
        if(hosts.size() == 0)
            return null;

        String host = hosts.get(idx++ % hosts.size());
        String url = "http://" + host + path;
        logger.info("consul.call with json：" + url);

        String resp = HttpClient.instance.post(url, "application/json", param.toString());
        if(StringUtils.isBlank(resp))
            return null;
        return Json.parse(resp);
    }

    /**
     * 通过kv格式的数据请求服务，content-type=application/x-www-form-urlencoded
     * @param path
     * @param param
     * @return
     */
    public String call(String path, Map<String, Object> param){
        List<String> hosts = detector.detect(serviceName);
        if(hosts.size() == 0)
            return null;

        String host = hosts.get(idx++ % hosts.size());
        String url = "http://" + host + path;

        logger.info("consul.call with param：" + url);
        StringBuffer buf = new StringBuffer();
        try{
            for(Map.Entry<String, Object> e : param.entrySet()){
                String v = "";
                if(e.getValue() != null){
                    v = String.valueOf(e.getValue());
                }
                buf.append(e.getKey()).append("=").append(URLEncoder.encode(v, "UTF-8")).append("&");
            }
            if(buf.length() > 0)
                buf.setLength(buf.length() - 1);
        }catch(Exception e){
            return null;
        }

        String resp = HttpClient.instance.post(url, "application/x-www-form-urlencoded", buf.toString());
        return resp;
    }
}
