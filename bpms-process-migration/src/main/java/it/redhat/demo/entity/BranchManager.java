package it.redhat.demo.entity;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.SequenceGenerator;

import org.drools.persistence.jpa.marshaller.VariableEntity;

@Entity
public class BranchManager extends VariableEntity {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -5127736260337310794L;
	
	private String firstName;
	private String lastName;
	private String location;
	private String branchCode;

	@Id
	@GeneratedValue(strategy = javax.persistence.GenerationType.AUTO, generator = "BRANCHMANAGER_ID_GENERATOR")
	@SequenceGenerator(sequenceName = "BRANCHMANAGER_ID_SEQ", name = "BRANCHMANAGER_ID_GENERATOR")
	private Long id;

	public String getFirstName() {
		return firstName;
	}

	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}

	public String getLastName() {
		return lastName;
	}

	public void setLastName(String lastName) {
		this.lastName = lastName;
	}

	public String getLocation() {
		return location;
	}

	public void setLocation(String location) {
		this.location = location;
	}

	public String getBranchCode() {
		return branchCode;
	}

	public void setBranchCode(String branchCode) {
		this.branchCode = branchCode;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public static long getSerialversionuid() {
		return serialVersionUID;
	}

}
