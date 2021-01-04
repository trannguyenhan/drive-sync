package server;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;
import java.util.List;

public class ThreadServer extends Thread {
	//private int cntClient;
	Socket socketOfServer = null;
	BufferedReader is = null;
	BufferedWriter os = null;
	String line = "NULL";
	List<BufferedWriter> listWrite = null;

	public ThreadServer(Socket socket, int cntClient, List<BufferedWriter> listWrite) {
		this.socketOfServer = socket;
		//this.cntClient = cntClient;
		this.listWrite = listWrite;
	}

	@Override
	public void run() {
		// open thread input, output in server
		try {
			is = new BufferedReader(new InputStreamReader(socketOfServer.getInputStream()));
			os = new BufferedWriter(new OutputStreamWriter(socketOfServer.getOutputStream()));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		listWrite.add(os);

		
//		try {
//			os.write("1 hanoi.txt Hoang Yen Nhi"); os.newLine();
//			os.flush();
//		} catch (IOException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
		
		while (true) {
			try {
				line = is.readLine();
				while(line != null) {
					System.out.println(line);
					for (BufferedWriter bf : listWrite) {
						if (!bf.equals(os)) {
							bf.write(line);
							bf.newLine();
							bf.flush();
						}
					}
					
					line = null;
				}
			} catch (IOException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
		}
	}
}
