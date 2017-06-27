import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.liveperson.api.AgentVep;
import com.liveperson.api.infra.GeneralAPI;
import com.liveperson.api.infra.ServiceName;
import org.junit.Test;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Path;

import java.io.IOException;
import java.util.List;
import java.util.Map;

/**
 * @author nirl
 * @since {version}
 */
public class CategoriesTest {

    public static final ObjectNode OBJECT_NODE = new ObjectMapper().createObjectNode();

    @ServiceName("accountConfigReadOnly")
    public interface Categories {
        @GET("le-categories/accountConfig/api/account/{accountId}/configuration/le-categories/categories/?v=2&select=id,name")
        Call<List<JsonNode>> allCategories(@Path("accountId") String accountId, @Header("Authorization") String bearer);
    }

    @Test
    public void get() throws IOException {
        Map<String, String> domains = GeneralAPI.getDomains("https://hc1n.dev.lprnd.net", LP_ACCOUNT);
        final JsonNode body = GeneralAPI.apiEndpoint(domains, AgentVep.class)
                .login(LP_ACCOUNT, OBJECT_NODE.put("username", "assafa@liveperson.com").put("password", "Assaf1000"))
                .execute().body();

        String bearer = body.get("bearer").asText();

        List<JsonNode> categories = GeneralAPI.apiEndpoint(domains, Categories.class)
                .allCategories(LP_ACCOUNT, String.format("Bearer %s", bearer))
                .execute()
                .body();
        System.out.println(categories);
    }

    private static final String LP_ACCOUNT = "le80213408";
}
