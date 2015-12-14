package com.gifisan.mtp.component;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketException;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.SocketChannel;

import com.gifisan.mtp.common.CloseUtil;
import com.gifisan.mtp.server.EndPoint;

public class EndPointImpl implements EndPoint{

	private SocketChannel channel 		= null;
	private boolean endConnect 		= false;
	private InetSocketAddress local 	= null;
	private int maxIdleTime 			= 0;
	private InetSocketAddress remote 	= null;
	private SelectionKey selectionKey 	= null;
	private Socket socket 				= null;
	
	public EndPointImpl(SelectionKey selectionKey, SocketChannel channel) 
			throws SocketException {
		this.selectionKey = selectionKey;
		this.channel = channel;
		socket = channel.socket();
		if (socket == null){
		    throw new SocketException("socket is empty");
		}
	    maxIdleTime=socket.getSoTimeout();
	}
	
	public void close() {
		CloseUtil.close(channel);
		this.selectionKey.cancel();
	}
	
	public ByteBuffer completeRead(int limit) throws IOException {
		ByteBuffer buffer = ByteBuffer.allocate(limit);
		int time = limit / 64;
		int _time = 0;
		int length = -1;
		try {
			length = channel.read(buffer);
			while (length < limit && _time < time) {
				int _length = channel.read(buffer);
				length += _length;
				_time ++;
			}
		} catch (IOException e) {
			throw handleException(e);
		}
		if (length < limit) {
			throw new ChannelException("network is too weak");
		}
		return buffer;
	}
	
	public void endConnect(){
		this.endConnect = true;
//		System.out.println("end connect!!!!!!!!!!!!!!!!!!!!!!!");
	}
	
	public String getLocalAddr() {
		if (local == null) {
			local=(InetSocketAddress)socket.getLocalSocketAddress();
		}
		return local.getAddress().getCanonicalHostName();
	}
	
	public String getLocalHost() {
		return local.getHostName();
	}
	
	public int getLocalPort() {
		return local.getPort();
	}
	
	public int getMaxIdleTime() {
		return maxIdleTime;
	}

	public String getRemoteAddr() {
		if (remote == null) {
			 remote=(InetSocketAddress)socket.getRemoteSocketAddress();
		}
		return remote.getAddress().getCanonicalHostName();
	}

	public String getRemoteHost() {
		if (remote == null) {
			 remote=(InetSocketAddress)socket.getRemoteSocketAddress();
		}
		return remote.getAddress().getHostName();
	}

	public int getRemotePort() {
		if (remote == null) {
			 remote=(InetSocketAddress)socket.getRemoteSocketAddress();
		}
		return remote.getPort();
	}

	private ChannelException handleException(IOException exception) throws ChannelException{
//		this.endConnect();
		this.endConnect = true;
		
		return new ChannelException(exception.getMessage(),exception);
	}

	public boolean isBlocking() {
		return channel.isBlocking();
	}
	
	public boolean isEndConnect(){
		return endConnect;
	}
	
	public boolean isOpened() {
		return this.channel.isOpen();
	}

	public int read(ByteBuffer buffer) throws IOException{
		try {
			return this.channel.read(buffer);
		} catch (IOException e) {
			throw handleException(e);
		}
	}

	public long read(ByteBuffer[] buffers) throws IOException{
		try {
			return this.channel.read(buffers);
		} catch (IOException e) {
			throw handleException(e);
		}
	}

	public long read(ByteBuffer[] buffers,int offset,int length) throws IOException{
		
		try {
			return this.channel.read(buffers, offset, length);
		} catch (IOException e) {
			throw handleException(e);
		}
	}
	
	public ByteBuffer read(int limit) throws IOException {
		ByteBuffer buffer = ByteBuffer.allocate(limit);
		int length = -1;
		try {
			length = channel.read(buffer);
		} catch (IOException e) {
			throw handleException(e);
		}
		if (length < limit) {
			throw new ChannelException("network is too weak");
		}
		return buffer;
	}
	
	public int write(ByteBuffer buffer) throws ChannelException {
		return write(channel, buffer);
	}
	
	public long write(ByteBuffer[] buffers) throws IOException {
		throw new IOException();
	}
	
	public long write(ByteBuffer[] buffers, int offset, int length)throws IOException {
		throw new IOException();
	}

	private int write(SocketChannel client,ByteBuffer buffer) throws ChannelException{
		try {
			int length = client.write(buffer);
			while(length == 0){
				length = client.write(buffer);
			}
			return length;
		} catch (IOException e) {
			throw handleException(e);
		}
	}
	
}