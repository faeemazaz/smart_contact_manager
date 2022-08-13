package com.smart.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.web.bind.annotation.PathVariable;

import com.smart.entity.User;

@Repository
public interface UserRepo extends JpaRepository<User, Integer>{
	@Query("SELECT u FROM User u Where u.email = :email")
	public User getUserByUserName(@PathVariable("email") String email);
}
