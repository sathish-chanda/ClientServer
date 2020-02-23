import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class RequestInformation {
	String status;
	String type;
	String verbose;
	String head;
	Map<String, String> hpairs;
	String data;
	List<String> dlist;
	String file;
	String filename;
	String url;
	String out;
	String outfilename;
	String msg;

	public RequestInformation() {
		status = "INVALID";
		type = "";
		verbose = "NO";
		head = "NO";
		hpairs = new HashMap<String, String>();
		data = "NO";
		dlist = new ArrayList<String>();
		file = "NO";
		filename = "";
		url = "";
		out = "NO";
		outfilename = "";
		msg = "";
	}

	public String toString() {
		return "\nType:\t" + type + "\nVerbose:\t" + verbose + "\nHeader:\t" + head + "\nhpairs:\t" + hpairs.toString()
				+ "\ninline-data:\t" + data + "\nidata:\t" + dlist.toString() + "\nreadfile:\t" + file + "\nfilename:\t"
				+ filename + "\nURL:\t" + url + "\nOut2file:\t" + out + "\nOutfile:\t" + outfilename + "\nStatus:\t"
				+ status + "\nMessage:\t" + msg;
	}
}
