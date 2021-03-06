package test.jms;

import java.io.IOException;

import test.ClientUtil;

import com.gifisan.nio.client.ClientConnector;
import com.gifisan.nio.client.ClientSesssion;
import com.gifisan.nio.jms.JMSException;
import com.gifisan.nio.jms.Message;
import com.gifisan.nio.jms.client.MessageConsumer;
import com.gifisan.nio.jms.client.impl.MessageConsumerImpl;

public class TestListener {

	public static void main(String[] args) throws IOException, JMSException {
		
		ClientConnector connector = ClientUtil.getClientConnector();
		
		connector.connect();
		
		ClientSesssion session = connector.getClientSession();
		
		MessageConsumer consumer = new MessageConsumerImpl(session, "sssssss");

		consumer.login("admin", "admin100");
		
		long old = System.currentTimeMillis();

		Message message = consumer.revice();

		System.out.println("Time:" + (System.currentTimeMillis() - old));
		System.out.println(message);

		consumer.logout();
		
		connector.close();

	}

}
