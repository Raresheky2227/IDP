package com.raresheky.userauth.entity;

import jakarta.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "users")
public class User {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(unique = true, nullable = false)
	private String username;

	@Column(nullable = false)
	private String password;

	@ElementCollection(fetch = FetchType.EAGER)
	@CollectionTable(
			name = "user_roles",
			joinColumns = @JoinColumn(name = "user_id")
	)
	@Column(name = "role")
	private List<String> roles = new ArrayList<>();

	public User() {}

	public User(String username, String password, List<String> roles) {
		this.username = username;
		this.password = password;
		this.roles = roles;
	}

	// getters & setters omitted for brevity
	// … (as in previous snippet)
}
