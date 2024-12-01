package com.imps.IMPS.repositories;

import java.util.ArrayList;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import com.imps.IMPS.models.PrintingDetails;

@Repository
public interface PrintingDetailsRepository extends CrudRepository<PrintingDetails, Integer> {

    @Modifying
    @Transactional
    @Query(value = "DELETE FROM printing_details WHERE school_id = :schoolId", nativeQuery = true)
    void deleteBySchoolId(@Param("schoolId") String schoolId);


    @Query(value = "SELECT * FROM printing_details WHERE requestid = ?1 AND file_name = ?2", nativeQuery = true)
    PrintingDetails findByID(String requestID, String fileName);

    @Query(value = "SELECT * FROM printing_details WHERE requestid = ?1", nativeQuery = true)
    PrintingDetails getID(String requestID);

    @Query(value = "SELECT * FROM printing_details WHERE file_type = 'Module' AND request_date > ?1", nativeQuery = true)
    ArrayList<PrintingDetails> getModules(String requestDate);

    @Query(value = "SELECT * FROM printing_details WHERE file_type = 'Office Form' AND request_date > ?1", nativeQuery = true)
    ArrayList<PrintingDetails> getOfficeForms(String requestDate);

    @Query(value = "SELECT * FROM printing_details WHERE file_type = 'Manual' AND request_date > ?1", nativeQuery = true)
    ArrayList<PrintingDetails> getManuals(String requestDate);

    @Query(value = "SELECT * FROM printing_details WHERE file_type = 'Exam' AND request_date > ?1", nativeQuery = true)
    ArrayList<PrintingDetails> getExams(String requestDate);
}
