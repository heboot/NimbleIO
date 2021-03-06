package com.test.servlet;

import com.gifisan.nio.common.StringUtil;
import com.gifisan.nio.server.NIOServlet;
import com.gifisan.nio.server.Request;
import com.gifisan.nio.server.Response;

public class TestSimpleServlet extends NIOServlet{
	
	private TestSimple1 simple1 = new TestSimple1();

	public void accept(Request request, Response response) throws Exception {

		String test = request.getContent();

		if (StringUtil.isNullOrBlank(test)) {
			test = "test";
		}
		response.write(simple1.dynamic());
		response.write(test);
		response.write("$");
		response.flush();
		
	}

}
