package com.gifisan.nio.client;

import java.nio.channels.ClosedChannelException;
import java.nio.channels.Selector;

import com.gifisan.nio.component.EndPoint;

public interface ClientEndPoint extends EndPoint{
	
	public abstract void register(Selector selector,int option) throws ClosedChannelException;
}
