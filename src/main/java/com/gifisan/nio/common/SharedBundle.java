package com.gifisan.nio.common;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLDecoder;
import java.util.Properties;
import java.util.concurrent.atomic.AtomicBoolean;

import org.apache.log4j.PropertyConfigurator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SharedBundle {

	private static SharedBundle	bundle	= new SharedBundle();

	public static SharedBundle instance() {
		return bundle;
	}

	private String			baseDIR		= null;
	private AtomicBoolean	initialized	= new AtomicBoolean(false);
	private Logger			logger		= null;
	private Properties		properties	= new Properties();

	private SharedBundle() {
		if (initialized.compareAndSet(false, true)) {
			try {
				initialize();
			} catch (Exception e) {
				throw new Error(e);
			}
		}
	}

	public String getBaseDIR() {
		return baseDIR;
	}

	public boolean getBooleanProperty(String key) {
		return getBooleanProperty(key, false);

	}

	public boolean getBooleanProperty(String key, boolean defaultValue) {
		String temp = properties.getProperty(key);
		if (StringUtil.isNullOrBlank(temp)) {
			return defaultValue;
		}
		return Boolean.valueOf(temp);
	}

	public double getDoubleProperty(String key) {
		return getDoubleProperty(key, 0);
	}

	public double getDoubleProperty(String key, double defaultValue) {
		String temp = properties.getProperty(key);
		if (StringUtil.isNullOrBlank(temp)) {
			return defaultValue;
		}
		return Double.valueOf(temp);
	}

	public int getIntegerProperty(String key) {
		return getIntegerProperty(key, 0);
	}

	public int getIntegerProperty(String key, int defaultValue) {
		String temp = properties.getProperty(key);
		if (StringUtil.isNullOrBlank(temp)) {
			return defaultValue;
		}
		return Integer.valueOf(temp);
	}

	public long getLongProperty(String key) {
		return getLongProperty(key, 0);
	}

	public long getLongProperty(String key, long defaultValue) {
		String temp = properties.getProperty(key);
		if (StringUtil.isNullOrBlank(temp)) {
			return defaultValue;
		}
		return Long.valueOf(temp);
	}

	public String getProperty(String key) {
		return getProperty(key, null);
	}

	public String getProperty(String key, String defaultValue) {
		String value = properties.getProperty(key);
		if (StringUtil.isNullOrBlank(value)) {
			return defaultValue;
		}
		return value;
	}

	public String getPropertyNoBlank(String key) throws Exception {
		String value = properties.getProperty(key);
		if (StringUtil.isNullOrBlank(value)) {
			throw new Exception("property " + key + " is empty");
		}
		return value;
	}

	private void initialize() throws Exception {
		URL url = SharedBundle.class.getClassLoader().getResource(".");
		File root = new File(url.getFile());
		String path = root.getAbsolutePath();
		path = URLDecoder.decode(path, "UTF-8");
		baseDIR = path + "/";
		File[] files = root.listFiles();
		for (File file : files) {
			if (file.isFile() && file.getName().endsWith(".properties")) {
				try {
					Properties temp = FileUtil.readProperties(file);
					if ("log4j.properties".equals(file.getName())) {
						PropertyConfigurator.configure(temp);
						logger = LoggerFactory.getLogger(SharedBundle.class);
						continue;
					}
					properties.putAll(temp);
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}

	public void loadLog4jProperties(Class<?> clazz, String file) throws IOException {
		String filePath = baseDIR + file;
		Properties log4j = loadProperties(FileUtil.openInputStream(new File(filePath)));
		PropertyConfigurator.configure(log4j);
		logger = LoggerFactory.getLogger(SharedBundle.class);
	}

	public Properties loadProperties(Class<?> clazz, String file) throws IOException {
		String filePath = baseDIR + file;
		logger.info("load properties [ {} ]", filePath);
		return loadProperties(FileUtil.openInputStream(new File(filePath)));
	}

	public Properties loadProperties(InputStream inputStream) throws IOException {
		Properties temp = new Properties();
		try {
			temp.load(inputStream);
			return temp;
		} finally {
			CloseUtil.close(inputStream);
		}
	}

	public void storageProperties(Class<?> clazz, String file) throws IOException {
		String filePath = baseDIR + file;
		logger.info("storage properties [ {} ]", filePath);
		storageProperties(FileUtil.openInputStream(new File(filePath)));
	}

	public void storageProperties(InputStream inputStream) throws IOException {
		Properties temp = new Properties();
		try {
			temp.load(inputStream);
			properties.putAll(temp);
		} finally {
			CloseUtil.close(inputStream);
		}
	}
}
