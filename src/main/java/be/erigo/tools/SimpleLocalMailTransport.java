package be.erigo.tools;

import jakarta.mail.*;
import jakarta.mail.internet.MimeMessage;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.logging.Logger;

public class SimpleLocalMailTransport extends Transport {
    private static final Logger logger = Logger.getLogger(SimpleLocalMailTransport.class.getName());
    private final String outputDir;
    private final boolean writeHeaders;

    public SimpleLocalMailTransport(Session session, URLName urlname) {
        super(session, urlname);
        this.outputDir = session.getProperty("mail.slmtp.directory");
        this.writeHeaders = Boolean.parseBoolean(session.getProperty("mail.slmtp.writeHeaders"));
        logger.info("👍 SimpleLocalMailTransport setup");
    }

    @Override
    public void sendMessage(Message message, Address[] addresses) throws MessagingException {
        try {
            File dir = new File(outputDir);
            if (!dir.exists()) dir.mkdirs();

            String filename = "mail-" + System.currentTimeMillis() + ".eml";
            File outFile = new File(dir, filename);

            try (FileOutputStream fos = new FileOutputStream(outFile)) {
                if (writeHeaders && message instanceof MimeMessage) {
                    ((MimeMessage) message).writeTo(fos);
                } else {
                    fos.write(message.getContent().toString().getBytes());
                }
            }

            logger.fine("[MockTransport] Saved email to: " + outFile.getAbsolutePath());
        } catch (IOException | MessagingException e) {
            logger.severe("Failed to write email to file: " + e.getMessage());
            throw new MessagingException("Failed to write email to file", e);
        }
    }

    @Override
    public void connect() throws MessagingException {
        // No-op
    }

    @Override
    public void close() throws MessagingException {
        // No-op
    }
}
