package com.imps.IMPS.models;

import java.sql.Date;

import org.springframework.format.annotation.DateTimeFormat;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

@Entity
public class PrintingRecord {
	@Id
    @GeneratedValue(strategy=GenerationType.IDENTITY)
    private Integer id;
	
	private String name;
	private String userID;
	
	private String requestID;
	
	private String fileType;
	
	private String fileName;
	
	@DateTimeFormat(pattern = "yyyy-MM-dd")
	private Date requestDate;
	
	@DateTimeFormat(pattern = "yyyy-MM-dd")
	private Date useDate;
	
	private String status;
	
	public PrintingRecord () {}
	
	public PrintingRecord (String name, String userID, String requestID, String fileType, String fileName, Date requestDate, Date useDate, String status) {
		this.setRequestersName(name);
		this.setUserID(userID);
		this.setRequestID(requestID);
		this.setFileType(fileType);
		this.setFileName(fileName);
		this.setRequestDate(requestDate);
		this.setUseDate(useDate);
		this.setStatus(status);
	}

	public String getRequestersName() {
		return name;
	}

	public void setRequestersName(String name) {
		this.name = name;
	}
	public String getRequestID() {
		return requestID;
	}

	public void setRequestID(String requestID) {
		this.requestID = requestID;
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

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
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

	public String getFileName() {
		return fileName;
	}

	public void setFileName(String fileName) {
		this.fileName = fileName;
	}
	
}
