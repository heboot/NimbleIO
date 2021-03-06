package com.gifisan.nio.jms.server;

import com.gifisan.nio.jms.Message;
import com.gifisan.nio.server.Request;
import com.gifisan.nio.server.Response;
import com.gifisan.nio.server.session.Session;

public class JMSProducerServlet extends JMSServlet{
	
	private final byte TRUE = 'T';
	
	private final byte FALSE = 'F';
	
	public void accept(Request request, Response response) throws Exception {
		
		Session session = request.getSession();
		
		MQContext context = getMQContext();
		
		if (context.isLogined(session)) {
			Message message = context.parse(request);
			
			byte result = context.offerMessage(message) ? TRUE : FALSE ;
			
			response.write(result);
			
		}else{
			
			response.write("用户未登录！");
				
		}

		response.flush();
		
	}

}
