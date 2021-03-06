package com.gifisan.nio.jms;

public interface Transaction {
	
	/**
	 * 连接启动事务后，需要进行显示的commit</BR>
	 * 如果出现以下情况则服务器进行回滚操作</BR>
	 * <ul>
	 * <li>连接失败</li>
	 * <li>连接被客户端主动关闭</li>
	 * <li>连接调用rollback方法</li>
	 * </ul>
	 * @throws JMSException
	 */
	public abstract boolean beginTransaction() throws JMSException;
	
	public abstract boolean commit() throws JMSException;
	
	public abstract boolean rollback() throws JMSException;
}
