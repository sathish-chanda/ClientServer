import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.Socket;

public class HTTPserver extends Thread {

	private Socket socket;

	public HTTPserver(Socket socket) {
		this.socket = socket;
	}

	public void run() {
		try {
			BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
			PrintWriter out = new PrintWriter(socket.getOutputStream());
			while (true) {
				String request = in.readLine().trim();
				System.out.println(request);
				int index = request.length();
				if(request.contains("header{"))
					index = request.indexOf("header");
				System.out.println("Client requested : " + request.substring(0,index));
				String cd = System.getProperty("user.dir");
				String directory = new String(cd);
				String response = "";
				index = request.indexOf("http://") + 7;
				if (index < request.length())
					directory = request.substring(index).trim().split(" ")[0];
				if (directory.equals(cd)) {
					if (request.contains("get /")) {
						if (request.contains("get / ")) {
							response = getAllCurrentFilesInDirectory("*");
						} else {
							index = request.indexOf("get /") + 5;
							String tmp = request.substring(index);
							String filename = tmp.split(" ")[0];
							response = getFileContents(filename);
						}
					} else if (request.contains("post /")) {
						index = request.indexOf("post /") + 6;
						if (index + 1 < request.length()) {
							String rest = request.substring(index);
							String[] arr = rest.split(" ");
							String filename = arr[0];
							String tmp = rest.substring(filename.length());
							int start = tmp.indexOf("contents{") + 9;
							String data = tmp.substring(start, tmp.indexOf("}contents"));
							start = tmp.indexOf("header{") + 7;
							if (start != tmp.indexOf("}header"))
								data = data + tmp.indexOf(start, tmp.indexOf("}header"));
							response = postDataInFile(filename, data);
						} else {
							response = "HTTP ERROR 408 FileName Not given to POST";
						}
					}
				} else {
					response = "HTTP ERROR 407 Can't Access the Directory";
				}
				System.out.println("Server response  : " + response);
				out.write(response);
				out.flush();
				out.close();
			}
		} catch (IOException e) {
			System.out.println("HTTP ERROR 409 Unable to start the server!");
		} finally {
			try {
				socket.close();
			} catch (IOException e) {
				System.out.println("HTTP ERROR 410 Socket Close Exception!");
			}
		}
	}

	public synchronized String getAllCurrentFilesInDirectory(String acceptKey) {
		StringBuilder builder = new StringBuilder();
		File folder = new File(System.getProperty("user.dir"));
		File[] files = folder.listFiles();
		for (File file : files) {
			if (file.isFile()) {
				if (!file.getName().equals(".project") && !file.getName().equals(".classpath")) {
					builder.append(file.getName() + "\n");
				}
			}
		}
		return builder.toString();
	}

	public synchronized String getFileContents(String filename) {
		StringBuilder builder = new StringBuilder();
		File file = new File(filename);
		if (file.exists()) {
			String line = "";
			BufferedReader br;
			try {
				br = new BufferedReader(new FileReader(filename));
			} catch (FileNotFoundException e1) {
				// TODO Auto-generated catch block
				return "HTTP ERROR 405 FileNotFound";
			}
			try {
				while ((line = br.readLine()) != null) {
					builder.append(line.trim() + "\n");
				}
			} catch (IOException e) {
				// TODO Auto-generated catch block
				return "HTTP ERROR 406 IOException";
			}
			if (builder.toString().length() != 0)
				return builder.toString();
		}
		// No content in the file
		return "HTTP ERROR 404";
	}

	public synchronized String postDataInFile(String filename, String data) {

		File file = new File(filename);
		if (file.exists()) {
			try {
				String reqData = data;
				StringBuilder sb = new StringBuilder();
				BufferedReader br = new BufferedReader(new FileReader(filename));
				String st;
				while ((st = br.readLine()) != null) {
					sb.append((st.trim() + "\n"));
				}
				String dataOffile = sb.toString();
				if (dataOffile.contains("<!-- Edit Prohibited -->")) {
					return "205 Failed! Write Restriction!";
				} else {
					dataOffile = dataOffile + "\n" + reqData;
					String outputFile = dataOffile;
					FileWriter fw = new FileWriter(filename);
					fw.write(outputFile);
					try {
						fw.close();
					} catch (IOException e) {
						// TODO Auto-generated catch block
						return "206 Failed! Error in closing the file!";
					}
					return "200 Ok! Write Successful!";
				}

			} catch (IOException e) {
				// TODO Auto-generated catch block
				return "204 Failed! File Restriction!";
			}

		} else {
			try {
				BufferedWriter out = new BufferedWriter(new FileWriter(filename));
				out.write(data);
				out.close();

			} catch (IOException e) {
				return "207 Failed! Can't create NEW File!";
			}
			return "200 Ok! Write Successful!";
		}
	}
}
