package de.immerarchiv.util.impl;

public class KeyImpl {

	private String primary;
	private String secondary;

	public KeyImpl(String[] ps, String[] ss) {

		for(String p : ps)
			validate(p);
		for(String s : ss)
			validate(s);
		
		this.primary = concat(ps);
		this.secondary = concat(ss);

		
	}

	
	public KeyImpl(KeyImpl key) {
		this.primary = key.primary;
		this.secondary = key.secondary;
	}


	private String concat(String[] s) {

		String c = "";
		for(int i = 0;i < s.length;i++)
			c += (i > 0?"#":"") + s[i];
		return c;
	}


	private void validate(String str) {
		if(str == null || str.isEmpty())
			throw new IllegalArgumentException();
	}
	
	public boolean samePrimary(KeyImpl key)
	{
		return primary.equals(key.primary);
	}
	
	@Override
	public String toString()
	{
		return primary + "##" + secondary;
	}
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((primary == null) ? 0 : primary.hashCode());
		result = prime * result
				+ ((secondary == null) ? 0 : secondary.hashCode());
		return result;
	}


	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		KeyImpl other = (KeyImpl) obj;
		if (primary == null) {
			if (other.primary != null)
				return false;
		} else if (!primary.equals(other.primary))
			return false;
		if (secondary == null) {
			if (other.secondary != null)
				return false;
		} else if (!secondary.equals(other.secondary))
			return false;
		return true;
	}


	public static KeyImpl parse(String key) {

		String p[] = key.split("##");
		
		if(p.length != 2)
			throw new IllegalArgumentException(key);
		return new KeyImpl(p[0].split("#"),p[1].split("#"));
		
	}

}
