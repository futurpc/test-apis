import com.fasterxml.jackson.databind.JsonNode;
import org.junit.Test;
import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.converter.jackson.JacksonConverterFactory;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;

import java.io.IOException;

/**
 * Created by maytals on 6/27/17.
 */
public class MessagingAvailabilityTest {

    public interface MessagingAvailabilityService {
        @GET("messaging-availability/api/account/{account}/agents/online?v=1")
        Call<JsonNode> agentOnline(@Path("account") String user ,@Query("skills") String skills);
    }

    @Test
    public void agentOnline() throws IOException {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("http://qtvr-wto0003:8080/")
                .addConverterFactory(JacksonConverterFactory.create())
                .build();

        MessagingAvailabilityService service = retrofit.create(MessagingAvailabilityService.class);
        final JsonNode body = service.agentOnline("le58450414", "708035012,748097612").execute().body();
        System.out.println(body);
    }
}
