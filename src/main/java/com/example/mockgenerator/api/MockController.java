package com.example.mockgenerator.api;

import com.example.mockgenerator.model.MockRequest;
import net.andreinc.mockneat.MockNeat;
import net.andreinc.mockneat.unit.text.CSVs;
import org.springframework.core.io.FileSystemResource;
import org.springframework.http.ResponseEntity;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.web.bind.annotation.*;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

@RestController
public class MockController {

    private JavaMailSender emailSender;

    public MockController(JavaMailSender emailSender) {
        this.emailSender = emailSender;
    }

    @PostMapping("/api/mock")
    public ResponseEntity<?> getMock(@RequestBody MockRequest body, @RequestParam String to, @RequestParam String subject, @RequestParam String text) throws MessagingException {
        System.out.println(body);
        MockNeat mockNeat = MockNeat.threadLocal();
        CSVs csvs = mockNeat.csvs();
        List<String> fields = body.getFields();
        for (String field: fields){
            if(field.equalsIgnoreCase("firstname")){
                csvs = csvs.column(mockNeat.names().first());
            }
            if(field.equalsIgnoreCase("lastname")){
                csvs = csvs.column(mockNeat.names().last());
            }
        }
        csvs.accumulate(body.getLimit(),"\n").consume(
                data-> {
                    try {
                        Files.write(Paths.get("data.csv"), data.getBytes());
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                }
        );

        MimeMessage message = emailSender.createMimeMessage();

        MimeMessageHelper helper = new MimeMessageHelper(message, true);

        helper.setFrom("atin@pragra.io");
        helper.setTo(to);
        helper.setSubject(subject);
        helper.setText(text);

        FileSystemResource file
                = new FileSystemResource(new File(Paths.get("data.csv").toAbsolutePath().toString()));
        helper.addAttachment("data.csv", file);

        emailSender.send(message);


        return ResponseEntity.accepted().body(Collections.singletonMap("status", "Email Sent succesfully"));

    }


    @GetMapping("/api/email")
    public void sendSimpleMessage(@RequestParam String to, @RequestParam String subject, @RequestParam String text) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom("noreply@baeldung.com");
        message.setTo(to);
        message.setSubject(subject);
        message.setText(text);
        emailSender.send(message);
    }

    @GetMapping("/api/email/attachment")
    public void sendMessageWithAttachment(
            @RequestParam String to, @RequestParam String subject,  @RequestParam String text) throws MessagingException {


        MimeMessage message = emailSender.createMimeMessage();

        MimeMessageHelper helper = new MimeMessageHelper(message, true);

        helper.setFrom("atin@pragra.io");
        helper.setTo(to);
        helper.setSubject(subject);
        helper.setText(text);

        FileSystemResource file
                = new FileSystemResource(new File(Paths.get("manu.csv").toAbsolutePath().toString()));
        helper.addAttachment("data.csv", file);

        emailSender.send(message);

    }
}
