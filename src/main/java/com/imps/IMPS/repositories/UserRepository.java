package com.imps.IMPS.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import com.imps.IMPS.models.User;

public interface UserRepository extends CrudRepository<User, Integer> {

    @Query(value = "SELECT * FROM user WHERE email = ?1 AND password = ?2", nativeQuery = true)
    User findByEmailAndPassword(String email, String password);
    
    @Query(value = "SELECT * FROM user WHERE is_head = 1", nativeQuery = true)
    User findHeadUser();
    
    @Query(value = "SELECT * FROM user WHERE is_admin = 1", nativeQuery = true)
    User findAdminUser();
    
    @Query(value = "SELECT * FROM user WHERE is_staff = 1", nativeQuery = true)
    List<User> findAllStaffUsers();

    @Query(value = "SELECT * FROM user WHERE email = ?1 AND password = ?2 AND is_admin = 1", nativeQuery = true)
    User checkAdmin(String email, String password);
    
    @Query(value = "SELECT * FROM user WHERE email = ?1 AND is_admin = 1", nativeQuery = true)
    User checkAdminEmail(String email);
    
    @Query(value = "SELECT * FROM user WHERE email = ?1", nativeQuery = true)
    User findByEmail(String email);
    
    @Query(value = "SELECT * FROM user WHERE name = ?1", nativeQuery = true)
    User findByName(String name);
        
    @Query("SELECT u FROM User u")
    List<User> getAll();

    @Query("SELECT u.role, COUNT(u) FROM User u GROUP BY u.role")
    List<Object[]> countEmployeesByRole();
    
    @Query(value = "SELECT * FROM user WHERE is_admin != 1", nativeQuery = true)
    Iterable<User> findAllNonAdminUsers();
    
    @Modifying
    @Transactional
    @Query(value = "UPDATE user SET token = ?2 WHERE email = ?1", nativeQuery = true)
    int setNewToken(String email, String token);
    
    @Query(value = "SELECT * FROM user WHERE email = ?1 AND token = ?2", nativeQuery = true)
    User findByEmailAndToken(String email, String token);

    @Query(value = "SELECT COUNT(*) FROM user", nativeQuery = true)
    int countAllUsers();

    @Modifying
    @Transactional
    @Query(value = "UPDATE user SET password = ?1 WHERE email = ?2 AND token = ?3", nativeQuery = true)
    int setNewPassword(String password, String email, String token);
    
    @Modifying
    @Transactional
    @Query(value = "UPDATE user SET password = ?1 WHERE email = ?2", nativeQuery = true)
    int setNewPasswordNoToken(String password, String email);
    
    @Modifying
    @Transactional
    @Query(value = "UPDATE user SET first_name = ?1 WHERE email = ?2", nativeQuery = true)
    int setNewFirstName(String name, String email);
    
    @Modifying
    @Transactional
    @Query(value = "UPDATE user SET last_name = ?1 WHERE email = ?2", nativeQuery = true)
    int setNewLastName(String name, String email);
    
    @Modifying
    @Transactional
    @Query(value = "UPDATE user SET email = ?1 WHERE email = ?2", nativeQuery = true)
    int setNewEmail(String newEmail, String email);
    
    @Query(value = "SELECT * FROM user WHERE school_id = ?1", nativeQuery = true)
    User findBySchoolId(String schoolId);

    @Query(value = "SELECT * FROM user WHERE email = ?1 AND school_id = ?2", nativeQuery = true)
    User findByEmailAndSchoolId(String email, String schoolId);

    @Query(value = "SELECT * FROM user WHERE role = ?1", nativeQuery = true)
    Iterable<User> findByRole(String role);

    @Query(value = "SELECT * FROM user WHERE department = ?1", nativeQuery = true)
    Iterable<User> findByDepartment(String department);
    
    @Query(value = "SELECT * FROM user WHERE role = ?1 AND department = ?2", nativeQuery = true)
    Iterable<User> findByRoleAndDepartment(String role, String department);
}
