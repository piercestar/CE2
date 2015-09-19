import static org.junit.Assert.*;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintStream;
import java.util.ArrayList;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;


public class SortTest {

	@Test
	public void testAdd() {
		
		String[] args = new String[1];
		args[0] = "TextBuddys.txt";
		TextBuddy.createFile(args);
		
		TextBuddy.executeCommand("clear");
		TextBuddy.executeCommand("add I");
		TextBuddy.executeCommand("add am");
		TextBuddy.executeCommand("add the");
		TextBuddy.executeCommand("add bone");
		TextBuddy.executeCommand("add of");
		TextBuddy.executeCommand("add my");
		TextBuddy.executeCommand("add sword");

		ArrayList<String> expectedOutput = new ArrayList<String>();
		expectedOutput.clear();
		expectedOutput.add("I");
		expectedOutput.add("am");
		expectedOutput.add("the");
		expectedOutput.add("bone");
		expectedOutput.add("of");
		expectedOutput.add("my");
		expectedOutput.add("sword");
		
		fileCompare(args, expectedOutput);
		
	}

	@Test
	public void testSortAdd() {
		String[] args = new String[1];
		args[0] = "TextBuddys.txt";
		TextBuddy.createFile(args);
		testAdd();
		assertEquals(args[0] + " sorted",TextBuddy.executeCommand("sort"));
		
		ArrayList<String> expectedOutput = new ArrayList<String>();
		expectedOutput.clear();
		expectedOutput.add("am");
		expectedOutput.add("bone");
		expectedOutput.add("I");
		expectedOutput.add("my");
		expectedOutput.add("of");
		expectedOutput.add("sword");
		expectedOutput.add("the");
		
		fileCompare(args, expectedOutput);
	}
	
	
	@Test
	public void testAddStringsWithNumbers() {
		String[] args = new String[1];
		args[0] = "TextBuddys.txt";
		TextBuddy.createFile(args);
		
		TextBuddy.executeCommand("Clear");
		TextBuddy.executeCommand("add duck1");
		TextBuddy.executeCommand("add duck2");
		TextBuddy.executeCommand("add vegeduck3");
		TextBuddy.executeCommand("add gokuduck2");
		TextBuddy.executeCommand("add GOKUduck1");
		TextBuddy.executeCommand("add GOKUduck2");
		TextBuddy.executeCommand("add duck1");

		ArrayList<String> expectedOutput = new ArrayList<String>();
		expectedOutput.clear();
		expectedOutput.add("duck1");
		expectedOutput.add("duck2");
		expectedOutput.add("vegeduck3");
		expectedOutput.add("gokuduck2");
		expectedOutput.add("GOKUduck1");
		expectedOutput.add("GOKUduck2");
		expectedOutput.add("duck1");
		
		fileCompare(args, expectedOutput);
	}

	@Test
	public void testSortAddStringsWithNumbers() {
		String[] args = new String[1];
		args[0] = "TextBuddys.txt";
		TextBuddy.createFile(args);
		testAddStringsWithNumbers();
		assertEquals(args[0] + " sorted",TextBuddy.executeCommand("sort"));
		
		ArrayList<String> expectedOutput = new ArrayList<String>();
		expectedOutput.clear();
		expectedOutput.add("duck1");
		expectedOutput.add("duck1");
		expectedOutput.add("duck2");
		expectedOutput.add("GOKUduck1");
		expectedOutput.add("gokuduck2");
		expectedOutput.add("GOKUduck2");
		expectedOutput.add("vegeduck3");
		
		fileCompare(args, expectedOutput);
	}
	
	
	@Test
	public void testSearch() {
		String[] args = new String[1];
		args[0] = "TextBuddys.txt";
		TextBuddy.createFile(args);
				
		assertEquals("",TextBuddy.executeCommand("search duck"));
	}
	
	@Test
	public void testSearchEmpty() {
		String[] args = new String[1];
		args[0] = "TextBuddys.txt";
		TextBuddy.createFile(args);
				
		assertEquals("Error: failed to search",TextBuddy.executeCommand("search"));
	}


	private void fileCompare(String[] args, ArrayList<String> expectedOutput) {
		BufferedReader br;
		try {
			br = new BufferedReader(new FileReader(args[0]));
			ArrayList<String> actualOutput = new ArrayList<String>(); 
			TextBuddy.readAndStoreEntireFile(br,actualOutput,0);
			assertEquals(expectedOutput,actualOutput);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
		
	
	public static void restoreFiles(BufferedReader br) {
		
	}

}
