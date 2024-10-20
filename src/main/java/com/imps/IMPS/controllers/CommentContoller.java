package com.imps.IMPS.controllers;

import java.sql.Date;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.imps.IMPS.EmailService;
import com.imps.IMPS.models.Comment;
import com.imps.IMPS.models.CommentResponse;
import com.imps.IMPS.models.Notification;
import com.imps.IMPS.repositories.CommentRepository;
import com.imps.IMPS.repositories.NotificationRepository;
import com.imps.IMPS.repositories.PrintingDetailsRepository;

@CrossOrigin
@RestController
@RequestMapping(path = "/comments")
public class CommentContoller {
	@Autowired
	private CommentRepository commentRepository;
    private EmailService emailService;
    
    @Autowired
    private PrintingDetailsRepository printingDetailsRepository;
    
    @Autowired
	private NotificationRepository notificationRepository;
    
    @GetMapping(path = "/all")
    public @ResponseBody Iterable<Comment> getAllComments() {
        return commentRepository.findAll();
    }
    
    @GetMapping(path = "/id")
    public @ResponseBody Iterable<Comment> getCommentsByID(@RequestParam String id) {
        return commentRepository.findByRequestID(id);
    }
    
    @PostMapping( path = "/newComment", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
	public CommentResponse saveUserComment(@RequestParam("content") String content, @RequestParam("header") String header,
			@RequestParam("requestID") String requestID, @RequestParam("sentBy") String sentBy, @RequestParam("sentDate") Date sentDate){
	   
			try{
				Comment comment = new Comment(requestID, header, content, sentBy, sentDate);
				
			commentRepository.save(comment);
			
			List<Comment> Created = new ArrayList<>();
			Created.add(comment);
			
			CommentResponse response = new CommentResponse(true, "New comment added.", null, Created);
			return response;
			}catch (Exception e) {
				CommentResponse error = new CommentResponse(false, "Unable to create new comment.", null, null);
				return error;
			}
	 }
    
    @PostMapping( path = "/newAdminComment", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
	public CommentResponse saveComment(@RequestParam("content") String content, @RequestParam("header") String header, @RequestParam("role") String role, 
			@RequestParam("requestID") String requestID, @RequestParam("sentBy") String sentBy, @RequestParam("sentDate") Date sentDate){
	   
			try{
				Comment comment = new Comment(requestID, header, content, sentBy, sentDate);
				
				Notification notification = new Notification(requestID, printingDetailsRepository.getID(requestID).getUserID(), "New Comment!", "Your request ID #" + requestID + " has a new comment!", sentDate, role, false, false, false, false);
		    	notificationRepository.save(notification);
			commentRepository.save(comment);
			
			List<Comment> Created = new ArrayList<>();
			Created.add(comment);
			
			CommentResponse response = new CommentResponse(true, "New comment added.", null, Created);
			return response;
			}catch (Exception e) {
				CommentResponse error = new CommentResponse(false, "Unable to create new comment.", null, null);
				return error;
			}
	 }
}
