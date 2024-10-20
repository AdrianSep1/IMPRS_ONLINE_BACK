package com.imps.IMPS.controllers;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.*;

import com.imps.IMPS.models.HomeDetails;
import com.imps.IMPS.models.ServerResponse;
import com.imps.IMPS.models.User;
import com.imps.IMPS.models.UserReport;
import com.imps.IMPS.models.UserResponse;
import com.imps.IMPS.repositories.HomeRepository;
import com.imps.IMPS.repositories.UserReportRepository;
import com.imps.IMPS.repositories.UserRepository;
import com.imps.IMPS.EmailService;


@CrossOrigin(origins = "https://citimpsonline.vercel.app")
@RestController
@RequestMapping(path = "/services")
public class UserController {


    @Autowired
    private UserRepository userRepository;

    @Autowired
    private EmailService emailService;
    
    @Autowired
    private UserReportRepository userReportRepository;

    private BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
    
    @Autowired
    private HomeRepository homeRepository;
    
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
        if (!schoolId.matches("\\d{2}-\\d{4}-\\d{3}")) {
            return new UserResponse(false, "Invalid School ID format!", null, null);
        }

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

                return new UserResponse(true, "User created successfully", null, created);
            }
        } catch(Exception e) {
            return new UserResponse(false, "Unable to create new user.", null, null);
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
        if (!schoolId.matches("\\d{2}-\\d{4}-\\d{3}")) {
            return new UserResponse(false, "Invalid School ID format!", null, null);
        }

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
    

    @PostMapping(path = "/createDefaultUsers")
    public @ResponseBody String createDefaultUsers(@RequestBody Map<String, String> request) {
        String adminEmail = request.get("adminEmail");
        String headEmail = request.get("headEmail");

            if (adminEmail == null || headEmail == null) {
            return ResponseEntity.badRequest().body("Missing adminEmail or headEmail");
        }
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


    @PostMapping(path = "/createHome")
    public @ResponseBody boolean setHome(@RequestParam String ann, @RequestParam String guide, @RequestParam String loc,
    		@RequestParam String pro, @RequestParam String upd) {
    		HomeDetails home = new HomeDetails(ann,guide,pro,loc,upd);
        	homeRepository.save(home);
        	return true;
    }
    
    @GetMapping(path = "/getHome")
    public @ResponseBody HomeDetails getHome() {
    	return homeRepository.findByID(homeRepository.getAll().size());
    }

    @GetMapping(path = "/user_count")
    public @ResponseBody Integer getUserCount() {
    	return userRepository.countAllUsers();
    }
    
    @GetMapping(path = "/getHomeNumber")
    public @ResponseBody Integer getHomeNumber() {
    	return homeRepository.getAll().size();
    }
    
    @PostMapping(path = "/editHome")
    public @ResponseBody HomeDetails editHome(@RequestParam String ann, @RequestParam String guide, @RequestParam String loc,
    		@RequestParam String pro, @RequestParam String upd) {
    	homeRepository.setAnnouncements(ann, 1);
    	homeRepository.setGuidelines(guide, 1);
    	homeRepository.setLocations(loc, 1);
    	homeRepository.setProcess(pro, 1);
    	homeRepository.setUpdates(upd, 1);
    	return homeRepository.findByID(1);
    }
    

}
