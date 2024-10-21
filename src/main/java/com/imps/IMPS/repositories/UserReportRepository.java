package com.imps.IMPS.repositories;

import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import com.imps.IMPS.models.UserReport;

public interface UserReportRepository extends CrudRepository<UserReport, Long> {

    @Modifying
    @Transactional
    @Query(value = "UPDATE user_report ur SET ur.role = :role, ur.status = :status WHERE ur.email = :email", nativeQuery = true)
    int updateUserReport(@Param("role") String role, @Param("status") String status, @Param("email") String email);

    @Query(value = "SELECT * FROM user_report WHERE email = ?1", nativeQuery = true)
    UserReport findByEmail(String email);

    @Query(value = "SELECT COUNT(*) FROM user_report u WHERE u.status = 'Accepted'", nativeQuery = true)
    int countAcceptedUsers();

    @Query(value = "SELECT COUNT(*) FROM user_report u WHERE u.status = 'Declined'", nativeQuery = true)
    int countDeclinedUsers();
    
    @Query(value = "SELECT COUNT(*) FROM user_report", nativeQuery = true)
    int countAllUsers();
}
