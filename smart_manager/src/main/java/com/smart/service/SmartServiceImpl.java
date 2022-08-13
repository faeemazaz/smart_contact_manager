package com.smart.service;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.smart.entity.Contact;
import com.smart.entity.User;
import com.smart.repository.ContactRepo;
import com.smart.repository.UserRepo;

@Service
public class SmartServiceImpl implements SmartService{

	@Autowired
	private UserRepo userRepo;
	
	@Autowired
	private ContactRepo contactRepo;
	
	@Override
	public void saveUser(User user) {
		
		userRepo.save(user);
	}

	@Override
	public User getUserByUserName(String email) {
		return userRepo.getUserByUserName(email);
	}

	@Override
	public Page<Contact> getContacts(Integer id, Pageable pageable) {
		return contactRepo.getContacts(id, pageable);
	}
	
	@Override
	public Optional<Contact> findByContactId(Integer cId) {
		return contactRepo.findById(cId);
	}

	@Override
	public void removeContact(Contact contact) {
		contactRepo.delete(contact);
	}
	
}
