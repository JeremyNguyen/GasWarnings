/** 
 * Class for the model Warning
 * A Warning has start and end dates, and the id of the sensor triggering it
 */

package data;

import java.util.Date;

public class Warning {
	
	private Date start;
	private Date end;
	private int id_gas = -1;
	
	public Warning(int id_gas, Date start, Date end){
		this.start = start;
		this.end = end;
		this.id_gas = id_gas;
	}

	public Date getStart() {
		return start;
	}

	public void setStart(Date start) {
		this.start = start;
	}

	public Date getEnd() {
		return end;
	}

	public void setEnd(Date end) {
		this.end = end;
	}

	public int getId_gas() {
		return id_gas;
	}

	public void setId_gas(int id_gas) {
		this.id_gas = id_gas;
	}

}
