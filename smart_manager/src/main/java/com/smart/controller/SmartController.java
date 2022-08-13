package com.smart.controller;

import javax.servlet.http.HttpSession;
import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import com.smart.entity.User;
import com.smart.handler.MessageHandler;
import com.smart.service.SmartService;

@Controller
public class SmartController {
	@Autowired
	private SmartService smartService;
	
	@Autowired
	private BCryptPasswordEncoder bCryptPasswordEncoder;
	
	@RequestMapping(value = "/", method = RequestMethod.GET)
	public String base(Model mdl)
	{
		mdl.addAttribute("title", "Smart Contact Manager");
		return "home";
	}
	
	@RequestMapping(value = "/about", method = RequestMethod.GET)
	public String about(Model mdl)
	{
		mdl.addAttribute("title", "About");
		return "about";
	}
	
	@RequestMapping(value = "/signup", method = RequestMethod.GET)
	public String signup(Model mdl)
	{
		mdl.addAttribute("title", "Signup");
		mdl.addAttribute("userentity", new User());
		return "signup";
	}
	
	@RequestMapping(value = "/adduser", method = RequestMethod.POST)
	public String saveUser(@Valid @ModelAttribute(name = "userentity") User user, BindingResult bindingResult, Model mdl, 
			@RequestParam(value = "agreement", defaultValue = "false") boolean agreement, HttpSession httpSession) throws Exception
	{
		try {
			if(!agreement)
			{
				mdl.addAttribute("userentiry", user);
				throw new Exception("You have not agreed condition");
			}
			
			if(bindingResult.hasErrors())
			{
				System.out.println(bindingResult);
				mdl.addAttribute("userentity", user);
				return "signup";
			}
			user.setPassword(bCryptPasswordEncoder.encode(user.getNewPassword()));
			user.setImageUrl("default.png");
			user.setRole("ROLE_USER");
			smartService.saveUser(user);
			
			mdl.addAttribute("user", new User());
			httpSession.setAttribute("message", new MessageHandler("Successfully Registration !!", "alert-success"));
		} catch (Exception e) {
			e.printStackTrace();
			mdl.addAttribute("user", new User());
			httpSession.setAttribute("message", new MessageHandler(e.getMessage() + " !!", "alert-danger"));
		}
		return "signup";
	}
	
	@RequestMapping(value = "/login")
	public String login(Model mdl)
	{
		mdl.addAttribute("title", "Login");
		return "login";
	}
}
