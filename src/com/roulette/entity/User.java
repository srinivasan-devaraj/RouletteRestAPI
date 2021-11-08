package com.roulette.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Class entity to map the User table in DB
 * @author srini
 */
@Entity
@Table(name = "User")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class User {

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	@Column(name = "Id")
	private Long id;

	@Column(name = "Name")
	private String name;

	@Column(name = "Email_Id", unique = true)
	private String email_id;

	@Column(name = "Amount")
	private Long amount = 0L;

	@OneToOne
	private Casino casino;

}