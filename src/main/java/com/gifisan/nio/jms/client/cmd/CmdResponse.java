package com.gifisan.nio.jms.client.cmd;

public class CmdResponse {

	private boolean _continue = true;
	
	private String response;

	public boolean isContinue() {
		return _continue;
	}

	public void setContinue(boolean cont) {
		this._continue = cont;
	}

	public String getResponse() {
		return response;
	}

	public void setResponse(String response) {
		this.response = response;
	}
	
	
	
}
