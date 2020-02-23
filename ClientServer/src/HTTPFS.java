import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.*;

public class HTTPFS {

	public static void main(String[] args) {
		Scanner scan = new Scanner(System.in);
		System.out.println("httpfs is a simple file server.\n");
		System.out.println("usage: httpfs [-v] [-p PORT] [-d PATH-TO-DIR]\n");
		System.out.println("\t-v Prints debugging messages.\n");
		System.out.println("\t-p Specifies the port number that the server will listen and serve at.\n");
		System.out.println("\t   Default is 8080.\n");
		System.out.println("\t-d Specifies the directory that the server will use to read/write\n");
		System.out.println("\t   requested files. Default is the current directory when launching the\n");
		System.out.println("\t   application.\n");
		String input = scan.nextLine().trim();
//		String input = "httpfs";
		String[] arr = input.split(" ");
		String directory = "Current";
		String dmsgs = "NO";
		int i = 0;
		int port;
		try {
			if (arr[i].equalsIgnoreCase("httpfs")) {
				i++;
			} else {
				System.out.println("Invalid Command.Please try again!");
				System.exit(0);
			}
			if (arr[i].equalsIgnoreCase("-v")) {
				i++;
			}
			if (arr[i].equalsIgnoreCase("-p")) {
				i++;
				FileWriter fw = new FileWriter("PORT.txt", false);
				port = Integer.parseInt(arr[i]);
				fw.write(arr[i].trim());
				fw.close();
				i++;
			}
			if (arr[i].equalsIgnoreCase("-d")) {
				i++;
				directory = arr[i];
				if(!directory.equals(System.getProperty("user.dir")))
				{
					System.out.println("Can't Access the Directory! Default directory - "+System.getProperty("user.dir"));
					System.exit(0);
				}
			}
		} catch (Exception ex) {
			System.out.println(ex.getMessage());
		}
		Scanner sc = null;
		try {
			sc = new Scanner(new File("PORT.txt"));
		} catch (FileNotFoundException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		port = sc.nextInt();
		System.out.println("Server Running in Port : " + port);
		try (ServerSocket server = new ServerSocket(port)) {
			System.out.println("Server is started");
			System.out.println("Waiting for client");
			while (true) {
				new HTTPserver(server.accept()).start();
			}
		} catch (IOException e) {
			System.out.println(e.getMessage());
		}
	}
}
