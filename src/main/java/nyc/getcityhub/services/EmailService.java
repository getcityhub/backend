package nyc.getcityhub.services;

import com.amazonaws.regions.Region;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.simpleemail.AmazonSimpleEmailServiceClient;
import com.amazonaws.services.simpleemail.model.*;
import freemarker.template.Template;
import freemarker.template.TemplateException;
import nyc.getcityhub.Main;
import nyc.getcityhub.models.Email;

import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;

import static nyc.getcityhub.Constants.FROM_EMAIL;

/**
 * Created by jackcook on 3/15/17.
 */
public class EmailService {

    public static void sendEmail(Email email, String destinationEmail) {
        Runnable runnable = () -> {
            try {
                String[] lines = new String(Files.readAllBytes(Paths.get("emails/" + email.getFileName()))).split("\n");

                HashMap<String, String> data = new HashMap<>();
                boolean readingData = false;
                int i = 0;

                for (String line : lines) {
                    i += 1;

                    if (line.matches("\\[(.*){2}-(.*){2,4}\\]")) {
                        if (line.replace("[", "").replace("]", "").equals(email.getLanguage().getId())) {
                            readingData = true;
                        }

                        continue;
                    } else if (line.length() == 0) {
                        if (readingData) {
                            break;
                        } else {
                            continue;
                        }
                    } else {
                        if (!readingData) continue;
                    }

                    String[] lineData = line.split(" = ", 2);

                    StringReader reader = new StringReader(lineData[1]);
                    Template lineTemplate = new Template("lineTemplate", reader, Main.FTL_CONFIG);
                    StringWriter writer = new StringWriter();
                    lineTemplate.process(email.getData(), writer);

                    data.put(lineData[0], writer.toString());
                }

                Template temp = Main.FTL_CONFIG.getTemplate("three-lines-button.ftl");
                StringWriter writer = new StringWriter();
                temp.process(data, writer);

                Destination destination = new Destination().withToAddresses(destinationEmail);

                Content subject = new Content().withCharset("UTF-8").withData(email.getSubject());
                Content textBody = new Content().withCharset("UTF-8").withData(writer.toString());
                Body body = new Body().withHtml(textBody);

                Message message = new Message().withSubject(subject).withBody(body);
                SendEmailRequest emailRequest = new SendEmailRequest().withSource(FROM_EMAIL).withDestination(destination).withMessage(message);

                AmazonSimpleEmailServiceClient client = new AmazonSimpleEmailServiceClient();
                Region region = Region.getRegion(Regions.US_EAST_1);
                client.setRegion(region);
                client.sendEmail(emailRequest);
            } catch (IOException | TemplateException e) {
                e.printStackTrace();
            }
        };

        new Thread(runnable).start();
    }
}
