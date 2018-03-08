/**
	Damage = KE = 0.5 * m * v^2
	
	A forearm is roughly 2.292% the body weight of a human.
	A shank (shin area of the leg) is roughly 4.33% the body weight of a human.
*/

import java.util.ArrayList;
import java.util.Collections;

public class Melee extends Weapon {
	private ArrayList<Double> masses = new ArrayList<Double>();
	private ArrayList<Double> speeds = new ArrayList<Double>();
	
	public Melee(String type, String name, double mass, double speed) {
		super(type, name);
		this.masses.add(mass);
		this.speeds.add(speed);
		organize();
	}
	
	public Melee(String type, String name, ArrayList<Double> masses, ArrayList<Double> speeds) {
		super(type, name);
		for(Double d : masses) {
			this.masses.add(d);
		}
		for(Double d : speeds) {
			this.speeds.add(d);
		}
		organize();
	}
	
	public ArrayList<Double> getMasses() {
		return this.masses;
	}
	
	public void setMasses(ArrayList<Double> masses) {
		this.masses = masses;
	}
	
	public ArrayList<Double> getSpeeds() {
		return this.speeds;
	}
	
	public void setSpeeds(ArrayList<Double> speeds) {
		this.speeds = speeds;
	}
	
	public void organize() {
		Collections.sort(this.masses);
		Collections.sort(this.speeds);
	}
}