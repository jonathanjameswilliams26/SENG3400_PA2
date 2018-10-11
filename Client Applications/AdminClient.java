import localhost.identity.Login_jws.*;
public class AdminClient
{
    public static void main(String[] args)
    {
        try
        {
            LoginService service = new LoginServiceLocator();
            Login loginInterface = service.getLogin();
            System.out.println(loginInterface.login("josh", "4321"));
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }
}