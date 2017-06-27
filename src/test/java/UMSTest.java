import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.liveperson.api.Idp;
import com.liveperson.api.MessagingConsumer;
import com.liveperson.api.infra.GeneralAPI;
import com.liveperson.api.infra.ws.WebsocketService;

import java.util.Map;

import static com.google.common.collect.ImmutableMap.of;

public class UMSTest {

//    @Test
    public void hello() throws Exception {
        Map<String, String> domains = GeneralAPI.getDomains("https://adminlogin.liveperson.net", LP_ACCOUNT);
        String jwt = GeneralAPI.apiEndpoint(domains, Idp.class)
                .signup(LP_ACCOUNT)
                .execute().body().path("jwt").asText();
        WebsocketService<MessagingConsumer> consumer = WebsocketService.create(MessagingConsumer.class,
                of("protocol", "wss", "account", LP_ACCOUNT), domains);

        consumer.methods().initConnection(OM.createObjectNode().put("jwt", jwt)).get();
        String convId = consumer.methods().consumerRequestConversation().get().path("body").path("conversationId").asText();
        Thread.sleep(100);
        consumer.methods().publishEvent(publishTextBody(convId, "hello"));
        Thread.sleep(5000);
        consumer.methods().updateConversationField(closeConvBody(convId)).get();
    }

    private static final String LP_ACCOUNT = "61326154";
    static ObjectMapper OM = new ObjectMapper();

    static ObjectNode publishTextBody(String convId, String text) {
        final ObjectNode body = OM.createObjectNode();
        body.put("dialogId", convId)
                .putObject("event")
                .put("type", "ContentEvent")
                .put("contentType", "text/plain")
                .put("message", text);
        return body;
    }

    static ObjectNode closeConvBody(String convId) {
        final ObjectNode body = OM.createObjectNode();
        body.put("conversationId", convId)
                .putObject("conversationField")
                .put("field", "ConversationStateField")
                .put("conversationState", "CLOSE");
        return body;
    }
}
