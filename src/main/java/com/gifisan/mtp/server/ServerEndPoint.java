package com.gifisan.mtp.server;

import java.io.IOException;
import java.nio.ByteBuffer;

import com.gifisan.mtp.component.MTPRequestInputStream;
import com.gifisan.mtp.component.ProtocolDecoder;
import com.gifisan.mtp.server.session.InnerSession;

public interface ServerEndPoint extends EndPoint {

	public abstract void attach(Object attachment);

	public abstract Object attachment();

	/**
	 * get comment,default value 0
	 * 
	 * @return
	 */
	public abstract int comment();

	public abstract ByteBuffer completeRead(int limit) throws IOException;

	public abstract void endConnect();

	public abstract MTPRequestInputStream getInputStream();

	public abstract String getLocalAddr();

	public abstract String getLocalHost();

	public abstract int getLocalPort();

	public abstract int getMaxIdleTime();

	public abstract ProtocolDecoder getProtocolDecoder();

	public abstract String getRemoteAddr();

	public abstract String getRemoteHost();

	public abstract int getRemotePort();

	public abstract boolean inStream();

	public abstract boolean isBlocking();

	public abstract boolean isEndConnect();

	public abstract boolean isOpened();

	public abstract boolean protocolDecode(ServletContext context) throws IOException;

	public abstract ByteBuffer read(int limit) throws IOException;

	/**
	 * <pre>
	 * [0       ~              9]
	 *  0       = 类型 [0=心跳，1=TEXT，2=STREAM，3=MULT]
	 *  1       = service name的长度
	 *  2,3,4   = parameters的长度
	 *  5,6,7,8 = 文件的长度
	 * </pre>
	 * 
	 * @return buffer
	 * @throws IOException
	 */
	public abstract int readHead(ByteBuffer buffer) throws IOException;

	/**
	 * set state,default value 0
	 * 
	 * @param comment
	 */
	public abstract void setComment(int comment);

	public abstract void setSession(InnerSession session);

	public abstract InnerSession getSession();

	public abstract void setMTPRequestInputStream(MTPRequestInputStream inputStream);

}
