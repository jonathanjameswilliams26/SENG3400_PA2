import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.rmi.RemoteException;
import javax.xml.rpc.ServiceException;
import javax.security.sasl.AuthenticationException;
import localhost.identity.Authorisation_jws.*;

public class CurrencyDatabaseInterface {
    
    //CONSTANTS
    private static final double INVALID_REQUEST = -1.0;
    
    private HashMap<String, Currency> currencies;

    
    /**
     * Default constructor, if the database file does not exist it creates the file.
     */
    public CurrencyDatabaseInterface() {
        
        File databaseFile = new File("currencyDatabase.txt");

        //The file does not exist, add the default currencies and rates
        if(!databaseFile.exists() && !databaseFile.isDirectory())
            initDatabase();
    }


    
    //Methods which do not require authorisation / authentication
    //------------------------------------------------------------------------
    //------------------------------------------------------------------------
    
    /**
     * Gets a list of all rates inside the database
     * 
     * @return - A String[] containing the list of rates
     */
    public String[] listRates() {
            
        //Read the database file for up to date information
        currencies = readDatabaseFile();

        Currency[] allCurrencies = getAllCurrencies();
        if(allCurrencies == null)
        {
            System.out.println("ERROR: There is no currencies in the database, cannot get rates.");
            return null;
        }

        //Loop through all of the currencies and build the array list
        ArrayList<String> ratesList = new ArrayList<>();
        for (Currency currency : allCurrencies) 
        {
            String[] rates = currency.getRates();

            if(rates == null)
                continue;
            
            //Loop through each of the rates and append them to the output
            for(String rate : rates)
            {
                ratesList.add(rate);
            }
        }

        //Return the arrayList as a normal string array
        return ratesList.toArray(new String[0]);
    }



    /**
     * Gets the conversion rate between the passed in currency codes
     * 
     * @param fromCurrencyCode - The currency converting from
     * @param toCurrencyCode - The currency converting to
     * @return - The conversion rate between the two currencies, -1 if either codes does not exist
     */
    public double rateOf(String fromCurrencyCode, String toCurrencyCode) {
        
        //Read the database file for up to date information
        currencies = readDatabaseFile();
        
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
     * Converts the amount passed in from one currency to another.
     * 
     * @param fromCurrencyCode - The currency converting from
     * @param toCurrencyCode - The currency converting to
     * @param amount - The amount being converted
     * @return - The converted amount or negative value if the amount is invalid or the codes does not exist
     */
    public double convert(String fromCurrencyCode, String toCurrencyCode, double amount) {

        //Read the database file for up to date information
        currencies = readDatabaseFile();

        //If the amount is equal to or less than 0 return invalid request
        if(amount <= 0)
            return INVALID_REQUEST;

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
     * 
     * @param - sessionKey - The users session key.
     * @param - currencyCode - The currency code to add to the service.
     * @return - TRUE if the currency was successfully added, FALSE otherwise.
     */
    public boolean addCurrency(String sessionKey, String currencyCode) throws ServiceException, RemoteException, AuthenticationException {
        
        validateSessionKey(sessionKey);

        //Read the database file for up to date information
        currencies = readDatabaseFile();
        
        //Confirm the currency code does not already exist within the database
        if(currencies.containsKey(currencyCode))
        {
            System.out.println("ERROR: Cannot execute addCurrency() because the currency code you are trying to add already exists");
            return false;
        }
            
        //Add the currency to the database
        currencies.put(currencyCode, new Currency(currencyCode));
        return save();
    }




    /**
     * Removes a currency and all associated conversion rates from the service.
     * 
     * @param - sessionKey - The users session key.
     * @param - currencyCode - The currency code to remove to the service.
     * @return - TRUE if the currency was successfully removed, FALSE otherwise.
     */
    public boolean removeCurrency(String sessionKey, String currencyCode) throws ServiceException, RemoteException, AuthenticationException {
        
        validateSessionKey(sessionKey);

        //Read the database file for up to date information
        currencies = readDatabaseFile();

        //Check the currency code passed in exists
        if(!currencies.containsKey(currencyCode))
        {
            System.out.println("ERROR: Cannot remove currency. The currency code does not exist in the database.");
            return false;
        }
        
        //The currency exists, remove from database
        currencies.remove(currencyCode);

        //Get all the currencies in the DB and remove the rate from each
        for (Currency currency : getAllCurrencies()) {
            if(currency.containsRate(currencyCode))
                currency.removeRate(currencyCode);
        }

        return save();
    }

    

    /**
     * Gets the list of all supported currencies for the service.
     * 
     * @param - sessionKey - The users session key.
     * @return - A string array containing the currencies supported.
     */
    public String[] listCurrencies(String sessionKey) throws ServiceException, RemoteException, AuthenticationException {
        
        validateSessionKey(sessionKey);

        //Read the database file for up to date information
        currencies = readDatabaseFile();

        Currency[] currencies = getAllCurrencies();

        //If null was returned from the database there is no currencies, return an empty array
        if(currencies == null)
            return new String[0];

        //Loop through all the currencies and build the currency codes array
        String[] codes = new String[currencies.length];
        for (int i = 0; i < currencies.length; i++)
            codes[i] = currencies[i].getCode();

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
     */
    public boolean addRate(String sessionKey, String fromCurrencyCode, String toCurrencyCode, double conversionRate) throws ServiceException, RemoteException, AuthenticationException {
      
        validateSessionKey(sessionKey);

        //If the conversionRate is invalid return false
        if(conversionRate <= 0)
        {
            System.out.println("ERROR: Cannot execute addRate() because the conversionRate is invalid.");
            return false;
        }

        //Read the database file for up to date information
        currencies = readDatabaseFile();

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
            
            //Save the database
            if(isSuccessful)
                isSuccessful = save();
        }

        return isSuccessful;
    }




    /**
     * Gets a list of all possible conversions with rate for the specified currency code.
     * In the format of: AUD-USD:0.7
     * 
     * @param - sessionKey - The users session key.
     * @param - currencyCode - The currency code to retrieve all rates for
     * @return - A string array containing all the conversion rates for the specified currency code
     */
    public String[] conversionsFor(String sessionKey, String currencyCode) throws ServiceException, RemoteException, AuthenticationException {
        
        validateSessionKey(sessionKey);

        //Read the database file for up to date information
        currencies = readDatabaseFile();

        //Get the currency from the database
        Currency currency = currencies.get(currencyCode);

        //If the currency does not exist return a null array
        if(currency == null)
            return null;

        //Get all the rates for the specified currency
        return currency.getRates();
    }




    /**
     * Updates the conversion rate between the specified currencies
     * This method will also update the inverse conversion rate.
     * 
     * @param - sessionKey - The users session key.
     * @param - fromCurrencyCode - The currency code the conversion is from.
     * @param - toCurrencyCode - The currency code the conversion is to.
     * @param - rate - The new rate at which the two currencies are converted.
     * @return - TRUE if the rate was successfully updated for the currency, FALSE otherwise.
     */
    public boolean updateRate(String sessionKey, String fromCurrencyCode, String toCurrencyCode, double rate) throws ServiceException, RemoteException, AuthenticationException {
        
        validateSessionKey(sessionKey);

        //If the rate is invalid return false
        if(rate <= 0)
        {
            System.out.println("ERROR: Cannot execute updateRate() because the rate is invalid.");
            return false;
        }
 
        //Read the database file for up to date information
        currencies = readDatabaseFile();

        //Confirm the fromCode and toCode exists in the database
        if(!currencies.containsKey(fromCurrencyCode) || !currencies.containsKey(toCurrencyCode))
        {
            System.out.println("ERROR: The fromCurrencyCode or toCurrencyCode does not exist in the database.");
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

            //Save the database
            if(isSuccessful)
                isSuccessful = save();
        }
        return isSuccessful;
    }


    /**
     * Removes all conversion rates between the specified currencies.
     * Will also remove the inverse conversion rate.
     * 
     * @param - sessionKey - The users session key.
     * @param - fromCurrencyCode - The currency code the conversion is from.
     * @param - toCurrencyCode - The currency code the conversion is to.
     * @return - TRUE if the rate was successfully removed from the currency, FALSE otherwise.
     */
    public boolean removeRate(String sessionKey, String fromCurrencyCode, String toCurrencyCode) throws ServiceException, RemoteException, AuthenticationException {
        
        validateSessionKey(sessionKey);
 
        //Read the database file for up to date information
        currencies = readDatabaseFile();

        //Confirm the the currency codes exist in the database
        if(!currencies.containsKey(fromCurrencyCode))
        {
            System.out.println("ERROR: Cannot remove rate. The fromCurrencyCode does not exist in the database.");
            return false;
        }
        if(!currencies.containsKey(toCurrencyCode))
        {
            System.out.println("ERROR: Cannot remove rate. The toCurrencyCode does not exist in the database.");
            return false;
        }

        //Get the currency to remove from
        Currency removeFrom = currencies.get(fromCurrencyCode);

        //Remove the rate from the currency
        boolean isSuccessful = removeFrom.removeRate(toCurrencyCode);

        //If successfully removed save the database
        if(isSuccessful)
            isSuccessful = save();

        return isSuccessful;
    }


    


    //HELPER METHODS
    //------------------------------------------------------------------------
    //------------------------------------------------------------------------

    private void validateSessionKey(String sessionKey) throws ServiceException, RemoteException, AuthenticationException {

        AuthorisationService service = new AuthorisationServiceLocator();
        Authorisation serviceInterface = service.getAuthorisation();
        
        //If the authorisation fails, throw a Authenication exception
        if(!serviceInterface.authorise(sessionKey))
            throw new AuthenticationException("The session key is invalid.");
    }


    private HashMap<String, Currency> readDatabaseFile() {
        
        try
        {
            FileInputStream in = new FileInputStream("currencyDatabase.txt");
            ObjectInputStream objectIn = new ObjectInputStream(in);

            Object obj = objectIn.readObject();
            objectIn.close();
            System.out.println("Successfully loaded currency database file.");
            return (HashMap<String, Currency>) obj;
        }
        catch (Exception e)
        {
            e.printStackTrace();
            return null;
        }
    }


    private boolean save() {
        try{
            FileOutputStream out = new FileOutputStream("currencyDatabase.txt");
            ObjectOutputStream objOut = new ObjectOutputStream(out);
            objOut.writeObject(currencies);
            objOut.close();
            System.out.println("Successfully saved database.");
            return true;
        }
        catch (Exception e)
        {
            e.printStackTrace();
            return false;
        }
    }



    private Currency[] getAllCurrencies() {
        
        //Read the database file for up to date information
        currencies = readDatabaseFile();

        //If there is no currencies in the database return null
        if(currencies.size() == 0)
        {
            System.out.println("ERROR: There is no currencies in the database.");
            return null;
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


    private void initDatabase() {
        currencies = new HashMap<>();
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
        save();
    }
}