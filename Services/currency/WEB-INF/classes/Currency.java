import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

/**
* This class represents a Currency, a currency is represented by a Currency code
* and a currency can contain a number of conversion rates between other currencies.
* This class offers CRUD operations in order to create/read/update/delete conversion rates.
*
* @author  Jonathan Williams - C3237808 - SENG3400 Assignment 2
* @since   18/10/2018
*/
public class Currency {
    
    //The value returned for invalid requests
    private static final double INVALID = -1.0;

    //Private member variables
    private String code;                    //The currency code, AUD, USD etc
    private HashMap<String, Double> rates;  //The conversion rates this currency can convert to. <toCurrencyCode, rate>
    private boolean tradeable;            //A flag value which indicates if the currency is tradeable (has a conversion rate)


    /**
     * Default constructor - Creates a currency with the specified code
     * @param code - The currency code
     */
    public Currency(String code) {
        this.code = code;
        rates = new HashMap<>();
        tradeable = false;
        System.out.println("SUCCESS: Created currency - " + code);
    }




    /**
     * Gets the currency code.
     * @return - The currency code.
     */
    public String getCode() { 
        return code;
    }



    /**
     * Outlines if the currency is tradeable (has conversion rates)
     * @return - TRUE = the currency is tradeable, FALSE otherwise
     */
    public boolean isTradable() {
        return tradeable;
    }
    



    /**
     * Adds a conversion rate to the currency if the conversion does not already exist.
     * @param codeToAdd - The currency code which is being added to the rates
     * @param rate - The conversion rate between this currency and the code to add
     * @return - TRUE if successful, FALSE otherwise
     */
    public boolean addRate(String codeToAdd, double rate) {
        
        System.out.println(code + " - Attempting to add conversion rate - " + codeToAdd + ":" + String.format("%.4f", rate));

        //Confirm the new rate is within the valid range
        if(rate <= 0)
        {
            System.out.println("ERROR: Cannot add rate because the <rate> is invalid.");
            return false;
        }
        
        //Confirm the codeToAdd does not already exist inside the rate
        if(rates.containsKey(codeToAdd))
        {
            System.out.println("ERROR: The rate you are trying to add already exists for this currency.");
            return false;
        }

        //Add the new rate to the HashMap
        rates.put(codeToAdd, rate);
        
        //If the rates HashMap now has rates, make the currency tradeable
        if(!tradeable)
            tradeable = true;

        System.out.println("SUCCESS: rate successfully added.");
        return true;
    }




    /**
     * Updates the conversion rate between the currency and the codeToUpdate.
     * @param codeToUpdate - The currency code which rate will be updated
     * @param newRate - The value of the new rate, must be greater than 0.
     * @return - TRUE if successful, FALSE otherwise
     */
    public boolean updateRate(String codeToUpdate, double newRate) {
        
        System.out.println(code + " - Attempting to update conversion rate - " + codeToUpdate + ":" + String.format("%.4f", newRate));

        //Confirm the new rate is within the valid range
        if(newRate <= 0)
        {
            System.out.println("ERROR: Cannot update rate because the <rate> is invalid.");
            return false;
        }
        
        //Confirm the codeToUpdate exists inside the rates
        if(!rates.containsKey(codeToUpdate))
        {
            System.out.println("ERROR: The rate you are trying to update does not exist for this currency.");
            return false;
        }

        //Update the rate
        rates.put(codeToUpdate, newRate);
        System.out.println("SUCCESS: rate successfully updated.");
        return true;        
    }




    /**
     * Removes the conversion rate between the currrency and the codeToRemove
     * @param codeToRemove - The currency code to remove from the conversion rates.
     * @return - TRUE if successful, FALSE otherwise
     */
    public boolean removeRate(String codeToRemove) {
        
        System.out.println(code + " - Attempting to remove conversion rate - " + codeToRemove);

        //Confirm the codeToRemove exists inside the rates
        if(!rates.containsKey(codeToRemove))
        {
            System.out.println("ERROR: The rate you are trying to remvoe does not exist for this currency.");
            return false;
        }

        //Remove the rate
        rates.remove(codeToRemove);

        //Check to see if there is atleast one rate inside the hashmap, if not set isTradable to false
        if(rates.isEmpty())
            tradeable = false;

        System.out.println("SUCCESS: rate successfully removed.");
        return true;
    }




    /**
     * Gets the conversion rate between the currency and the specified code.
     * @param code - The currency code to get the conversion rate for.
     * @return - The conversion rate between the currencies if code exists.
     *           negative value if the code does not exist.
     */
    public double rateOf(String code) {
        
        System.out.println(this.code + " - Attempting to get the conversion rate of - " + code);

        //Confirm the code passed in exists
        if(!rates.containsKey(code))
        {
            System.out.println("ERROR: The rate you are trying retrieve does not exist for this currency.");
            return INVALID;
        }

        //Return the conversion rate of the currency
        System.out.println("SUCCESS: Successfully retreived rate from currency.");
        return rates.get(code);
    }




    /**
     * Converts the amount from the currency to the convertToCurrency.
     * Converts the amount with a 1% conversion fee
     * @param convertToCurrency - The currency to convert to.
     * @param amount - The amount to be converted. Must be greater than 0.
     * @return - The converted amount, negative value if unsuccessful.
     */
    public double convert(String convertToCurrency, double amount) {

        System.out.println(code + " - Attempting to convert " + String.format("%.4f", amount) + " to " + convertToCurrency);

        //confirm the amount is valid
        if(amount <= 0)
        {
            System.out.println("ERROR: Cannot convert because the <amount> is invalid.");
            return INVALID;
        }
        
        //Confirm the convertToCode exists in the rates
        if(!rates.containsKey(convertToCurrency))
        {
            System.out.println("ERROR: Cannot convert currency. The convertToCurrency rate does not exist for this currency.");
            return INVALID;
        }

        //Calculate the conversion including the 1% conversion fee
        double convertedAmount = amount * rates.get(convertToCurrency) * 0.99;
        System.out.println("SUCCESS: Conversion successful, the converted amount is: " + String.format("%.4f", convertedAmount));
        return convertedAmount;
    }




    
    /**
     * Confirms if the currency contains a conversion rate with the specified currency code.
     * @param code - The currency code of the convertToCurrency
     * @return - TRUE if the currency contains a conversion rate for the specified code, FALSE otherwise.
     */
    public boolean containsRate(String code) {
        return rates.containsKey(code);
    }





    /**
     * Gets a list of all the rates the currency contains in the following format:
     * <fromCode>-<toCode>:Rate
     * AUD-USD:1.3
     * @return - A string array containing the rates for the currency. An empty array
     * if the currency does not contain any rates
     */
    public String[] getRates() {

        System.out.println(code + " - Attempting to get all the conversion rates.");

        //Confirm the currency has conversion rates, return an empty array if not
        if(!tradeable)
        {
            System.out.println("ERROR:" + code + " does not contain any rates.");
            return new String[0];
        }
            
        
        String[] formattedRates = new String[rates.size()];

        //Loop through all the rates and format the string accordingly
        //Makes the rates to 4 decimal places for readability
        int i = 0;
        for (Map.Entry<String, Double> entry : rates.entrySet())
        {
            formattedRates[i] = code + "-" + entry.getKey() + ":" + String.format("%.4f", entry.getValue());
            i++;
        }

        System.out.println("SUCCESS: Successfully retrieved conversion rates.");
        return formattedRates;
    }
}