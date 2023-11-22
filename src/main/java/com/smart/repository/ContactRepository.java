package com.smart.repository;


import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.smart.bin.Contact;

import jakarta.websocket.server.PathParam;

public interface ContactRepository extends JpaRepository<Contact,Integer> {
	
	//method for pagination..
	@Query("from Contact as c where c.user.id=:userId")
	//public List<Contact> getContactsByUser (@Param("userId")int userId);
	public Page<Contact> getContactsByUser (@PathParam("userId")int userId,Pageable pageable);

	//pageable must come from package of springframework 
}
