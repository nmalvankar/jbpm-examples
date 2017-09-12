package org.jbpm.test.jpa.project;

import java.io.Serializable;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.SequenceGenerator;

import org.drools.persistence.jpa.marshaller.VariableEntity;

@Entity
public class Person extends VariableEntity implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 6278099028079339226L;
	@Id
	@GeneratedValue(strategy = javax.persistence.GenerationType.AUTO, generator="PERSON_ID_GENERATOR")
	@SequenceGenerator(sequenceName = "PERSON_ID_SEQ", name = "PERSON_ID_GENERATOR")
	private Long id;
	private String firstName;
	private String lastName;
	private Integer age;
	private String address;
	
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
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
	public Integer getAge() {
		return age;
	}
	public void setAge(Integer age) {
		this.age = age;
	}
	public String getAddress() {
		return address;
	}
	public void setAddress(String address) {
		this.address = address;
	}
	
	
}
