import java.util.Scanner;

import localhost.currency.Conversion_jws.*;

public class CurrencyClient {

    public static void main(String[] args) {

        boolean exit = false;

        System.out.println("Welcome to Currency Client...");
        Scanner reader = new Scanner(System.in);

        Conversion conversionInterface = null;
        ConversionService service = null;

        try{
            service = new ConversionServiceLocator();
            conversionInterface = service.getConversion();
        }
        catch (Exception e)
        {
            System.out.println("An error occured getting the conversion service.");
        }
        

        while(!exit)
        {
            //Output the prompt options to the user
            System.out.println("Please type in one of the following options:");
            System.out.println("1. convert <fromCurrency> <toCurrency> <amount>");
            System.out.println("2. rateOf <fromCurrency> <toCurrency>");
            System.out.println("3. listRates");
            System.out.println("4. exit");

            String input = reader.nextLine();

            if(input.equals("listRates"))
            {
                try{
                    String[] rates = conversionInterface.listRates();
                    for(int i = 0; i < rates.length; i++)
                    {
                        System.out.println(rates[i]);
                    }
                }
                catch (Exception e)
                {
                    System.out.println("An error occured executing the list rates.");
                }
            }
            else if (input.equals("exit"))
            {
                exit = true;
            }
            else
            {

            }
        }

        System.out.println("Exiting Program...");
    }
}