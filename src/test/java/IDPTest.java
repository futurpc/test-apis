
import com.fasterxml.jackson.databind.JsonNode;
import com.liveperson.api.Idp;
import com.liveperson.api.infra.GeneralAPI;
import java.io.IOException;
import java.util.Map;
import org.junit.Test;

public class IDPTest {

    @Test
    public void hello() throws IOException {
        Map<String, String> domains = GeneralAPI.getDomains("https://adminlogin.liveperson.net", LP_ACCOUNT);
        final JsonNode body = GeneralAPI.apiEndpoint(domains, Idp.class)
                .signup(LP_ACCOUNT)
                .execute().body();
        System.out.println(body);
    }
    private static final String LP_ACCOUNT = "61326154";
}
