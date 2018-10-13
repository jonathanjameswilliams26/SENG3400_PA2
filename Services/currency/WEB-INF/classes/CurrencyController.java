public class CurrencyController {
    
    //The "fake" database which holds all the currency information
    private static CurrencyDatabase database = new CurrencyDatabase();

    public String[] listRates() {
        //Call the database to get all the rates of all the currencies
        return database.getAllRates();
    }


    public double rateOf(String fromCurrencyCode, String toCurrencyCode) {
        Currency currency = database.getCurrency(fromCurrencyCode);

        //The fromCurrencyCode does not exist in the database, return the negative value to indicate an error
        if(currency == null)
            return -1.0;

        //Get the rate of the toCurrencyCode
        return currency.rateOf(toCurrencyCode);
    }

    public double convert(String fromCurrencyCode, String toCurrencyCode, double amount) {
        
        Currency currency = database.getCurrency(fromCurrencyCode);

        //The fromCurrencyCode does not exist in the database, return the negative value to indicate an error
        if(currency == null)
            return -1.0;

        return currency.convert(toCurrencyCode, amount);
    }

}