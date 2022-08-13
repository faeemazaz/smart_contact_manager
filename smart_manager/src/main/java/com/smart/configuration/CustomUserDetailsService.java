package com.smart.configuration;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import com.smart.entity.User;
import com.smart.repository.UserRepo;

public class CustomUserDetailsService implements UserDetailsService{

	@Autowired
	private UserRepo userRepo;
	
	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		User user = userRepo.getUserByUserName(username);
		if(user == null)
		{
			throw new UsernameNotFoundException(username);
		}
		
		CustomUserDetails getUserByUserName = new CustomUserDetails(user);
		
		return getUserByUserName;
	}

}
