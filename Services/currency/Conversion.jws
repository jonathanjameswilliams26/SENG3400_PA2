public class Conversion {

    private static CurrencyDatabaseInterface database = new CurrencyDatabaseInterface();

    /**
     * Gets a list of all rates inside the database
     * @return - A String[] containing the list of rates, an empty array if the database does not contain any currencies or rates.
     */
    public String[] listRates() {
        System.out.println("\nConversion Endpoint - Executing listRates()");
        return database.listRates();
    }

    /**
     * Gets the conversion rate between the passed in currency codes
     * @param fromCurrencyCode - The currency converting from
     * @param toCurrencyCode - The currency converting to
     * @return - The conversion rate between the two currencies, -1 if either codes does not exist
     */
    public double rateOf(String fromCurrencyCode, String toCurrencyCode) {
        System.out.println("\nConversion Endpoint - Executing rateOf()");
        return database.rateOf(fromCurrencyCode, toCurrencyCode);
    }

    /**
     * Converts the amount passed in from one currency to another including a 1% conversion fee.
     * @param fromCurrencyCode - The currency converting from
     * @param toCurrencyCode - The currency converting to
     * @param amount - The amount being converted
     * @return - The converted amount or negative value if the amount is invalid or the codes does not exist
     */
    public double convert(String fromCurrencyCode, String toCurrencyCode, double amount) {
        System.out.println("\nConversion Endpoint - Executing convert()");
        return database.convert(fromCurrencyCode, toCurrencyCode, amount);
    }
}