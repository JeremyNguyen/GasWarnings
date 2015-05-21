package data;

import java.util.Date;
import java.util.Random;

public class Entry implements Comparable<Entry> {
	
	private Date date;
	private float temperature;
	private float gas;
	
	public Entry(float temperature, float gas){
		this.date = new Date();
		this.temperature = temperature;
		this.gas = gas;
	}
	
	public Entry(Date date, float temperature, float gas){
		this.date = date;
		this.temperature = temperature;
		this.gas = gas;		
	}

	public float getTemperature() {
		return temperature;
	}

	public void setTemperature(float temperature) {
		this.temperature = temperature;
	}

	public float getGas() {
		return gas;
	}

	public void setGas(float gas) {
		this.gas = gas;
	}
	
	public Date getDate() {
		return date;
	}

	public void setDate(Date date) {
		this.date = date;
	}

	@Override
	public String toString(){
		return "temp = "+temperature+" gas = "+gas+" date = "+date.toString();
	}
	
	public static Entry randomEntry(){
		Random random = new Random();
		Entry e = new Entry(random.nextInt(100),random.nextInt(100));
		return e;
	}

	@Override
	public int compareTo(Entry e) {
		return this.date.compareTo(e.getDate());
	}
	
	@Override
	public boolean equals(Object o){
		Entry e = (Entry) o;
		return this.compareTo(e) == 0;
	}
	
}
