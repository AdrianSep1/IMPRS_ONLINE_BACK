package com.imps.IMPS.repositories;

import java.util.ArrayList;
import java.util.Optional;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.transaction.annotation.Transactional;

import com.imps.IMPS.models.HomeDetails;

public interface HomeRepository extends CrudRepository<HomeDetails, Integer> {
		
	// Correcting the findById method in HomeRepository to use Integer for ID
	@Query(value = "SELECT * FROM home_details WHERE id = ?1", nativeQuery = true)
	Optional<HomeDetails> findById(Integer id);
	
	@Query(value = "SELECT * FROM home_details", nativeQuery = true)
	ArrayList<HomeDetails> getAll();
	
	@Modifying
	@Transactional
	@Query(value = "UPDATE home_details SET announcements = REPLACE(?2, '\\n', '<br>') WHERE id = ?1", nativeQuery = true)
	int setAnnouncements(String ann, Integer id);
	
	@Modifying
	@Transactional
	@Query(value = "UPDATE home_details SET guidelines = ?1 WHERE id = ?2", nativeQuery = true)
	int setGuidelines(String guide, Integer id);
	
	@Modifying
	@Transactional
	@Query(value = "UPDATE home_details SET locations = ?1 WHERE id = ?2", nativeQuery = true)
	int setLocations(String loc, Integer id);
	
	@Modifying
	@Transactional
	@Query(value = "UPDATE home_details SET process = ?1 WHERE id = ?2", nativeQuery = true)
	int setProcess(String pro, Integer id);
	
	@Modifying
	@Transactional
	@Query(value = "UPDATE home_details SET updates = ?1 WHERE id = ?2", nativeQuery = true)
	int setUpdates(String upd, Integer id);
}
