package finance;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintStream;

public class Logger {

	public static PrintStream out;
	PrintStream notUsedOut;
	
	public Logger() throws IOException {
		System.out.println("Current working directory: " + System.getProperty("user.dir"));
		out = notUsedOut = new PrintStream(new File("log.txt"));
//		new FileWriter("log.txt")
	}
	
	public void toggleStdout() {
		if (out==System.out)
			out = notUsedOut;
		else
			out = System.out;
	}
}
