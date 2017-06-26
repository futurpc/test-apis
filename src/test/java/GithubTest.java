
import com.fasterxml.jackson.databind.JsonNode;
import java.io.IOException;
import java.util.List;
import org.junit.Test;
import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.converter.jackson.JacksonConverterFactory;
import retrofit2.http.GET;
import retrofit2.http.Path;

public class GithubTest {

    public interface GitHubService {
        @GET("users/{user}/repos")
        Call<List<JsonNode>> listRepos(@Path("user") String user);
    }

//    @Test
    public void hello() throws IOException {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://api.github.com/")
                .addConverterFactory(JacksonConverterFactory.create())
                .build();

        GitHubService service = retrofit.create(GitHubService.class);
        final List<JsonNode> body = service.listRepos("eitan101").execute().body();
        System.out.println(body);
    }
}
