package com.imps.IMPS.controllers;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.http.HttpStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.*;
import com.imps.IMPS.repositories.NotificationRepository;

import com.imps.IMPS.models.HomeDetails;
import com.imps.IMPS.models.ServerResponse;
import com.imps.IMPS.models.User;
import com.imps.IMPS.models.UserReport;
import com.imps.IMPS.models.UserResponse;
import com.imps.IMPS.repositories.HomeRepository;
import com.imps.IMPS.repositories.PrintingRecordsRepository;
import com.imps.IMPS.repositories.PrintingDetailsRepository;
import com.imps.IMPS.repositories.UserReportRepository;
import com.imps.IMPS.repositories.UserRepository;
import com.imps.IMPS.EmailService;
import java.util.Optional;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import com.imps.IMPS.models.Notification;
import java.util.Map;
import java.sql.Date;
import java.time.LocalDate;


@CrossOrigin
@RestController
@RequestMapping(path = "/services")

public class UserController {

    private static final Logger logger = LoggerFactory.getLogger(UserController.class);


    @Autowired
    private UserRepository userRepository;

    @Autowired
    private EmailService emailService;
    
    @Autowired
    private UserReportRepository userReportRepository;

    private BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
    
    @Autowired
    private HomeRepository homeRepository;

    @Autowired
    private PrintingDetailsRepository printingDetailsRepository;

    @Autowired
    private PrintingRecordsRepository printingRecordsRepository;

    @Autowired
    private NotificationHandler notificationHandler;

    @Autowired
	private NotificationRepository notificationRepository;
	
    public UserController(EmailService emailService) {
    	this.emailService = emailService;
    }
  

    @PostMapping(path = "/NewUserRegistration")
    public @ResponseBody UserResponse addNewUser(
            @RequestParam String firstName, 
            @RequestParam String lastName,
            @RequestParam String password, 
            @RequestParam String email,  
            @RequestParam String schoolId,
            @RequestParam String role,  
            @RequestParam Boolean adminVerified,
            @RequestParam String college,
            @RequestParam(required = false) String department,
            @RequestParam(required = false) String office
    ) {

        try {
            String token = UUID.randomUUID().toString().replaceAll("-", "");

            // Check if the email already exists
            if(userRepository.findByEmail(email) != null) {
                return new UserResponse(false, "User email already exists!", null, null);
            } else {
                List<User> created = new ArrayList<>();
                List<UserReport> createdReport = new ArrayList<>();

                // Generate userID based on the current date and user count
                String month = String.format("%02d", LocalDate.now().getMonthValue());
                int userCount = userRepository.getAll().size();
                String userNumber = (userCount < 10) ? "00" + (userCount + 1) : (userCount < 100) ? "0" + (userCount + 1) : Integer.toString(userCount + 1);
                String userID = LocalDate.now().getYear() + month + userNumber;

                // Create and set user details
                User IMPSUser = new User();
                IMPSUser.setFirstName(firstName);
                IMPSUser.setLastName(lastName);
                IMPSUser.setEmail(email);
                IMPSUser.setPassword(encoder.encode(password));
                IMPSUser.setToken(token);
                IMPSUser.setUserID(userID);
                IMPSUser.setSchoolId(schoolId);
                IMPSUser.setRole(role);
                IMPSUser.setCollege(college);
                IMPSUser.setDepartment(department);
                IMPSUser.setOffice(office);
                IMPSUser.setAdminVerified(adminVerified);

                created.add(IMPSUser);
                userRepository.save(IMPSUser);

                // Create user report
                UserReport IMPSReport = new UserReport();
                IMPSReport.setRole(role);
                IMPSReport.setStatus("Waiting");
                IMPSReport.setEmail(email);
                createdReport.add(IMPSReport);
                userReportRepository.save(IMPSReport);

                // Get the admin user details for notification
                User adminUser = userRepository.findAdminUser();
                String adminUserId = adminUser != null ? adminUser.getUserID() : null;

                // Generate a random adminNotifId like Reg105
                String adminNotifId = "Reg" + (int)(Math.random() * 1000);  // Generates a number between 0-999

                // Get the current date and convert it to java.sql.Date
                java.sql.Date createdDate = java.sql.Date.valueOf(LocalDate.now());

                // Send notification about the new user registration
                notificationHandler.sendNotification("New user registered: " + firstName + " " + lastName);

                // Create and save admin notification
                Notification adminNotification = new Notification(
                        adminNotifId, 
                        adminUserId, 
                        "New User Registration!", 
                        "A new registration has been created and is currently waiting for approval.", 
                        createdDate, 
                        "admin", 
                        false, 
                        false, 
                        false, 
                        true
                );    
                notificationRepository.save(adminNotification);

                // Return success response
                return new UserResponse(true, "User created successfully", null, created);
            }
        } catch(Exception e) {
            return new UserResponse(false, "Unable to create new user.", null, null);
        }
    }

   @DeleteMapping(path = "/deleteUser")
    public ResponseEntity<ServerResponse> deleteUser(@RequestBody Map<String, String> request) {
        ServerResponse response = new ServerResponse();

        try {
            String email = request.get("email");
            User user = userRepository.findByEmail(email);

            if (user != null) {
                // Fetch the school ID (which is the same as user ID in printing records)
                String schoolId = user.getSchoolId(); // Assuming getSchoolId() exists

                // Delete associated printing details
                printingDetailsRepository.deleteBySchoolId(schoolId);

                // Delete associated printing records using userId
                printingRecordsRepository.deleteByUserId(schoolId);

                // Log deletions
                logger.info("Deleted printing details and printing records for user with ID {}", schoolId);

                // Delete the user
                user.setAdminVerified(false);
                userRepository.save(user);

                userRepository.delete(user);
                response.setStatus(true);
                response.setMessage("User and all associated records have been removed successfully.");
                return ResponseEntity.ok(response);
            } else {
                response.setStatus(false);
                response.setMessage("User not found.");
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
            }
        } catch (Exception e) {
            logger.error("Error while removing user: ", e);
            response.setStatus(false);
            response.setMessage("Error removing user: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }



    
    @PostMapping(path = "/NewStaffRegistration")
    public @ResponseBody UserResponse addNewStaff(
            @RequestParam String firstName, 
            @RequestParam String lastName,
            @RequestParam String password, 
            @RequestParam String email,  
            @RequestParam String schoolId,
            @RequestParam String role 
    ) {
        try {
            String token = UUID.randomUUID().toString().replaceAll("-", "");

            if(userRepository.findByEmail(email) != null) {
                return new UserResponse(false, "User email already exists!", null, null);
            } else {
                List<User> created = new ArrayList<>();
                List<UserReport> createdReport = new ArrayList<>();

                String month;
                String userNumber;

                month = String.format("%02d", LocalDate.now().getMonthValue());
                int userCount = userRepository.getAll().size();

                if (userCount < 10) {
                    userNumber = "00" + (userCount + 1);
                } else if (userCount < 100) {
                    userNumber = "0" + (userCount + 1);
                } else {
                    userNumber = Integer.toString(userCount + 1);
                }

                String userID = LocalDate.now().getYear() + month + userNumber;

                User IMPSUser = new User();
                IMPSUser.setFirstName(firstName);
                IMPSUser.setLastName(lastName);
                IMPSUser.setEmail(email);
                IMPSUser.setPassword(encoder.encode(password));
                IMPSUser.setToken(token);
                IMPSUser.setUserID(userID);
                IMPSUser.setSchoolId(schoolId);
                IMPSUser.setRole(role);
                IMPSUser.setIsStaff(true);
                IMPSUser.setAdminVerified(true);
                created.add(IMPSUser);
                userRepository.save(IMPSUser);

                UserReport IMPSReport = new UserReport();
                IMPSReport.setRole(role);
                IMPSReport.setStatus("Accepted");
                IMPSReport.setEmail(email);
                createdReport.add(IMPSReport);
                userReportRepository.save(IMPSReport);

                return new UserResponse(true, "User created successfully", null, created);
            }
        } catch(Exception e) {
            return new UserResponse(false, "Unable to create new user.", null, null);
        }
    }
    
    @PutMapping(path = "/updateStaff")
    public @ResponseBody UserResponse updateStaff(
            @RequestParam String id, 
            @RequestParam String firstName, 
            @RequestParam String lastName,
            @RequestParam(required = false) String password, 
            @RequestParam String email,  
            @RequestParam String schoolId,
            @RequestParam String role 
    ) {

        try {
            User existingUser = userRepository.findStaffId(id); 

            if (existingUser == null) {
                return new UserResponse(false, "User not found!", null, null);
            }

            // Update user fields
            existingUser.setFirstName(firstName);
            existingUser.setLastName(lastName);
            existingUser.setEmail(email);
            existingUser.setSchoolId(schoolId);
            existingUser.setRole(role);
            
            // Only update the password if it's provided
            if (password != null && !password.isEmpty()) {
                existingUser.setPassword(encoder.encode(password));
            }

            userRepository.save(existingUser); 

            return new UserResponse(true, "Staff updated successfully", null, Arrays.asList(existingUser));
        } catch(Exception e) {
            return new UserResponse(false, "Unable to update staff: " + e.getMessage(), null, null);
        }
    }

 
    @PostMapping(path = "/createDefaultUsers")
    public @ResponseBody String createDefaultUsers(@RequestBody Map<String, String> request) {
        try {
            String adminEmail = request.get("adminEmail");
            String headEmail = request.get("headEmail");

            // Check if Admin exists
            if (userRepository.findByEmail(adminEmail) == null) {
                User adminUser = new User();
                String token = UUID.randomUUID().toString().replaceAll("-", "");
                adminUser.setFirstName("Admin");
                adminUser.setLastName("User");
                adminUser.setEmail(adminEmail);
                adminUser.setPassword(encoder.encode("admin")); 
                adminUser.setRole("admin");
                adminUser.setToken(token);
                adminUser.setIsAdmin(true);
                adminUser.setUserID("ADMIN001");
                adminUser.setSchoolId("00-0000-000");
                userRepository.save(adminUser);
            }

            // Check if Head exists
            if (userRepository.findByEmail(headEmail) == null) {
                String token = UUID.randomUUID().toString().replaceAll("-", "");
                User headUser = new User();
                headUser.setFirstName("Head");
                headUser.setLastName("User");
                headUser.setEmail(headEmail);
                headUser.setPassword(encoder.encode("head")); 
                headUser.setRole("head");
                headUser.setToken(token);
                headUser.setIsHead(true);
                headUser.setUserID("HEAD001");
                headUser.setSchoolId("00-0000-001");
                userRepository.save(headUser);
            }

            return "Default admin and head users created if they didn't exist.";
        } catch (Exception e) {
            e.printStackTrace(); // Log the exception to the console
            return "Error creating default users: " + e.getMessage(); // Return a friendly error message
        }
    }


    
    @PostMapping(path = "/updateAdminVerified")
    public @ResponseBody ServerResponse updateAdminVerified(@RequestBody Map<String, String> request) {
        ServerResponse response = new ServerResponse();
    
        try {
            String email = request.get("email");
            User user = userRepository.findByEmail(email); 
    
            if (user != null) {
                user.setAdminVerified(true);
                userRepository.save(user); 
    
                emailService.sendEmail(email, "Account Accepted", "Congratulations! Your account has been accepted. You can now login using your email account");

                UserReport userReport = userReportRepository.findByEmail(email);
                userReport.setStatus("Accepted"); 
                userReportRepository.save(userReport); 

                response.setStatus(true);
                response.setMessage("Admin verification status updated successfully. An acceptance email has been sent.");
            } else {
                response.setStatus(false);
                response.setMessage("User not found.");
            }
        } catch (Exception e) {
            response.setStatus(false);
            response.setMessage("Error updating admin verification status: " + e.getMessage());
        }
    
        return response;
    }

    @GetMapping(path = "/getEmployeeCounts")
    public Map<String, Long> getEmployeeCountByRole() {
        List<Object[]> results = userRepository.countEmployeesByRole();
        Map<String, Long> employeeCountByRole = new HashMap<>();
        
        for (Object[] result : results) {
            String role = (String) result[0];
            Long count = ((Number) result[1]).longValue();
            employeeCountByRole.put(role, count);
        }
        
        return employeeCountByRole;
    }
    
    @GetMapping(path = "/statistics")
    public Map<String, Integer> getUserStatistics() {
        Map<String, Integer> statistics = new HashMap<>();
        
        statistics.put("accepted", userReportRepository.countAcceptedUsers());
        statistics.put("declined", userReportRepository.countDeclinedUsers());
        statistics.put("total", userReportRepository.countAllUsers());
        return statistics;
    }

    @PostMapping(path = "/declineUser")
    public @ResponseBody ServerResponse declineUser(@RequestBody Map<String, String> request) {
        ServerResponse response = new ServerResponse();
    
        try {
            String email = request.get("email"); 
            User user = userRepository.findByEmail(email); 
    
            if (user != null) {
                user.setAdminVerified(false); 
                userRepository.save(user); 
    
                emailService.sendEmail(email, "Account Declined", "We're sorry, but your account has been declined. Please contact support for more information.");
    
                UserReport userReport = userReportRepository.findByEmail(email);
                userReport.setStatus("Declined"); 
                userReportRepository.save(userReport); 
                userRepository.delete(user); 
                response.setStatus(true);
                response.setMessage("User has been declined and removed successfully. A notification email has been sent.");
            } else {
                response.setStatus(false);
                response.setMessage("User not found.");
            }
        } catch (Exception e) {
            response.setStatus(false);
            response.setMessage("Error declining user: " + e.getMessage());
        }
    
        return response;
    }
    


    
    @GetMapping(path = "/all")
    public @ResponseBody List<User> getAllUsers() {
        return userRepository.getAll();
    }

    @GetMapping(path = "/encrypt")
    public @ResponseBody String testEncrypt(@RequestParam String input) {
        return encoder.encode(input);
    }
    
    @GetMapping(path = "/decrypt")
    public @ResponseBody Boolean testDecrypt(@RequestParam String input, @RequestParam String test) {
        return encoder.matches(input, test);
    }
    
    @GetMapping(path = "/getid")
    public @ResponseBody User getUserID(@RequestParam String email) {
        return userRepository.findByEmail(email);
    }

    @GetMapping(path = "/exists")
    public @ResponseBody Boolean checkUser(
            @RequestParam(required = false) String email, 
            @RequestParam(required = false) String schoolId) {
        
        if (email != null && userRepository.findByEmail(email) != null) {
            return true; 
        }
        
        if (schoolId != null && userRepository.findBySchoolId(schoolId) != null) {
            return true; 
        }
        
        return false; 
    }
    
    @GetMapping(path = "/getname")
    public @ResponseBody User getEmail(@RequestParam String email) {
        return userRepository.findByEmail(email);
    }
    
    @GetMapping(path = "/checkAdmin")
    public @ResponseBody Boolean checkAdmin(@RequestParam String email) {
    	if(userRepository.checkAdminEmail(email) != null) {
    		return true;
    	}else {
    		return false;
    	}
    }

    @GetMapping(path = "/userLogin")
    public @ResponseBody ServerResponse checkAuth(@RequestParam String email, 
            @RequestParam String password) {
        ServerResponse Response = new ServerResponse();
        User user = userRepository.findByEmail(email);
        if(user != null) {
            if(encoder.matches(password, user.getPassword())) {
                if(user.getRole().equals("admin")) {
                    Response.setStatus(true);
                    Response.setMessage("Admin login");
                    Response.setServerToken(null);
                    return Response;
                } else if(user.getRole().equals("staff")){
                    Response.setStatus(true);
                    Response.setMessage("Staff login");
                    Response.setServerToken(null);
                    return Response;
                } else if(user.getRole().equals("head")){
                    Response.setStatus(true);
                    Response.setMessage("Head login");
                    Response.setServerToken(null);
                    return Response;
                } else{
                    Response.setStatus(true);
                    Response.setMessage("User login");
                    Response.setServerToken(null);
                    return Response;
                }
            } else {
                Response.setStatus(false);
                Response.setMessage("Authentication failed.");
                Response.setServerToken(null);
                return Response;
            }
        } else {
            Response.setStatus(false);
            Response.setMessage("User not found.");
            Response.setServerToken(null);
            return Response;
        }
    }

    @GetMapping(path = "/checkAuth")
    public @ResponseBody Boolean checkAuthByPass(@RequestParam String email, 
    		@RequestParam String password) {
    	if(encoder.matches(password, userRepository.findByEmail(email).getPassword())==true) {	
    		return true;
    	}else {
    		return false;
    	}
    }
    
    @PostMapping(path = "/ForgotPasswordStep1")
    public @ResponseBody boolean forgotPassword(@RequestParam String email) {
    	String token = UUID.randomUUID().toString().replaceAll("-", "");
    	userRepository.setNewToken(email, token);
    	emailService.sendEmail(email, "IMPS Password Reset Token","Hello, here is your password reset token: " + token);
    	return true;
    }

    @GetMapping(path = "/CheckEmail")
    public @ResponseBody boolean checkToken(@RequestParam String email) {
    	if(userRepository.findByEmail(email)!=null) {
    		return true;
    	}else {
    		return false;
    	}
    }

    @GetMapping(path = "/ForgotPasswordStep2")
    public @ResponseBody boolean checkToken(@RequestParam String email, 
    		@RequestParam String token) {
    	if(userRepository.findByEmailAndToken(email, token) != null) {
    		return true;
    	}else {
    		return false;
    	}
    }

    @PostMapping(path = "/ForgotPasswordStep3")
    public @ResponseBody boolean setNewPassword(@RequestParam String email,
    		@RequestParam String token, @RequestParam String password) {
    	userRepository.setNewPassword(encoder.encode(password), email, token);
    	emailService.sendEmail(email, "IMPS Password Reset","Hello, your password has been successfully changed.");
    	return true;
    }

    @PostMapping(path = "/newPassword")
    public @ResponseBody boolean setPassword(@RequestParam String email, @RequestParam String password) {
    	userRepository.setNewPasswordNoToken(encoder.encode(password), email);
    	emailService.sendEmail(email, "IMPS Password Change","Hello, your password has been successfully changed.");
    	return true;
    }

    @PostMapping(path = "/newEmail")
    public @ResponseBody boolean setEmail(@RequestParam String newEmail, @RequestParam String email) {
    	userRepository.setNewEmail(newEmail, email);
    	emailService.sendEmail(email, "IMPS Email Change","Hello, your email, " +email+ " has been successfully changed to " + newEmail);
    	emailService.sendEmail(newEmail, "IMPS Email Change","Hello, your email, " +email+ " has been successfully changed to " + newEmail);
    	return true;
    }

    @PostMapping(path = "/newName")
    public @ResponseBody boolean setEmail(@RequestParam String firstName, @RequestParam String lastName, @RequestParam String email) {
    	userRepository.setNewFirstName(firstName, email);
    	userRepository.setNewLastName(lastName, email);
    	return true;
    }

    // @PostMapping(path = "/createHome")
    // public @ResponseBody boolean setHome(@RequestBody HomeDetails homeDetails) {
    //     HomeDetails home = new HomeDetails(homeDetails.getAnnouncements(), homeDetails.getGuidelines(), homeDetails.getProcess(), homeDetails.getLocations(), homeDetails.getUpdates());
    //     homeRepository.save(home);
    //     return true;
    // }

    // @PostMapping(path = "/editHome")
    // public @ResponseBody HomeDetails editHome(@RequestBody HomeDetails homeDetails) {
    //     homeRepository.setAnnouncements(homeDetails.getAnnouncements(), 1);
    //     homeRepository.setGuidelines(homeDetails.getGuidelines(), 1);
    //     homeRepository.setLocations(homeDetails.getLocations(), 1);
    //     homeRepository.setProcess(homeDetails.getProcess(), 1);
    //     homeRepository.setUpdates(homeDetails.getUpdates(), 1);
    //     return homeRepository.findByID(1);
    // }

    // @GetMapping(path = "/getHome")
    // public @ResponseBody HomeDetails getHome() {
    // 	return homeRepository.findByID(homeRepository.getAll().size());
    // }

    // @GetMapping(path = "/user_count")
    // public @ResponseBody Integer getUserCount() {
    // 	return userRepository.countAllUsers();
    // }
    
    // @GetMapping(path = "/getHomeNumber")
    // public @ResponseBody Integer getHomeNumber() {
    // 	return homeRepository.getAll().size();
    // }

    // Method to update the home details
    @PutMapping(path = "/updateHomeDetails")
    public @ResponseBody ServerResponse updateHomeDetails(@RequestBody Map<String, String> request) {
        ServerResponse response = new ServerResponse();

        try {
            // Retrieve the details from the request
            String ann = request.get("ann");
            String guide = request.get("guide");
            String pro = request.get("pro");
            String loc = request.get("loc");
            String upd = request.get("upd");

            // Check if HomeDetails record already exists (assuming "1" as the unique ID for home details)
            Optional<HomeDetails> optionalHomeDetails = homeRepository.findById(1);

            if (optionalHomeDetails.isPresent()) {
                // Update existing details
                HomeDetails homeDetails = optionalHomeDetails.get();
                homeDetails.setAnnouncements(ann);
                homeDetails.setGuidelines(guide);
                homeDetails.setProcess(pro);
                homeDetails.setLocations(loc);
                homeDetails.setUpdates(upd);

                // Save the updated details
                homeRepository.save(homeDetails);

                response.setStatus(true);
                response.setMessage("Home details updated successfully.");
            } else {
                // If no existing details, create a new record
                HomeDetails homeDetails = new HomeDetails();
                homeDetails.setId("1"); // Set the ID to a fixed value (or generate a new one)
                homeDetails.setAnnouncements(ann);
                homeDetails.setGuidelines(guide);
                homeDetails.setProcess(pro);
                homeDetails.setLocations(loc);
                homeDetails.setUpdates(upd);

                // Save the new details
                homeRepository.save(homeDetails);

                response.setStatus(true);
                response.setMessage("Home details created successfully.");
            }
        } catch (Exception e) {
            response.setStatus(false);
            response.setMessage("Error updating home details: " + e.getMessage());
        }

        return response;
    }

    // Method to retrieve the home details
    @GetMapping(path = "/getHomeDetails")
    public @ResponseBody HomeDetails getHomeDetails() {
        try {
            // Fetch the home details (assuming "1" is the ID for the HomeDetails)
            Optional<HomeDetails> optionalHomeDetails = homeRepository.findById(1);

            if (optionalHomeDetails.isPresent()) {
                return optionalHomeDetails.get();
            } else {
                return null; // Or return an empty object with default values
            }
        } catch (Exception e) {
            e.printStackTrace();
            return null; // Handle error (e.g., return an empty response or error message)
        }
    }

}
