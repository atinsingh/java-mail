package com.example.mockgenerator.api;

import com.example.mockgenerator.model.MockRequest;
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
import java.nio.file.Paths;
import java.util.Optional;

@RestController
public class MockController {

    private JavaMailSender emailSender;

    public MockController(JavaMailSender emailSender) {
        this.emailSender = emailSender;
    }

    @PostMapping("/api/mock")
    public ResponseEntity<?> getMock(@RequestBody MockRequest body) {
        System.out.println(body);
        return ResponseEntity.of(Optional.empty());
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
