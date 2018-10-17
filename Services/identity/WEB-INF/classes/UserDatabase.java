import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Random;

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
            return INVALID_REQUEST;

        //Confirm the password is correct for the username
        if(!password.equals(users.get(username)))
            return INVALID_REQUEST;

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
            sessions.remove(key);
            return true;
        }

        //Otherwise, the session key does not exist, return false.
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
            return true;
        return false;
    }
}