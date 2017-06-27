import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.liveperson.api.AgentVep;
import com.liveperson.api.infra.GeneralAPI;
import com.liveperson.api.infra.ServiceName;
import org.junit.Test;
import retrofit2.Call;
import retrofit2.Response;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Path;

import java.io.IOException;
import java.util.List;
import java.util.Map;

/**
 * @author assafa
 */
public class SiteSettingTest {
    private static ObjectMapper OM = new ObjectMapper();

    @ServiceName("accountConfigReadOnly")
    public interface SiteSetting {
        @GET("api/account/{account}/configuration/setting/properties?v=3.0&groups=loginSession")
        Call<List<JsonNode>> listSetting(@Path("account") String account, @Header("Authorization") String bearer);
    }

    @Test
    public void hello() throws IOException {
        ObjectNode node = OM.createObjectNode().put("username", "assafa@liveperson.com").put("password", "Assaf1000");
        Map<String, String> domains = GeneralAPI.getDomains("https://hc1n.dev.lprnd.net", LP_ACCOUNT);
        final String bearer = getBearer(node, domains);
        SiteSetting siteSetting = GeneralAPI.apiEndpoint(domains, SiteSetting.class);
        Response<List<JsonNode>> execute = siteSetting.listSetting(LP_ACCOUNT, "Bearer " + bearer).execute();
        List<JsonNode> setting = execute.body();
        System.out.println(setting);
    }

    private String getBearer(ObjectNode node, Map<String, String> domains) throws IOException {
        return GeneralAPI.apiEndpoint(domains, AgentVep.class)
                .login(LP_ACCOUNT, node)
                .execute().body()
                .get("bearer").asText();
    }

    private static final String LP_ACCOUNT = "le80213408";
}
