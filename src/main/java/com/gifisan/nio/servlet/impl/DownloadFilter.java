package com.gifisan.nio.servlet.impl;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import com.gifisan.nio.common.CloseUtil;
import com.gifisan.nio.common.DebugUtil;
import com.gifisan.nio.common.StringUtil;
import com.gifisan.nio.component.Configuration;
import com.gifisan.nio.component.RESMessage;
import com.gifisan.nio.component.RequestParam;
import com.gifisan.nio.concurrent.ExecutorThreadPool;
import com.gifisan.nio.server.AsynchServletAcceptJob;
import com.gifisan.nio.server.Request;
import com.gifisan.nio.server.Response;
import com.gifisan.nio.server.ServerContext;
import com.gifisan.nio.servlet.AbstractNIOFilter;

public class DownloadFilter extends AbstractNIOFilter {

	private boolean			exclude			= false;
	private Map<String, String>	excludesMap		= null;

	public void accept(Request request, Response response) throws Exception {

		String serviceName = request.getServiceName();
		int point = serviceName.lastIndexOf('.');
		if (point == -1) {
			return;
		}
		String subfix = serviceName.substring(point);

		if (canDownload(subfix)) {
			ExecutorThreadPool executor = request.getExecutorThreadPool();
			
			DownloadJob downloadJob = new DownloadJob(request, response);
			
			executor.dispatch(downloadJob);
			
		}

	}

	private boolean canDownload(String subfix) {
		return exclude ? !excludesMap.containsKey(subfix) : true;

	}
	
	public void initialize(ServerContext context, Configuration config) throws Exception {
		String excludesContent = (String) config.getAttribute("excludes");
		if (StringUtil.isNullOrBlank(excludesContent)) {
			return;
		}
		this.exclude = true;
		this.excludesMap = new HashMap<String, String>();
		String[] excludes = excludesContent.split("\\|");
		for (String exclude : excludes) {
			if (StringUtil.isNullOrBlank(exclude)) {
				continue;
			}
			excludesMap.put(exclude, null);
		}

	}

	public void destroy(ServerContext context, Configuration config) throws Exception {
		
	}
	
	class DownloadJob extends AsynchServletAcceptJob {

		private int		BLOCK	= 102400;

		public DownloadJob(Request request, Response response) {
			super(request, response);
		}

		public void accept(Request request, Response response) throws Exception {
			String serviceName = request.getServiceName();
			String filePath = serviceName;
			RequestParam param = request.getParameters();
			int start = param.getIntegerParameter("start");
			int downloadLength = param.getIntegerParameter("length");
			File file = new File(filePath);

			FileInputStream inputStream = null;
			try {
				if (!file.exists()) {
					RESMessage message = new RESMessage(404, "file not found:" + filePath);
					response.write(message.toString());
					response.flush();
					return;
				}

				inputStream = new FileInputStream(file);

				int available = inputStream.available();

				if (downloadLength == 0) {
					downloadLength = available - start;
				}

				response.setStreamResponse(downloadLength);

				inputStream.skip(start);

				int BLOCK = this.BLOCK;

				if (BLOCK > downloadLength) {
					byte[] bytes = new byte[downloadLength];
					inputStream.read(bytes);
					response.write(bytes);
				} else {
					byte[] bytes = new byte[BLOCK];
					int times = downloadLength / BLOCK;
					int remain = downloadLength % BLOCK;
					while (times > 0) {
						inputStream.read(bytes);
						response.write(bytes);
						times--;
					}
					if (remain > 0) {
						inputStream.read(bytes, 0, remain);
						response.write(bytes, 0, remain);
					}
				}
			} catch (FileNotFoundException e) {
				DebugUtil.debug(e);
			} catch (IOException e) {
				DebugUtil.debug(e);
			} finally {
				CloseUtil.close(inputStream);
			}
			
		}
	}
	
}
