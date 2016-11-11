package com.walmart.ticketing;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest
public class TicketingServiceApplicationTests {

	@Rule
	public final ExpectedException exception = ExpectedException.none();

	@Test
	public void contextLoads() {
	}

}
