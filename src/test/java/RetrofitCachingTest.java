/*
 * Copyright (C) 2013 Square, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import java.io.File;
import java.io.IOException;
import java.util.UUID;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import okhttp3.Cache;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import static org.hamcrest.CoreMatchers.*;
import org.junit.Assert;
import static org.junit.Assert.assertThat;
import org.junit.Test;
import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.converter.jackson.JacksonConverterFactory;
import retrofit2.http.GET;
import retrofit2.http.Path;

public class RetrofitCachingTest {
    interface SodaService {
        @GET("/{brand}")
        Call<Object> cola(@Path("brand") String brand);
    }

    @Test
    public void hello() throws Exception {
        // Create a web server. MockWebServer is good. Use it.
        MockWebServer mockWebServer = new MockWebServer();
        mockWebServer.start(53513);
        System.out.println(mockWebServer.url("/").toString());

        final Cache cache = new Cache(new File(System.getProperty("java.io.tmpdir"), UUID.randomUUID().toString()), 1024);
        final OkHttpClient.Builder okBuilder = new OkHttpClient.Builder().cache(cache);
        final Retrofit.Builder retroBuilder = new Retrofit.Builder()                
                .callbackExecutor(Executors.newCachedThreadPool())
                .addConverterFactory(JacksonConverterFactory.create())
                .baseUrl(mockWebServer.url("/").toString());

        SodaService sodaService = retroBuilder
                .client(okBuilder.build())
                .build().create(SodaService.class);

        // /pepsi hits the web server and returns a response that will be fully cached for 60 seconds.
        mockWebServer.enqueue(new MockResponse()
                .addHeader("Cache-Control: max-age=60")
                .setBody("\"You got the right one, baby\""));
        assertThat(sodaService.cola("pepsi").execute().body(), is("You got the right one, baby"));
        assertThat(cache.requestCount(), is(1));
        assertThat(cache.networkCount(), is(1));
        assertThat(cache.hitCount(), is(0));

        // /coke hits the web server and returns a response that will be conditionally cached.
        mockWebServer.enqueue(new MockResponse()
                .addHeader("ETag: v1")
                .setBody("\"Always Coca-Cola\""));
        assertThat(sodaService.cola("coke").execute().body(), is("Always Coca-Cola"));
        assertThat(cache.requestCount(), is(2));
        assertThat(cache.networkCount(), is(2));
        assertThat(cache.hitCount(), is(0));

        // /coke validates the cached response. The server says the cached version is still good.
        mockWebServer.enqueue(new MockResponse()
                .setResponseCode(304));
        assertThat(sodaService.cola("coke").execute().body(), is("Always Coca-Cola"));
        assertThat(cache.requestCount(), is(3));
        assertThat(cache.networkCount(), is(3));
        assertThat(cache.hitCount(), is(1));

        // ***** Server is offline *****
        mockWebServer.shutdown();

        // /pepsi returns a response from the cache.
        assertThat(sodaService.cola("pepsi").execute().body(), is("You got the right one, baby"));
        assertThat(cache.requestCount(), is(4));
        assertThat(cache.networkCount(), is(3));
        assertThat(cache.hitCount(), is(2));

        // /coke is forced to use cache without validating the Etag revision
        SodaService sodaServiceCache = retroBuilder
                .client(okBuilder.addInterceptor(chain -> chain.proceed(chain.request().newBuilder().addHeader("Cache-Control", "only-if-cached, max-stale=" + Integer.MAX_VALUE).build())).build())
                .build().create(SodaService.class);

        assertThat(sodaServiceCache.cola("coke").execute().body(), is("Always Coca-Cola"));
    }
}
