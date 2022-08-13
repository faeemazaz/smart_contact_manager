package com.smart.service;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.smart.entity.Contact;
import com.smart.entity.User;

public interface SmartService {
	public void saveUser(User user);
	public User getUserByUserName(String email);
	public Page<Contact> getContacts(Integer id, Pageable pageable);
	public Optional<Contact> findByContactId(Integer cId);
	public void removeContact(Contact contact); 
}
