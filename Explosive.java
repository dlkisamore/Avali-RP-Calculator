/**
	Damage(spherical) = (yield / (4 * pi * r^2)) * SA(avali)
	Damage(shaped) = (yield / ((1/3) * pi * r^2 * r)) * SA(avali)
*/

import java.util.ArrayList;
import java.util.Collections;

public class Explosive extends Weapon {
	private ArrayList<Double> yields = new ArrayList<Double>(); //joules (1 g of TNT = 4184 J) (velocity of detonation: TNT: 6900 m/s)
	private ArrayList<String> shaped = new ArrayList<String>(); //true: directed explosion   false: radial explosion
	
	public Explosive(String type, String name, double yield, boolean shaped) {
		super(type, name);
		this.yields.add(yield);
		if(shaped) {
			this.shaped.add("Shaped");
		} else {
			this.shaped.add("Spherical");
		}
		organize();
	}
	
	public Explosive(String type, String name, double yield, String shaped) {
		super(type, name);
		this.yields.add(yield);
		this.shaped.add(shaped);
		organize();
	}
	
	public Explosive(String type, String name, ArrayList<Double> yields, ArrayList<Boolean> shaped) {
		super(type, name);
		for(Double d : yields) {
			this.yields.add(d);
		}
		for(Boolean b : shaped) {
			if(b) {
				this.shaped.add("Shaped");
			} else {
				this.shaped.add("Spherical");
			}
		}
		organize();
	}
	
	public ArrayList<Double> getYields() {
		return this.yields;
	}
	
	public void setYields(ArrayList<Double> yields) {
		this.yields = yields;
	}
	
	public ArrayList<String> getShaped() {
		return this.shaped;
	}
	
	public void setShaped(ArrayList<String> shaped) {
		this.shaped = shaped;
	}
	
	public void organize() {
		Collections.sort(this.yields);
		Collections.sort(this.shaped);
	}
}