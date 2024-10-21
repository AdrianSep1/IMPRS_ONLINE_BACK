package com.imps.IMPS.repositories;

import java.sql.Date;
import java.util.ArrayList;
import java.util.List; 
import org.springframework.data.repository.query.Param;

import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.transaction.annotation.Transactional;

import com.imps.IMPS.models.PrintingRecord;

public interface PrintingRecordsRepository extends CrudRepository<PrintingRecord, Integer> {
	
    @Query("SELECT pr FROM PrintingRecord pr WHERE pr.requestDate = :requestDate")
    List<PrintingRecord> findByRequestDate(@Param("requestDate") Date requestDate);
	
	@Query(value = "SELECT * FROM printing_record", nativeQuery = true)
	Iterable<PrintingRecord> findAllRecords();

	@Query(value = "SELECT * FROM printing_record WHERE status = 'Pending'", nativeQuery = true)
	Iterable<PrintingRecord> findPending();
	
	@Query(value = "SELECT * FROM printing_record WHERE status != 'Pending'", nativeQuery = true)
	Iterable<PrintingRecord> findCurrent();
	
	@Query(value = "SELECT * FROM printing_record WHERE userid = ?1", nativeQuery = true)
	Iterable<PrintingRecord> findByID(String userID);
	
	@Query(value = "SELECT * FROM printing_record WHERE requestid = ?1", nativeQuery = true)
	PrintingRecord findByRequestID(String requestID);
	
	@Query(value = "SELECT * FROM printing_record WHERE file_type = ?1", nativeQuery = true)
	ArrayList<PrintingRecord> findByFileType(String fileType);
	
	@Query(value = "SELECT * FROM printing_record WHERE file_type = 'Module' AND request_date > ?1", nativeQuery = true)
	ArrayList<PrintingRecord> getModules(String requestDate);
	
	@Query(value = "SELECT * FROM printing_record WHERE file_type = 'Office Form' AND request_date > ?1", nativeQuery = true)
	ArrayList<PrintingRecord> getOfficeForms(String requestDate);
	
	@Query(value = "SELECT * FROM printing_record WHERE file_type = 'Manual' AND request_date > ?1", nativeQuery = true)
	ArrayList<PrintingRecord> getManuals(String requestDate);
	
	@Query(value = "SELECT * FROM printing_record WHERE file_type = 'Exam' AND request_date > ?1", nativeQuery = true)
	ArrayList<PrintingRecord> getExams(String requestDate);
	
	@Query(value = "SELECT COUNT(*) FROM printing_record WHERE status = 'Pending'", nativeQuery = true)
	int countPendingRequests();

	@Query(value = "SELECT COUNT(*) FROM printing_record WHERE status = 'In Progress'", nativeQuery = true)
	int countInProgressRequests();

	@Query(value = "SELECT COUNT(*) FROM printing_record WHERE status = 'Completed'", nativeQuery = true)
	int countCompletedRequests();

	@Query(value = "SELECT COUNT(*) FROM printing_record WHERE status = 'Rejected'", nativeQuery = true)
	int countRejectedRequests();	

	@Modifying
	@Transactional
	@Query(value = "UPDATE printing_record r SET r.status = ?2 WHERE requestid = ?1", nativeQuery = true)
	int setNewStatus(String requestid, String status);
}
