package com.smart.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.smart.bin.Message;
import com.smart.bin.User;
import com.smart.repository.UserRepository;

import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;

@Controller
public class HomeController {
	
	@Autowired
	private UserRepository userRepository;
	
	@Autowired
	private BCryptPasswordEncoder passwordEncoder;
	
	@GetMapping("/")
	public String home(Model model) {
		System.out.println("Home Controller - Smart Contact Manager home Display");
		model.addAttribute("title","Home- Smart Contact Manager");
		return "home";
	}
	
	@GetMapping("/about")
	public String about(Model model) {
		System.out.println("Home Controller - Smart Contact Manager about display");
		model.addAttribute("title","About- Smart Contact Manager");
		return "about";
	}
	
	@GetMapping("/signup")
	public String signup(Model model) {
		System.out.println("Home Controller - Smart Contact Manager Sign up Form");
		model.addAttribute("title","Register- Smart Contact Manager");
		model.addAttribute("user",new User());
		return "signup";
	}
	

	

	/**
	 * Handler for saving user
	 * @param user 		---getting user data from signup.html page
	 * @param agreement ---getting value of check box signup.html page and converting into boolean
	 * @param Valid		---to add validation on user using hibernet validation
	 * @param BindingResult ---
	 */
	@PostMapping("/register")
	public String registerUser(@Valid@ModelAttribute("user") User user,
			BindingResult bindingResult,
			@RequestParam(value = "agreement",defaultValue = "false" )boolean agreement,
			Model model,HttpSession session)
	{
		System.out.println("Home Controller - Smart Contact Manager registaring user");
		try {
		
		if(bindingResult.hasErrors()){
			System.out.println(bindingResult);
			return "signup";
		}	
		
		if(!agreement) {
			System.out.println("You Have not Agreed terms and condition");
			throw new Exception("You Have not Agreed terms and condition");
		}
		
		//setting role id as user
		user.setRole("ROLE_USER");
		user.setEnabled(true);
		user.setImageUrl("dummy.png");
		user.setPassword(passwordEncoder.encode(user.getPassword()));
		//printing just for checking
		System.out.println("Aggrement "+agreement);
		System.out.println("USER "+user);
		//saving into database
		User result= this.userRepository.save(user);
		//printing saved data
		System.out.println("SAVED USER "+result);
		//adding saved user is user successful then send new i.e blank user on next page
		model.addAttribute("user",new User());
		//sending successful message
		session.setAttribute("message",new Message("User registred successfully ","alert-success"));
		//alert-success and alert-danger are classes of bootstrap which we are sending dynamically to show color
		}
		catch (Exception e) {
			e.printStackTrace();
			//if any exception send old data
			model.addAttribute("user",user);
			//adding error massage in session
			session.setAttribute("message",new Message("Something Went Wroung!! ","alert-danger"));
			return "signup";
		}
		return "signup";
	}
	
	
	//handler for custom login
	
	@GetMapping("/signin")
	public String customLogin(Model model) {
		System.out.println("Home Controller - Smart Contact Manager Sign in Form");
		model.addAttribute("title","Welcome- Smart Contact Manager");
		return "signin";
	}
	
}
