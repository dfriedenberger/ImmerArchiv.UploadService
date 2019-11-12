package de.immerarchiv.job.impl;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.function.Function;
import java.util.stream.Collectors;

import de.immerarchiv.job.model.BagIt;

public class BestBagitStrategy implements Function<Map<BagIt, Double>, List<BagIt>> {

	private class Candidates
	{

		private final List<BagIt> bagits = new ArrayList<>();
		private final double match;
		private final String id;

		public Candidates(String id,List<Entry<BagIt, Double>> l) {
			
			this.id = id;
			
			List<Double> matches = new ArrayList<>();

			for(Entry<BagIt, Double> e : l)
			{
				bagits.add(e.getKey());	
				matches.add(e.getValue());
			}
			
			match = matches.stream().mapToDouble(Double::doubleValue).average().getAsDouble();
		}

		public List<BagIt> bagits() {
			return bagits;
		}

		public int size() {
			return bagits.size();
		}

		public double match() {
			return match;
		}

		public String id() {
			return id;
		}

		@Override
		public String toString() {
			return "Candidates [bagits=" + bagits.size() + ", match=" + match
					+ ", id=" + id + "]";
		}
		
	}
	
	private Comparator<Candidates> comparator = new Comparator<Candidates>() {

		@Override
		public int compare(Candidates c2, Candidates c1) {

			
			if(c1.size() != c2.size())
				return Integer.compare(c1.size(), c2.size());
			
			if(c1.match() != c2.match())
				return Double.compare(c1.match(), c2.match());
			return c1.id().compareTo(c2.id());
		}
		
	};

	@Override
	public List<BagIt> apply(Map<BagIt, Double> matching) {

		
		if(matching.size() == 0)
			return new ArrayList<>();
		
		//Convert to Candidates List
		List<Candidates> candidates = 
				matching.entrySet().stream().collect(Collectors.groupingBy(e -> e.getKey().getId()))
				 .entrySet().stream().map(e -> new Candidates(e.getKey(),e.getValue())).collect(Collectors.toList());
		
		return candidates.stream().sorted(comparator).findFirst().get().bagits();
	
	}

}
