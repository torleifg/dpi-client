package no.unit.dpi;

import com.google.gson.Gson;
import com.google.gson.JsonParseException;
import no.digdir.dpi.model.digital.*;
import org.apache.http.*;
import org.apache.http.client.HttpClient;
import org.apache.http.client.HttpResponseException;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.message.BasicHttpResponse;
import org.apache.http.message.BasicStatusLine;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class IntegrationpointTest {
    Integrationpoint integrationpoint;

    @Mock
    HttpClient client;

    @BeforeEach
    void setUp() {
        integrationpoint = new Integrationpoint(client);
    }

    @Test
    void sendMultipartMessageShouldReturnStandardBusinessDocumentWhenValidResponseStatus() throws IOException {
        var response = new BasicHttpResponse(new BasicStatusLine(HttpVersion.HTTP_1_1, 200, null));
        response.setEntity(new StringEntity(new Gson().toJson(new StandardBusinessDocument()), ContentType.APPLICATION_JSON));

        when(client.execute(any())).thenReturn(response);

        var entity = integrationpoint.sendMultipartMessage(new StandardBusinessDocument(), new byte[0]);

        assertTrue(entity.isPresent());
    }

    @Test
    void sendMultipartMessageShouldThrowExceptionWhenInvalidResponseEntity() throws IOException {
        var response = new BasicHttpResponse(new BasicStatusLine(HttpVersion.HTTP_1_1, 200, null));
        response.setEntity(new StringEntity(new Gson().toJson(""), ContentType.APPLICATION_JSON));

        when(client.execute(any())).thenReturn(response);

        assertThrows(JsonParseException.class, () -> integrationpoint.sendMultipartMessage(new StandardBusinessDocument(), new byte[0]));
    }

    @Test
    void sendMultipartMessageShouldThrowExceptionWhenInvalidResponseStatus() throws IOException {
        var response = new BasicHttpResponse(new BasicStatusLine(HttpVersion.HTTP_1_1, 500, null));
        response.setEntity(new StringEntity(new Gson().toJson(new StandardBusinessDocument()), ContentType.APPLICATION_JSON));

        when(client.execute(any())).thenThrow(HttpResponseException.class);

        assertThrows(HttpResponseException.class, () -> integrationpoint.sendMultipartMessage(new StandardBusinessDocument(), new byte[0]));
    }

    @Test
    void getConversationByMessageIdShouldReturnConversationWhenValidResponseStatus() throws IOException {
        var response = new BasicHttpResponse(new BasicStatusLine(HttpVersion.HTTP_1_1, 200, null));
        response.setEntity(new StringEntity(new Gson().toJson(new Conversation()), ContentType.APPLICATION_JSON));

        when(client.execute(any())).thenReturn(response);

        var entity = integrationpoint.getConversationByMessageId("messageId");

        assertTrue(entity.isPresent());
    }

    @Test
    void getConversationByMessageIdShouldThrowExceptionWhenInvalidResponseEntity() throws IOException {
        var response = new BasicHttpResponse(new BasicStatusLine(HttpVersion.HTTP_1_1, 200, null));
        response.setEntity(new StringEntity(new Gson().toJson(""), ContentType.APPLICATION_JSON));

        when(client.execute(any())).thenReturn(response);

        assertThrows(JsonParseException.class, () -> integrationpoint.getConversationByMessageId("messageId"));
    }

    @Test
    void getConversationByMessageIdShouldThrowExceptionWhenInvalidResponseStatus() throws IOException {
        var response = new BasicHttpResponse(new BasicStatusLine(HttpVersion.HTTP_1_1, 500, null));
        response.setEntity(new StringEntity(new Gson().toJson(new Conversation()), ContentType.APPLICATION_JSON));

        when(client.execute(any())).thenThrow(HttpResponseException.class);

        assertThrows(HttpResponseException.class, () -> integrationpoint.getConversationByMessageId("messageId"));
    }

    @Test
    void deleteConversationByMessageIdShouldReturnConversationWhenValidResponseStatus() throws IOException {
        var response = new BasicHttpResponse(new BasicStatusLine(HttpVersion.HTTP_1_1, 200, null));

        when(client.execute(any())).thenReturn(response);

        assertTrue(integrationpoint.deleteConversationByMessageId("messageId"));
    }

    @Test
    void deleteConversationByMessageIdShouldThrowExceptionWhenInvalidResponseStatus() throws IOException {
        var response = new BasicHttpResponse(new BasicStatusLine(HttpVersion.HTTP_1_1, 500, null));

        when(client.execute(any())).thenReturn(response);

        assertThrows(HttpResponseException.class, () -> integrationpoint.deleteConversationByMessageId("messageId"));
    }
}