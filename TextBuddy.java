import java.io.*;
import java.util.Scanner;

public class TextBuddy {
	
	// User commands
	private static final String COMMAND_EXIT = "exit";
	private static final String COMMAND_DELETE = "delete";
	private static final String COMMAND_CLEAR = "clear";
	private static final String COMMAND_DISPLAY = "display";
	private static final String COMMAND_ADD = "add";
	// Successful User Feedback messages
	private static final String MESSAGE_ADD_SUCCESSFUL = "added to %1$s: \"%2$s\"";
	private static final String MESSAGE_DELETE_SUCCESSFUL = "deleted from %1$s: \"%2$s\"";
	private static final String MESSAGE_CLEARED = "all content deleted from %1$s";
	
	private static final String WELCOME_MESSAGE = "Welcome to TextBuddy.%1$s is ready for use";
	
	// Error Messages
	private static final String MESSAGE_FAILED_TO_ADD = "Error: Failed to add";
	private static final String MESSAGE_FAILED_TO_DISPLAY = "Error: Failed to display";
	private static final String MESSAGE_FAILED_TO_CLEAR = "Error: Failed to clear";
	private static final String MESSAGE_FAILED_TO_DELETE = "Error: Failed to delete";
	private static final String MESSAGE_INVALID_FORMAT = "invalid command format :%1$s";
	
	private static Scanner scanner = new Scanner(System.in);

	private static final boolean BOOLEAN_OVERWRITE = false;
	private static final boolean BOOLEAN_APPEND = true;
	
	private static String fileName;

	enum COMMAND_TYPE {
		ADD, DISPLAY, DELETE, CLEAR, EXIT, INVALID;
	}

	public static void main(String[] args){
		fileName = args[0];
		showToUser(String.format(WELCOME_MESSAGE,fileName));
		while (true) {
			System.out.print("Enter command:");
			String command = scanner.nextLine();
			String userCommand = command;
			String feedback = executeCommand(userCommand);
			showToUser(feedback);
		}
	}

	/**
	 * Executes the corresponding user requested command.
	 * @param userCommand
	 * @return 		Message Feedback of the function indicating whether it was successful or not.
	 */
	private static String executeCommand(String userCommand) {
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
		if (commandTypeString == null)
			throw new Error("command type string cannot be null!");

		if (commandTypeString.equalsIgnoreCase(COMMAND_ADD)) {
			return COMMAND_TYPE.ADD;
		} else if (commandTypeString.equalsIgnoreCase(COMMAND_DISPLAY)) {
			return COMMAND_TYPE.DISPLAY;
		} else if(commandTypeString.equalsIgnoreCase(COMMAND_CLEAR)){
			return COMMAND_TYPE.CLEAR;
		} else if(commandTypeString.equalsIgnoreCase(COMMAND_DELETE)){
			return COMMAND_TYPE.DELETE;
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
			if(!file.exists()){
				file.createNewFile();
			}
			FileWriter fw = new FileWriter(file.getAbsoluteFile(),BOOLEAN_APPEND);
			BufferedWriter bw = new BufferedWriter(fw);
			bw.write(userCommand);
			// newLine character as delimiter.
			bw.write('\n'); 
			bw.close();
			return String.format(MESSAGE_ADD_SUCCESSFUL,fileName,userCommand);
		} catch (IOException e) {
			e.printStackTrace();
		}
		return MESSAGE_FAILED_TO_ADD;
	}

	/**
	 * Displays all lines currently in the file.
	 * An Index is attached to the beginning of each line.
	 * @return 		Returns a feedback message based on whether the functions was successful or not. 
	 * 				Returns an empty string if successful.
	 */
	private static String display() {
		BufferedReader br = null;
		try {
			String currentLine;
			br = new BufferedReader(new FileReader(fileName));
			int index = 1;
			while ((currentLine = br.readLine()) != null) {
				System.out.println(index + ". " + currentLine);
				index++;
			}
			return "";
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				if (br != null) br.close();
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
			if(!file.exists()){
				file.createNewFile();
			}
			FileWriter fw = new FileWriter(file.getAbsoluteFile(),BOOLEAN_OVERWRITE);
			BufferedWriter bw = new BufferedWriter(fw);
			bw.close();
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
			boolean isDeleted = false;
			int index = 1;

			// Read each line until the end of file is reached.
			while ((currentLine = reader.readLine()) != null) {
				// If Index to be deleted is found, Mark as deleted, skip line and continue.
				if(index == Integer.parseInt(userCommand)){ 
					isDeleted = true;
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
				return String.format(MESSAGE_DELETE_SUCCESSFUL, fileName, userCommand);
			} else {
				return MESSAGE_FAILED_TO_DELETE;
				}
		} catch(IOException e) {
			e.printStackTrace();
		}
		return MESSAGE_FAILED_TO_DELETE;
	}
	
	/**
	 * Used to determine if a string can be converted into an Integer.
	 * Returns true/false based on whether the string is an Integer.
	 * @param str		String to check.
	 * @return 			boolean value true/false based on whether the String can be converted into an Integer.
	 */
	private static boolean isInteger(String str)  {  
	  try {  
	    Integer digit = Integer.parseInt(str);  
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