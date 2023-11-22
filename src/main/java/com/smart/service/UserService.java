package com.smart.service;

import java.io.File;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import com.smart.bin.Contact;
import com.smart.bin.User;
import com.smart.repository.ContactRepository;
import com.smart.repository.UserRepository;

import jakarta.transaction.Transactional;

@Service
@Transactional
public class UserService 
{
	@Autowired
	UserRepository userRepository;
	@Autowired
	ContactRepository contactRepository;
	
	public User userRegister(User user) {
		System.out.println("userService : "+user );
		return userRepository.save(user);
	}
	
	public User findUserByEmail(String email) {
		User resultUser = userRepository.getUserByUserEmail(email);
		return resultUser;
	}
	
	public User getUserByUserName(String userName) {
		User resultUser = userRepository.getUserByUserName(userName);
		return resultUser;
	}
	
	public User addContactInUser(User user ) {
		User result = userRepository.save(user);
		return result;
	}
	
	/** find contact info by using user ID */
	public Contact getContactById(int cId) {
		Optional<Contact> optionalContact = this.contactRepository.findById(cId);
		Contact contact = optionalContact.get();
		return contact;
	}
	
	/** get all contacts list with respective users UserID */
	public Page<Contact> getContactsList(int userId, Pageable pageable){
		Page<Contact> listContactsByUser = this.contactRepository.getContactsByUser(userId, pageable);
		return listContactsByUser;
	}
	
	
/** delete contact by using ID */
	
	public void deleteContact(User user, Contact contact) {
		
		try {
				
		/** It is not deleted directly because its is mapped with user */
		user.getContacts().remove(contact);
			
		//contact.setUser(null);
		//this.contacRepository.delete(contact);
		
		// Now we must delete photo from folder
		 File saveFile = new ClassPathResource("/static/image").getFile();
		 
		File deleteFile = new File(saveFile,contact.getImage());
		deleteFile.delete();
		System.out.println(contact.getCid()+"ID Contact deleted successfully ");
		} catch (Exception e) {
			e.printStackTrace();
		}

		
	}
	
	public Contact updateContactInUser(Contact contact) {
		Contact saveContact = this.contactRepository.save(contact);
		return saveContact;
	}
	
	

}
