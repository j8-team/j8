package cn.j8.tcp.sock;

import cn.j8.tcp.sock.sm.SocketManager;

public class Context {
	private INio service;
	public Context(INio service){
		this.service = service;
	}
	
	/**
	 * 释放所有与socket相关的资源，并在释放之前调用Job的beforeCloseSocket方法 <br>
	 * 关闭socket请永远使用这个方法来做！
	 * @param socket
	 */
	public void releaseSocket(NioSocket socket){
		//任务在关闭channel前可以做一些事情
		service.getJob().beforeCloseSocket(socket);
		
		//关闭channel前先从LRU管理器中移除
		SocketManager mng = service.getSocketManager();
		if(mng != null)
			mng.remove(socket);
		
		//最后关闭这个socket
		socket.close();
	}
	
	public Job getJob(){
		return service.getJob();
	}
	
	public TaskExecutor getTaskExecutor(){
		return service.getTaskExecutor();
	}
}
