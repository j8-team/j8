package cn.j8.tcp.http;

import java.io.Serializable;
import java.util.Map;
import java.util.TreeMap;
import java.util.UUID;

public class HttpSession implements Serializable{
	private static final long serialVersionUID = -591438032479699008L;
	
	private String id;
	private Map<String, Serializable> attrs = new TreeMap<>();
	public HttpSession(){
		this.id = "Z-MTSERVICE-" + UUID.randomUUID();
	}
	
	public String getId(){
		return id;
	}
	
	public void setAttr(String key, Serializable value){
		attrs.put(key, value);
	}
	
	public Object getAttr(String key){
		return attrs.get(key);
	}
}
