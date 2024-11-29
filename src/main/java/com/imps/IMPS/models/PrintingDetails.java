package com.imps.IMPS.models;

import java.sql.Date;
import java.time.LocalDateTime;

import org.springframework.format.annotation.DateTimeFormat;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

@Entity
public class PrintingDetails {
	@Id
    @GeneratedValue(strategy=GenerationType.IDENTITY)
    private Integer id;
	
	private String userID;
	private String schoolId;	
	private String requestID;
	
	private String fileType;
	
	private String fileName;
	
	private String description;
	
	private Integer noOfCopies;
	
	private String colored;
	
	private Boolean giveExam;
	
	private String paperSize;
	
	private String paperType;

	@DateTimeFormat(pattern = "yyyy-MM-dd")
	private Date requestDate;
	
	private LocalDateTime requestTime;
	
	@DateTimeFormat(pattern = "yyyy-MM-dd")
	private Date useDate;
	
	private String requesterName;
	
	private String requesterEmail;
	
	private String status;
	private String department;
	private String office;
	private String role;
	private String college;
	
	private String downloadURL;
	
	public PrintingDetails() {}

	
    public PrintingDetails(String userID, String requestID, String filename, String filetype, String description, Integer noOfCopies, String colored, Boolean giveExam, String paperSize, String schoolId, String paperType, Date requestDate, LocalDateTime requestTime, Date useDate, String name, String email, String department, String college, String office, String role, String downloadURL) {
        this.setUserID(userID);
        this.setRequestID(requestID);
        this.setFileName(filename);
        this.setFileType(filetype);
        this.setDescription(description);
        this.setNoOfCopies(noOfCopies);
        this.setColored(colored);
        this.setGiveExam(giveExam);
        this.setPaperSize(paperSize);
		this.setSchoolId(schoolId);
        this.setPaperType(paperType);
        this.setRequestDate(requestDate);
        this.setRequestTime(requestTime);
        this.setUseDate(useDate);
        this.setRequesterName(name);
        this.setRequesterEmail(email);
        this.setDepartment(department);
		this.setCollege(college);
		this.setOffice(office);
		this.setRole(role);
        this.setDownloadURL(downloadURL);
        this.setStatus("Pending"); 
    }

    // Getter and Setter for status
    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }


	public String getFileName() {
		return fileName;
	}

	public void setFileName(String fileName) {
		this.fileName = fileName;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public Integer getNoOfCopies() {
		return noOfCopies;
	}

	public void setNoOfCopies(Integer noOfCopies) {
		this.noOfCopies = noOfCopies;
	}

	public String getColored() {
		return colored;
	}
	
	public void setColored(String colored) {
		this.colored = colored;
	}


	public String getSchoolId() {
		return schoolId;
	}

	public void setSchoolId(String schoolId) {
		this.schoolId = schoolId;
	}

	public String getPaperSize() {
		return paperSize;
	}

	public void setPaperSize(String paperSize) {
		this.paperSize = paperSize;
	}

	public String getPaperType() {
		return paperType;
	}

	public void setPaperType(String paperType) {
		this.paperType = paperType;
	}


	public Date getRequestDate() {
		return requestDate;
	}

	public void setRequestDate(Date requestDate) {
		this.requestDate = requestDate;
	}

	public Date getUseDate() {
		return useDate;
	}

	public void setUseDate(Date useDate) {
		this.useDate = useDate;
	}

	public String getRequesterName() {
		return requesterName;
	}

	public void setRequesterName(String requesterName) {
		this.requesterName = requesterName;
	}

	public String getRequesterEmail() {
		return requesterEmail;
	}

	public void setRequesterEmail(String requesterEmail) {
		this.requesterEmail = requesterEmail;
	}

	public String getDepartment() {
		return department;
	}

	public void setCollege(String college) {
		this.college = college;
	}


	public String getCollege() {
		return college;
	}

	public void setOffice(String office) {
		this.office = office;
	}


	public String getOffice() {
		return office;
	}

	public void setRole(String role) {
		this.role = role;
	}


	public String getRole() {
		return role;
	}


	public void setDepartment(String department) {
		this.department = department;
	}


	public LocalDateTime getRequestTime() {
		return requestTime;
	}

	public void setRequestTime(LocalDateTime requestTime) {
		this.requestTime = requestTime;
	}

	public String getRequestID() {
		return requestID;
	}

	public void setRequestID(String requestID) {
		this.requestID = requestID;
	}

	public String getUserID() {
		return userID;
	}

	public void setUserID(String userID) {
		this.userID = userID;
	}

	public String getFileType() {
		return fileType;
	}

	public void setFileType(String fileType) {
		this.fileType = fileType;
	}

	public Boolean getGiveExam() {
		return giveExam;
	}

	public void setGiveExam(Boolean giveExam) {
		this.giveExam = giveExam;
	}

	public String getDownloadURL() {
		return downloadURL;
	}

	public void setDownloadURL(String downloadURL) {
		this.downloadURL = downloadURL;
	}
	

}
