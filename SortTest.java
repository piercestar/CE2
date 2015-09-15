import static org.junit.Assert.*;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

import org.junit.Test;


public class SortTest {

	@Test
	public void test() {
		
		String[] args = new String[1];
		args[0] = "TextBuddy.txt";
		TextBuddy.createFile(args);
		
		assertEquals("all content deleted from " + args[0],TextBuddy.executeCommand("clear"));
		assertEquals("Nothing to sort",TextBuddy.executeCommand("sort"));
		
		TextBuddy.executeCommand("add I");
		TextBuddy.executeCommand("add am");
		TextBuddy.executeCommand("add the");
		TextBuddy.executeCommand("add bone");
		TextBuddy.executeCommand("add of");
		TextBuddy.executeCommand("add my");
		TextBuddy.executeCommand("add sword");
		assertEquals(args[0] + " sorted",TextBuddy.executeCommand("sort"));

		ArrayList<String> expectedOutput = new ArrayList<String>();
		expectedOutput.add("am");
		expectedOutput.add("bone");
		expectedOutput.add("i");
		expectedOutput.add("my");
		expectedOutput.add("of");
		expectedOutput.add("sword");
		expectedOutput.add("the");
		
		BufferedReader br;
		try {
			br = new BufferedReader(new FileReader("TextBuddy.txt"));
			ArrayList<String> actualOutput = new ArrayList<String>(); 
			TextBuddy.readAndStoreEntireFile(br,actualOutput,0);
			assertEquals(actualOutput,expectedOutput);
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
	}

}
