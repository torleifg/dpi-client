package no.unit.dpi;

import no.digdir.dpi.model.digital.*;

import java.time.LocalDate;
import java.util.List;


public class SbdGenerator {

    public static StandardBusinessDocument create(String sender, String receiver) {
        return new StandardBusinessDocument()
                .withStandardBusinessDocumentHeader(createHeader(sender, receiver))
                .withDigital(createPayload());
    }

    private static StandardBusinessDocumentHeader createHeader(String sender, String receiver) {
        return new StandardBusinessDocumentHeader()
                .withHeaderVersion("1.0")
                .withSender(List.of(new Actor()
                        .withIdentifier(new Identifier()
                                .withAuthority("iso6523-actorid-upis")
                                .withValue("0192:" + sender))))
                .withReceiver(List.of(new Actor()
                        .withIdentifier(new Identifier()
                                .withAuthority("iso6523-actorid-upis")
                                .withValue(receiver))))
                .withDocumentIdentification(new DocumentIdentification()
                        .withStandard("urn:no:difi:digitalpost:xsd:digital::digital")
                        .withTypeVersion("1.0")
                        .withType("digital"))
                .withBusinessScope(new BusinessScope()
                        .withScope(List.of(new Scope()
                                .withType("ConversationId")
                                .withIdentifier("urn:no:difi:profile:digitalpost:vedtak:ver1.0"))));
    }

    private static Digital createPayload() {
        return new Digital()
                .withHoveddokument("test.pdf")
                .withSikkerhetsnivaa(3)
                .withTittel("FS")
                .withSpraak("no")
                .withDigitalPostInfo(new DigitalPostInfo()
                        .withVirkningsdato(LocalDate.now().toString())
                        .withAapningskvittering(false));
    }
}