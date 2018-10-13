import java.util.Scanner;
import localhost.currency.Conversion_jws.*;

public class CurrencyClient {

    public static void main(String[] args) {

        //Initalise variables
        Scanner input = new Scanner(System.in);
        boolean exit = false;
        ConversionService service = null;
        Conversion serviceInterface = null;
        try
        {
            service = new ConversionServiceLocator();
            serviceInterface = service.getConversion();
        }
        catch (Exception e)
        {
            System.out.println("ERROR: An error occurred while establishing a connection with the service.");
            exit = true;
        }
        
        //Run the program while the user has not exited the program
        while(!exit)
        {
            //Output the prompt options to the user and capture the users input
            String userInput = getUserInput(input);

            //Execute listRates
            if(userInput.equals("listRates"))
                listRates(serviceInterface);


            //Exit the application
            else if (userInput.equals("exit"))
                exit = true;

            //Otherwise, the input may be convert, rateOf or invalid.
            else
            {
                //Split the input into token, split based on the use of spaces.
                String[] tokens = tokeniseInput(userInput);

                //Invalid input detected
                if(tokens == null)
                    System.out.println("Invalid input, please try again.");

                //Execute rateOf
                else if (tokens[0].equals("rateOf"))
                    rateOf(serviceInterface, tokens);

                //Execute convert
                else if (tokens[0].equals("convert"))
                    convert(serviceInterface, tokens);

                //Invalid input
                else
                    System.out.println("ERROR: Invalid input, please try again."); 
            }      
        }
        System.out.println("Exiting Program...");
    }



    private static String getUserInput(Scanner input) {
        System.out.println("\nPlease type in one of the following options:");
        System.out.println("1. convert <fromCurrency> <toCurrency> <amount>");
        System.out.println("2. rateOf <fromCurrency> <toCurrency>");
        System.out.println("3. listRates");
        System.out.println("4. exit\n");
        System.out.print("Enter Input: ");
        return input.nextLine();
    }


    private static void listRates(Conversion serviceInterface) {

        try
        {
            String[] rates = serviceInterface.listRates();

            if(rates == null)
            {
                System.out.println("No currencies in the database.");
                return; 
            }

            if(rates.length == 0)
            {
                System.out.println("No currency rates in the database.");
                return;
            }

            //Loop through the rates and print them out
            System.out.println("\nConversion Rates:");
            System.out.println("--------------------------------------");
            for (String rate : rates) 
                System.out.println(rate);
            System.out.println("--------------------------------------");
        }
        catch (Exception e)
        {
            System.out.println("ERROR: An error occurred while trying to execute listRates from the web service.");
        }
    }



    private static void rateOf(Conversion serviceInterface, String[] tokens) {
        try
        {
            //Confirm there is 3 tokens
            if(tokens.length != 3)
            {
                System.out.println("ERROR: Cannot execute rateOf() because the input is invalid, please enter the correct input required.");
                return;   
            }
            
            //Execute the web service
            double rate = serviceInterface.rateOf(tokens[1], tokens[2]);

            if(rate == -1.0)
                System.out.println("ERROR: Cannot execute rateOf(). The fromCurrency and toCurrency code pair does not exist in the database.");
            
            //Print out the rate results
            else
            {
                System.out.println("\nRate Of " + tokens[1] + "-" + tokens[2] + ":");
                System.out.println("--------------------------------------");
                System.out.println(String.format("%.4f", rate));
                System.out.println("--------------------------------------");
            }
        }
        catch (Exception e)
        {
            System.out.println("ERROR: An error occurred while trying to execute rateOf from the web service.");
        }
    }

    private static void convert(Conversion serviceInterface, String[] tokens) {
        try
        {
            //Confirm there is 4 tokens
            if(tokens.length != 4)
            {
                System.out.println("ERROR: Cannot execute convert() because the input is invalid, please enter the correct input required.");
                return;   
            }

            //Convert the last token to a double
            double amount = -1;
            try
            {
                amount = Double.parseDouble(tokens[3]);
            }
            catch (NumberFormatException e)
            {
                System.out.println("ERROR: Cannot execute convert() because the input is invalid, the <amount> entered is not a double data type.");
                return; 
            }
            
            //Execute the web service
            amount = serviceInterface.convert(tokens[1], tokens[2], amount);

            if(amount == -1.0)
                System.out.println("ERROR: Cannot execute convert(). The fromCurrency and toCurrency code pair does not exist in the database or the amount entered is less than or equal to 0.");
            
            //Print out the rate results
            else
            {
                System.out.println("\nCurrency Conversion From " + tokens[1] + "-" + tokens[2] + ":");
                System.out.println("--------------------------------------");
                System.out.println(String.format("$%.2f", amount));
                System.out.println("--------------------------------------");
            }
        }
        catch (Exception e)
        {
            System.out.println("ERROR: An error occurred while trying to execute rateOf from the web service.");
        }
    }


    private static String[] tokeniseInput(String userInput) {
        
        if(userInput == null)
            return null;

        //If the user input does not contain a space then the input is invalid
        if(!userInput.contains(" "))
            return null;

        //Get the tokens from the input
        String[] tokens = userInput.split(" ");

        //There must be atleast 3 tokens to be a possible valid input since rateOf uses three tokens
        if(tokens.length < 3)
            return null;

        return tokens;
    }
}