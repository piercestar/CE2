import java.io.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Scanner;

public class TextBuddy {
	
	// User commands
	private static final String COMMAND_EXIT = "exit";
	private static final String COMMAND_DELETE = "delete";
	private static final String COMMAND_CLEAR = "clear";
	private static final String COMMAND_DISPLAY = "display";
	private static final String COMMAND_ADD = "add";
	private static final String COMMAND_SEARCH = "search";
	private static final String COMMAND_SORT = "sort";
	
	// Successful User Feedback messages
	private static final String MESSAGE_ADD_SUCCESSFUL = "added to %1$s: \"%2$s\"";
	private static final String MESSAGE_DELETE_SUCCESSFUL = "deleted from %1$s: \"%2$s\"";
	private static final String MESSAGE_CLEARED = "all content deleted from %1$s";
	private static final String MESSAGE_NOTHING_TO_SORT = "Nothing to sort";
	
	private static final String WELCOME_MESSAGE = "Welcome to TextBuddy.%1$s is ready for use";
	
	// Error Messages
	private static final String MESSAGE_FAILED_TO_SEARCH = "Error: Failed to search";
	private static final String MESSAGE_FAILED_TO_ADD = "Error: Failed to add";
	private static final String MESSAGE_FAILED_TO_DISPLAY = "Error: Failed to display";
	private static final String MESSAGE_FAILED_TO_CLEAR = "Error: Failed to clear";
	private static final String MESSAGE_FAILED_TO_DELETE = "Error: Failed to delete";
	private static final String MESSAGE_INVALID_FORMAT = "invalid command format :%1$s";
	
	private static Scanner scanner = new Scanner(System.in);

	private static final boolean BOOLEAN_APPEND = true;
	
	private static String fileName;

	enum COMMAND_TYPE {
		ADD, DISPLAY, DELETE, CLEAR, EXIT, INVALID, SORT, SEARCH
	}

	public static void main(String[] args){
		createFile(args);
		showToUser(String.format(WELCOME_MESSAGE,fileName));
		while (true) {
			System.out.print("Enter command:");
			String command = scanner.nextLine();
			String userCommand = command;
			String feedback = executeCommand(userCommand);
			showToUser(feedback);
		}
	}
	
	/*
	 * Initializes the file and Creates one if an existing one is not found. 
	 */
	public static void createFile(String[] args) {
		fileName = args[0];
		File file = new File(fileName);
		try {
			fileChecker(file);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Executes the corresponding user requested command.
	 * @param userCommand
	 * @return 		Message Feedback of the function indicating whether it was successful or not.
	 */
	public static String executeCommand(String userCommand) {
		if(checkIfEmptyString(userCommand))
			return String.format(MESSAGE_INVALID_FORMAT,userCommand);

		COMMAND_TYPE commandType = getCommandType(userCommand);
		switch (commandType) {
		case ADD:
			return add(removeFirstWord(userCommand));
		case DISPLAY:
			return display();
		case CLEAR:
			return clear();
		case DELETE:
			return delete(removeFirstWord(userCommand));
		case SORT:
			return sort();
		case SEARCH:
			return search(removeFirstWord(userCommand));
		case INVALID:
			return String.format(MESSAGE_INVALID_FORMAT, userCommand);
		case EXIT:
			System.exit(0);
		default:
			//throw an error if the command is not recognized
			throw new Error("Unrecognized command type");
		}
	}

	/**
	 * Associates the user input with it's corresponding Command Type.
	 * @param commandTypeString		The raw string as input by the user.
	 * @return						Returns the corresponding Command Type. 
	 */
	private static COMMAND_TYPE determineCommandType(String commandTypeString) {
		if (commandTypeString == null) {
			throw new Error("command type string cannot be null!");
		}
		if (commandTypeString.equalsIgnoreCase(COMMAND_ADD)) {
			return COMMAND_TYPE.ADD;
		} else if (commandTypeString.equalsIgnoreCase(COMMAND_DISPLAY)) {
			return COMMAND_TYPE.DISPLAY;
		} else if(commandTypeString.equalsIgnoreCase(COMMAND_CLEAR)) {
			return COMMAND_TYPE.CLEAR;
		} else if(commandTypeString.equalsIgnoreCase(COMMAND_DELETE)) {
			return COMMAND_TYPE.DELETE;
		} else if (commandTypeString.equalsIgnoreCase(COMMAND_SORT)) {
			return COMMAND_TYPE.SORT;
		} else if (commandTypeString.equalsIgnoreCase(COMMAND_SEARCH)) {
			return COMMAND_TYPE.SEARCH;
		} else if (commandTypeString.equalsIgnoreCase(COMMAND_EXIT)) {
			return COMMAND_TYPE.EXIT;
		} else {
			return COMMAND_TYPE.INVALID;
		}
	}
	
	/**
	 * Adds the user String into the file.
	 * @param userCommand	String to be added to the file as required by the user.
	 * @return 				Feedback Message based on whether the function was successful or not.
	 */
	private static String add(String userCommand) {
		try {
			File file = new File(fileName);
			fileChecker(file);
			FileWriter fileWriter = new FileWriter(file.getAbsoluteFile(),BOOLEAN_APPEND);
			BufferedWriter bufferedWriter = new BufferedWriter(fileWriter);
			bufferedWriter.write(userCommand);
			// newLine character as delimiter.
			bufferedWriter.write('\n'); 
			bufferedWriter.close();
			return String.format(MESSAGE_ADD_SUCCESSFUL,fileName,userCommand);
		} catch (IOException e) {
			e.printStackTrace();
		}
		return MESSAGE_FAILED_TO_ADD;
	}

	private static void fileChecker(File file) throws IOException {
		if(!file.exists()){
			file.createNewFile();
		}
	}

	/**
	 * Displays all lines currently in the file.
	 * An Index is attached to the beginning of each line.
	 * @return 		Returns a feedback message based on whether the functions was successful or not. 
	 * 				Returns an empty string if successful.
	 */
	private static String display() {
		BufferedReader bufferedWriter = null;
		try {
			String currentLine;
			bufferedWriter = new BufferedReader(new FileReader(fileName));
			int index = 1;
			while ((currentLine = bufferedWriter.readLine()) != null) {
				System.out.println(index + ". " + currentLine);
				index++;
			}
			return "";
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				if (bufferedWriter != null) bufferedWriter.close();
			} catch (IOException ex) {
				ex.printStackTrace();
			}
		}
		return MESSAGE_FAILED_TO_DISPLAY;
	}
	
	/**
	 * Clears the file of all entries.
	 * @return 		Feedback message showing if whether the clear function was successful or not.
	 */
	private static String clear() {
		try {
			File file = new File(fileName);
			file.delete();
			fileChecker(file);

			return String.format(MESSAGE_CLEARED, fileName);
		} catch (IOException e) {
			e.printStackTrace();
		}
		return MESSAGE_FAILED_TO_CLEAR;
	}
	
	/**
	 * Deletes the user specified line and pushes up the subsequent lines to take up the deleted line's spot.
	 * if the userCommand is invalid, an error message will be returned and no changes will take place.
	 * @param userCommand	The delete index specified by the user.
	 * @return returns 		Feedback Message based on whether the function was successful or not.
	 */
	private static String delete(String userCommand) {
		try{
			if(!isInteger(userCommand)) {
				return MESSAGE_FAILED_TO_DELETE;
			}
			
			// Open file and create a new temp file for simultaneous reading and writing. 
			File readerFile = new File(fileName);
			File writerFile = new File("temp" + fileName);

			BufferedReader reader = new BufferedReader(new FileReader(readerFile));
			BufferedWriter writer = new BufferedWriter(new FileWriter(writerFile));

			String currentLine;
			String deletedLine = "";
			boolean isDeleted = false;
			int index = 1;

			// Read each line until the end of file is reached.
			while ((currentLine = reader.readLine()) != null) {
				// If Index to be deleted is found, Mark as deleted, skip line and continue.
				if(index == Integer.parseInt(userCommand)){ 
					isDeleted = true;
					deletedLine = currentLine;
				} else {
					writer.write(currentLine);
					writer.write('\n');
				}
				index++;
			}
			writer.close(); 
			reader.close(); 
			// Delete old file and "OverWrite" with new updated file.
			readerFile.delete();
			writerFile.renameTo(readerFile);
			
			// Return feedback output based on whether deletion was successful.
			if(isDeleted) {
				return String.format(MESSAGE_DELETE_SUCCESSFUL, fileName, deletedLine);
			} else {
				return MESSAGE_FAILED_TO_DELETE;
				}
		} catch(IOException e) {
			e.printStackTrace();
		}
		return MESSAGE_FAILED_TO_DELETE;
	}
	
	/*
	 * Sorts the file lexicographically
	 * @return		Feedback message based on whether the sort was successful or not.  
	 */
	private static String sort() {
		
		try {
		File readerFile = new File(fileName);
		File writerFile = new File("temp" + fileName);
		
		BufferedReader reader = new BufferedReader(new FileReader(readerFile));
		BufferedWriter writer = new BufferedWriter(new FileWriter(writerFile));
		
		ArrayList<String> lineStore = new ArrayList<String>();
		int index = 0;
		
		index = readAndStoreEntireFile(reader, lineStore, index);
		
		// Check if file is empty
		if (index == 0) {
			writer.close();
			writerFile.delete();
			reader.close();
			return MESSAGE_NOTHING_TO_SORT;
		}
		
		rewriteSortedFile(writer, lineStore, index);
		
		// Delete old file and "OverWrite" with new updated file.
		readerFile.delete();
		writerFile.renameTo(readerFile);
		
		writer.close(); 
		reader.close(); 
				
		return	String.format("%1$s sorted", fileName);
		
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return	String.format("%1$s sorted", fileName);
	}
	
	/*
	 * Searches the entire file and outputs all lines containing the keyword.
	 * @param keyword	The user input string to be used to searched in the file.
	 * @return 			Returns a not found output message when keyword can't be found in the file.
	 * 					Returns an empty string if the keyword was found in a line.
	 */
	public static String search(String keyword) {
		
			try {
				File reader = new File(fileName);
				BufferedReader br = new BufferedReader(new FileReader(reader));				
				ArrayList<String> lineStore = new ArrayList<String>();
				int index = 0;
				boolean isFound = false;
				
				index  = readAndStoreEntireFile(br, lineStore, index);
				
				// Print out all lines with the keywords
				for (int i = 0; i < index; i++) {
					if (lineStore.get(i).contains(keyword)) {
						System.out.println(lineStore.get(i));
						isFound = true;
					}
				}
				
				if (isFound == true) {
					return "";
				} else {
					return String.format("%1$s not found",keyword);
				}
				
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
		
		return MESSAGE_FAILED_TO_SEARCH;
	}
	
	/*
	 *  Sorts a file and rewrites it in sorted order.
	 */
	private static void rewriteSortedFile(BufferedWriter writer,
			ArrayList<String> lineStore, int index) throws IOException {
		Collections.sort(lineStore, String.CASE_INSENSITIVE_ORDER);
		for (int i = 0; i < index; i++) {
			writer.write(lineStore.get(i));
			writer.write('\n');
		}
	}

	/*
	 * Reads a file and stores the lines in an Arraylist and returns the number of lines in the file.
	 */
	public static int readAndStoreEntireFile(BufferedReader reader,
			ArrayList<String> lineStore, int index) throws IOException {
		String currentLine;
		while ((currentLine = reader.readLine()) != null) {
			lineStore.add(currentLine);
			index++;
		}
		return index;
	}
	
	/**
	 * Used to determine if a string can be converted into an Integer.
	 * Returns true/false based on whether the string is an Integer.
	 * @param str		String to check.
	 * @return 			boolean value true/false based on whether the String can be converted into an Integer.
	 */
	private static boolean isInteger(String str)  {  
	  try {  
	    Integer.parseInt(str);  
	  } catch(NumberFormatException nfe) {  
	    return false;  
	  }
	  return true;  
	}
	
	/**
	 * Trims the user input String to obtain the Command Type required by the user.
	 * @param userCommand	The raw String as input by the user.
	 * @return				Returns the corresponding Command Type required by the user.
	 */
	private static COMMAND_TYPE getCommandType(String userCommand) {
		String commandTypeString = getFirstWord(userCommand);
		COMMAND_TYPE commandType = determineCommandType(commandTypeString);
		return commandType;
	}

	private static String getFirstWord(String userCommand) {
		String commandTypeString = userCommand.trim().split("\\s+")[0];
		return commandTypeString;
	}

	private static boolean checkIfEmptyString(String userCommand) {
		return userCommand.trim().equals("");
	}

	private static void showToUser(String message) {
		System.out.println(message);
	}
	
	private static String removeFirstWord(String userCommand) {
		return userCommand.replace(getFirstWord(userCommand), "").trim();
	}
}
