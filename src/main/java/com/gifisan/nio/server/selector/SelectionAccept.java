package com.gifisan.nio.server.selector;

import java.io.IOException;
import java.nio.channels.SelectionKey;

public interface SelectionAccept {

	public void accept(SelectionKey selectionKey) throws IOException;
	
}
