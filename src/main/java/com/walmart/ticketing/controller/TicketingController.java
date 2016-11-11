package com.walmart.ticketing.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.walmart.ticketing.domain.SeatHold;
import com.walmart.ticketing.dto.Response;
import com.walmart.ticketing.exception.TicketingServiceErrorCode;
import com.walmart.ticketing.exception.TicketingServiceException;
import com.walmart.ticketing.service.ResetService;
import com.walmart.ticketing.service.TicketService;

import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;

@RestController
@RequestMapping("/venue")
public class TicketingController {

	@Autowired
	TicketService ticketService;

	@Autowired
	ResetService resetService;

	@ApiOperation(value = "Find total number of seats available in the venue",
			notes = "The returned number can be zero or more",
			response = Integer.class)
	@ApiResponses(value = { 
			@ApiResponse(code = 200, message = ""),
			@ApiResponse(code = 500, message = "Server error")})
	@RequestMapping(value="/total-seats-available", method = RequestMethod.GET)
	ResponseEntity<Integer> getAvailableSeats() {
		return new ResponseEntity<>(ticketService.numSeatsAvailable(), HttpStatus.OK);
	}

	@ApiOperation(value = "put seats on hold",
			notes = "Use the provided number of seats to but the best available seats on hold.",
			response = SeatHold.class)
	@ApiResponses(value = { 
			@ApiResponse(code = 400, message = "Invalid numSeat/email supplied"),
			@ApiResponse(code = 500, message = "Server error")})
	@RequestMapping(value="/hold/num-seats/{totalSeats}/email/{email}", method = RequestMethod.POST)
	ResponseEntity<SeatHold> findAndHoldSeats( @ApiParam(value = "Total number of seats to be reserved", required = true) @PathVariable Integer totalSeats, @ApiParam(value = "Customer email to be used for the hold", required = true) @PathVariable String email) {
		SeatHold hold = ticketService.findAndHoldSeats(totalSeats, email);
		return new ResponseEntity<>(hold, HttpStatus.OK);
	}

	@ApiOperation(value = "Confirm seat hold",
			notes = "Confirm a seat hold using the provided holdId and email.",
			response = String.class)
	@ApiResponses(value = { 
			@ApiResponse(code = 400, message = "Invalid ID/email supplied"),
			@ApiResponse(code = 404, message = "hold not found"),
			@ApiResponse(code = 500, message = "Server error")})
	@RequestMapping(value="/hold/{holdId}/email/{email}/reserve", method = RequestMethod.POST)
	ResponseEntity<String> reserveSeats(@ApiParam(value = "Existing holdId", required = true) @PathVariable Integer holdId, @ApiParam(value = "Customer email address", required = true) @PathVariable String email) {
		String confirmationCode = ticketService.reserveSeats(holdId, email);
		return new ResponseEntity<>(confirmationCode, HttpStatus.OK);	
	}

	@ApiOperation(value = "Reset venue reservations and hold",
			notes = "Get rid of all existing hold and reservations in the venue",
			response = String.class)
	@ApiResponses(value = { 
			@ApiResponse(code = 500, message = "Server error")})
	@RequestMapping(value="/reset", method = RequestMethod.POST)
	ResponseEntity<Response> resetVenue() {
		resetService.resetVenue();
		Response response = new Response();
		response.setStatus(200);
		response.setMessage("venue reset complete");
		return new ResponseEntity<>(response, HttpStatus.OK);	
	}

	@ExceptionHandler(TicketingServiceException.class)
	public ResponseEntity<Response> exceptionHandler(Exception ex) {
		TicketingServiceException serviceException = (TicketingServiceException) ex;
		Response response = new Response();
		response.setMessage(serviceException.getMessage());

		if(serviceException.getErrorCode() == TicketingServiceErrorCode.E_HoldNotFound){
			response.setStatus(404);
			return new ResponseEntity<Response>(response, HttpStatus.NOT_FOUND);
		}
		else if(serviceException.getErrorCode() == TicketingServiceErrorCode.E_SeatsNumberNotAvaiable
				|| serviceException.getErrorCode() == TicketingServiceErrorCode.E_HoldExpired
				|| serviceException.getErrorCode() == TicketingServiceErrorCode.E_InvalidEmailAddress
				|| serviceException.getErrorCode() == TicketingServiceErrorCode.E_InvalidNumSeat){
			response.setStatus(400);
			return new ResponseEntity<Response>(response, HttpStatus.BAD_REQUEST);
		}
		else{
			response.setStatus(500);
			return new ResponseEntity<Response>(response, HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}
}
