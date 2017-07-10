
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.liveperson.api.AgentVep;
import com.liveperson.api.infra.GeneralAPI;
import org.junit.Test;
import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.converter.jackson.JacksonConverterFactory;
import retrofit2.http.*;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by grigoryr on 7/10/2017.
 */
public class EngHistoryTest {

    public static final ObjectNode OBJECT_NODE = new ObjectMapper().createObjectNode();

    public interface EngHistApiInterface {


        @Headers({
                "Accept:application/json",
                "Content-Type:application/json"
        })
        @POST("/interaction_history/api/account/{account}/interactions/search?limit=50&offset=0")
        Call<JsonNode> getEngagements(@Path("account") String account, @Body HashMap<String, Object> body, @Header("Authorization") String bearer);

    }

    public static Call<JsonNode> fetch(final String baseUrl, String account, String userName, String password, HashMap<String, Object>body) throws IOException {
        Map<String, String> domains = GeneralAPI.getDomains("https://adminlogin.liveperson.net", account);
        final JsonNode bearerBody = GeneralAPI.apiEndpoint(domains, AgentVep.class)
                .login(account, OBJECT_NODE.put("username", userName).put("password", password))
                .execute().body();

        String bearer = "Bearer " + bearerBody.get("bearer").asText();
        return new Retrofit.Builder()
                .baseUrl(baseUrl)
                .addConverterFactory(JacksonConverterFactory.create())
                .build().create(EngHistApiInterface.class).getEngagements(account, body, bearer);
    }


    @Test
    public void getInteractions() throws IOException {
        HashMap<String, Object> body = new HashMap<>();
        HashMap<String, Object> start = new HashMap<>();
        start.put("from", 1497948147507L);
        start.put("to", 1498552971273L);
        body.put("start", start);

        String AUTOMATION_ACCOUNT = "37148534";
        String userName = "grigoryr@liveperson.com";
        String password = "lp123456";
        System.out.println(fetch("https://va.enghist.liveperson.net", AUTOMATION_ACCOUNT, userName, password, body).execute().body());
    }

}
