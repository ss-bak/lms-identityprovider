package com.smoothstack.lms.auth.model;

import java.io.Serializable;
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

@Entity
@Table(name = "t_role")
@Access(AccessType.FIELD)
public class Role implements Serializable {

	@Id
	@Column(name = "roleId")
	@SequenceGenerator(name = "role", sequenceName = "roleId", initialValue = 1, allocationSize = 1)
	@GeneratedValue(generator = "role")
	@NotNull
	private long roleId;

	@Column(name = "roleName", nullable = false)
	@NotNull
	@NotBlank
	private String roleName;

	@ManyToMany(cascade = { CascadeType.PERSIST, CascadeType.MERGE, CascadeType.DETACH }, fetch = FetchType.LAZY)
	@JoinTable(name = "r_user_role", joinColumns = @JoinColumn(name = "roleId", referencedColumnName = "roleId"), inverseJoinColumns = @JoinColumn(name = "userId", referencedColumnName = "userId"))
	private Set<User> roleUserSet = new HashSet<>();

	public Role() {
	}

	public Role(@NotNull @NotBlank String roleName) {
		this.roleName = roleName;
	}

}
