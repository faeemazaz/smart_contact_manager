package com.smart.controller;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.security.Principal;
import java.util.Optional;

import javax.servlet.http.HttpSession;
import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import com.smart.entity.Contact;
import com.smart.entity.User;
import com.smart.handler.MessageHandler;
import com.smart.service.SmartService;

@Controller
@RequestMapping("/user")
public class UserControllor {
	
	@Autowired
	private SmartService smartService;

	@ModelAttribute
	public void getUser(Model mdl, Principal principal)
	{
		User user = smartService.getUserByUserName(principal.getName());
		mdl.addAttribute("userentity", user);
	}
	
	@RequestMapping(value = "/index", method = RequestMethod.GET)
	public String home(Model mdl)
	{
		mdl.addAttribute("title", "Smart Contact Manager");
		return "user/user_dashboard";
	}
	
	@RequestMapping(value = "/add-contact", method = RequestMethod.GET)
	public String goToAddContact(Model mdl)
	{
		mdl.addAttribute("title", "Add Contact");
		mdl.addAttribute("contactentity", new Contact());
		return "user/add_contact";
	}
	
	@RequestMapping(value = "/contact", method = RequestMethod.POST)
	public String addContact(@Valid @ModelAttribute("contactentity") Contact contact, BindingResult result,
			Model mdl, Principal principal, @RequestParam("profileImage") MultipartFile file, HttpSession httpSession) throws IOException
	{
		String url = "";
		try {
			if(result.hasErrors())
			{
				System.out.println(result);
				mdl.addAttribute("contactentity", contact);	
			}
			
			// get User globally
			User user = smartService.getUserByUserName(principal.getName());
			
			if(contact.getcId() != null)
			{
				// Update data into contact from user
				Contact	oldContact = smartService.findByContactId(contact.getcId()).get();
				if(file.isEmpty())
				{
					contact.setImage(oldContact.getImage());
				}
				else
				{
					File deleteFile = new ClassPathResource("static/img").getFile();
					File delfile = new File(deleteFile, oldContact.getImage());
					delfile.delete();
					
					contact.setImage(file.getOriginalFilename());
					File saveFile = new ClassPathResource("static/img").getFile();
					Path path = Paths.get(saveFile.getAbsolutePath() + File.separator + file.getOriginalFilename());
					Files.copy(file.getInputStream(), path, StandardCopyOption.REPLACE_EXISTING);
				}
				httpSession.setAttribute("message", new MessageHandler("Successfully update contact !!", "alert-success"));
				url = "redirect:/user/contact/" + contact.getcId();
			}
			else
			{	
				
				// Insert data into contact from user
				if(file.isEmpty())
				{
					System.out.println("File is empty");
					contact.setImage("contact.png");
				}
				else
				{	
					contact.setImage(file.getOriginalFilename());
					File saveFile = new ClassPathResource("static/img").getFile();
					Path path = Paths.get(saveFile.getAbsolutePath() + File.separator + file.getOriginalFilename());
					Files.copy(file.getInputStream(), path, StandardCopyOption.REPLACE_EXISTING);
				}
				httpSession.setAttribute("message", new MessageHandler("Successfully save contact !!", "alert-success"));
				url = "user/add_contact";
			}
			
			contact.setUser(user);
			user.getContact().add(contact);
			smartService.saveUser(user);
			
			mdl.addAttribute("contactentity", new Contact());
		} catch (Exception e) {
			e.printStackTrace();
			httpSession.setAttribute("message", new MessageHandler(e.getMessage() + " !!", "alert-danger"));
		}
			
		return url;
	}
	
	@RequestMapping(value = "/view-contact/{page}", method = RequestMethod.GET)
	public String viewContact(Model mdl, Principal principal, @PathVariable Integer page)
	{
		mdl.addAttribute("title", "View Contact");
		User user = smartService.getUserByUserName(principal.getName());
		
		Pageable pageable = PageRequest.of(page, 3);
		
		Page<Contact> contact = smartService.getContacts(user.getId(), pageable);
		
		mdl.addAttribute("contacts", contact);
		mdl.addAttribute("currentPage", page);
		mdl.addAttribute("totalPage", contact.getTotalPages());
		
		return "user/view_contact";
	}
	
	@RequestMapping(value="/contact/delete/{cId}", method = RequestMethod.GET)
	public String removeContact(@PathVariable Integer cId, HttpSession httpSession, Principal principal) throws IOException
	{
		User user = smartService.getUserByUserName(principal.getName());
		
		Optional<Contact> optionalContact = smartService.findByContactId(cId);
		Contact contact = optionalContact.get();
		
		if(contact.getUser().getId() == user.getId())
		{
			File file = new ClassPathResource("static/img").getFile();
			Path path = Paths.get(file.getAbsolutePath() + File.separator + contact.getImage());
			if(Files.exists(path))
			{
				System.out.println(path);
				Files.delete(path);
			}
			
			smartService.removeContact(contact);
			httpSession.setAttribute("message", new MessageHandler("Successfully deleted " + contact.getName(), "alert-success"));
		}
		return "redirect:/user/view-contact/0";
	}
	
	@RequestMapping(value="/contact/{cId}", method = RequestMethod.GET)
	public String detailsOfContact(@PathVariable Integer cId, Principal principal, Model mdl, HttpSession httpSession) throws IOException
	{
		User user = smartService.getUserByUserName(principal.getName());
		
		Optional<Contact> optionalContact = smartService.findByContactId(cId);
		Contact contact = optionalContact.get();
		if(user.getId() == contact.getUser().getId())
		{
			mdl.addAttribute("contacts", contact);
		}
		
		return "user/contact_detail";
	}
	
	@RequestMapping(value="/contact/update/{cId}", method = RequestMethod.GET)
	public String editContact(@PathVariable Integer cId, Principal principal, Model mdl) throws IOException
	{
		User user = smartService.getUserByUserName(principal.getName());
		
		Optional<Contact> optionalContact = smartService.findByContactId(cId);
		Contact contact = optionalContact.get();
		if(user.getId() == contact.getUser().getId())
		{
			mdl.addAttribute("contactentity", contact);
			mdl.addAttribute("update", "update");
		}
		
		return "user/add_contact";
	}
	
	@RequestMapping(value="/profile", method = RequestMethod.GET)
	public String viewProfile(Principal principal, Model mdl) throws IOException
	{
		mdl.addAttribute("title", "Profile");
		User user = smartService.getUserByUserName(principal.getName());
		mdl.addAttribute("user", user);
		
		return "user/show_profile";
	}
	
	
}
