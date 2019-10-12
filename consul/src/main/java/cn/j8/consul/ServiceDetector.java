package cn.j8.consul;

import cn.j8.json.Json;
import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.List;

class ServiceDetector {

    private String consulHost;
    public ServiceDetector(String consulHost){
        this.consulHost = consulHost;
    }

    public List<String> detect(String serviceName){
        String url = "http://" + consulHost + "/v1/catalog/service/" + serviceName;

        String resp = HttpClient.instance.get(url);
        if(StringUtils.isBlank(resp)){
            return new ArrayList<>();
        }

        List<String> hosts = new ArrayList<>();
        Json json = Json.parse(resp);
        for(int i=0; i<json.size(); ++i){
            Json obj = json.asArray().val(i);
//            String status = obj.asObj().strVal("AggregatedStatus");
//            if(!"passing".equalsIgnoreCase(status))
//                continue;
//
//            Json service = obj.asObj().val("Service");
//            String addr = service.asObj().strVal("Address");

            //FIXME 这里还要增加对节点健康度的检查，时间原因国庆后再来！


            String addr = obj.asObj().strVal("Address");
            int port = obj.asObj().intVal("ServicePort");
            hosts.add(addr + ":" + port);
        }
        return hosts;
    }
}
