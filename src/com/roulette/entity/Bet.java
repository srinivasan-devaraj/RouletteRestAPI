package com.roulette.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;
import javax.persistence.Table;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Class entity to map the Bet table in DB
 * @author srini
 */
@Entity
@Table(name = "Bet")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Bet {

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	@Column(name = "Id")
	private Long id;

	@OneToOne
	private User user;

	@ManyToOne
	private Game game;

	@Column(name = "Time")
	private Long time;

	@Column(name = "Number")
	private int number;

	@Column(name = "Amount")
	private Long amount;

	@Column(name = "Status")
	private String status;

}
