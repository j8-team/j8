package cn.j8.tcp.http;

import cn.j8.tcp.http.multipart.FilePart;
import cn.j8.tcp.http.multipart.MultipartParser;
import cn.j8.tcp.http.multipart.ParamPart;
import cn.j8.tcp.http.multipart.Part;

import java.io.IOException;
import java.util.Collection;
import java.util.Map;
import java.util.TreeMap;

public class Multipart {
	
	private Map<String, Part> params = new TreeMap<>();
	public Multipart(HttpRequest request){
		try{
			MultipartParser parser = new MultipartParser(request);
			Part p = null;
			while((p = parser.readNextPart()) != null){
				params.put(p.getName(), p);
			}
		}catch(IOException e){
			e.printStackTrace();
		}
	}
	
	public Part getPart(String name){
		return params.get(name);
	}
	
	public ParamPart getParamPart(String name){
		Part p = params.get(name);
		if(p != null && p.isParam())
			return (ParamPart)p;
		return null;
	}
	
	public FilePart getFilePart(String name){
		Part p = params.get(name);
		if(p != null && p.isFile())
			return (FilePart)p;
		return null;
	}
	
	public Collection<String> paramNames(){
		return params.keySet();
	}
}
