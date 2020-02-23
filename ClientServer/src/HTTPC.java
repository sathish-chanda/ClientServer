import java.io.*;
import java.net.InetAddress;
import java.net.MalformedURLException;
import java.net.Socket;
import java.net.URL;
import java.net.UnknownHostException;
import java.util.Scanner;

public class HTTPC {
	private static int port;

	public static void main(String[] args) throws UnknownHostException, IOException {
		// 1.reading the input
		Scanner scan = new Scanner(System.in);
		System.out.println("Enter the Http link along with the command: ");
		String request = scan.nextLine().trim();
		RequestInformation reqinfo = verifyRequest(request);
		if (reqinfo.status.equals("VALID")) {
			Scanner sc = null;
			try {
				sc = new Scanner(new File("PORT.txt"));
			} catch (FileNotFoundException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			port = sc.nextInt();
			// Get method
			// System.out.println(reqinfo);
			if (reqinfo.type.equalsIgnoreCase("get") || reqinfo.type.equalsIgnoreCase("Only Header")) {
				get(reqinfo, request);
			} else {
				post(reqinfo, request);
			}
		} else
			System.out.println(reqinfo.msg);
	}

	public static void get(RequestInformation reqinfo, String request) throws UnknownHostException, IOException {
		InetAddress ip;
		String hostAddress = null;
		try {
			ip = InetAddress.getLocalHost();
			hostAddress = ip.getHostName();
		} catch (UnknownHostException e) {

			e.printStackTrace();
		}
		Scanner sc = null;
		try {
			sc = new Scanner(new File("PORT.txt"));
		} catch (FileNotFoundException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		port = sc.nextInt();
		Socket socket = new Socket(hostAddress, port);
		PrintWriter out = new PrintWriter(socket.getOutputStream());
		BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
		out.println(request);
		out.flush();
		BufferedWriter fw = null;
		
		if (reqinfo.out.equalsIgnoreCase("YES")) {
			fw = new BufferedWriter(new FileWriter(reqinfo.outfilename));
			String line=null;
			while ((line = in.readLine()) != null) {
				if (fw != null) {
					fw.write(line.trim());
					fw.newLine();
				}
			}
			fw.close();
		}
		else
		{
		String line=null;
		while ((line = in.readLine()) != null) {			
				System.out.println(line.trim());
		}
		}
	}

	public static void post(RequestInformation reqinfo, String request) throws UnknownHostException, IOException {
	    InetAddress ip;
		String hostAddress = null;
		try {
			ip = InetAddress.getLocalHost();
			hostAddress = ip.getHostName();
		} catch (UnknownHostException e) {
   	    e.printStackTrace();
		}
		Scanner sc = null;
		try {
			sc = new Scanner(new File("PORT.txt"));
		} catch (FileNotFoundException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		port = sc.nextInt();
		Socket socket = new Socket(hostAddress, port);
		PrintWriter pw = new PrintWriter(socket.getOutputStream());
		BufferedReader br = new BufferedReader(new InputStreamReader(socket.getInputStream()));
		StringBuilder builder = new StringBuilder();
		for (String key : reqinfo.hpairs.keySet()) {
			builder.append(key);
			builder.append(": ");
			builder.append(reqinfo.hpairs.get(key));
			builder.append(",");
		}
		String contents = "";
		String headerinfo = "";
		headerinfo = builder.toString();
		if (reqinfo.data.equalsIgnoreCase("YES") || reqinfo.file.equalsIgnoreCase("YES")) {
			contents = reqinfo.dlist.toString();
			contents = contents.substring(1, contents.length() - 1);
		}
		if (headerinfo.length() < 3)
			headerinfo += ",";
		String reqxt = request + " header{" +headerinfo.substring(0, headerinfo.length() - 1) + "}header contents{" + contents +"}contents";
		pw.println(reqxt);
		pw.flush();
		InputStream inputStream = socket.getInputStream();
		InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
		StringBuilder sb = new StringBuilder();

		int breakpoint;
		while ((breakpoint = inputStreamReader.read()) != -1) {
			sb.append((char) breakpoint);
		}
		display(reqinfo, sb.toString());
	}

	public static void display(RequestInformation reqinfo, String output) throws IOException {
		if (reqinfo.out.equalsIgnoreCase("YES")) {
			System.out.println("Write the output to the file");
			System.out.println(output);
			BufferedWriter out = new BufferedWriter(new FileWriter(reqinfo.outfilename));
			out.write(output);
			out.close();
		} else
			System.out.println(output);
	}

	public static RequestInformation verifyRequest(String cmd) {
		RequestInformation rinfo = new RequestInformation();
		String tmp = cmd.trim();
		String[] arr = cmd.split(" ");
		try // For IndexOutOfBound
		{
			int index = 0;
			if (!arr[index].equalsIgnoreCase("httpc")) {
				rinfo.msg = "Missing 'httpc'";
				return rinfo;
			}
			tmp = tmp.substring(6);
			index++;
			if (!arr[index].equalsIgnoreCase("GET") && !arr[index].equalsIgnoreCase("POST")
					&& !arr[index].equalsIgnoreCase("-v")) {

				rinfo.msg = "Neigther GET nor POST request and -v";
				return rinfo;
			}
			if (arr[index].equalsIgnoreCase("GET")) {

				rinfo.type = "GET";
				tmp = tmp.substring(4).trim();
			} else if (arr[index].equalsIgnoreCase("POST")) {

				rinfo.type = "POST";
				tmp = tmp.substring(5).trim();
			} else {

				rinfo.type = "Only Header";
			}
			if (tmp.split(" ")[0].equals("/")) {
				tmp = tmp.substring(1).trim();
			} else {
				String slash = tmp.split(" ")[0];
				tmp = tmp.substring(slash.length()).trim();
			}
			if (tmp.contains("-v")) {
				rinfo.verbose = "YES";
				index = tmp.indexOf("-v");
				tmp = tmp.substring(0, index).trim() + tmp.substring(index + 2).trim();
			}
			if (tmp.contains("-h")) {
				rinfo.head = "YES";

				while (tmp.contains("-h")) {
					index = tmp.indexOf("-h");
					String rest = tmp.substring(index + 2).trim();
					String hpair = "";
					String[] p = rest.trim().split(" ");
					if (p.length > 0)
						hpair = p[0];
					int sindex = hpair.indexOf(":");
					if (sindex < hpair.length()) {

						rinfo.hpairs.put(hpair.substring(0, sindex), hpair.substring(sindex + 1));
						String left = tmp.substring(0, index).trim();
						String right = tmp.substring(index + 2 + hpair.length() + 1).trim();
						tmp = left + right;
					} else {
						rinfo.msg = "Invalid -h key:value format";
						return rinfo;
					}
				}
			}

			if (tmp.contains("-d") && tmp.contains("-f")) {
				rinfo.msg = "Can't have both -d and -f together! Request format is wrong!";
				return rinfo;
			}
			if (tmp.contains("-d")) {
				rinfo.data = "YES";
				index = tmp.indexOf("-d");
				String rest = tmp.substring(index + 3).trim();
				int bindex = rest.indexOf("'");
				int eindex = rest.substring(bindex + 1).indexOf("'");
				String data = rest.substring(bindex + 1, eindex + 1);
				if (data.length() > 0)
					rinfo.dlist.add(data);
				else {
					rinfo.msg = "Invalid -d 'inline-data' format";
					return rinfo;
				}
				tmp = tmp.substring(0, index).trim() + tmp.substring(index + 3 + data.length() + 3).trim();
			}
			
			if (tmp.contains("-f")) {
				rinfo.file = "YES";
				index = tmp.indexOf("-f");
				String rest = tmp.substring(index + 2).trim();
				String filename = rest.substring(0, rest.indexOf(" "));
				if (filename.length() > 0) {
					rinfo.filename = filename;
					BufferedReader br = new BufferedReader(new FileReader(filename));
					String ch = "";
					StringBuilder contents = new StringBuilder();
					while ((ch = br.readLine()) != null) {
						contents.append(ch + " ");
					}
					br.close();
					rinfo.dlist.add(contents.toString().trim());
				} else {
					rinfo.msg = "Invalid -f filename format";
					return rinfo;
				}
				tmp = tmp.substring(0, index).trim() + tmp.substring(index + 2 + filename.length() + 1).trim();
			}
			
			if (tmp.length() > 0) {
				String[] remaining = tmp.split(" ");
				if (remaining[0].charAt(0) == '\'')
					rinfo.url = remaining[0].substring(1, remaining[0].length() - 1).trim();
				else
					rinfo.url = remaining[0].trim();
				if (remaining.length == 3) {
					if (remaining[1].contains("-Content-Disposition:attachment")) {
						rinfo.out = "YES";
					} else {
						rinfo.msg = "Invalid -Content-Dispositon:attachment format";
						return rinfo;
					}
					rinfo.outfilename = remaining[2].trim();
				}
				rinfo.status = "VALID";
			} else {
				rinfo.msg = "missing URL";
			}
		} catch (Exception ex) {
			System.out.println(ex.toString());
			rinfo.msg = "Exception";
			return rinfo;
		}
		return rinfo;
	}

}