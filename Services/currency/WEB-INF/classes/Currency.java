import java.util.HashMap;
import java.util.Map;

public class Currency {
    private String code;
    private HashMap<String, Double> rates;
    private boolean isTradeable;

    public Currency(String code) {
        this.code = code;
        rates = new HashMap<>();
        isTradeable = false;
    }
    
    public boolean addRate(String codeToAdd, double rate) {
        
        //Confirm the codeToAdd does not already exist inside the rate
        if(rates.containsKey(codeToAdd))
        {
            System.out.println("ERROR: The rate you are trying to add already exists for this currency.");
            return false;
        }

        //Add the new rate to the HashMap
        rates.put(codeToAdd, rate);
        
        //If the rates HashMap now has rates, make the currency tradeable
        if(!isTradeable)
            isTradeable = true;

        return true;
    }


    public boolean updateRate(String codeToUpdate, double newRate) {
        
        //Confirm the codeToUpdate exists inside the rates
        if(!rates.containsKey(codeToUpdate))
        {
            System.out.println("ERROR: The rate you are trying to update does not exist for this currency.");
            return false;
        }

        //Update the rate
        rates.put(codeToUpdate, newRate);
        return true;        
    }


    public boolean removeRate(String codeToRemove) {
        
        //Confirm the codeToRemove exists inside the rates
        if(!rates.containsKey(codeToRemove))
        {
            System.out.println("ERROR: The rate you are trying to remvoe does not exist for this currency.");
            return false;
        }

        //Remove the rate
        rates.remove(codeToRemove);

        //Check to see if there is atleast one rate inside the hashmap, if not set isTradable to false
        if(rates.size() == 0)
            isTradeable = false;

        return true;
    }


    public double rateOf(String code) {
        
        //Confirm the code passed in exists
        if(!rates.containsKey(code))
        {
            System.out.println("ERROR: The rate you are trying retrieve does not exist for this currency.");
            return -1.0;
        }

        //Return the conversion rate of the currency
        return rates.get(code);
    }


    public double convert(String convertToCode, double amount) {

        //Confirm the convertToCode exists in the rates
        if(!rates.containsKey(convertToCode))
        {
            System.out.println("ERROR: Cannot convert currency. The convertToCurrency rate does not exist for this currency.");
            return -1.0;
        }

        //Calculate the conversion including the 1% conversion fee
        return amount * rates.get(convertToCode) * 0.99;
    }

    public String[] getRates() {

        if(!isTradeable)
            return null;
        
        String[] formattedRates = new String[rates.size()];

        //Loop through all the rates and format the string accordingly
        //Makes the rates to 2 decimal places for readability
        int i = 0;
        for (Map.Entry<String, Double> entry : rates.entrySet())
        {
            formattedRates[i] = code + "-" + entry.getKey() + ":" + String.format("%.2f", entry.getValue());
            i++;
        }

        return formattedRates;
    }
}