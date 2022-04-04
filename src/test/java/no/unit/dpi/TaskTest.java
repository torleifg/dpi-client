package no.unit.dpi;

import no.digdir.dpi.model.digital.Conversation;
import no.digdir.dpi.model.digital.MessageStatus;
import org.apache.http.HttpStatus;
import org.apache.http.client.HttpResponseException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TaskTest {
    Task task;

    @Mock
    Integrationpoint integrationpoint;

    @BeforeEach
    void setUp() {
        task = new Task(integrationpoint, "messageId", 0, 1);
    }

    @Test
    void runShouldCompleteWhenStatusEqualsLevert() throws IOException {
        var conversation = new Conversation().withMessageStatuses(List.of(new MessageStatus()
                .withLastUpdate("2022-02-15T13:28:57.669+01:00")
                .withStatus(MessageStatus.Status.LEVERT), new MessageStatus()
                .withLastUpdate("2022-02-15T13:28:56.386+01:00")
                .withStatus(MessageStatus.Status.OPPRETTET)));

        when(integrationpoint.getConversationByMessageId("messageId"))
                .thenReturn(Optional.of(conversation));

        when(integrationpoint.deleteConversationByMessageId("messageId"))
                .thenReturn(true);

        task.run();
    }

    @Test
    void runShouldCompleteWhenStatusEqualsLevertAndOptionalIsEmpty() throws IOException {
        var conversation = new Conversation().withMessageStatuses(List.of(new MessageStatus().withStatus(MessageStatus.Status.LEVERT)));

        when(integrationpoint.getConversationByMessageId("messageId"))
                .thenReturn(Optional.empty())
                .thenReturn(Optional.of(conversation));

        when(integrationpoint.deleteConversationByMessageId("messageId"))
                .thenReturn(true);

        task.run();
    }

    @Test
    void runShouldCompleteWhenStatusEqualsLevertAndExceptionIsThrownOnGet() throws IOException {
        var conversation = new Conversation().withMessageStatuses(List.of(new MessageStatus().withStatus(MessageStatus.Status.LEVERT)));

        when(integrationpoint.getConversationByMessageId("messageId"))
                .thenThrow(new HttpResponseException(HttpStatus.SC_SERVICE_UNAVAILABLE, null))
                .thenReturn(Optional.of(conversation));

        when(integrationpoint.deleteConversationByMessageId("messageId"))
                .thenReturn(true);

        task.run();
    }

    @Test
    void runShouldCompleteWhenStatusEqualsLevertAndExceptionIsThrownOnDelete() throws IOException {
        var conversation = new Conversation().withMessageStatuses(List.of(new MessageStatus().withStatus(MessageStatus.Status.LEVERT)));

        when(integrationpoint.getConversationByMessageId("messageId"))
                .thenReturn(Optional.of(conversation));

        when(integrationpoint.deleteConversationByMessageId("messageId"))
                .thenThrow(new HttpResponseException(HttpStatus.SC_SERVICE_UNAVAILABLE, null))
                .thenReturn(true);

        task.run();
    }

    @Test
    void runShouldCompletePartiallyWhenResponseStatusEqualsNotFound() throws IOException {
        when(integrationpoint.getConversationByMessageId("messageId"))
                .thenThrow(new HttpResponseException(HttpStatus.SC_NOT_FOUND, null));

        task.run();
    }

    @Test
    void runShouldCompletePartiallyWhenStatusEqualsLevetidUtlopt() throws IOException {
        var conversation = new Conversation().withMessageStatuses(List.of(new MessageStatus().withStatus(MessageStatus.Status.LEVETID_UTLOPT)));

        when(integrationpoint.getConversationByMessageId("messageId"))
                .thenReturn(Optional.of(conversation));

        task.run();
    }

    @Test
    void runShouldCompletePartiallyWhenStatusEqualsFeil() throws IOException {
        var conversation = new Conversation().withMessageStatuses(List.of(new MessageStatus().withStatus(MessageStatus.Status.FEIL)));

        when(integrationpoint.getConversationByMessageId("messageId"))
                .thenReturn(Optional.of(conversation));

        task.run();
    }
}
