package com.gifisan.nio.server;

import java.util.Set;

public abstract interface Attributes {
	  
	  public abstract Object removeAttribute(String key);
	  
	  public abstract void setAttribute(String key, Object value);
	  
	  public abstract Object getAttribute(String key);
	  
	  public abstract Set<String> getAttributeNames();
	  
	  public abstract void clearAttributes();


}