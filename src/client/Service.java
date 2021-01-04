package client;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Set;

public class Service extends Thread {
	String res;
	String pathFolder;
	BufferedWriter os = null;
	BufferedReader is = null;
	Set<String> semaphore = null;

	public Service(String res, String pathFolder) {
		this.res = res;
		this.pathFolder = pathFolder;
	}

	public Service(String pathFolder, BufferedWriter os, BufferedReader is, Set<String> semaphore) {
		this.pathFolder = pathFolder;
		this.os = os;
		this.is = is;
		this.semaphore = semaphore;
	}

	@Override
	public void run() {
		while (true) {
			int status = -1;
			try {
				res = is.readLine();
				while (res != null) {
					status = Integer.parseInt(res.substring(0, 1));
					System.out.println(res);

					semaphore.add(res);
					//System.out.println(res);

					String[] nd = res.split(" ", 3);
					String path = pathFolder + "/" + nd[1];
					String data = nd[2];
					if (data.equals("0")) {
						data = "";
					}

					if (status == 0) {	
						File file = new File(path);
						try {
							file.createNewFile();
						} catch (IOException e1) {
							// TODO Auto-generated catch block
							e1.printStackTrace();
						}
						Path file_path = Path.of(path);

						try {
							Files.writeString(file_path, data);
						} catch (IOException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					} else if (status == 1) {
						BufferedWriter writer = new BufferedWriter(new FileWriter(path));
						writer.write(data);
						writer.close();

					} else {
						File file = new File(path);
						file.delete();
					}
					
					res = null;
				}
			} catch (Exception e) {
				// TODO: handle exception
			}
		}
	}

//	public static void main(String[] args) {
//		String res = "0 hanoi.txt Hyn";
//		String res_tmp = "1 " + res.substring(2);
//		System.out.println(res_tmp);
//	}
}
