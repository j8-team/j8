package cn.j8.consul;

import okhttp3.*;
import org.apache.commons.lang3.StringUtils;

import java.util.concurrent.TimeUnit;

class HttpClient {
    private OkHttpClient client = new OkHttpClient.Builder()
            .followRedirects(false)
            .followSslRedirects(false)
            .connectTimeout(10, TimeUnit.SECONDS)
            .readTimeout(10, TimeUnit.SECONDS)
            .writeTimeout(10, TimeUnit.SECONDS)
            .build();

    public static final HttpClient instance = new HttpClient();

    public String get(String url){
        Request.Builder proxyReqBuilder = new Request.Builder().url(url);
        proxyReqBuilder.cacheControl(CacheControl.FORCE_NETWORK);
        Request proxyReq = proxyReqBuilder.get().build();
        try{
            Response proxyResp = client.newCall(proxyReq).execute();
            return proxyResp.body().string();
        }catch(Exception e){
            return null;
        }
    }

    public String post(String url, String contentType, String param){
        return __ioWrite(url, contentType, param, "post");
    }

    public String put(String url, String contentType, String param){
        return __ioWrite(url, contentType, param, "put");
    }

    public String delete(String url, String contentType, String param){
        return __ioWrite(url, contentType, param, "delete");
    }

    public String delete(String url, String contentType){
        return delete(url, contentType, null);
    }

    private String __ioWrite(String url, String contentType, String param, String method){
        Request.Builder proxyReqBuilder = new Request.Builder().url(url);
        proxyReqBuilder.cacheControl(CacheControl.FORCE_NETWORK);
        Request proxyReq = null;
        try{
            RequestBody body = RequestBody.create(MediaType.parse(contentType), param);
            if("post".equalsIgnoreCase(method)){
                proxyReq = proxyReqBuilder.post(body).build();
            }
            else if("put".equalsIgnoreCase(method)) {
                proxyReq = proxyReqBuilder.put(body).build();
            }
            else if("delete".equalsIgnoreCase(method)){
                proxyReq = proxyReqBuilder.delete(body).build();
            }
            Response proxyResp = client.newCall(proxyReq).execute();
            return proxyResp.body().string();
        }catch(Exception e){
            return null;
        }
    }

}
