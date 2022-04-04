package no.unit.dpi;

import no.digdir.dpi.model.digital.*;
import org.apache.http.impl.client.HttpClients;

import java.io.*;
import java.util.Scanner;
import java.util.UUID;
import java.util.concurrent.*;

public class App {

    public static void main(String[] args) throws Exception {
        var executorService = Executors.newWorkStealingPool();

        var integrationpoint = new Integrationpoint(HttpClients.createDefault());

        var sbd = SbdGenerator.create("919477822", "07126400459");

        byte[] attachment;
        try (FileInputStream fileInputStream = new FileInputStream("test.pdf")) {
            attachment = fileInputStream.readAllBytes();
        }

        var scanner = new Scanner(System.in);

        while (scanner.hasNext()) {
            var input = scanner.next();

            if (input.equals("send")) {
                var messageId = integrationpoint.sendMultipartMessage(sbd, attachment)
                        .map(StandardBusinessDocument::getStandardBusinessDocumentHeader)
                        .map(StandardBusinessDocumentHeader::getDocumentIdentification)
                        .map(DocumentIdentification::getInstanceIdentifier)
                        .map(UUID::toString);

                messageId.ifPresent(id -> executorService.submit(new Task(integrationpoint, id, 5, 10)));

            } else if (input.equals("pool")) {
                System.out.println("Pool size is now " + ((ForkJoinPool) executorService).getActiveThreadCount() +
                        " of " + ((ForkJoinPool) executorService).getParallelism());
            }
        }
    }
}