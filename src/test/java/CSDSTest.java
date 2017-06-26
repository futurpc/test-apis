import com.liveperson.api.infra.GeneralAPI;
import java.util.Map;
import org.junit.Test;

public class CSDSTest {
    
//     @Test
     public void hello() {
         Map<String, String> domains = GeneralAPI.getDomains("https://adminlogin.liveperson.net", "61326154");
         System.out.println(domains);
     }
}
