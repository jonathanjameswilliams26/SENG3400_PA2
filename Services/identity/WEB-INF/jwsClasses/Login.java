public class Login {

    private static LoginLogic logic = new LoginLogic();

    /**
     * The endpoint which logs a user into the application.
     * 
     * @param username - The username logging in.
     * @param password - The password for the user.
     * @return - A random 5 character string which is the session key. If the
     *         username or password is incorrect "INVALID" is returned.
     */
    public String login(String username, String password) {
        return logic.login(username, password);
    }
}