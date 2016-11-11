package com.walmart.ticketing.controller;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.setup.MockMvcBuilders.webAppContextSetup;

import org.apache.commons.collections.CollectionUtils;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.util.Assert;
import org.springframework.web.context.WebApplicationContext;

import com.walmart.ticketing.TicketingServiceApplicationTests;
import com.walmart.ticketing.domain.SeatHold;
import com.walmart.ticketing.repository.VenueRepository;
import com.walmart.ticketing.service.TicketService;

public class TicketingServiceControllerTest extends TicketingServiceApplicationTests {
	
	 
	    @InjectMocks
	    private TicketingController ticketingController;
	    
	    @Autowired
	    private WebApplicationContext webApplicationContext;
	 
	    private MockMvc mockMvc;
	    
	    
	    @Autowired
	    TicketService ticketService;
	    
	    @Autowired
	    VenueRepository venueRepository;
	 
	    @Before
	    public void setup() {
	        // Process mock annotations
	        MockitoAnnotations.initMocks(this);
	        this.mockMvc = webAppContextSetup(webApplicationContext).build();
	 
	    }
	    
	    
	    @Test
	    public void getAvailableSeats() throws Exception {
	        this.mockMvc.perform(get("/venue/total-seats-available"))
	                .andExpect(status().isOk());
	    }
	    
	    
	    @Test
	    public void findAndHoldSeats() throws Exception {
	        this.mockMvc.perform(post("/venue/hold/num-seats/5/email/zack.doe@gmail.com"))
            .andExpect(status().isOk());
	        
	        this.mockMvc.perform(post("/venue/hold/num-seats/500/email/zack.doe@gmail.com"))
            .andExpect(status().isBadRequest());
	     
	    }
	    
	    
	    @Test
	    public void reserveSeats() throws Exception {
	    	SeatHold hold = ticketService.findAndHoldSeats(7, "zack.doe@gmail.com");
	    	this.mockMvc.perform(post("/venue/hold/num-seats/5/email/zack.doe.2@gmail.com"))
            .andExpect(status().isOk());
	    	
	        this.mockMvc.perform(post("/venue/hold/"+hold.getId()+"/email/"+hold.getCustomerEmail()+"/reserve"))
            .andExpect(status().isOk());
	        
	        this.mockMvc.perform(post("/venue/hold/10/email/zack.doe.3@gmail.com/reserve"))
            .andExpect(status().isNotFound());
	    }
	    
	    
	    @Test
	    public void resetVenue() throws Exception {
	    	ticketService.findAndHoldSeats(7, "zack.doe@gmail.com");
	    	this.mockMvc.perform(post("/venue/reset"))
            .andExpect(status().isOk());
	    	Assert.isTrue(CollectionUtils.isEmpty(venueRepository.findAll().get(0).getSeatHolds()));
	    }
	    
	    

}
