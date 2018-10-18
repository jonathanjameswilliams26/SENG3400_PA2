/**
* The Login Endpoint provided by the Identity Service.
* @author  Jonathan Williams - C3237808 - SENG3400 Assignment 2
* @since   18/10/2018
*/
public class Login {

    private static UserDatabase database = new UserDatabase();

    /**
     * The endpoint which logs a user into the application.
     * 
     * @param username - The username logging in.
     * @param password - The password for the user.
     * @return - A random 5 character string which is the session key. If the
     *         username or password is incorrect "INVALID" is returned.
     */
    public String login(String username, String password) {
        System.out.println("\nLogin Endpoint - Executing login()");
        return database.login(username, password);
    }


    /**
     * Logs the users out of the application by invalidating a users session key.
     * 
     * @param key - The users session key.
     * @return - TRUE if the users successfully logged out. FALSE otherwise.
     */
    public boolean logout(String key) {
        System.out.println("\nLogin Endpoint - Executing logout()");
        return database.logout(key);
    }
}