package com.fdmgroup.documentuploader.model.account;

import com.fdmgroup.documentuploader.model.document.Document;
import com.fdmgroup.documentuploader.model.user.User;
import io.swagger.annotations.ApiModelProperty;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.SequenceGenerator;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.Set;

/**
 * Encapsulates information related to an account which exists within the
 * system.
 * 
 * @author Noah Anderson
 */
@Entity
public class Account implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -3154632996316510630L;

	@ApiModelProperty(notes = "Unique identifier of the Account.",
		example = "1", required = true)
	@Id
	@Column(name = "account_id")
	@GeneratedValue(generator = "account_gen", strategy = GenerationType.SEQUENCE)
	@SequenceGenerator(name = "account_gen", sequenceName = "account_seq", allocationSize = 1)
	private long id;

	@ApiModelProperty(notes = "Name of the Account.",
			example = "Account Name", required = true)
	@NotBlank(message = "{account.name.not-empty}")
	@Column(nullable = false, unique = true)
	private String name;

	@ApiModelProperty(notes = "Creator of the Account.", required = true)
	@NotNull(message = "{account.owner.not-empty}")
	@OneToOne(cascade = { CascadeType.REFRESH })
	@JoinColumn(nullable = false, name = "owner_id")
	private User owner;

	@ApiModelProperty(notes = "The service level of the Account.", required = true)
	@NotNull(message = "{account.service-level.not-empty}")
	@Enumerated(EnumType.STRING)
	private ServiceLevel serviceLevel;

	@ApiModelProperty(notes = "The collection of all users with access to the Account.",
			required = true)
	@NotNull(message = "{account.users.not-empty}")
	@ManyToMany(cascade = { CascadeType.REFRESH })
	@JoinTable(joinColumns = { @JoinColumn(name = "account_id") }, inverseJoinColumns = {
			@JoinColumn(name = "user_id") })
	private Set<User> users;

	@ApiModelProperty(notes = "The collection of all documents on the Account.",
		required = true)
	@NotNull(message = "{account.documents.not-empty}")
	@OneToMany(cascade = { CascadeType.MERGE, CascadeType.REFRESH, CascadeType.REMOVE })
	@JoinTable(joinColumns = { @JoinColumn(name = "account_id") }, inverseJoinColumns = {
			@JoinColumn(name = "document_id") })
	private Set<Document> documents;

	public Account() {
		super();
	}

	public Account(@NotBlank(message = "{account.name.not-empty}") String name,
			@NotNull(message = "{account.owner.not-empty}") User owner,
			@NotNull(message = "{account.service-level.not-empty}") ServiceLevel serviceLevel,
			@NotNull(message = "{account.users.not-empty}") Set<User> users,
			@NotNull(message = "{account.documents.not-empty}") Set<Document> documents) {
		this.name = name;
		this.owner = owner;
		this.serviceLevel = serviceLevel;
		this.users = users;
		this.documents = documents;
	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public User getOwner() {
		return owner;
	}

	public void setOwner(User owner) {
		this.owner = owner;
	}

	public ServiceLevel getServiceLevel() {
		return serviceLevel;
	}

	public void setServiceLevel(ServiceLevel serviceLevel) {
		this.serviceLevel = serviceLevel;
	}

	public Set<User> getUsers() {
		return users;
	}

	public void setUsers(Set<User> users) {
		this.users = users;
	}

	public Set<Document> getDocuments() {
		return documents;
	}

	public void setDocuments(Set<Document> documents) {
		this.documents = documents;
	}

	@Override
	public String toString() {
		return "Account [id=" + id + ", name=" + name + ", owner=" + owner + ", serviceLevel=" + serviceLevel
				+ ", users=" + users + ", documents=" + documents + "]";
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((documents == null) ? 0 : documents.hashCode());
		result = prime * result + (int) (id ^ (id >>> 32));
		result = prime * result + ((name == null) ? 0 : name.hashCode());
		result = prime * result + ((owner == null) ? 0 : owner.hashCode());
		result = prime * result + ((serviceLevel == null) ? 0 : serviceLevel.hashCode());
		result = prime * result + ((users == null) ? 0 : users.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Account other = (Account) obj;
		if (documents == null) {
			if (other.documents != null)
				return false;
		} else if (!documents.equals(other.documents))
			return false;
		if (id != other.id)
			return false;
		if (name == null) {
			if (other.name != null)
				return false;
		} else if (!name.equals(other.name))
			return false;
		if (owner == null) {
			if (other.owner != null)
				return false;
		} else if (!owner.equals(other.owner))
			return false;
		if (serviceLevel != other.serviceLevel)
			return false;
		if (users == null) {
			if (other.users != null)
				return false;
		} else if (!users.equals(other.users))
			return false;
		return true;
	}

}
