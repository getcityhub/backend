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
import java.io.StringWriter;

import static nyc.getcityhub.Constants.FROM_EMAIL;

/**
 * Created by jackcook on 3/15/17.
 */
public class EmailService {

    public static void sendEmail(Email email, String destinationEmail) {
        Runnable runnable = () -> {
            try {
                Template temp = Main.FTL_CONFIG.getTemplate(email.getFileName());
                StringWriter writer = new StringWriter();
                temp.process(email.getData(), writer);

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
