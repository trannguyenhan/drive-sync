package server;

import java.io.BufferedWriter;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

public class Server extends Thread {
	private int cntClient;
	List<BufferedWriter> listWrite = new ArrayList<>();
	
	@SuppressWarnings("resource")
	@Override
	public void run() {
		ServerSocket listener = null;

		try {
			listener = new ServerSocket(9999);
		} catch (IOException e) {
			System.out.println(e);
			System.exit(1);
		}

		while(true) {
			try {
				Socket socketOfServer = null;
				System.out.println("Server is waiting to accept client...");
				socketOfServer = listener.accept();
				cntClient++;
				System.out.println("Accept a client " + cntClient + " !");

				new ThreadServer(socketOfServer, cntClient, listWrite).start();
			} catch (Exception e) {
				continue;
			}	
		}
	}
	
	public static void main(String[] args) {
		new Server().start();
	}
}
