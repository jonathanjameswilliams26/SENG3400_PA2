import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Random;

/**
* This class represents a "dummy" database for user accounts and active sessions.
* User accounts will be stored in the database as <Username, password> pairs and
* contains the default username of "hayden" and "josh". 
*
* This class also stores session information, so when a user logs in they are assigned a sesson key.
*
* IMPORTANT NOTE: The session information is not persistent, so when the server shuts down all session
* information will be removed.
*
* @author  Jonathan Williams - C3237808 - SENG3400 Assignment 2
* @since   18/10/2018
*/
public class UserDatabase {

    //CONSTANTS
    private static final String INVALID_REQUEST = "INVALID";
    private static final String ALPHA_NUM = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
    private static final Random RANDOM = new Random();
    private static final int KEY_LENGTH = 5;

    //The list of authenticated users, will only contains the default users of Hayden and Josh - <Username, Password>
    private static HashMap<String, String> users = new HashMap<>();

    //The list of currently active sessions / logged in users - <SessionKey, Username>
    private static HashMap<String, String> sessions = new HashMap<>();


    /**
     * Default Constructor, inits the HashMap of authenticated users to the default.
     */
    public UserDatabase() {
        users.put("hayden", "1234");
        users.put("josh", "4321");
    }




    
    /**
     * Logs a user into the application and generates a session key.
     * 
     * @param username - The username logging in.
     * @param password - The password for the user.
     * @return - A random 5 character string which is the session key. If the
     *         username or password is incorrect "INVALID" is returned.
     */
    public String login(String username, String password) {
        
        //Confirm the username exists
        if(!users.containsKey(username))
        {
            System.out.println("ERROR: The username does not exist.");
            return INVALID_REQUEST;
        }

        //Confirm the password is correct for the username
        if(!password.equals(users.get(username)))
        {
            System.out.println("ERROR: The password is incorrect");
            return INVALID_REQUEST;
        }

        //The username and password is valid, generate and set the session key
        return generateNewSessionKey(username);
    }





    /**
     * Generates a unique 5 character session key for the username passed in.
     * Will replace any existing session key if the username passed in already has
     * a session key assigned to them.
     * 
     * @param username - The username logging in and being given a session key.
     * @return - A random 5 character alpha numeric session key.
     */
    private String generateNewSessionKey(String username) {
        
        System.out.println("Generating session key.");
        
        String key = "";
        boolean isComplete = false;
        while(!isComplete)
        {
            //Generate the key
            key = "";
            for(int i = 0; i < KEY_LENGTH; i++)
                key += ALPHA_NUM.charAt(RANDOM.nextInt(ALPHA_NUM.length()));

            //Confirm the key is unique, if not unique generate a new key
            if(sessions.containsKey(key))
                continue;

            //Check to see if the user is already logged in, if so overwrite the session key
            if(sessions.containsValue(username)) 
            {
                //Find the corresponding key matching the username value and remove
                for (Map.Entry<String, String> entry : sessions.entrySet())
                {
                    if(entry.getValue().equals(username))
                    {
                        sessions.remove(entry.getKey());
                        break;
                    }
                }
                //Replace the old session key with the new session key
                sessions.put(key, username);
            }
            
            //Otherwise, add the session key to the map
            else
                sessions.put(key, username);

            isComplete = true;
        }
        System.out.println("SUCCESS: User successfully logged in. Session key: " + key);
        return key;
    }





    /**
     * Logs the users out of the application by invalidating a users session key.
     * 
     * @param key - The users session key.
     * @return - TRUE if the users successfully logged out. FALSE otherwise.
     */
    public boolean logout(String key) {

        //If the key exists then invalidate / remove the key from the Map
        if(sessions.containsKey(key))
        {
            System.out.println("SUCCESS: Successfully logged out " + sessions.remove(key));
            return true;
        }

        //Otherwise, the session key does not exist, return false.
        System.out.println("ERROR: Failed to log out user because session key does not exist.");
        return false;
    }





    /**
     * Authorises a users session key to determine if the user is logged in
     * and using a valid session key.
     * 
     * @param key - The users session key.
     * @return - TRUE if the session key is valid, FALSE otherwise.
     */
    public boolean authorise(String key) {
        if(sessions.containsKey(key))
        {
            System.out.println("SUCCESS: User is authorised.");
            return true;
        }

        System.out.println("ERROR: The user is not authorised, session key does not exist.");
        return false;
    }
}