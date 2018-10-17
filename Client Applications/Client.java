import java.io.Console;
import java.util.ArrayList;
import java.util.Arrays;
import javax.xml.rpc.ServiceException;
import java.rmi.RemoteException;
import javax.security.sasl.AuthenticationException;

public abstract class Client {
    protected boolean exit;
    protected Console in;
    protected String input;
    protected ArrayList<String> command;
    protected String promptMessage;

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

    protected void exit() {
        exit = true;
        System.out.println("Goodbye.");
    }

    protected void prompt() {
        System.out.print(promptMessage);
        input = in.readLine();
        System.out.println("");
    }


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
    protected abstract void executeCommand() throws RemoteException, AuthenticationException;
}