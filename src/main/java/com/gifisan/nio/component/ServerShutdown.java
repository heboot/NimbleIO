package com.gifisan.nio.component;

import java.io.IOException;
import java.util.Scanner;

import com.gifisan.nio.client.ClientConnector;
import com.gifisan.nio.client.ClientSesssion;
import com.gifisan.nio.client.Response;
import com.gifisan.nio.common.CloseUtil;

public class ServerShutdown {

	public static void main(String[] args) throws IOException {

		ServerShutdown shutdown = new ServerShutdown();
		
		shutdown.shutdown(args);
		
	}

	public void shutdown(String [] args) throws IOException {
		if (args == null || args.length < 3) {

			System.out.print("参数不正确，按回车键退出>>");

			Scanner scanner = new Scanner(System.in);

			scanner.nextLine();

			return;
		}

		int port = Integer.valueOf(args[0]);

		String username = args[1];

		String passwrod = args[2];

		String serviceKey = "stop-server";

		ClientConnector connector = new ClientConnector("localhost", port);
		
		connector.connect();
		
		ClientSesssion session = connector.getClientSession();

		String param = "{username:\"" + username + "\",password:\"" + passwrod + "\"}";

		Response response = session.request(serviceKey, param);

		System.out.println(response.getContent());

		CloseUtil.close(connector);

		System.out.print("按回车键退出>>");

		Scanner scanner = new Scanner(System.in);

		scanner.nextLine();
	}

}
