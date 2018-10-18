import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.rmi.RemoteException;
import javax.xml.rpc.ServiceException;
import javax.security.sasl.AuthenticationException;
import localhost.identity.Authorisation_jws.*;

public class CurrencyDatabaseInterface {
    
    //CONSTANTS
    private static final double INVALID_REQUEST = -1.0;
    
    private static boolean initialised = false;                             //A flag which outlines if the database has been initialised with the default currencies and rates               
    private static HashMap<String, Currency> currencies = new HashMap<>();  //The data structure which stores the currencies <CurrencyCode, Currency>

    
    /**
     * Default constructor, if the database file does not exist it creates the file.
     */
    public CurrencyDatabaseInterface() {
        
        //If the database has not been initialised add the default currencies
        if(!initialised)
            initDatabase();
    }


    
    //Methods which do not require authorisation / authentication
    //------------------------------------------------------------------------
    //------------------------------------------------------------------------
    
    /**
     * Gets a list of all rates inside the database
     * @return - A String[] containing the list of rates, an empty array if the database does not contain any currencies or rates.
     */
    public String[] listRates() {

        //Confirm the database is not empty
        if(currencies.isEmpty())
        {
            System.out.println("ERROR: There is no currencies in the database, cannot get rates.");
            return new String[0];
        }

        //Loop through all of the currencies and build the array list
        Currency[] allCurrencies = getAllCurrencies();
        ArrayList<String> ratesList = new ArrayList<>();
        for (Currency currency : allCurrencies) 
        {
            //If the currency does not contain any rates move onto the next currency
            if(!currency.isTradable())
                continue;
            
            //Get the rates and loop through each of the rates and append them to the output
            String[] rates = currency.getRates();
            for(String rate : rates)
                ratesList.add(rate);
        }

        //Print a success or error message if there is rates to display
        if(ratesList.isEmpty())
            System.out.println("ERROR: There is no rates in the database.");
        else
            System.out.println("SUCCESS: Retrieved all rates from the database.");

        //Return the arrayList as a normal string array
        return ratesList.toArray(new String[0]);
    }






    /**
     * Gets the conversion rate between the passed in currency codes
     * @param fromCurrencyCode - The currency converting from
     * @param toCurrencyCode - The currency converting to
     * @return - The conversion rate between the two currencies, -1 if either codes does not exist
     */
    public double rateOf(String fromCurrencyCode, String toCurrencyCode) {
        
        //If the currency does not exist in the database return a negative value
        if(!currencies.containsKey(fromCurrencyCode))
        {
            System.out.println("ERROR: Cannot execute rateOf() because the fromCurrencyCode does not exist.");
            return INVALID_REQUEST;
        }

        //Otherwise get the rate of the toCurrencyCode
        Currency currency = currencies.get(fromCurrencyCode);
        return currency.rateOf(toCurrencyCode);
    }






    /**
     * Converts the amount passed in from one currency to another including a 1% conversion fee.
     * @param fromCurrencyCode - The currency converting from
     * @param toCurrencyCode - The currency converting to
     * @param amount - The amount being converted
     * @return - The converted amount or negative value if the amount is invalid or the codes does not exist
     */
    public double convert(String fromCurrencyCode, String toCurrencyCode, double amount) {

        //If the currency does not exist in the database return a negative value
        if(!currencies.containsKey(fromCurrencyCode))
        {
            System.out.println("ERROR: Cannot execute convert() because the fromCurrencyCode does not exist.");
            return INVALID_REQUEST;
        }

        //The fromCurrencyCode exist, perform the conversion
        Currency currency = currencies.get(fromCurrencyCode);
        return currency.convert(toCurrencyCode, amount);
    }







    //Admin Methods which require authorisation / authentication
    //------------------------------------------------------------------------
    //------------------------------------------------------------------------


    /**
     * Adds a new currency to be converted by the Currency Service.
     * @param - sessionKey - The users session key.
     * @param - currencyCode - The currency code to add to the service.
     * @return - TRUE if the currency was successfully added, FALSE otherwise.
     * @throws AuthenticationException If the session key does not exist
     */
    public boolean addCurrency(String sessionKey, String currencyCode) throws AuthenticationException {
        
        validateSessionKey(sessionKey);
        
        //Confirm the currency code does not already exist within the database
        if(currencies.containsKey(currencyCode))
        {
            System.out.println("ERROR: Cannot execute addCurrency() because the currency code you are trying to add already exists");
            return false;
        }
            
        //Add the currency to the database
        System.out.println("SUCCESS: Currency successfully added.");
        currencies.put(currencyCode, new Currency(currencyCode));
        return true;
    }






    /**
     * Removes a currency and all associated conversion rates from the service.
     * @param - sessionKey - The users session key.
     * @param - currencyCode - The currency code to remove to the service.
     * @return - TRUE if the currency was successfully removed, FALSE otherwise.
     * @throws AuthenticationException If the session key does not exist
     */
    public boolean removeCurrency(String sessionKey, String currencyCode) throws AuthenticationException {
        
        validateSessionKey(sessionKey);

        //Check the currency code passed in exists
        if(!currencies.containsKey(currencyCode))
        {
            System.out.println("ERROR: Cannot execute removeCurrency() because the currencyCode does not exist in the database.");
            return false;
        }
        
        //The currency exists, remove from database
        currencies.remove(currencyCode);

        //Get all the currencies in the DB and remove the rate from each
        if(!currencies.isEmpty())
        {
            for (Currency currency : getAllCurrencies()) {
                if(currency.containsRate(currencyCode))
                    currency.removeRate(currencyCode);
            }
        }
        
        System.out.println("SUCCESS: Currency successfully removed.");
        return true;
    }

    




    /**
     * Gets the list of all supported currencies for the service.
     * @param - sessionKey - The users session key.
     * @return - A string array containing the currencies supported.
     * @throws AuthenticationException If the session key does not exist
     */
    public String[] listCurrencies(String sessionKey) throws AuthenticationException {
        
        validateSessionKey(sessionKey);

        //Confirm the database is not empty
        if(currencies.isEmpty())
        {
            System.out.println("ERROR: Cannot execute listCurrencies() because the database is empty.");
            return new String[0];
        }

        //Loop through all the currencies and build the currency codes array
        Currency[] currencies = getAllCurrencies();
        String[] codes = new String[currencies.length];
        for (int i = 0; i < currencies.length; i++)
            codes[i] = currencies[i].getCode();

        System.out.println("SUCCESS: Currencies retrieved.");
        return codes;
    }






    /**
     * Adds a conversion rate between the two passed currency codes.
     * This method will also add the inverse conversion rate.
     * 
     * @param - sessionKey - The users session key.
     * @param - fromCurrencyCode - The currency code the conversion is from.
     * @param - toCurrencyCode - The currency code the conversion is to.
     * @param - conversionRate - The rate at which the two currencies are converted.
     * @return - TRUE if the rate was successfully added to the currency, FALSE otherwise.
     * @throws AuthenticationException If the session key does not exist
     */
    public boolean addRate(String sessionKey, String fromCurrencyCode, String toCurrencyCode, double conversionRate) throws AuthenticationException {
      
        validateSessionKey(sessionKey);

        //Confirm the fromCode and toCode exists in the database
        if(!currencies.containsKey(fromCurrencyCode) || !currencies.containsKey(toCurrencyCode))
        {
            System.out.println("ERROR: Cannot execute addRate() because the fromCurrencyCode or toCurrencyCode does not exist in the database.");
            return false;
        }
        
        //Get the Currency from the database
        Currency currency = currencies.get(fromCurrencyCode);

        //Add the rate to the currency
        boolean isSuccessful = false;
        isSuccessful = currency.addRate(toCurrencyCode, conversionRate);

        //If the rate was successfully added to the currency add the inverse rate to the other currency and save the database
        if(isSuccessful)
        {
            Currency inverseCurrency = currencies.get(toCurrencyCode);
            double inverseRate = 1.0/conversionRate;
            isSuccessful = inverseCurrency.addRate(fromCurrencyCode, inverseRate);
        }

        return isSuccessful;
    }






    /**
     * Gets a list of all possible conversions with rate for the specified currency code.
     * In the format of: AUD-USD:0.7
     * @param - sessionKey - The users session key.
     * @param - currencyCode - The currency code to retrieve all rates for
     * @return - A string array containing all the conversion rates for the specified currency code
     * @throws AuthenticationException If the session key does not exist
     */
    public String[] conversionsFor(String sessionKey, String currencyCode) throws AuthenticationException {
        
        validateSessionKey(sessionKey);

        //Confirm the currency code exists in the database
        if(!currencies.containsKey(currencyCode))
        {
            System.out.println("ERROR: Cannot execute conversionFor() because the currencyCode does not exist in the database.");
            return new String[0];
        }

        //Get the currency from the database
        Currency currency = currencies.get(currencyCode);

        //Get all the rates for the specified currency
        return currency.getRates();
    }






    /**
     * Updates the conversion rate between the specified currencies
     * This method will also update the inverse conversion rate.
     * @param - sessionKey - The users session key.
     * @param - fromCurrencyCode - The currency code the conversion is from.
     * @param - toCurrencyCode - The currency code the conversion is to.
     * @param - rate - The new rate at which the two currencies are converted.
     * @return - TRUE if the rate was successfully updated for the currency, FALSE otherwise.
     * @throws AuthenticationException If the session key does not exist
     */
    public boolean updateRate(String sessionKey, String fromCurrencyCode, String toCurrencyCode, double rate) throws AuthenticationException {
        
        validateSessionKey(sessionKey);

        //Confirm the fromCode and toCode exists in the database
        if(!currencies.containsKey(fromCurrencyCode) || !currencies.containsKey(toCurrencyCode))
        {
            System.out.println("ERROR: Cannot execute updateRate() because the fromCurrencyCode or toCurrencyCode does not exist in the database.");
            return false;
        }

        //Get the Currency from the database
        Currency currency = currencies.get(fromCurrencyCode);

        //update the rate of the currency
        boolean isSuccessful = false;
        isSuccessful = currency.updateRate(toCurrencyCode, rate);

        //If the update was successful update the inverse and save the database
        if(isSuccessful)
        {
            Currency inverseCurrency = currencies.get(toCurrencyCode);
            double inverseRate = 1.0/rate;
            isSuccessful = inverseCurrency.updateRate(fromCurrencyCode, inverseRate);
        }
        return isSuccessful;
    }






    /**
     * Removes all conversion rates between the specified currencies.
     * Will also remove the inverse conversion rate.
     * @param - sessionKey - The users session key.
     * @param - fromCurrencyCode - The currency code the conversion is from.
     * @param - toCurrencyCode - The currency code the conversion is to.
     * @return - TRUE if the rate was successfully removed from the currency, FALSE otherwise.
     * @throws AuthenticationException If the session key does not exist
     */
    public boolean removeRate(String sessionKey, String fromCurrencyCode, String toCurrencyCode) throws AuthenticationException {
        
        validateSessionKey(sessionKey);

        //Confirm the the currency codes exist in the database
        if(!currencies.containsKey(fromCurrencyCode))
        {
            System.out.println("ERROR: Cannot execute removeRate() because the fromCurrencyCode does not exist in the database.");
            return false;
        }
        if(!currencies.containsKey(toCurrencyCode))
        {
            System.out.println("ERROR: Cannot execute removeRate() because the toCurrencyCode does not exist in the database.");
            return false;
        }

        //Get the currency to remove from
        Currency removeFrom = currencies.get(fromCurrencyCode);

        //Remove the rate from the currency
        boolean isSuccessful = removeFrom.removeRate(toCurrencyCode);

        //Remove the inverse rate
        Currency inverse = currencies.get(toCurrencyCode);
        isSuccessful = inverse.removeRate(fromCurrencyCode);
        return isSuccessful;
    }





    /**
     * Gets a list of all rates inside the database
     * @return - A String[] containing the list of rates, an empty array if the database does not contain any currencies or rates.
     * @throws AuthenticationException If the session key does not exist
     */
    public String[] listRates(String sessionKey) throws AuthenticationException {
        validateSessionKey(sessionKey);
        return listRates();
    }


    


    //HELPER METHODS
    //------------------------------------------------------------------------
    //------------------------------------------------------------------------


    /**
     * Validates a users session key and confirm the session key exists using the Authorisation endpoint
     * @param sessionKey - The session key to validate
     * @throws AuthenticationException If the session key does not exist
     */
    private void validateSessionKey(String sessionKey) throws AuthenticationException {

        try
        {
            //Use the authorisation end point to call the Authorise method
            AuthorisationService service = new AuthorisationServiceLocator();
            Authorisation serviceInterface = service.getAuthorisation();
            
            //If the authorisation fails, throw a Authenication exception
            if(!serviceInterface.authorise(sessionKey))
                throw new AuthenticationException();
        }
        catch (Exception e)
        {
            throw new AuthenticationException();
        }     
    }




    /**
     * Gets all the currencies from the database.
     * @return - A Currency array of all the currencies in the database.
     * An empty array if the database is empty
     */
    private Currency[] getAllCurrencies() {
        //If there is no currencies in the database return null
        if(currencies.isEmpty())
        {
            System.out.println("ERROR: There is no currencies in the database.");
            return new Currency[0];
        }
        
        //Loop through all of the currencies in the database and build the array
        Currency[] currenciesArray = new Currency[currencies.size()];
        int i = 0;
        for(Map.Entry<String, Currency> entry : currencies.entrySet())
        {
            currenciesArray[i] = entry.getValue();
            i++;
        }
        return currenciesArray;
    }





    /**
     * Initalises the database with the default currencies and conversion rates.
     */
    private void initDatabase() {
        //Add the default currencies
        currencies.put("AUD", new Currency("AUD"));
        currencies.put("USD", new Currency("USD"));
        currencies.put("NZD", new Currency("NZD"));
        currencies.put("GBP", new Currency("GBP"));

        //Add the default rates
        currencies.get("AUD").addRate("USD", 0.7);
        currencies.get("AUD").addRate("NZD", 1.09);
        currencies.get("AUD").addRate("GBP", 0.55);

        //Add the inverse rates
        currencies.get("USD").addRate("AUD", 1.0/0.7);
        currencies.get("NZD").addRate("AUD", 1.0/1.09);
        currencies.get("GBP").addRate("AUD", 1.0/0.5);

        initialised = true;
    }
}