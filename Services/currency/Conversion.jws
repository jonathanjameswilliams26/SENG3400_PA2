public class Conversion {

    private static CurrencyDatabaseInterface database = new CurrencyDatabaseInterface();

    /**
     * Lists all known conversion rates formatted as:
     * AUD-USD:0.73
     * 
     * @return - An array containing the list of all known conversion
     *           rates in the specified format.
     */
    public String[] listRates() {
        return database.listRates();
    }

    /**
     * Gets the conversion rate between the two currencies
     * 
     * @param fromCurrencyCode - The currency being converted from.
     * @param toCurrencyCode - The currency being converted to.
     * @return - The conversion rate between the two currencies.
     *           negative double if the pair does not exist.
     */
    public double rateOf(String fromCurrencyCode, String toCurrencyCode) {
        return database.rateOf(fromCurrencyCode, toCurrencyCode);
    }

    /**
     * Converts the amount of the fromCurrencyCode passed in to the toCurrencyCode amount.
     * 
     * @param fromCurrencyCode - The currency being converted from.
     * @param toCurrencyCode - The currency being converted to.
     * @param amount - The amount being converted.
     * @return - The converted amount, including a 1% conversion fee.
     *           Negative is returned if the currency pair does not exist.
     */
    public double convert(String fromCurrencyCode, String toCurrencyCode, double amount) {
        return database.convert(fromCurrencyCode, toCurrencyCode, amount);
    }
}