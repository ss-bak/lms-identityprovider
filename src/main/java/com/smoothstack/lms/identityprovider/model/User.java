package com.smoothstack.lms.identityprovider.model;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import javax.persistence.Access;
import javax.persistence.AccessType;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

@Entity
@Table(name = "t_user")
@Access(AccessType.FIELD)
public class User implements UserDetails {

	@Id
	@Column(name = "userId")
	@SequenceGenerator(name = "user", sequenceName = "userId", initialValue = 1, allocationSize = 1)
	@GeneratedValue(generator = "user")
	@NotNull
	private long userId;

	@Column(name = "username")
	@NotNull
	@NotBlank
	private String username;

	@Column(name = "password", nullable = false)
	@NotNull
	@NotBlank
	private String password;

	@ManyToMany(cascade = { CascadeType.PERSIST, CascadeType.MERGE, CascadeType.DETACH }, fetch = FetchType.LAZY)
	@JoinTable(name = "r_user_role", joinColumns = @JoinColumn(name = "userId", referencedColumnName = "userId"), inverseJoinColumns = @JoinColumn(name = "roleId", referencedColumnName = "roleId"))
	private Set<Role> userRoleSet = new HashSet<>();

	public User() {
	}

	public User(@NotNull @NotBlank String username, @NotNull @NotBlank String password) {
		this.username = username;
		this.password = password;
	}

	@Override
	public Collection<? extends GrantedAuthority> getAuthorities() {
		// TODO Auto-generated method stub
		return null;
	}

	public long getUserId() {
		return userId;
	}

	public void setUserId(long userId) {
		this.userId = userId;
	}

	@Override
	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	@Override
	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public Set<Role> getUserRoleSet() {
		return userRoleSet;
	}

	public void setUserRoleSet(Set<Role> userRoleSet) {
		this.userRoleSet = userRoleSet;
	}

	@Override
	public boolean isAccountNonExpired() {
		// TODO Auto-generated method stub
		return true;
	}

	@Override
	public boolean isAccountNonLocked() {
		// TODO Auto-generated method stub
		return true;
	}

	@Override
	public boolean isCredentialsNonExpired() {
		// TODO Auto-generated method stub
		return true;
	}

	@Override
	public boolean isEnabled() {
		// TODO Auto-generated method stub
		return true;
	}

}
