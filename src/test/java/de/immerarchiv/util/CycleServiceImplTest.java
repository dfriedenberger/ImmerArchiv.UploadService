package de.immerarchiv.util;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import de.immerarchiv.util.impl.CycleServiceImpl;
import de.immerarchiv.util.interfaces.CycleService;
import de.immerarchiv.util.interfaces.TimestampService;

public class CycleServiceImplTest {

	@Mock
	TimestampService timestampService;
	
	@Before
	public void init() {
		MockitoAnnotations.initMocks(this);
	}
	
	@Test
	public void testFirstCycle() {

		CycleService service = new CycleServiceImpl(timestampService,5,24 * 60);
		when(timestampService.getTime()).thenReturn(1234567L);
		
		service.triggerCycle("TESTEVENT"); //is ignored
		assertTrue(service.IsNextCycle());
		assertTrue(service.IsNextCycle());
		
	}
	
	@Test
	public void testNormalCycle() {

		
		CycleService service = new CycleServiceImpl(timestampService,5,60);
		when(timestampService.getTime()).thenReturn(1234567L);

		service.incrCycle();
		assertFalse(service.IsNextCycle());
		
		when(timestampService.getTime()).thenReturn(1234567L + 60 * 60 * 1000);  //1 Hour later	
		assertFalse(service.IsNextCycle());

		when(timestampService.getTime()).thenReturn(1234567L + 60 * 60 * 1000 + 1);  //1 Hour later	and 1 second
		assertTrue(service.IsNextCycle());
		
	}
	
	@Test
	public void testNormalDayCycle() {

		
		CycleService service = new CycleServiceImpl(timestampService,5,24 * 60);
		when(timestampService.getTime()).thenReturn(1234567L);

		service.incrCycle();
		assertFalse(service.IsNextCycle());
		
		when(timestampService.getTime()).thenReturn(1234567L + 60 * 60 * 1000);  //1 Hour later	
		assertFalse(service.IsNextCycle());

		when(timestampService.getTime()).thenReturn(1234567L + 60 * 60 * 1000 + 1);  //1 Hour later	and 1 second
		assertFalse(service.IsNextCycle());
		
		
		when(timestampService.getTime()).thenReturn(1234567L + 24 * 60 * 60 * 1000);  //1 Hour later	
		assertFalse(service.IsNextCycle());

		when(timestampService.getTime()).thenReturn(1234567L + 24 * 60 * 60 * 1000 + 1);  //1 Hour later	and 1 second
		assertTrue(service.IsNextCycle());
		
	}
	
	@Test
	public void testTriggeredCycle() {

		
		CycleService service = new CycleServiceImpl(timestampService,5,60);
		when(timestampService.getTime()).thenReturn(1234567L);

		service.incrCycle();
		assertFalse(service.IsNextCycle());
		
		
		service.triggerCycle("TestEvent");
		when(timestampService.getTime()).thenReturn(1234567L + 5 * 60 * 1000);  //5 Minutes later	
		assertFalse(service.IsNextCycle());

		when(timestampService.getTime()).thenReturn(1234567L + 5 * 60 * 1000 + 1);  //5 Minutes later and 1 second
		assertTrue(service.IsNextCycle());
		
	}
	
	@Test
	public void testTwiceTriggeredCycle() {

		
		CycleService service = new CycleServiceImpl(timestampService,5,60);
		when(timestampService.getTime()).thenReturn(1234567L);

		service.incrCycle();
		assertFalse(service.IsNextCycle());
		
		
		service.triggerCycle("TestEvent");
		when(timestampService.getTime()).thenReturn(1234567L + 5 * 60 * 1000);  //5 Minutes later	
		assertFalse(service.IsNextCycle());

		service.triggerCycle("TestEvent"); //next trigger in same time

		
		when(timestampService.getTime()).thenReturn(1234567L + 5 * 60 * 1000 + 1);  //5 Minutes later and 1 second
		assertTrue(service.IsNextCycle());
		
	}
	
	@Test
	public void testResetTrigger() {

		
		CycleService service = new CycleServiceImpl(timestampService,5,60);
		when(timestampService.getTime()).thenReturn(1234567L);

		service.incrCycle();
		assertFalse(service.IsNextCycle());
		
		
		service.triggerCycle("TestEvent");
	

		service.incrCycle(); //next trigger in same time

		
		when(timestampService.getTime()).thenReturn(1234567L + 5 * 60 * 1000 + 1);  //5 minutes later and 1 second
		assertFalse(service.IsNextCycle());
		
		when(timestampService.getTime()).thenReturn(1234567L + 60 * 60 * 1000 + 1);  //1 Hour later and 1 second
		assertTrue(service.IsNextCycle());
		
	}
	
	@Test
	public void testIgnoreTriggerIfWouldIncreaseCycle() {

		
		CycleService service = new CycleServiceImpl(timestampService,5,60);
		when(timestampService.getTime()).thenReturn(1234567L);

		service.incrCycle();
		assertFalse(service.IsNextCycle());
		
		when(timestampService.getTime()).thenReturn(1234567L + 60 * 60 * 1000);  //1 Hour later	
		assertFalse(service.IsNextCycle());

		service.triggerCycle("TestEvent"); //Ignore

		when(timestampService.getTime()).thenReturn(1234567L + 60 * 60 * 1000 + 1);  //1 Hour later and 1 second
		assertTrue(service.IsNextCycle());
		
		
	}

}
