import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Scanner;

public class MultiThreading {
	public static void main(String[] args) {
		Scanner sc = null;
		try {
			sc = new Scanner(new File("PORT.txt"));
		} catch (FileNotFoundException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		int port = sc.nextInt();
		Runnable task1 = () -> {
			HTTPC client1 = new HTTPC();
			String request1 = "httpc get /HelloWorld.txt http://G:\\workspace\\CNProgramming2";
			// String request2 = "httpc get /HelloWorld.txt
			// http://G:\\workspace\\CNProgramming2";
			RequestInformation reqinfo1 = client1.verifyRequest(request1);
	        try {
				client1.get(reqinfo1, request1);
			} catch (UnknownHostException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		};
		Runnable task2 = () -> {
			HTTPC client2 = new HTTPC();
			String request2 = "httpc post /HelloWorld.txt -d '{\"Assignment\": 1}' http://G:\\workspace\\CNProgramming2";
			// String request2 = "httpc get /HelloWorld.txt
			// http://G:\\workspace\\CNProgramming2";
			RequestInformation reqinfo2 = client2.verifyRequest(request2);
	        try {
				client2.post(reqinfo2, request2);
			} catch (UnknownHostException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		};
		for(int i=0;i<20;i++)
		{ 
		   task2.run();
		   task1.run();
		}
	}
}
