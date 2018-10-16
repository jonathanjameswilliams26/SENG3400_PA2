public class Authorisation {

    private static UserDatabase database = new UserDatabase();

    /**
     * The endpoint which authorises a users session key to determine if
     * they are actually logged into the system.
     * 
     * @param key - The users session key.
     * @return - TRUE if the session key is valid. FALSE otherwise
     */
    public boolean authorise(String key) {
        return database.authorise(key);
    }
}