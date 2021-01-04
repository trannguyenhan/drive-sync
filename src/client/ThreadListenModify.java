package client;

import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardWatchEventKinds;
import java.nio.file.WatchEvent;
import java.nio.file.WatchKey;
import java.nio.file.WatchService;
import java.util.AbstractMap;
import java.util.Vector;

public class ThreadListenModify extends Thread {
	String pathFile;
	//Queue<AbstractMap.SimpleEntry<Integer, String>> qstatus = null;
	Vector<AbstractMap.SimpleEntry<Integer, String>> qstatus = null;
	
	public ThreadListenModify(String pathFile, Vector<AbstractMap.SimpleEntry<Integer, String>> status) {
		this.pathFile = pathFile;
		this.qstatus = status;
	}

	@SuppressWarnings("unchecked")
	@Override
	public void run() {
		WatchService watchService = null;

		try {
			watchService = FileSystems.getDefault().newWatchService();
		} catch (IOException e1) {
			// TODO Auto-generated catch block
		}
		Path dirPath = Paths.get(pathFile);
		WatchKey watchKey = null;

		try {
			dirPath.register(watchService, StandardWatchEventKinds.ENTRY_CREATE, StandardWatchEventKinds.ENTRY_DELETE,
					StandardWatchEventKinds.ENTRY_MODIFY);
		} catch (IOException e1) {
			// TODO Auto-generated catch block
		}

		while (true) {
			try {
				watchKey = watchService.take();
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			for (WatchEvent<?> event : watchKey.pollEvents()) {
				WatchEvent.Kind<?> kind = event.kind();
				WatchEvent<Path> ev = (WatchEvent<Path>) event;
				Path fileName = ev.context();

				// 0 : create
				// 1 : modify
				// 2 : delete
				if (kind == StandardWatchEventKinds.ENTRY_CREATE) {
					//System.out.printf("A file %s was create.%n", fileName.getFileName());
					String path_file = pathFile + "/" + fileName.getFileName().toString();
					Path file = Path.of(path_file);

					String data = "0";
					try {
						data = Files.readString(file);
					} catch (IOException e) {
						//
					}
					if(data.equals("")) data = "0";
					
					data = "0 " + fileName.getFileName().toString() + " " + data;
					qstatus.add(new AbstractMap.SimpleEntry<>(0, data));
					
					//System.out.println("CREATE :" + data);
				} else if (kind == StandardWatchEventKinds.ENTRY_MODIFY) {
					//System.out.printf("A file %s was modified.%n", fileName.getFileName());
					String path_file = pathFile + "/" + fileName.getFileName().toString();
					Path file = Path.of(path_file);

					String data = "0";
					try {
						data = Files.readString(file);
					} catch (IOException e) {
						//
					}
					if(data.equals("")) continue;
					
					data = "1 " + fileName.getFileName().toString() + " " + data;
					qstatus.add(new AbstractMap.SimpleEntry<>(1, data));

					//System.out.println("MODIFY :" + data);
				} else if (kind == StandardWatchEventKinds.ENTRY_DELETE) {
					//System.out.printf("A file %s was deleted.%n", fileName.getFileName());
					String data = "2 " + fileName.getFileName().toString() + " 0";
					qstatus.add(new AbstractMap.SimpleEntry<>(2, data));
					
					//System.out.println("DELETE :" + data);
				}

			}

			boolean valid = watchKey.reset();
			if (!valid) {
				break;
			}
		}
	}
	
//	public static void main(String[] args) {
//		Vector<AbstractMap.SimpleEntry<Integer, String>> qstatus = new Vector<>();
//		new ThreadListenModify("resource/client1", qstatus).start();
//	}
}