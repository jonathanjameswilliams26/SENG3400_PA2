import java.rmi.RemoteException;
import javax.xml.rpc.ServiceException;
import javax.security.sasl.AuthenticationException;
public class Admin {

    private static CurrencyDatabaseInterface database = new CurrencyDatabaseInterface();

    /**
     * Adds a new currency to be converted by the Currency Service.
     * 
     * @param - sessionKey - The users session key.
     * @param - currencyCode - The currency code to add to the service.
     * @return - TRUE if the currency was successfully added, FALSE otherwise.
     */
    public boolean addCurrency(String sessionKey, String currencyCode) throws ServiceException, RemoteException, AuthenticationException {
        return database.addCurrency(sessionKey, currencyCode);
    }

    /**
     * Removes a currency and all associated conversion rates from the service.
     * 
     * @param - sessionKey - The users session key.
     * @param - currencyCode - The currency code to remove to the service.
     * @return - TRUE if the currency was successfully removed, FALSE otherwise.
     */
    public boolean removeCurrency(String sessionKey, String currencyCode) throws ServiceException, RemoteException, AuthenticationException {
        return database.removeCurrency(sessionKey, currencyCode);
    }

    /**
     * Gets the list of all supported currencies for the service.
     * 
     * @param - sessionKey - The users session key.
     * @return - A string array containing the currencies supported.
     */
    public String[] listCurrencies(String sessionKey) throws ServiceException, RemoteException, AuthenticationException {
        return database.listCurrencies(sessionKey);
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
     */
    public boolean addRate(String sessionKey, String fromCurrencyCode, String toCurrencyCode, double conversionRate) throws ServiceException, RemoteException, AuthenticationException {
        return database.addRate(sessionKey, fromCurrencyCode, toCurrencyCode, conversionRate);
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
        return database.updateRate(sessionKey, fromCurrencyCode, toCurrencyCode, rate);
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
        return database.removeRate(sessionKey, fromCurrencyCode, toCurrencyCode);
    }


    /**
     * Gets a list of all known conversion rates formatted as:
     * AUD-USD:0.73
     * 
     * @return - An array containing the list of all known conversion
     *           rates in the specified format.
     */
    public String[] listRates() throws ServiceException, RemoteException, AuthenticationException {
        return database.listRates();
    }
}