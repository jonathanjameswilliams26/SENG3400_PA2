import localhost.identity.Login_jws.*;
import localhost.identity.Authorisation_jws.*;

public class AdminClient
{
    public static void main(String[] args)
    {
        try
        {
            //Getting the login JWS
            LoginService service = new LoginServiceLocator();
            Login loginInterface = service.getLogin();
            String key = loginInterface.login("josh", "4321");
            System.out.println("The key is: " + key);

            //Getting the Authorisation JWS
            AuthorisationService authService = new AuthorisationServiceLocator();
            Authorisation authInterface = authService.getAuthorisation();
            if(authInterface.authorise(key))
                System.out.println("Authorisation successful");
            else
                System.out.println("authorissation FAILDED");
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }
}