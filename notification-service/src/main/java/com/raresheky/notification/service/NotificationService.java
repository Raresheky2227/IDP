package com.raresheky.notification.service;

import com.raresheky.notification.model.Notification;
import com.raresheky.notification.repository.NotificationRepository;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class NotificationService {
    private final NotificationRepository repo;
    private final JavaMailSender mailSender;

    public NotificationService(NotificationRepository repo, JavaMailSender mailSender) {
        this.repo = repo;
        this.mailSender = mailSender;
    }

    public Notification createNotification(String recipient, String message) {
        Notification n = new Notification(recipient, message);
        repo.save(n);

        // Send email notification
        sendEmailNotification(recipient, message);

        return n;
    }

    public List<Notification> getNotifications(String recipient) {
        return repo.findByRecipientOrderByCreatedAtDesc(recipient);
    }

    public void markAsRead(Long id) {
        repo.findById(id).ifPresent(n -> {
            n.setRead(true);
            repo.save(n);
        });
    }

    private void sendEmailNotification(String recipient, String message) {
        SimpleMailMessage mail = new SimpleMailMessage();
        mail.setTo("raresheky@gmail.com"); // Send all emails to you as a mock
        mail.setSubject("New Notification for: " + recipient);
        mail.setText(message);
        mailSender.send(mail);
    }
}
