package cn.j8.tcp.sock.sm;

import cn.j8.tcp.sock.NioSocket;

import java.util.Iterator;
import java.util.LinkedHashSet;

/**
 * 基于LRU策略的socket管理器，可以通过该管理器限制服务端最大的保持连接数，当有更多连接进来的时候，将会将最旧的连接踢出<br>
 * <b>该管理器尚属于实验性质！！</b>
 * @author zimT_T
 *
 */
public class LRUSocketManager implements SocketManager{
	private int capacity;
	public LRUSocketManager(int capacity){
		this.capacity = capacity;
	}
	
	private LinkedHashSet<NioSocket> channels = new LinkedHashSet<NioSocket>();
	
	@Override
	public NioSocket add(NioSocket socket){
		synchronized (this) {
			NioSocket old_channel = null;
			//如果超过最大容量，移除开头的channel
			if(channels.size() >= capacity){
				Iterator<NioSocket> it = channels.iterator();
				if(it.hasNext()){
					old_channel = it.next();
					it.remove();
				}
			}
			
			//追加这个channel
			channels.add(socket);
			
			return old_channel;
		}
	}
	
	public void update(NioSocket socket){
		synchronized (this) {
			if(channels.contains(socket)){
				channels.remove(socket);
				channels.add(socket);
			}
		}
	}

	@Override
	public void remove(NioSocket socket){
		synchronized (this) {
			channels.remove(socket);
		}
	}

	@Override
	public int size(){
		return channels.size();
	}
}
