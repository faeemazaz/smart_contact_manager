package com.smart.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.smart.entity.Contact;

@Repository
public interface ContactRepo extends JpaRepository<Contact, Integer>{
	@Query(value = "SELECT * FROM contact WHERE user_id = ?", nativeQuery = true)
	public Page<Contact> getContacts(Integer id, Pageable pageable);
}
