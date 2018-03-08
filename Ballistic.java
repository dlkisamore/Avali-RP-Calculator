/**
	Damage = KE * RateOfFire = rateOfFire * (0.5 * mass(ammo) * [muzzleVelocity(gun) OR velocity(ammo)]^2)
*/

import java.util.ArrayList;
import java.util.Collections;

public class Ballistic extends Weapon {
	private ArrayList<String> ammoTypes = new ArrayList<String>(); // ,,, used to separate ammo types
	private ArrayList<Double> muzzleVelocities = new ArrayList<Double>(); //defaults to ammo speed if value is 0 (m/s)
	private ArrayList<Double> ratesOfFire = new ArrayList<Double>(); //0 for non-automatic weapons (rounds / second)
	
	public Ballistic(String type, String name, ArrayList<String> ammoTypes, double muzzleVelocity, double rateOfFire) {
		super(type, name);
		Collections.sort(ammoTypes);
		for(String s : ammoTypes) { 
			this.ammoTypes.add(s);
		}
		this.muzzleVelocities.add(muzzleVelocity);
		this.ratesOfFire.add(rateOfFire);
		organize();
	}
	
	public Ballistic(String type, String name, ArrayList<String> ammoTypes, ArrayList<Double> muzzleVelocities, ArrayList<Double> ratesOfFire) {
		super(type, name);
		for(String s : ammoTypes) { 
			this.ammoTypes.add(s);
		}
		for(Double d : muzzleVelocities) {
			this.muzzleVelocities.add(d);
		}
		for(Double d : ratesOfFire) {
			this.ratesOfFire.add(d);
		}
		organize();
	}
	
	public ArrayList<String> getAmmoTypes() {
		return this.ammoTypes;
	}
	
	public String getAmmo(int index) {
		return this.ammoTypes.get(index);
	}
	
	public void addAmmo(String ammo) {
		this.ammoTypes.add(ammo);
	}
	
	public void setAmmoTypes(ArrayList<String> ammoTypes) {
		this.ammoTypes = ammoTypes;
	}
	
	public void addAmmo(Ammunition ammo) {
		this.ammoTypes.add(ammo.getName());
	}
	
	public ArrayList<Double> getRatesOfFire() {
		return this.ratesOfFire;
	}
	
	public void setRatesOfFire(ArrayList<Double> ratesOfFire) {
		this.ratesOfFire = ratesOfFire;
	}
	
	public ArrayList<Double> getMuzzleVelocities() {
		return this.muzzleVelocities;
	}
	
	public void setMuzzleVelocities(ArrayList<Double> muzzleVelocities) {
		this.muzzleVelocities = muzzleVelocities;
	}
	
	
	
	public void organize() {
		Collections.sort(this.ammoTypes);
		Collections.sort(this.muzzleVelocities);
		Collections.sort(this.ratesOfFire);
	}
}