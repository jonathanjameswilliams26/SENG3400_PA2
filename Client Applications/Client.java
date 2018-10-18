import java.io.Console;
import java.util.ArrayList;
import java.util.Arrays;
import javax.xml.rpc.ServiceException;
import java.rmi.RemoteException;
import javax.security.sasl.AuthenticationException;

public abstract class Client {
    
    protected boolean exit;                 //A flag value which indicates if the program should exit
    protected Console in;                   //The console capturing the users input
    protected String input;                 //The input captured from the user via the command line
    protected ArrayList<String> command;    //An array list containing the command and input parameters for the command
    protected String promptMessage;         //The main menu message prompted to the user.




    /**
     * Run the client application until requested to exit
     * @throws RemoteException if an exception occurs while attempting to use a web service
     * @throws AuthenticationException if attempting to use a web service which requries authentication but authentication fails
     */
    public void run() throws RemoteException, AuthenticationException {
        
        //Run the command prompt until the user decides to log out
        while(!exit)
        {
            //Prompt the user to enter a command
            prompt();

            //Execute the users command
            executeCommand();
        }
    }
    



    /**
     * Splits the user's input into tokens and adds them to the command array list.
     * Will split the users input on "spaces"
     */
    protected void tokenise() {
        command = new ArrayList<>();

        if(input == null)
            return;

        //If the input does not contain any spaces just use the input provided
        if(!input.contains(" "))
            command.add(input);
        
        //Otherwise, split the user input into each indivdual string
        else
        {
            String[] tokens = input.split(" ");
            command = new ArrayList<>(Arrays.asList(tokens));
        }
    }




    /**
     * Exit the application
     */
    protected void exit() {
        exit = true;
        System.out.println("Goodbye.");
    }




    /**
     * Display the main menu prompt to the user and capture the users input.
     */
    protected void prompt() {
        System.out.print(promptMessage);
        input = in.readLine();
        System.out.println("");
    }





    /**
     * Print the string array to the console.
     * @param arrayToPrint - The string array to print
     * @param title - The title of the printed array. Will display above the contents
     * @param emptyMSG - The error message to display if the array is null or empty
     */
    protected void printList(String[] arrayToPrint, String title, String emptyMSG) {
        
        System.out.println("--------------------------------------");
        System.out.println(title);
        System.out.println("--------------------------------------");

        //if the arrayToPrint is null or empty print the empty error message
        if(arrayToPrint == null)
            System.out.println(emptyMSG);
        else if (arrayToPrint.length == 0)
            System.out.println(emptyMSG);

        //Otherwise, print the list
        else
        {
            for (String item : arrayToPrint) {
                System.out.println(item);
            }
        }
        System.out.println("--------------------------------------");
    }





    /**
     * Checks if the command input by the user is a valid input and contains the required number of characters.
     * @param requiredSize - The number of parameters the command requires
     * @param errorMSG - The error message to display if the command is null or does not contain the required number of parameters
     * @return - TRUE if the command is valid, FALSE otherwise
     */
    protected boolean isCommandValid(int requiredSize, String errorMSG) {

        //If the command does not contain the required amount of items
        //print the error message and return false
        if(command == null)
        {
            System.out.println("--------------------------------------");
            System.out.println(errorMSG);
            System.out.println("--------------------------------------");
            return false;
        }
        else if(command.size() != requiredSize)
        {
            System.out.println("--------------------------------------");
            System.out.println(errorMSG);
            System.out.println("--------------------------------------");
            return false;
        }
        else
            return true;
    }




    /**
     * Prints the result of a TRUE or FALSE response
     * @param successful - A flag which outlines if the result was successful or not
     * @param successMSG - The message to display if the result was successful
     * @param errorMSG -  The message to display if the result was an error.
     */
    protected void printResult(boolean successful, String successMSG, String errorMSG) {
        
        System.out.println("--------------------------------------");

        //If the result is successful print the success message
        if(successful)
            System.out.println("SUCCESS: " + successMSG);

        //otherwise, print the error message
        else
            System.out.println("FAILED: " + errorMSG);
        
        System.out.println("--------------------------------------");
    }




    
    //ABSTRACT METHODS
    /**
     * Executes a specified command input by the user.
     * @throws RemoteException if an exception occurs while attempting to use a web service
     * @throws AuthenticationException if attempting to use a web service which requries authentication but authentication fails
     */
    protected abstract void executeCommand() throws RemoteException, AuthenticationException;
}