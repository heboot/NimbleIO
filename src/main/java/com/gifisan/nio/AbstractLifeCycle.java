package com.gifisan.nio;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class AbstractLifeCycle implements LifeCycle {

	private Logger 				logger 					= LoggerFactory.getLogger(AbstractLifeCycle.class);
	private boolean 				failed 					= false;
	private List<LifeCycleListener> 	lifeCycleListeners			= new ArrayList<LifeCycleListener>();
	private Comparator<LifeCycleListener> lifeCycleListenerSorter	= new Comparator<LifeCycleListener>() {

		public int compare(LifeCycleListener o1, LifeCycleListener o2) {

			return o1.lifeCycleListenerSortIndex() > o2.lifeCycleListenerSortIndex() ? 1 : -1;
		}
	};
	
	private boolean running  = false;
	private boolean starting = false;
	private boolean stopped  = false;
	private boolean stopping = false;
	
	public void addLifeCycleListener(LifeCycleListener listener) {
		synchronized (lifeCycleListeners) {
			lifeCycleListeners.add(listener);
			Collections.sort(lifeCycleListeners, lifeCycleListenerSorter);
		}
	}

	protected abstract void doStart() throws Exception;

	protected abstract void doStop() throws Exception;

	private void fireEvent(int event) {
		if (lifeCycleListeners.size() == 0) {
			return;
		}
		switch (event) {
		case STARTING:
			synchronized (lifeCycleListeners) {
				for (LifeCycleListener listener :lifeCycleListeners) {
					try {
						listener.lifeCycleStarting(this);
					} catch (Exception e) {
						logger.error(e.getMessage(),e);
					}
				}
			}
			break;
		case RUNNING:
			synchronized (lifeCycleListeners) {
				for (LifeCycleListener listener :lifeCycleListeners) {
					try {
						listener.lifeCycleStarted(this);
					} catch (Exception e) {
						logger.error(e.getMessage(),e);
					}
				}
			}
			break;
		case STOPPING:
			synchronized (lifeCycleListeners) {
				for (LifeCycleListener listener :lifeCycleListeners) {
					try {
						listener.lifeCycleStopping(this);
					} catch (Exception e) {
						logger.error(e.getMessage(),e);
					}
				}
			}
			break;
		case STOPPED:
			synchronized (lifeCycleListeners) {
				for (LifeCycleListener listener :lifeCycleListeners) {
					try {
						listener.lifeCycleStopped(this);
					} catch (Exception e) {
						logger.error(e.getMessage(),e);
					}
				}
			}
			break;
		default:
			break;
		}
	}

	private void fireFailed(Exception exception){
		if (lifeCycleListeners.size() == 0) {
			return;
		}
		synchronized (lifeCycleListeners) {
			for (LifeCycleListener listener :lifeCycleListeners) {
				try {
					listener.lifeCycleFailure(this,exception);
				} catch (Exception e) {
					logger.error(e.getMessage(),e);
				}
			}
		}
	}

	public boolean isFailed() {
		return this.failed;
	}

	public boolean isRunning() {
		return this.running;
	}

	public boolean isStarted() {
		return this.running;
	}

	public boolean isStarting() {
		return this.starting;
	}

	public boolean isStopped() {
		return this.stopped;
	}

	public boolean isStopping() {
		return this.stopping;
	}

	public void removeLifeCycleListener(LifeCycleListener listener) {
		synchronized (lifeCycleListeners) {
			lifeCycleListeners.remove(listener);
			Collections.sort(lifeCycleListeners, lifeCycleListenerSorter);
		}
	}

	public void start() throws Exception {
		
		this.starting = true;
		
		this.fireEvent(STARTING);
		
		try {
			
			this.doStart();
			
			logger.info("[NIOServer] 加载完成："+this.toString());
			
		} catch (Exception e) {
		
			this.failed = true;
			
			this.fireFailed(e);
			
			throw e;
		}
		
		this.starting = false;
		
		this.running = true;
		
		this.fireEvent(RUNNING);
		
	}

	public void stop() throws Exception {
		
		this.running = false;
		
		this.stopping = true;
		
		this.fireEvent(STOPPING);
		
		try {
			
			this.doStop();
			
			logger.info("[NIOServer] 卸载完成："+this.toString());
			
		} catch (Exception e) {
			
			logger.error(e.getMessage(),e);
			
		}
		
		this.stopping = false;
		
		this.stopped = true;
		
		this.fireEvent(STOPPED);
		
	}
	
}
