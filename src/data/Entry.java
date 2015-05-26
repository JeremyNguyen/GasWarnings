/**
 * Class for the model Entry
 * An Entry is a record of the data collected by the sensors, contains their id, the date of recording, and the values
 */

package data;

import java.util.Date;
import java.util.Random;

public class Entry implements Comparable<Entry> {
	
	private Date date;
	private float temperature;
	private float gas;
	private int id_temp = -1;
	private int id_gas = -1;
	
	public Entry(Date date, int id_temp, float temperature, int id_gas, float gas){
		this.date = date;
		this.temperature = temperature;
		this.gas = gas;		
		this.id_temp = id_temp;
		this.id_gas = id_gas;
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

	public int getId_temp() {
		return id_temp;
	}

	public void setId_temp(int id_temp) {
		this.id_temp = id_temp;
	}

	public int getId_gas() {
		return id_gas;
	}

	public void setId_gas(int id_gas) {
		this.id_gas = id_gas;
	}

	/* Creates an Entry randomly generated (for testing) */
	public static Entry randomEntry(){
		Random random = new Random();
		Entry e = new Entry(new Date(), -1, random.nextInt(100), -1, random.nextInt(100));
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
