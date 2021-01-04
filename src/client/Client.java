package client;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.AbstractMap;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.Vector;

public class Client extends Thread {
	Set<String> semaphore_tmp = new HashSet<>();
	Set<String> semaphore = Collections.synchronizedSet(semaphore_tmp);

	// private int index;
	private String pathFile;

	// Queue<AbstractMap.SimpleEntry<Integer, String>> qstatus = new LinkedList<>();
	Vector<AbstractMap.SimpleEntry<Integer, String>> qstatus = new Vector<>();

	public String getPathFile() {
		return pathFile;
	}

	public void setPathFile(String pathFile) {
		this.pathFile = pathFile;
	}

	public Client(String pathFile) {
		setPathFile(pathFile);
	}

	@SuppressWarnings("resource")
	@Override
	public void run() {
		// create folder with pathFile
		File file = new File(pathFile);
		boolean dirCreate = file.mkdir();
		if (dirCreate) {
			System.out.println("Successfully create FOLDER : " + pathFile + "\n_______\n");
		} else {
			System.out.println("FOLDER was create : " + pathFile + "\n_______\n");
		}

		// domain server
		final String serverHost = "localhost";
		Socket socketOfClient = null;
		BufferedWriter os = null;
		BufferedReader is = null;

		// connect host
		try {
			// send to port 9999
			socketOfClient = new Socket(serverHost, 9999);

			// create thread send data
			os = new BufferedWriter(new OutputStreamWriter(socketOfClient.getOutputStream()));

			// create thread receive data from server
			is = new BufferedReader(new InputStreamReader(socketOfClient.getInputStream()));

		} catch (UnknownHostException e) {
			System.err.println("Don't know about host " + serverHost);
			// return;
		} catch (IOException e) {
			System.err.println("Couldn't get I/O for the connection to " + serverHost);
			// return;
		}

		// thread listen modify
		new ThreadListenModify(pathFile, qstatus).start();
		// thread service
		new Service(pathFile, os, is, semaphore).start();

		while(true) {
			while(!qstatus.isEmpty()) {
				int status = qstatus.firstElement().getKey();
				String data = qstatus.firstElement().getValue();
				if(status == 0) {
					System.out.println("[client notification] CREATE : " + data);
					
					if(semaphore.contains(data)) {
						semaphore.remove(data);
						qstatus.remove(0);
						break;
					}
					try {
						os.write(data);
						os.newLine();
						os.flush();
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				} else if(status == 1) {
					System.out.println("[client notification] MODIFY : " + data);
					
					if(semaphore.contains(data)) {
						semaphore.remove(data);
						qstatus.remove(0);
						break;
					}
					try {
						os.write(data);
						os.newLine();
						os.flush();
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				} else {
					System.out.println("[client notification] DELETE : " + data);
					
					if(semaphore.contains(data)) {
						semaphore.remove(data);
						qstatus.remove(0);
						break;
					}
					try {
						os.write(data);
						os.newLine();
						os.flush();
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
				
				qstatus.remove(0);
			}
		}

	}

	public static void main(String[] args) throws IOException {
		// path folder want sync
		String pathFile = args[0];
		new Client(pathFile).start();
	}
}
