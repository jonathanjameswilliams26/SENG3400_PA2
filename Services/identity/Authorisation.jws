/**
* The Login Endpoint provided by the Identity Service.
* @author  Jonathan Williams - C3237808 - SENG3400 Assignment 2
* @since   18/10/2018
*/
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
        System.out.println("Authorisation Endpoint - Executing authorise()");
        return database.authorise(key);
    }
}