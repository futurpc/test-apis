import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.liveperson.api.AgentVep;
import com.liveperson.api.infra.GeneralAPI;
import com.liveperson.api.infra.ServiceName;
import org.junit.Test;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Path;

import java.io.IOException;
import java.util.Map;

/**
 * Created by litoms on 6/27/17.
 */
public class UmsStatisticsRestTest {
    private static final ObjectMapper OM = new ObjectMapper();
    public String getBearer(String account,Map<String, String> domains,String user,String pass) throws IOException {
        JsonNode agentLoginInfo = GeneralAPI.apiEndpoint(domains, AgentVep.class)
                .login(account, OM.createObjectNode()
                        .put("username", user)
                        .put("password", pass))
                .execute().body();
        String agentBearer = agentLoginInfo.path("bearer").asText();
        return agentBearer;

    }

    @ServiceName("asyncMessagingEnt")
    public interface UmsStatistics {



        @GET("api/messaging/rest/reporting/brands/{account}/msgstatistics?v=1")

        Call<JsonNode> getStatistics(@Path("account") String account,@Header("Authorization") String bearer);

    }

    @Test
    public void test() throws IOException {
        final String account = "le52755052";
        Map<String, String> domains = GeneralAPI.getDomains("https://hc1n.dev.lprnd.net", account);
        String bearer = getBearer(account,domains,"litoms@liveperson.com","lp123456");
        System.out.println(bearer);
        final JsonNode body = GeneralAPI.apiEndpoint(domains, UmsStatistics.class)
                .getStatistics(account,"Bearer " +bearer)
                .execute().body();
        System.out.println(body);



    }
}
