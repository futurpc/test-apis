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
import retrofit2.http.Query;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import static junit.framework.TestCase.fail;

/**
 * @author assafa
 */
public class SiteSettingTest {

    private static final ObjectMapper OM = new ObjectMapper();
    private static final ObjectNode CREDENTIALS = OM.createObjectNode().put("username", "assafa@liveperson.com").put("password", "Assaf1000");
    private static final String CSDS = "https://hc1n.dev.lprnd.net";
    private static final String LP_ACCOUNT = "le80213408";
    private static final Map<String, String> DOMAINS = GeneralAPI.getDomains(CSDS, LP_ACCOUNT);
    private static final String BEARER = getBearer(CREDENTIALS, DOMAINS);

    @ServiceName("accountConfigReadOnly")
    public interface SiteSetting {

        @GET("api/account/{account}/configuration/setting/properties?v=3.0")
        Call<List<JsonNode>> all(@Path("account") String account, @Header("Authorization") String bearer);

        @GET("api/account/{account}/configuration/setting/properties?v=3.0")
        Call<List<JsonNode>> groups(@Path("account") String account, @Header("Authorization") String bearer, @Query("groups") String groups);

        @GET("api/account/{account}/configuration/setting/properties/{propertyId}?v=3.0")
        Call<JsonNode> id(@Path("account") String account, @Header("Authorization") String bearer, @Path("propertyId") String propertyId);
    }

    @Test
    public void all() throws IOException {
        SiteSetting siteSetting = GeneralAPI.apiEndpoint(DOMAINS, SiteSetting.class);
        Response<List<JsonNode>> execute = siteSetting.all(LP_ACCOUNT, "Bearer " + BEARER).execute();
        List<JsonNode> setting = execute.body();
        System.out.println(setting);
    }

    @Test
    public void groups() throws IOException {
        SiteSetting siteSetting = GeneralAPI.apiEndpoint(DOMAINS, SiteSetting.class);
        Response<List<JsonNode>> execute = siteSetting.groups(LP_ACCOUNT, "Bearer " + BEARER, "cobrowse").execute();
        List<JsonNode> setting = execute.body();
        System.out.println(setting);
    }

    @Test
    public void id() throws IOException {
        SiteSetting siteSetting = GeneralAPI.apiEndpoint(DOMAINS, SiteSetting.class);
        Response<JsonNode> execute = siteSetting.id(LP_ACCOUNT, "Bearer " + BEARER, "le.campaign.capping").execute();
        JsonNode setting = execute.body();
        System.out.println(setting);
    }

    private static String getBearer(ObjectNode node, Map<String, String> domains) {
        try {
            return GeneralAPI.apiEndpoint(domains, AgentVep.class)
                    .login(LP_ACCOUNT, node)
                    .execute().body()
                    .get("bearer").asText();
        } catch (IOException e) {
            fail("failed getting bearer");
        }
        return null;
    }
}
