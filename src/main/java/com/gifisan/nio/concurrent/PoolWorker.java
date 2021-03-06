package com.gifisan.nio.concurrent;

import com.gifisan.nio.AbstractLifeCycle;
import com.gifisan.nio.schedule.Job;

public class PoolWorker extends AbstractLifeCycle implements Runnable {

	private boolean	working	= false;
	private Queue<Job>	jobs		= null;

	public PoolWorker(Queue<Job> jobs) {
		this.jobs = jobs;
	}

	public void run() {
		for (;isRunning();) {
			working = true;
			Job job = jobs.poll();
			if (job != null) {
				job.schedule();
			}
			working = false;
		}
	}

	protected void doStart() throws Exception {

	}

	protected void doStop() throws Exception {
		for (;working;) {
			Thread.sleep(8);
		}
	}

}