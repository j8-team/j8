package cn.j8.tcp.msg;

import cn.j8.tcp.sock.*;
import cn.j8.tcp.util.Common;
import com.alibaba.fastjson.JSONObject;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class MessageService implements Job {

	private static Logger logger = LoggerFactory.getLogger(MessageService.class);
	
	private int soTimeout = 3000;
	private int serverPort = 9999;
	private int workerCount = 4;
	private int reactorCount = 4;
	private NioService service;
	
	public void setPort(int port){
		this.serverPort = port;
	}
	
	public void setReactorCount(int count){
		this.reactorCount = count;
	}
	
	public void setWorkerCount(int count){
		this.workerCount = count;
	}
	
	public void setSoTimeout(int soTimeout){
		this.soTimeout = soTimeout;
	}
	
	/**
	 * 对数据进行加密压缩
	 * @param s
	 * @return
	 */
	protected byte[] encode(String s){
		try{
			byte[] bs = s.getBytes("UTF-8");
			bs = Common.gzencode(bs);
			return bs;
		}catch(Exception e){
			logger.error("encode.error", e);
			return new byte[0];
		}
	}
	
	/**
	 * 对数据解压解密
	 * @param bs
	 * @return
	 */
	protected String decode(byte[] bs){
		try{
			//对数据内容先进行解压，再进行m1解密
			bs = Common.gzdecode(bs);
			if(bs == null)
				return null;
			return new String(bs, "UTF-8");
		}catch(Exception e){
			logger.error("decode.error", e);
			return null;
		}
	}
	
	@Override
	public boolean doJob(NioSocket socket, Object packet) {
		try{
			//处理数据， 对数据内容先进行解压，再进行m1解密
			byte[] bs = (byte[])packet;
			String content = decode(bs);
			if(StringUtils.isBlank(content))
				return false;
			
			JSONObject json = JSONObject.parseObject(content);
			return doJob(socket, json);
		}catch(Exception e){
			logger.error("doJob error", e);
			return false;
		}
	}
	
	public boolean init(){
		ServiceConfig config = new ServiceConfig();
		config.setSoTimeout(this.soTimeout);
		
		service = new NioService(this.serverPort);
		service.setWorkerCount(this.workerCount);
		service.setReactorCount(this.reactorCount);
		service.setJob(this);
		service.setConfig(config);
		service.setProtocalParserFactory(new ProtocalParserFactory() {
			@Override
			public ProtocalParser create() {
				return new MessageProtocalParser();
			}
		});
		return service.init();
	}
	
	public void run(){
		//开始监听用户请求
		logger.info("服务启动成功，开始监听用户请求");
		while(!service.accpet()){
			logger.error("服务出现错误，重启Acceptor!");
		}
	}

	/**
	 * 子类实现这个方法来处理数据。
	 * @param socket
	 * @param data
	 * @return
	 */
	protected abstract boolean doJob(NioSocket socket, JSONObject data);
	
	@Override
	public void beforeCloseSocket(NioSocket socket) {
		//默认什么也不做
	}
}
