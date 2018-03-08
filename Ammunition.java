/**
	Damage = KE = 0.5 * m * v^2
	
	Ammo must be in the following format:
		name:::mass:::velocity
*/

import java.util.ArrayList;
import java.util.Collections;

public class Ammunition extends Weapon {
	private ArrayList<Double> masses = new ArrayList<Double>(); //kilograms
	private ArrayList<Double> velocities = new ArrayList<Double>(); //meters per second
	
	public Ammunition(String type, String name, double mass, double velocity) {
		super(type, name);
		this.masses.add(mass);
		this.velocities.add(velocity);
		organize();
	}
	
	public Ammunition(String type, String name, ArrayList<Double> masses, ArrayList<Double> velocities) {
		super(type, name);
		for(Double d : masses) {
			this.masses.add(d);
		}
		for(Double d : velocities) {
			this.velocities.add(d);
		}
		organize();
	}
	
	public String getName() {
		return this.name;
	}
	
	public ArrayList<Double> getMasses() {
		return this.masses;
	}
	
	public Double getMass() {
		return this.masses.get(0);
	}
	
	public void setMasses(ArrayList<Double> masses) {
		this.masses = masses;
	}
	
	public ArrayList<Double> getVelocities() {
		return this.velocities;
	}
	
	public Double getVelocity() {
		return this.velocities.get(0);
	}
	
	public void setVelocities(ArrayList<Double> velocities) {
		this.velocities	= velocities;
	}
	
	public void organize() {
		Collections.sort(this.masses);
		Collections.sort(this.velocities);
	}
}