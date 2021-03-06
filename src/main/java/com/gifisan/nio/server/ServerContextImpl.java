package com.gifisan.nio.server;

import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.gifisan.nio.AbstractLifeCycle;
import com.gifisan.nio.Encoding;
import com.gifisan.nio.common.DebugUtil;
import com.gifisan.nio.common.LifeCycleUtil;
import com.gifisan.nio.common.SharedBundle;
import com.gifisan.nio.component.FilterService;
import com.gifisan.nio.concurrent.ExecutorThreadPool;

public class ServerContextImpl extends AbstractLifeCycle implements ServerContext {

	private Charset			encoding			= null;
	private NIOServer			server			= null;
	private ServerEndpointFactory	endpointFactory	= null;
	private FilterService		filterService		= null;
	private ExecutorThreadPool	servletLazyExecutor	= null;
	private String				appLocalAddres		= null;
	private Logger				logger			= LoggerFactory.getLogger(ServerContextImpl.class);

	public ServerContextImpl(NIOServer server) {
		this.server = server;
		this.endpointFactory = new ServerEndpointFactory();
	}

	protected void doStart() throws Exception {
		SharedBundle bundle = SharedBundle.instance();
		
		Charset ENCODING = Charset.forName(bundle.getProperty("SERVER.ENCODING","GBK"));
		
		Encoding.DEFAULT = ENCODING;

		boolean debug = bundle.getBooleanProperty("SERVER.DEBUG");
		
		DebugUtil.setEnableDebug(debug);
		
		int CORE_SIZE = bundle.getIntegerProperty("SERVER.CORE_SIZE", 4);
		
		this.encoding = ENCODING;
		this.appLocalAddres = bundle.getBaseDIR() + "app/";
		this.filterService = new FilterService(this);
		this.servletLazyExecutor = new ExecutorThreadPool(CORE_SIZE, "Servlet-accept-Job");

		logger.info("[NIOServer] 工作目录：{}" , appLocalAddres);
		logger.info("[NIOServer] 项目编码：{}" , ENCODING);

		this.filterService.start();
		this.servletLazyExecutor.start();
		this.endpointFactory.start();
	}

	protected void doStop() throws Exception {
		LifeCycleUtil.stop(endpointFactory);
		LifeCycleUtil.stop(servletLazyExecutor);
		LifeCycleUtil.stop(filterService);
	}


	public Charset getEncoding() {
		return encoding;
	}

	public ServerEndpointFactory getServerEndpointFactory() {
		return endpointFactory;
	}

	public NIOServer getServer() {
		return server;
	}

	public String getAppLocalAddress() {
		return appLocalAddres;
	}

	public void setEncoding(Charset encoding) {
		this.encoding = encoding;
	}

	public FilterService getFilterService() {
		return filterService;
	}

	public ExecutorThreadPool getExecutorThreadPool() {
		return servletLazyExecutor;
	}

	public boolean redeploy() {
		return this.filterService.redeploy();
	}
	
	private Map<String, Object> attributes = new HashMap<String, Object>();

	public Object removeAttribute(String key) {
		return this.attributes.remove(key);
		
	}

	public void setAttribute(String key, Object value) {
		this.attributes.put(key, value);
	}

	public Object getAttribute(String key) {
		return this.attributes.get(key);
	}

	public Set<String> getAttributeNames() {
		return this.attributes.keySet();
	}

	public void clearAttributes() {
		this.attributes.clear();
		
	}

}
