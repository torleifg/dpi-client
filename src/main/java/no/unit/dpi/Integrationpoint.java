package no.unit.dpi;

import com.google.gson.Gson;
import no.digdir.dpi.model.digital.*;
import org.apache.http.HttpStatus;
import org.apache.http.ParseException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.HttpResponseException;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.entity.mime.content.ByteArrayBody;
import org.apache.http.entity.mime.content.StringBody;
import org.apache.http.util.EntityUtils;

import java.io.IOException;
import java.util.Optional;

public class Integrationpoint {
    private final HttpClient client;

    public Integrationpoint(HttpClient client) {
        this.client = client;
    }

    public Optional<StandardBusinessDocument> sendMultipartMessage(StandardBusinessDocument sbd, byte[] attachment) throws IOException, ParseException {
        var requestEntity = MultipartEntityBuilder.create()
                .addPart("sbd", new StringBody(new Gson().toJson(sbd), ContentType.APPLICATION_JSON))
                .addPart("brev", new ByteArrayBody(attachment, "test.pdf"))
                .build();

        var post = new HttpPost("http://integrasjonspunkt-test.aws.unit.no:10101/api/messages/out/multipart");
        post.setEntity(requestEntity);

        var response = client.execute(post);

        var statusLine = response.getStatusLine();
        if (statusLine.getStatusCode() != HttpStatus.SC_OK) {
            throw new HttpResponseException(statusLine.getStatusCode(), statusLine.getReasonPhrase());
        }

        var responseEntity = new Gson()
                .fromJson(EntityUtils.toString(response.getEntity()), StandardBusinessDocument.class);

        return Optional.ofNullable(responseEntity);
    }

    public Optional<Conversation> getConversationByMessageId(String messageId) throws IOException, ParseException {
        var get = new HttpGet("http://integrasjonspunkt-test.aws.unit.no:10101/api/conversations/messageId/".concat(messageId));

        var response = client.execute(get);

        var statusLine = response.getStatusLine();
        if (statusLine.getStatusCode() != HttpStatus.SC_OK) {
            throw new HttpResponseException(statusLine.getStatusCode(), statusLine.getReasonPhrase());
        }

        var responseEntity = new Gson().fromJson(EntityUtils.toString(response.getEntity()), Conversation.class);

        return Optional.ofNullable(responseEntity);
    }

    public boolean deleteConversationByMessageId(String messageId) throws IOException {
        var delete = new HttpDelete("http://integrasjonspunkt-test.aws.unit.no:10101/api/conversations/messageId/".concat(messageId));

        var response = client.execute(delete);

        var statusLine = response.getStatusLine();
        if (statusLine.getStatusCode() != HttpStatus.SC_OK) {
            throw new HttpResponseException(statusLine.getStatusCode(), statusLine.getReasonPhrase());
        }

        return true;
    }
}