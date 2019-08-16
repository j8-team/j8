package cn.j8.mq;

import cn.j8.mq.msg.Message;
import cn.j8.mq.msg.Metadata;

public interface IConsumerWorker {
	public void work(Metadata meta, Message msg);
}
