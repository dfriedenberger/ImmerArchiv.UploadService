package de.immerarchiv.job;

import static org.junit.Assert.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Test;

import de.immerarchiv.job.impl.BestBagitStrategy;
import de.immerarchiv.job.model.BagIt;

public class BestBagitStrategyTest {



	@Test
	public void testWithNoCandidates() {
		
		
		BestBagitStrategy strategy = new BestBagitStrategy();
		
		
		Map<BagIt, Double> candidates = new HashMap<>();
		
		List<BagIt> bagits = strategy.apply(candidates);
		assertEquals(0,bagits.size());
	}
	
	
	@Test
	public void testWithOneCandidate() {
		
		
		BestBagitStrategy strategy = new BestBagitStrategy();
		
		
		Map<BagIt, Double> candidates = new HashMap<>();
		
		 BagIt bagIt = new BagIt();
		 bagIt.setId("id");
		 bagIt.setRepo("repo1");

		candidates.put(bagIt, 1.0);
		
		List<BagIt> bagits = strategy.apply(candidates);
		
		
		assertEquals(1,bagits.size());
	}
	
	@Test
	public void testWithTwoCandidates() {
		
		
		BestBagitStrategy strategy = new BestBagitStrategy();
		
		
		Map<BagIt, Double> candidates = new HashMap<>();
		
		 BagIt bagIt = new BagIt();
		 bagIt.setId("id");
		 bagIt.setRepo("repo1");

		candidates.put(bagIt, 1.0);
		
		 BagIt bagIt2 = new BagIt();
		 bagIt2.setId("id");
		 bagIt2.setRepo("repo2");
		candidates.put(bagIt2, 0.1);

		List<BagIt> bagits = strategy.apply(candidates);
		
		
		assertEquals(2,bagits.size());
	}
	
	@Test
	public void testWithTwoDifferentCandidates() {
		
		
		BestBagitStrategy strategy = new BestBagitStrategy();
		
		
		Map<BagIt, Double> candidates = new HashMap<>();
		
		 BagIt bagIt = new BagIt();
		 bagIt.setId("id");
		 bagIt.setRepo("repo1");

		candidates.put(bagIt, 1.0);
		
		 BagIt bagIt2 = new BagIt();
		 bagIt2.setId("id2");
		 bagIt2.setRepo("repo2");
		candidates.put(bagIt2, 0.1);

		List<BagIt> bagits = strategy.apply(candidates);
		
		
		assertEquals(1,bagits.size());
		assertTrue(bagits.contains(bagIt));

	}
	
	@Test
	public void testWithThreeCandidates() {
		
		
		BestBagitStrategy strategy = new BestBagitStrategy();
		
		
		Map<BagIt, Double> candidates = new HashMap<>();
		
		 BagIt bagIt = new BagIt();
		 bagIt.setId("id");
		 bagIt.setRepo("repo1");

		candidates.put(bagIt, 1.0);
		
		 BagIt bagIt2 = new BagIt();
		 bagIt2.setId("id2");
		 bagIt2.setRepo("repo1");
		candidates.put(bagIt2, 0.1);
		
		 BagIt bagIt3 = new BagIt();
		 bagIt3.setId("id2");
		 bagIt3.setRepo("repo2");
		candidates.put(bagIt3, 0.1);

		List<BagIt> bagits = strategy.apply(candidates);
		
		
		assertEquals(2,bagits.size());
		assertTrue(bagits.contains(bagIt2));
		assertTrue(bagits.contains(bagIt3));

	}
	

}
