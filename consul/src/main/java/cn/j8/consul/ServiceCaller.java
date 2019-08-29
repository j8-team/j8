package cn.j8.consul;

import cn.j8.json.Json;
import org.apache.commons.lang3.StringUtils;
import java.util.List;

/**
 * 只支持json方式传递数据的服务调用
 */
public class ServiceCaller {
    private ServiceDetector detector;
    private String serviceName;
    public ServiceCaller(String consulHost, String serviceName){
        detector = new ServiceDetector(consulHost);
        this.serviceName = serviceName;
    }

    private int idx = 0;
    public Json call(String path, Json param){
        List<String> hosts = detector.detect(serviceName);
        if(hosts.size() == 0)
            return null;

        String host = hosts.get(idx++ % hosts.size());
        String url = "http://" + host + path;

        String resp = HttpClient.instance.post(url, "application/json", param.toString());
        if(StringUtils.isBlank(resp))
            return null;
        return Json.parse(resp);
    }
}
