package com.gifisan.nio.jms.server;

import java.util.HashMap;
import java.util.Map;

import com.gifisan.nio.AbstractLifeCycle;
import com.gifisan.nio.common.LifeCycleUtil;
import com.gifisan.nio.common.SharedBundle;
import com.gifisan.nio.component.MessageWriterJob;
import com.gifisan.nio.component.RequestParam;
import com.gifisan.nio.concurrent.BlockingQueueThreadPool;
import com.gifisan.nio.concurrent.ThreadPool;
import com.gifisan.nio.jms.Message;
import com.gifisan.nio.server.Request;
import com.gifisan.nio.server.Response;

public class SubscribeProductLine extends AbstractLifeCycle implements MessageQueue, Runnable {

	private ThreadPool				messageWriteThreadPool	= null;
	private Map<String, ConsumerGroup>	consumerGroupMap		= null;
	private MQContext				context				= null;
	private MessageGroup			messageGroup			= null;
	private boolean				running				= false;
	private long					dueTime				= 0;

	public SubscribeProductLine(MQContext context) {
		this.context = context;
	}

	protected void doStart() throws Exception {

		this.running = true;

		this.messageGroup = new MessageGroup();

		this.consumerGroupMap = new HashMap<String, ConsumerGroup>();

		this.dueTime = context.getMessageDueTime();
		
		SharedBundle bundle = SharedBundle.instance();

		int CORE_SIZE = bundle.getIntegerProperty("SERVER.CORE_SIZE",4);

		this.messageWriteThreadPool = new BlockingQueueThreadPool("Message-write-Job", CORE_SIZE);

		this.messageWriteThreadPool.start();

	}

	// TODO 处理剩下的message 和 receiver
	protected void doStop() throws Exception {

		this.running = false;

		LifeCycleUtil.stop(messageWriteThreadPool);
	}

	private ConsumerGroup getConsumerGroup(String queueName) {

		ConsumerGroup consumerGroup = consumerGroupMap.get(queueName);

		if (consumerGroup == null) {

			synchronized (consumerGroupMap) {

				consumerGroup = consumerGroupMap.get(queueName);

				if (consumerGroup == null) {
					consumerGroup = new ConsumerGroup();
					consumerGroupMap.put(queueName, consumerGroup);
				}
			}
		}
		return consumerGroup;
	}

	public MQContext getContext() {
		return context;
	}

	public boolean offerMessage(Message message) {
		return messageGroup.offer(message);
	}

	public void pollMessage(Request request, Response response) {

		RequestParam param = request.getParameters();
		
		String queueName = param.getParameter("queueName");

		ConsumerGroup consumerGroup = getConsumerGroup(queueName);

		Consumer consumer = new Consumer(request, response,consumerGroup,queueName);

		consumerGroup.offer(consumer);
	}

	public void run() {

		while (running) {

			Message message = messageGroup.poll(16);

			if (message == null) {
				continue;
			}

			String queueName = message.getQueueName();

			ConsumerGroup consumerGroup = getConsumerGroup(queueName);

			Consumer consumer = consumerGroup.poll(16);

			if (consumer == null) {

				filterUseless(message);

				continue;
			}

			MessageWriterJob job = new MessageWriterJob(messageGroup, consumer, message);

			messageWriteThreadPool.dispatch(job);

		}

	}

	private void filterUseless(Message message) {
		long now = System.currentTimeMillis();
		long dueTime = this.dueTime;

		if (now - message.createTime() < dueTime) {
			this.offerMessage(message);
		}
		// 消息过期了

	}

}
