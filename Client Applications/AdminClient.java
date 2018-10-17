import localhost.identity.Login_jws.*;
import localhost.currency.Admin_jws.*;
import java.io.Console;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.NoSuchElementException;
import javax.management.ServiceNotFoundException;
import javax.security.sasl.AuthenticationException;
import javax.xml.rpc.ServiceException;
import org.apache.axis.AxisFault;

public class AdminClient extends Client
{
    private String username;
    private String sessionKey;
    private LoginService loginService;
    private Login loginInterface;
    private AdminService adminService;
    private Admin adminInterface;

    public static void main(String[] args) {
        //If the user did not provide a command line argument exit the application
        if(args.length != 1)
        {
            System.out.println("ERROR: You did not run the application correctly. Please run the application by specifying a username as a command line argument.");
            System.exit(0);
        }

        //Run the application
        try
        {
            Client client = new AdminClient(args[0]);
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


    public AdminClient(String username) throws IllegalArgumentException, NoSuchElementException, ServiceNotFoundException {
        
        //Confirm the username is not just whitespace
        if(username.trim().length() == 0)
            throw new IllegalArgumentException("ERROR: The username is empty. You must provide a username");
        
        this.username = username;
        sessionKey = null;
        in = System.console();
        input = null; 
        command = null;
        exit = false;

        //If cannot obtain a console to receive the user input exit the application
        if(in == null)
            throw new NoSuchElementException("ERROR: The Console does not exist, the console is required to take user input.");

        //Initalise the services
        try
        {
            loginService = new LoginServiceLocator();
            loginInterface = loginService.getLogin();
        }
        catch (ServiceException e)
        {
            throw new ServiceNotFoundException("ERROR: An error occurred trying to initalise the Login Service. The service was not found.");
        }
        try
        {
            adminService = new AdminServiceLocator();
            adminInterface = adminService.getAdmin();
        }
        catch (ServiceException e)
        {
            throw new ServiceNotFoundException("ERROR: An error occurred trying to initalise the Admin Service. The service was not found.");
        }

        promptMessage = "\nPlease type in one of the following options:\n"
                        + "1. addCurrency <currencyCode>\n"
                        + "2. removeCurrency <currencyCode>\n"
                        + "3. listCurrencies\n"
                        + "4. conversionsFor <currencyCode>\n"
                        + "5. addRate <fromCurrency> <toCurrency> <rate>\n"
                        + "6. updateRate <fromCurrency> <toCurrency> <rate>\n"
                        + "7. removeRate <fromCurrency> <toCurrency>\n"
                        + "8. listRates\n"
                        + "9. logout\n"
                        + "Enter Input: ";
    }



    @Override
    public void run() throws RemoteException, AuthenticationException {

        //Login the user
        login();

        //Run the command prompt until the user decides to log out
        while(!exit)
        {
            //Prompt the user to enter a command
            prompt();

            //Execute the users command
            executeCommand(); 
        }
    }


    private void login() throws RemoteException, AuthenticationException {

        int attempts = 0;
        boolean isLoggedIn = false;
        while(!isLoggedIn && attempts < 3)
        {
            //If this is not the first attempt at logging in request the username again to confirm
            if(attempts != 0)
            {
                System.out.print("Please enter your username again: ");
                username = in.readLine();
            }

            //Capture the users password securely
            System.out.print("Please enter your password (Hidden Input): ");
            char[] passwordArray = in.readPassword();
            String password = new String(passwordArray);

            //Attempt to log the user in by calling the service
            try 
            {
                sessionKey = loginInterface.login(username, password);
            }
            //Exception thrown when calling login() 
            catch (RemoteException e) 
            {
                throw new RemoteException("ERROR: A network error occurred while trying to execute login(). Please confirm the service is available.");
            }

            attempts++;

            //If the login attempt was unsuccessful the service will return invalid, print an error message to the user.
            if(sessionKey.equals("INVALID"))
            {
                sessionKey = null;
                if(attempts < 3)
                    System.out.println("ERROR: Login attempt failed. " + (3 - attempts) + " attempts remaining.");
            }
            
            //Otherwise, the user successfully logged into the application
            else
                isLoggedIn = true;  
        }

        //If the user is not logged in after the number of attempts throw an exception
        if(!isLoggedIn)
            throw new AuthenticationException("ERROR: You failed to log in too many times.");
        else
            System.out.println("Welcome, " + username);
    }


    
    protected void executeCommand() throws RemoteException, AuthenticationException {
        
        //Tokenise the users input, split the input using the spaces
        tokenise();

        //if the command list is empty print an error and leave the method because there is no command to execute
        if(command.isEmpty())
        {
            System.out.println("ERROR: The command is empty. please try again.");
            return;
        }

        String methodToExecute = command.get(0);

        //Execute the command
        switch (methodToExecute) 
        {
            case "addCurrency":
                addCurrency();
                break;

            case "removeCurrency":
                removeCurrency();
                break;

            case "listCurrencies":
                listCurrencies();
                break;
            
            case "conversionsFor":
                conversionsFor();
                break;

            case "addRate":
                addRate();
                break;

            case "updateRate":
                updateRate();
                break;
            
            case "removeRate":
                removeRate();
                break;

            case "listRates":
                listRates();
                break;

            case "logout":
                exit();
                break;

            default:
                System.out.println("ERROR: Invalid command, please try again.");
                break;
        }
    }



    private void addCurrency() throws AuthenticationException, RemoteException {
        String networkError = "ERROR: A network error occurred while trying to execute addCurrency(). Please confirm the service is available.";

        //Confirm there is 2 items in the command list, one being the command and the other being the currency code to add
        //If the command does not contain print an error message and leave the method because it was executed incorrectly
        String commandErrorMSG = "ERROR: Cannot execute addCurrency command. You did not supply a <currencyCode>.";
        if(!isCommandValid(2, commandErrorMSG))
            return;

        //Call the service to add the currency
        try
        {
            String successMSG = command.get(1).toUpperCase() + " successfully added.";
            String errorMSG = command.get(1).toUpperCase() + " was not added. The currency already exists or an error occurred on the server.";
            printResult(adminInterface.addCurrency(sessionKey, command.get(1)), successMSG, errorMSG);
        }
        catch (RemoteException e)
        {
            throwAuthExeception(e, networkError);
        }
    }

    private void removeCurrency() throws AuthenticationException, RemoteException {
        String networkError = "ERROR: A network error occurred while trying to execute removeCurrency(). Please confirm the service is available.";
        
        //Confirm there is 2 items in the command list, one being the command and the other being the currency code to remove
        //If the command does not contain 2 items print an error message and leave the method because it was executed incorrectly
        String commandErrorMSG = "ERROR: Cannot execute removeCurrency command. You did not supply a <currencyCode>";
        if(!isCommandValid(2, commandErrorMSG))
            return;
        

        //Call the service to remove the currency
        try
        {
            String successMSG = command.get(1).toUpperCase() + " successfully removed.";
            String errorMSG = command.get(1).toUpperCase() + " was not removed. The currency does not exists or an error occurred on the server.";
            printResult(adminInterface.removeCurrency(sessionKey, command.get(1)), successMSG, errorMSG);
        }
        catch (RemoteException e)
        {
            throwAuthExeception(e, networkError);
        }
    }

    private void listCurrencies() throws AuthenticationException, RemoteException {
        String networkError = "ERROR: A network error occurred while trying to execute listCurrencies(). Please confirm the service is available.";        
        String title = "List of All Currencies:";
        String emptyMSG = "There is no currencies in the database or an error occurred on the server.";

        //Call the service to print the list of currencies
        try
        {
            printList(adminInterface.listCurrencies(sessionKey), title, emptyMSG);
        }
        catch (RemoteException e)
        {
            throwAuthExeception(e, networkError);
        }
    }

    private void conversionsFor() throws AuthenticationException, RemoteException {
        String networkError = "ERROR: A network error occurred while trying to execute conversionsFor(). Please confirm the service is available.";        

        //Confirm there is 2 items in the command list, one being the command and the other being the currency code to list
        //If the command does not contain 2 items print an error message and leave the method because it was executed incorrectly
        String commandErrorMSG = "ERROR: Cannot execute conversionsFor command. You did not supply a <currencyCode>";
        if(!isCommandValid(2, commandErrorMSG))
            return;

        //Call the service to list the conversions for
        try
        {
            String title = "Conversion Rates For " + command.get(1).toUpperCase();
            String emptyMSG = "The currency code does not exist or the currency has no conversion rates.";
            printList(adminInterface.conversionsFor(sessionKey, command.get(1)), title, emptyMSG);
        }
        catch (RemoteException e)
        {
            throwAuthExeception(e, networkError);
        }
    }





    private void addRate() throws AuthenticationException, RemoteException {
        String networkError = "ERROR: A network error occurred while trying to execute addRate(). Please confirm the service is available.";        
        String commandErrorMSG = "ERROR: Cannot execute addRate command. You did not supply the correct parameters such as <fromCurrencyCode>, <toCurrency> and <rate>.";
        
        //Confirm there is 4 items in the command list, 1 = command, 2 = fromCurrencyCode, 3 = toCurrencyCode, 4 = conversionRate
        //If the command does not contain 4 items print an error message and leave the method because it was executed incorrectly
        if(!isCommandValid(4, commandErrorMSG))
            return;

        //Confirm the conversion rate is actually a double
        double actualRate = 0;
        try 
        {
            actualRate = Double.parseDouble(command.get(3));
        } 
        catch (Exception e) 
        {
            System.out.println("ERROR: Cannot execute addRate command. <rate> is invalid, you must enter a number.");
            return;
        }

        //Call the service to add the rate
        try
        {
            String successMSG = command.get(1).toUpperCase() + "-" + command.get(2).toUpperCase() + ":" + actualRate + " was successfully added.";
            String errorMSG = command.get(1).toUpperCase() + "-" + command.get(2).toUpperCase() + ":" + actualRate + " was not added. The currencies provided do not exist, the rate is invalid or an error occurred on the server.";
            printResult(adminInterface.addRate(sessionKey, command.get(1), command.get(2), actualRate), successMSG, errorMSG);
        }
        catch (RemoteException e)
        {
            throwAuthExeception(e, networkError);
        }
    }


    private void updateRate() throws AuthenticationException, RemoteException {
        String networkError = "ERROR: A network error occurred while trying to execute updateRate(). Please confirm the service is available.";        
        
        //Confirm there is 4 items in the command list, 1 = command, 2 = fromCurrencyCode, 3 = toCurrencyCode, 4 = rate
        //If the command does not contain 4 items print an error message and leave the method because it was executed incorrectly
        String commandErrorMSG = "ERROR: Cannot execute updateRate command. You did not supply the correct parameters such as <fromCurrencyCode>, <toCurrency> and <rate>.";
        if(!isCommandValid(4, commandErrorMSG))
            return;

        //Confirm the conversion rate is actually a double
        double actualRate = 0;
        try 
        {
            actualRate = Double.parseDouble(command.get(3));
        } 
        catch (Exception e) 
        {
            System.out.println("ERROR: Cannot execute addRate command. <rate> is invalid, you must enter a number.");
            return;
        }

        //Call the service to update the rate
        try
        {
            String successMSG = "The rate between " + command.get(1).toUpperCase() + "-" + command.get(2).toUpperCase() + " was successfully updated to " + actualRate;
            String errorMSG = "The rate between " + command.get(1).toUpperCase() + "-" + command.get(2).toUpperCase() + " was not updated, the currencies do not exist, a conversion rate between the currencies does not exist, the new rate is invalid or an error occurred on the server.";
            printResult(adminInterface.updateRate(sessionKey, command.get(1), command.get(2), actualRate), successMSG, errorMSG);
        }
        catch (RemoteException e)
        {
            throwAuthExeception(e, networkError);
        }
    }



    private void removeRate() throws AuthenticationException, RemoteException {
        String networkError = "ERROR: A network error occurred while trying to execute removeRate(). Please confirm the service is available.";
        
        //Confirm there is 3 items in the command list, 1 = command, 2 = fromCurrencyCode, 3 = toCurrencyCode
        //If the command does not contain 3 items print an error message and leave the method because it was executed incorrectly
        String commandErrorMSG = "ERROR: Cannot execute removeRate command. You did not supply the correct parameters such as <fromCurrencyCode>, <toCurrency>.";
        if(!isCommandValid(3, commandErrorMSG))
            return;

        //Call the service to remove the rate
        try
        {
            String successMSG = command.get(1).toUpperCase() + "-" + command.get(2).toUpperCase() + " was successfully removed.";
            String errorMSG = command.get(1).toUpperCase() + "-" + command.get(2).toUpperCase() + " was not removed. The currencies provided do not exist, there is no conversion rate between the two currencies or an error occurred on the server.";
            printResult(adminInterface.removeRate(sessionKey, command.get(1), command.get(2)), successMSG, errorMSG);
        }
        catch (RemoteException e)
        {
            throwAuthExeception(e, networkError);
        }
    }


    private void listRates() throws AuthenticationException, RemoteException {
        String networkError = "ERROR: A network error occurred while trying to execute removeCurrency(). Please confirm the service is available.";
        String title = "List of All Conversion Rates";
        String emptyMSG = "No conversion rates found. The database contains no conversion rates or an error occurred on the service.";
        
        //Call the service to list all the rates
        try
        {
            printList(adminInterface.listRates(sessionKey), title, emptyMSG);
        }
        catch (RemoteException e)
        {
            throwAuthExeception(e, networkError);
        }
    }




    @Override
    protected void exit() {

        //Execute the web service method
        try 
        {
            String successMSG = "Logout successful, goodbye " + username + ".";
            String errorMSG = "Logout failed.";
            printResult(loginInterface.logout(sessionKey), successMSG, errorMSG);
            exit = true;
        } 
        catch (RemoteException e) 
        {
            System.out.println("ERROR: A network error occurred while trying to execute logout(). Please confirm the service is available.");
            exit = true;
        }
    }


    private void throwAuthExeception(RemoteException e, String exceptionMessageIfNotAuthException) throws AuthenticationException, RemoteException {
        
        //The type of exception thrown by the Axis web service
        if(e instanceof AxisFault){
            AxisFault ex = (AxisFault) e;
            
            //The type of exception thrown when the client is not authenticated correctly
            if(ex.getFaultString().contains("javax.security.sasl.AuthenticationException"))
                throw new AuthenticationException("ERROR: Authentication Error. Your session key is invalid.");
            
            //Otherwise just throw a remove exception
            else
                throw new RemoteException(exceptionMessageIfNotAuthException);
        }
    }
}