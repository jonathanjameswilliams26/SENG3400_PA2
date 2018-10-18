import java.rmi.RemoteException;
import java.util.NoSuchElementException;
import javax.management.ServiceNotFoundException;
import javax.xml.rpc.ServiceException;
import localhost.currency.Conversion_jws.*;

public class CurrencyClient extends Client {

    private ConversionService service;      //The conversion web service       
    private Conversion serviceInterface;    //The service interface to invoke web service methods

    /**
     * Main Method
     */
    public static void main(String[] args) {
        //Run the application
        try
        {
            Client client = new CurrencyClient();
            client.run();
        }
        //Catch any exceptions thrown throughout the application and return the error message
        catch(Exception e)
        {
            System.out.println(e.getMessage());
        }
        //Exit the application
        finally
        {
            System.out.println("Exiting application.");
            System.exit(0);
        }
    }





    /**
     * Default Constructor
     * @throws NoSuchElementException if a Console used to accept user input is unavailable
     * @throws ServiceNotFoundException if and error occurs initalising the ConversionService
     */
    public CurrencyClient() throws NoSuchElementException, ServiceNotFoundException {
        in = System.console();
        input = null; 
        command = null;
        exit = false;

        //If cannot obtain a console to receive the user input exit the application
        if(in == null)
            throw new NoSuchElementException("ERROR: The Console does not exist, the console is required to take user input.");

        //Initalise the conversion service
        try
        {
            service = new ConversionServiceLocator();
            serviceInterface = service.getConversion();
        }
        catch (ServiceException e)
        {
            throw new ServiceNotFoundException("ERROR: An error occurred trying to initalise the Conversion Service. The service was not found.");
        }

        //Initialise the message which will be displayed to the user
        promptMessage = "\nPlease type in one of the following options:\n"
                        + "1. convert <fromCurrency> <toCurrency> <amount>\n"
                        + "2. rateOf <fromCurrency> <toCurrency>\n"
                        + "3. listRates\n"
                        + "4. exit\n"
                        + "Enter Input: ";
    }




    /**
     * Implementing the base class abstract method.
     * Executes a command specified by the client from the main menu input.
     */
    protected void executeCommand() throws RemoteException {
        
        //Tokenise the users input, split the input using the spaces
        tokenise();

        //if the command list is empty print an error and leave the method because there is no command to execute
        if(command.isEmpty())
        {
            System.out.println("ERROR: The command is empty. please try again.");
            return;
        }

        //Execute the command
        String methodToExecute = command.get(0);
        switch (methodToExecute) 
        {
            case "convert":
                convert();
                break;

            case "rateOf":
                rateOf();
                break;

            case "listRates":
                listRates();
                break;
            
            case "exit":
                exit();
                break;

            default:
                System.out.println("ERROR: Invalid command, please try again.");
                break;
        }
    }





    /**
     * Execute the convert command.
     * Converts an amount from one currency to another using the conversion web service.
     * @throws RemoteException if an error occurred while trying to execute the web service method.
     */
    private void convert() throws RemoteException {
        String networkError = "ERROR: A network error occurred while trying to execute convert(). Please confirm the service is available.";

        //Confirm there is 4 items in the command list, 1 = command, 2 = fromCurrency, 3 = toCurrency, 4 = amount
        //If the command does not contain 4 items print an error message and leave the method because it was executed incorrectly
        String commandErrorMSG = "ERROR: Cannot execute addCurrency command. You did not supply a <fromCurrency>, <toCurrency>, <amount>.";
        if(!isCommandValid(4, commandErrorMSG))
            return;

        //Confirm the amount to convert is actually a double
        double actualAmount = 0;
        try 
        {
            actualAmount = Double.parseDouble(command.get(3));
        } 
        catch (Exception e) 
        {
            System.out.println("ERROR: Cannot execute convert command. <amount> is invalid, you must enter a number.");
            return;
        }

        //Call the service to convert the amount and print the result
        try
        {
            double convertedAmount = serviceInterface.convert(command.get(1), command.get(2), actualAmount);
            boolean successful = convertedAmount != -1.0;
            String successMSG = "$" + String.format("%.2f", convertedAmount) + " (including 1% fee)";
            String errorMSG = "Could not convert from " + command.get(1).toUpperCase() + " to " + command.get(2).toUpperCase() + " because a conversion rate between the currencies does not exist, or an error occurred on the server";
            printResult(successful, successMSG, errorMSG);
        }

        //Exception thrown if the web service fails to execute
        catch (RemoteException e)
        {
            throw new RemoteException(networkError);
        }
    }





    /**
     * Execute the rateOf command.
     * Gets the conversion rate from one currency to another using the conversion web service.
     * @throws RemoteException if an error occurred while trying to execute the web service method.
     */
    private void rateOf() throws RemoteException {
        String networkError = "ERROR: A network error occurred while trying to execute rateOf(). Please confirm the service is available.";

        //Confirm there is 4 items in the command list, 1 = command, 2 = fromCurrency, 3 = toCurrency
        //If the command does not contain 3 items print an error message and leave the method because it was executed incorrectly
        String commandErrorMSG = "ERROR: Cannot execute addCurrency command. You did not supply a <fromCurrency> or <toCurrency>.";
        if(!isCommandValid(3, commandErrorMSG))
            return;

        //Call the service to get the rate of and print the result
        try
        {
            double rate = serviceInterface.rateOf(command.get(1), command.get(2));
            boolean successful = rate != -1.0;
            String successMSG = "Rate of " + command.get(1).toUpperCase() + "-" + command.get(2).toUpperCase() + " is: " + String.format("%.4f", rate);
            String errorMSG = "The rate of " + command.get(1).toUpperCase() + "-" + command.get(2).toUpperCase() + " does not exist or an error occurred on the server.";
            printResult(successful, successMSG, errorMSG);
        }
        catch (RemoteException e)
        {
            throw new RemoteException(networkError);
        }
    }





    /**
     * Lists the rates of all conversion rates the service offers.
     * @throws RemoteException if an error occurred while trying to execute the web service method.
     */
    private void listRates() throws RemoteException {
        String networkError = "ERROR: A network error occurred while trying to execute removeCurrency(). Please confirm the service is available.";
        String title = "List of All Conversion Rates";
        String emptyMSG = "No conversion rates found. The database contains no conversion rates or an error occurred on the service.";
        
        //Call the service to list all the rates
        try
        {
            printList(serviceInterface.listRates(), title, emptyMSG);
        }
        catch (RemoteException e)
        {
            throw new RemoteException(networkError);
        }
    }
}