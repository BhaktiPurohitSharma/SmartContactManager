package com.smart.bin;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@NoArgsConstructor @AllArgsConstructor @ToString @Getter @Setter
@Entity
@Table(name="CONTACT")
public class Contact {
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private int cid;
	private String firstName;
	private String lastName;
	private String work;
	private String email;
	private String phone;
	private String image;
	@Column(length = 50000)
	private String description;
		
	@ManyToOne(cascade = CascadeType.ALL,fetch = FetchType.EAGER)
	private User user;

	@Override
	public boolean equals(Object obj) {
		return this.cid == ((Contact)obj).getCid();
	}
	
	
}
