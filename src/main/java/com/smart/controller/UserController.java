package com.smart.controller;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.security.Principal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import com.smart.bin.Contact;
import com.smart.bin.Message;
import com.smart.bin.User;
import com.smart.repository.ContactRepository;
import com.smart.repository.UserRepository;
import com.smart.service.UserService;

import jakarta.servlet.http.HttpSession;

@Controller
@RequestMapping("/user")
public class UserController {
	
	@Autowired
	UserService userService;
	User currentLogInUserDetails = null;

	/* Common data retrieved for all handler methods */
	@ModelAttribute
	public void commonUserData(Model model, Principal principal) {
		// to get username who logged in i.e email
		String userName = principal.getName();
		System.out.println("Logged In Username :" + userName);
		User currentLogInUserDetails = userService.getUserByUserName(userName);
		System.out.println(currentLogInUserDetails);
		model.addAttribute("user", currentLogInUserDetails);
		this.currentLogInUserDetails = currentLogInUserDetails;
	}

	/* User home handler */
	@GetMapping("/index")
	public String dashBoard(Model model) {
		System.out.println("User Controller - Smart Contact Manager displaying userdashboard");
		model.addAttribute("title", "User Dashboard");
		return "user/user_dashboard";
	}

	/* Handler to add contact to user dash-board */
	@GetMapping("/addContact")
	public String openAddContactForm(Model model) {
		System.out.println("User Controller - Smart Contact Manager userdashboard add form");
		model.addAttribute("title", "Add Contact");
		model.addAttribute("contact", new Contact());
		return "user/addContactForm";
	}


	//Add contact-form-data processing to store in respective users account
	@PostMapping("/processContact")
	public String processContact(@ModelAttribute Contact contact, 
			@RequestParam("profileImage") MultipartFile file,
			Principal principal, 
			HttpSession session) 
	{
		Path path = null;
		String originalFilename = null;
		/**
		 * Here we added contact to respective user list to get list of contact using
		 * method First we retrieve current user next add current user into his contact
		 * fields next add this contact info retrieved from form data into users contact
		 * List send this updated contact form data to user contact-list
		 */
		 /** making image name unique*/
		String currDateTime= (LocalDateTime.now()+"").replace(":", "-");
		try {
			/**
			 * Setting explicitly retrieving image data using @RequestParam, first save
			 * image into /resource/static/image folder then save this image unique name
			 * into database as a Url string
			 */
			System.out.println("User Controller - Smart Contact Manager userdashboard processing form");
			System.out.println("Added Contact:" + contact);

			String name = principal.getName();
			User user = this.userService.getUserByUserName(name);

			// processing and uploading file
			if (file.isEmpty()) {
				// if file is empty
				System.out.println("file is empty");
			}
			else {
				// upload the file to folder & update name to contact
				originalFilename = currDateTime+"@"+file.getOriginalFilename();
				/** retrieve current class-path resource folder relative path */
				File saveFile = new ClassPathResource("static/images").getFile();
				path = Paths.get(saveFile.getAbsolutePath()+File.separator+originalFilename);
				System.out.println("Image path :"+path);
				//Files.copy(file.getInputStream(), path, StandardCopyOption.REPLACE_EXISTING);
				contact.setImage(originalFilename);
				System.out.println("File is Uploded");

			}
			//first complete contact form setting all attributes details
			contact.setUser(currentLogInUserDetails);
			/**
			 * Retrieving current users all contact list and again add current retrieved
			 * contact info into the list
			 */
			user.getContacts().add(contact);
			//save this updated or added contacts user into database
			User addedContactResult=this.userService.addContactInUser(user);
			if(addedContactResult !=null) {	
				/** Now actual storing image into given path, when first Registered this file path location into DB then now*/
				Files.copy(file.getInputStream(),path, StandardCopyOption.REPLACE_EXISTING);
				System.out.println("After successful contact added : "+addedContactResult);
			}
			/** success message alert */
			session.setAttribute("message", new Message("Your Contact added Successfully", "success"));
		} 
		catch (Exception e) 
		{
			System.out.println("ERROR:" + e.getMessage());
			e.printStackTrace();
			// message error...
			session.setAttribute("message", new Message("Something went wroung", "danger"));

		}
		return "user/addContactForm";
	}

	// per page= n=5 contact
	// current page=0[page]
	@GetMapping("/showContacts/{page}")
	public String openShowContactForm(@PathVariable("page") Integer page, Model model, Principal principal) {
		System.out.println("User Controller - Smart Contact Manager userdashboard show form");
		model.addAttribute("title", "show Contact");
		/** first get current log in user details */
		String userName = principal.getName();
		User user = this.userService.getUserByUserName(userName);

		/**
		 * First we will get Pageable objects to store current page index and number of
		 * records to show end user
		 * 
		 * - Current page index is - 0, --> page - Number of contacts per page = 6 --->
		 */
		Pageable pageable = PageRequest.of(page, 5);

		/** getting list of contacts from user */
		Page<Contact> contacts = this.userService.getContactsList(user.getId(), pageable);

		model.addAttribute("contacts", contacts);
		model.addAttribute("currentPage", page);
		model.addAttribute("totalPages", contacts.getTotalPages());
		return "user/showContactForm";
	}

	/** Show respective contact details */

	@GetMapping("/{cid}/contact")
	public String showContact(@PathVariable("cid") int cid, Principal principal, Model model) {

		System.out.println("User Controller - Smart Contact Manager userdashboard show Contact details handler");
		System.out.println("CID : " + cid);
		model.addAttribute("title", "Contact details");

		/** Retrieving user contact details */
		Contact contact= this.userService.getContactById(cid);

		String currentUser = principal.getName();
		User user = this.userService.getUserByUserName(currentUser);

		/**
		 * checking if url miss-leading happen then check both current login-user and
		 * contact user name is same
		 */
		if (!(user.getId() == contact.getUser().getId()))
			model.addAttribute("message", new Message("You are not an authorized user for this contact", "denger"));
		else
			model.addAttribute("contact", contact);

		return "user/showUserContactDetails";
	}
	
	
	/** deleting contact from given user lists */
	@GetMapping("/deleteContact/{cid}")
	public String deleteContact(@PathVariable("cid") Integer cid, 
								Principal principal, 
								Model model,
								HttpSession httpSession)
	{
		System.out.println("User Controller - Smart Contact Manager userdashboard Delete Contact details handler");
		System.out.println("CID : " + cid);
		model.addAttribute("title", "Contact details");
		
		/** first take current login user */
		String currentUser = principal.getName();
		User user= this.userService.getUserByUserName(currentUser);
		
		/** take contact details by using its ID */
		Contact contact= this.userService.getContactById(cid);
		
		
		
		/** compare both userID for avoiding url miss-leading purpose  */
		if(user.getId() == contact.getUser().getId()) 
		{
			/**Remove contact from given user List*/
			this.userService.deleteContact(user, contact);
			httpSession.setAttribute("message",new Message("Contact Deleted Succeussfully","success"));
			
			/**delete image from our folder also*/
			//code here
		}
		else 
		{
			model.addAttribute("message", new Message("You are not an authorized user for this contact", "danger"));
		}
		return "redirect:/user/showContacts/0";
	}
	
	/** Open Update form handler */
	@GetMapping("/updateContact/{cid}")
	public String updateContact(@PathVariable("cid") Integer cid, Model model ) 
	{
		/** take contact details by using its ID */
		Contact contact= this.userService.getContactById(cid);
		
		
		model.addAttribute("title", "Update contact - Smart Contact Manager");
		model.addAttribute("subTitle", "Update your Contact");
		model.addAttribute("contact", contact);
		
		return "user/updateContact";
	}

	/*	 Process the update contact form  */
	@PostMapping("/processUpdateContact")
	public String processUpdateContact(@ModelAttribute Contact contact,
										   @RequestParam("profileImage") MultipartFile file,
										   Model model,
										   Principal principal,
										   HttpSession session)
	{
		System.out.println("User Controller - Smart Contact Manager userdashboard update Contact details handler");

		//old contact details
		Contact oldContact = this.userService.getContactById(contact.getCid());

		try 
		{
			/** we need current user details to set inside contact entity fields */
			User currentUser = this.userService.getUserByUserName(principal.getName());

			/** now set this user into passed contact list */
			contact.setUser(currentUser);

			/** multi-part file writer */
			File saveFile = new ClassPathResource("/static/images").getFile();

			/** creating unique file-name for each image */
			String uniqueImageName = (LocalDateTime.now() + "").replace(":", "-") + "@" + file.getOriginalFilename();

			/** If file is not empty */
			if (!file.isEmpty())
			{
				/** means user send updated photo */
				// first delete previous photo from DB and from saved folder
				if (oldContact.getImage() != null)
				{
					File deleteFile = new File(saveFile, oldContact.getImage());
					deleteFile.delete();
				}
				// Update new image into our folder location
				Path path = Paths.get(saveFile + File.separator + uniqueImageName);
				Files.copy(file.getInputStream(), path, StandardCopyOption.REPLACE_EXISTING);
				/** now copied file name must be save with same name inside DATABASE */
				contact.setImage(uniqueImageName);

			} 
			else 
			{
				/** mean user not update photo, then set it's previous/old photo */
				if (oldContact.getImage() != null)
					contact.setImage(oldContact.getImage());
				else
					contact.setImage("contact_profile.png");
			}

			/**
			 * here contact is saved if exist then update otherwise it saved as new contact
			 */
			Contact updatedContact = this.userService.updateContactInUser(contact);
			session.setAttribute("message", new Message("Contact successfully updated", "success"));
		} 
		catch (Exception e) 
		{
			session.setAttribute("message", new Message("Contact updation failed ", "danger"));
			e.printStackTrace();
			model.addAttribute("contact", oldContact);
			return "redirect:/user/updateContact/" + contact.getCid();
		}

		System.out.println("contact name : " + contact.getFirstName());
		System.out.println("contact ID : " + contact.getCid());
		return "redirect:/user/" + contact.getCid() + "/contact";
	}

	@GetMapping("/profile")
	public String openProfileForm(Model model) {
		System.out.println("User Controller - Smart Contact Manager userdashboard profile form");
		model.addAttribute("title", "Profile");
		return "user/profile";
	}

}
