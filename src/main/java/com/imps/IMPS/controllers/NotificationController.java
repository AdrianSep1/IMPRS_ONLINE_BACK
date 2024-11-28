package com.imps.IMPS.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.imps.IMPS.EmailService;
import com.imps.IMPS.models.Notification;
import com.imps.IMPS.repositories.NotificationRepository;

@CrossOrigin(origins = "http://localhost:3000")
@RestController
@RequestMapping(path = "/notifications")
public class NotificationController {
	@Autowired
	private NotificationRepository notificationRepository;
	private EmailService emailService;
	
	@GetMapping(path = "/all")
    public @ResponseBody Iterable<Notification> getAllNotifications() {
        return notificationRepository.findAll();
    }
	
	@GetMapping(path = "/id")
    public @ResponseBody Iterable<Notification> getNotificationsByID(@RequestParam String id) {
        return notificationRepository.findByUserID(id);
    }
	
}
