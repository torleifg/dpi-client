package no.unit.dpi;

import no.digdir.dpi.model.digital.Conversation;
import no.digdir.dpi.model.digital.MessageStatus;
import org.apache.http.HttpStatus;
import org.apache.http.ParseException;
import org.apache.http.client.HttpResponseException;

import java.io.IOException;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

public class Task implements Runnable {
    private final Integrationpoint integrationpoint;

    private final String messageId;
    private final int initial;
    private final int fixed;

    public Task(Integrationpoint ip, String messageId, int initial, int fixed) {
        this.integrationpoint = ip;
        this.messageId = messageId;
        this.initial = initial;
        this.fixed = fixed;
    }

    @Override
    public void run() {
        try {
            TimeUnit.SECONDS.sleep(initial);

            while (true) {
                Optional<Conversation> conversation;
                try {
                    conversation = integrationpoint.getConversationByMessageId(messageId);
                } catch (IOException | ParseException e) {
                    if (e instanceof HttpResponseException) {
                        if (((HttpResponseException) e).getStatusCode() == HttpStatus.SC_NOT_FOUND) {
                            /*
                            handle
                             */
                            break;
                        }
                    }
                    TimeUnit.SECONDS.sleep(fixed);
                    continue;
                }

                var status = conversation.map(Conversation::getMessageStatuses)
                        .orElseGet(List::of)
                        .stream()
                        .reduce(this::getLastUpdated)
                        .map(MessageStatus::getStatus)
                        .orElse(MessageStatus.Status.OPPRETTET);

                if (status.equals(MessageStatus.Status.FEIL) || status.equals(MessageStatus.Status.LEVETID_UTLOPT)) {
                    /*
                    handle
                     */
                    break;
                }

                if (status.equals(MessageStatus.Status.LEVERT)) {
                    try {
                        integrationpoint.deleteConversationByMessageId(messageId);
                        break;
                    } catch (IOException ignored) {
                    }
                }

                TimeUnit.SECONDS.sleep(fixed);
            }
        } catch (InterruptedException e) {
            throw new IllegalStateException("Task interrupted", e);
        }
    }

    private MessageStatus getLastUpdated(MessageStatus first, MessageStatus second) {
        if (OffsetDateTime.parse(first.getLastUpdate()).compareTo(OffsetDateTime.parse(second.getLastUpdate())) > 0) {
            return first;
        } else {
            return second;
        }
    }
}