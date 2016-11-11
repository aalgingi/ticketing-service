package com.walmart.ticketing.repository;

import java.util.Optional;

import org.springframework.stereotype.Repository;

import com.walmart.ticketing.domain.SeatHold;


@Repository
public interface SeatHoldRepository extends BaseRepository<SeatHold, Integer> {
	Optional<SeatHold> findByIdAndCustomerEmail(Integer id, String customerEmail);
}
