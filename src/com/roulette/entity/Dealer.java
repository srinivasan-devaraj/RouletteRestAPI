package com.roulette.entity;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Transient;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Class entity to map the Dealer table in DB
 * @author srini
 */
@Entity
@Table(name = "Dealer")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Dealer {

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	@Column(name = "Id")
	private Long id;

	@Column(name = "Name")
	private String name;

	@Column(name = "Email_Id")
	private String email_id;

	@ManyToOne
	@JoinColumn(name = "casino_id", referencedColumnName = "id")
	private Casino casino;

	@Transient
	@OneToMany
	private List<Game> game = new ArrayList<>();

}
