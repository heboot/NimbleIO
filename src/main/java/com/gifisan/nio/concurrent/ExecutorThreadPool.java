package com.gifisan.nio.concurrent;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import com.gifisan.nio.AbstractLifeCycle;

public class ExecutorThreadPool extends AbstractLifeCycle {

	private int	corePoolSize		= 4;
	private int	maximumPoolSize	= 4 << 4;
	private long	keepAliveTime		= 60 * 1000L;
	private String	threadPoolName		= null;

	public ExecutorThreadPool(int corePoolSize, String threadPoolName) {
		this.corePoolSize = corePoolSize;
		this.maximumPoolSize = corePoolSize << 4;
		this.threadPoolName = threadPoolName;
	}

	public ExecutorThreadPool(int corePoolSize, int maximumPoolSize, String threadPoolName) {
		this.corePoolSize = corePoolSize;
		this.maximumPoolSize = maximumPoolSize;
		this.threadPoolName = threadPoolName;
	}

	public ExecutorThreadPool(int corePoolSize, int maximumPoolSize, long keepAliveTime, String threadPoolName) {
		this.corePoolSize = corePoolSize;
		this.maximumPoolSize = maximumPoolSize;
		this.keepAliveTime = keepAliveTime;
		this.threadPoolName = threadPoolName;
	}

	private ThreadPoolExecutor	poolExecutor	= null;

	public void dispatch(Runnable job) {
		this.poolExecutor.execute(job);

	}

	protected void doStart() throws Exception {

		NamedThreadFactory threadFactory = new NamedThreadFactory(threadPoolName);

		poolExecutor = new ThreadPoolExecutor(corePoolSize, maximumPoolSize, keepAliveTime, TimeUnit.MILLISECONDS,
				new ArrayBlockingQueue<Runnable>(corePoolSize * 1000000), threadFactory);
	}

	protected void doStop() throws Exception {
		poolExecutor.shutdown();
	}

}
