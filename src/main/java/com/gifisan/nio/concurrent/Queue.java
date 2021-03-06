package com.gifisan.nio.concurrent;

public interface Queue<T> {

	public abstract T poll();
	
	public abstract void offer(T t);
	
	public abstract boolean empty();
	
}
