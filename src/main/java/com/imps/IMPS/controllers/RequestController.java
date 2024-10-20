package com.imps.IMPS.controllers;

import java.sql.Date;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.imps.IMPS.models.File;
import com.imps.IMPS.models.Notification;
import com.imps.IMPS.models.User;
import com.imps.IMPS.models.PrintingDetails;
import com.imps.IMPS.models.PrintingRecord;
import com.imps.IMPS.models.RequestResponse;
import com.imps.IMPS.models.ServerResponse;
import com.imps.IMPS.repositories.FileRepository;
import com.imps.IMPS.repositories.NotificationRepository;
import com.imps.IMPS.repositories.PrintingDetailsRepository;
import com.imps.IMPS.repositories.PrintingRecordsRepository;
import com.imps.IMPS.repositories.UserRepository;

@CrossOrigin
@RestController
@RequestMapping(path = "/requests")
public class RequestController {
	@Autowired
    private PrintingDetailsRepository printingDetailsRepository;

	@Autowired
    private UserRepository userRepository;
	
	@Autowired
	private NotificationRepository notificationRepository;
	
	@Autowired
    private PrintingRecordsRepository recordRepository;
	
	@PostMapping( path = "/newRequest", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
	public RequestResponse saveRequest(@RequestParam("requestID") String requestID, @RequestParam("userID") String userID, @RequestParam("role") String role,
			@RequestParam("fileType") String fileType, @RequestParam("fileName") String fileName, @RequestParam("desc") String desc,
			@RequestParam("noOfCopies") Integer noOfCopies, @RequestParam("colored") String colored, 
			@RequestParam("giveExam") Boolean giveExam, @RequestParam("paperSize") String paperSize, @RequestParam("schoolId") String schoolId, @RequestParam("paperType") String paperType,
			@RequestParam("requestDate") Date requestDate, @RequestParam("useDate") Date useDate, @RequestParam("name") String name, @RequestParam("isHead") Boolean isHead, @RequestParam("isStaff") Boolean isStaff, @RequestParam("isAdmin") Boolean isAdmin,
			@RequestParam("email") String email, @RequestParam("department") String department, @RequestParam("URL") String downloadURL){
			
			try{

				User headUser = userRepository.findHeadUser();
				String headUserID = headUser != null ? headUser.getUserID() : null;
				
				PrintingDetails request = new PrintingDetails(userID, requestID, fileName, fileType, desc, noOfCopies, colored, giveExam, paperSize, schoolId, paperType, requestDate, java.time.LocalDateTime.now(), useDate, name, email, department, downloadURL);
				Notification notification = new Notification(requestID, userID, "New Request Created!", 
				"You have successfully created a new request and it is currently pending for approval.", 
				requestDate, role, false, false, false, false);				
		
				PrintingRecord newRecord = new PrintingRecord(schoolId, requestID, fileType, fileName, requestDate, useDate, "Pending");
				
				Notification adminNotification = new Notification(requestID, headUserID, "New Request Submitted!", 
                "A new request has been submitted and is currently waiting for approval.", 
                requestDate, "head", false, isHead, isStaff, isAdmin);    

				printingDetailsRepository.save(request);
				recordRepository.save(newRecord);
				notificationRepository.save(notification);
				notificationRepository.save(adminNotification);

			List<PrintingDetails> Created = new ArrayList<>();
			Created.add(request);
			
			RequestResponse response = new RequestResponse(true, "New request created.", null, Created);
			return response;
			}catch (Exception e) {
				RequestResponse error = new RequestResponse(false, "Unable to create new request.", null, null);
				return error;
			}
	 }

	@GetMapping(path = "/id")
    public @ResponseBody PrintingDetails getRequestByID(@RequestParam String id, @RequestParam String fileName) {
        return printingDetailsRepository.findByID(id, fileName);
    }
	
	@GetMapping(path = "/download")
    public @ResponseBody PrintingDetails getDownloadURLByID(@RequestParam String id, @RequestParam String fileName) {
        return printingDetailsRepository.findByID(id, fileName);
    }
	
	@GetMapping(path = "/getModuleCopies")
    public @ResponseBody Integer getModuleCopies(String dates) {
    	Integer total = 0;
    	for (PrintingDetails p : printingDetailsRepository.getModules(dates)) {
    		total += p.getNoOfCopies();
    	}
    	
    	return total;
    }
	
	@GetMapping(path = "/getOfficeFormCopies")
    public @ResponseBody Integer getOfficeFormCopies(String dates) {
    	Integer total = 0;
    	for (PrintingDetails p : printingDetailsRepository.getOfficeForms(dates)) {
    		total += p.getNoOfCopies();
    	}
    	
    	return total;
    }
	
	@GetMapping(path = "/getManualCopies")
    public @ResponseBody Integer getManualCopies(String dates) {
    	Integer total = 0;
    	for (PrintingDetails p : printingDetailsRepository.getManuals(dates)) {
    		total += p.getNoOfCopies();
    	}
    	
    	return total;
    }
	
	@GetMapping(path = "/getExamCopies")
    public @ResponseBody Integer getExamCopies(String dates) {
    	Integer total = 0;
    	for (PrintingDetails p : printingDetailsRepository.getExams(dates)) {
    		total += p.getNoOfCopies();
    	}
    	
    	return total;
    }
	
}

