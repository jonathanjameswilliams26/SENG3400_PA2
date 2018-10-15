import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class CurrencyDatabase {
    private static HashMap<String, Currency> currencies = new HashMap<>();
    private static boolean isInitialised;

    public CurrencyDatabase() {
        //Add the default currencies and ratesfor the database
        if(!isInitialised)
        {
            addCurrency("AUD");
            addCurrency("USD");
            addCurrency("NZD");
            addCurrency("GBP");

            addRate("AUD", "USD", 0.7);
            addRate("AUD", "NZD", 1.09);
            addRate("AUD", "GBP", 0.55);
        }
    }

    public Currency getCurrency(String code) {
        Currency currency = currencies.get(code);

        if(currency == null)
            System.out.println("ERROR: The currency code passed in does not exist.");

        return currency;
    }

    public boolean addCurrency(String code) {
        
        //Confirm the currency code does not already exist within the database
        if(currencies.containsKey(code))
        {
            System.out.println("ERROR: Cannot add currency because the currency code you are trying to add already exists");
            return false;
        }
            
        //Add the currency to the database
        currencies.put(code, new Currency(code));
        return true;
    }


    public boolean removeCurrency(String code) {
        
        //Check the currency code passed in exists
        if(!currencies.containsKey(code))
        {
            System.out.println("ERROR: Cannot remove currency. The currency code does not exist in the database.");
            return false;
        }
        
        //The currency exists, remove from database
        currencies.remove(code);

        //Get all the currencies in the DB and remove the rate from each
        for (Currency currency : getAllCurrencies()) {
            if(currency.containsRate(code))
                currency.removeRate(code);
        }

        return true;
    }

    public Currency[] getAllCurrencies() {
        
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


    public boolean addRate(String fromCode, String toCode, double rate) {

        //Confirm the fromCode and toCode exists in the database
        if(!currencies.containsKey(fromCode) || !currencies.containsKey(toCode))
        {
            System.out.println("ERROR: The fromCurrencyCode or toCurrencyCode does not exist in the database.");
            return false;
        }
        
        //Get the Currency from the database
        Currency currency = getCurrency(fromCode);

        //Add the rate to the currency
        boolean isSuccessful = false;
        isSuccessful = currency.addRate(toCode, rate);

        //If the rate was successfully added to the currency add the inverse rate to the other currency
        if(isSuccessful)
        {
            Currency inverseCurrency = getCurrency(toCode);
            double inverseRate = 1.0/rate;
            isSuccessful = inverseCurrency.addRate(fromCode, inverseRate);
        }

        return isSuccessful;
    }


    public boolean updateRate(String fromCode, String toCode, double rate) {
        
        //Confirm the fromCode and toCode exists in the database
        if(!currencies.containsKey(fromCode) || !currencies.containsKey(toCode))
        {
            System.out.println("ERROR: The fromCurrencyCode or toCurrencyCode does not exist in the database.");
            return false;
        }

        //Get the Currency from the database
        Currency currency = getCurrency(fromCode);

        //update the rate of the currency
        boolean isSuccessful = false;
        isSuccessful = currency.updateRate(toCode, rate);

        //If the update was successful update the inverse
        if(isSuccessful)
        {
            Currency inverseCurrency = getCurrency(toCode);
            double inverseRate = 1.0/rate;
            isSuccessful = inverseCurrency.updateRate(fromCode, inverseRate);
        }
        return isSuccessful;
    }


    public boolean removeRate(String fromCode, String toCode) {
        
        //Confirm the the currency codes exist in the database
        if(!currencies.containsKey(fromCode))
        {
            System.out.println("ERROR: Cannot remove rate. The fromCode does not exist in the database.");
            return false;
        }
        if(!currencies.containsKey(toCode))
        {
            System.out.println("ERROR: Cannot remove rate. The toCode does not exist in the database.");
            return false;
        }

        //Get the currency to remove from
        Currency removeFrom = getCurrency(fromCode);

        //Remove the rate from the currency
        return removeFrom.removeRate(toCode);
    }


    public String[] getAllRates() {
                
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
}