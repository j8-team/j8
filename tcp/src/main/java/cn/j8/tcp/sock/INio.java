package cn.j8.tcp.sock;

import cn.j8.tcp.sock.sm.SocketManager;

public interface INio {
	/**
	 * 获取任务
	 * @return
	 */
	public Job getJob();
	
	/**
	 * 获取socket管理器
	 * @return
	 */
	public SocketManager getSocketManager();
	
	/**
	 * 获取任务执行器
	 * @return
	 */
	public TaskExecutor getTaskExecutor();
}
