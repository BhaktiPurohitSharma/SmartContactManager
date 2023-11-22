package com.smart.bin;

import java.util.ArrayList;
import java.util.List;

import org.hibernate.validator.constraints.NotBlank;

import com.smart.helper.ValidPasswordHelper;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@NoArgsConstructor  @Getter @Setter @AllArgsConstructor
@Entity
@Table(name="USER")
public class User {
	
	@Id
	@GeneratedValue(strategy =GenerationType.AUTO)
	private int id;
	
	@NotBlank(message="User Name can not be empty !!")
	@Size(min=2,max=20,message = "min 2 and max 20 character are allowed")
	private String name;
	
	@Column(unique = true)
	@Email(regexp = "[a-z0-9]+@[a-z]+\\.[a-z]{2,3}", message = "invalid email")
	private String email;
	
	@ValidPasswordHelper
	private String password;
	private String role;
	@Override
	public String toString() {
		return "User [id=" + id + ", name=" + name + ", email=" + email + ", password=" + password + ", role=" + role
				+ ", imageUrl=" + imageUrl + ", about=" + about + ", enabled=" + enabled + "]";
	}

	private String imageUrl;
	@Column(length=500)
	private String  about;
	private boolean enabled;
	
	@OneToMany(cascade = CascadeType.ALL,fetch = FetchType.EAGER)
	private List<Contact> contacts=new ArrayList<Contact>();

}
