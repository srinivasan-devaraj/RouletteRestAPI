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
 * Class entity to map the Game table in DB
 * @author srini
 */
@Entity
@Table(name = "Game")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Game {

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	@Column(name = "Id")
	private Long id;

	@Column(name = "Start_Time")
	private Long start_time;

	@Column(name = "End_Time")
	private Long end_time;

	@OneToOne
	private Dealer dealer;

	@Column(name = "Status")
	private String status;

	@Column(name = "Thrown_Number")
	private String thrown_number;

}
