import java.rmi.RemoteException;
import javax.xml.rpc.ServiceException;
import javax.security.sasl.AuthenticationException;

/**
* The Admin Endpoint provided by the Currency Service.
* @author  Jonathan Williams - C3237808 - SENG3400 Assignment 2
* @since   18/10/2018
*/
public class Admin {

    private static CurrencyDatabaseInterface database = new CurrencyDatabaseInterface();

    /**
     * Adds a new currency to be converted by the Currency Service.
     * @param - sessionKey - The users session key.
     * @param - currencyCode - The currency code to add to the service.
     * @return - TRUE if the currency was successfully added, FALSE otherwise.
     * @throws AuthenticationException If the session key does not exist
     */
    public boolean addCurrency(String sessionKey, String currencyCode) throws AuthenticationException {
        System.out.println("\nAdmin Endpoint - Executing addCurrency()");
        return database.addCurrency(sessionKey, currencyCode);
    }

    /**
     * Removes a currency and all associated conversion rates from the service.
     * @param - sessionKey - The users session key.
     * @param - currencyCode - The currency code to remove to the service.
     * @return - TRUE if the currency was successfully removed, FALSE otherwise.
     * @throws AuthenticationException If the session key does not exist
     */
    public boolean removeCurrency(String sessionKey, String currencyCode) throws AuthenticationException {
        System.out.println("\nAdmin Endpoint - Executing removeCurrency()");
        return database.removeCurrency(sessionKey, currencyCode);
    }

    /**
     * Gets the list of all supported currencies for the service.
     * @param - sessionKey - The users session key.
     * @return - A string array containing the currencies supported.
     * @throws AuthenticationException If the session key does not exist
     */
    public String[] listCurrencies(String sessionKey) throws AuthenticationException {
        System.out.println("\nAdmin Endpoint - Executing listCurrencies()");
        return database.listCurrencies(sessionKey);
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
        System.out.println("\nAdmin Endpoint - Executing conversionsFor()");
        return database.conversionsFor(sessionKey, currencyCode);
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
        System.out.println("\nAdmin Endpoint - Executing addRate()");
        return database.addRate(sessionKey, fromCurrencyCode, toCurrencyCode, conversionRate);
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
        System.out.println("\nAdmin Endpoint - Executing updateRate()");
        return database.updateRate(sessionKey, fromCurrencyCode, toCurrencyCode, rate);
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
        System.out.println("\nAdmin Endpoint - Executing removeRate()");
        return database.removeRate(sessionKey, fromCurrencyCode, toCurrencyCode);
    }


    /**
     * Gets a list of all rates inside the database
     * @return - A String[] containing the list of rates, an empty array if the database does not contain any currencies or rates.
     * @throws AuthenticationException If the session key does not exist
     */
    public String[] listRates(String sessionKey) throws AuthenticationException {
        System.out.println("\nAdmin Endpoint - Executing listRates()");
        return database.listRates(sessionKey);
    }
}