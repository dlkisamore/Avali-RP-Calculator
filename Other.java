/**
	This category will be used for both energy weapons and weapons that only have an energy output rather than specs. Energy outputs for these weapons will need manually calculated before input into the WeaponProfiles.txt
	
	Damage = output
*/

import java.util.ArrayList;
import java.util.Collections;

public class Other extends Weapon {
	private ArrayList<Double> outputs = new ArrayList<Double>();
	
	public Other(String type, String name, double output) {
		super(type, name);
		this.outputs.add(output);
		organize();
	}

	public Other(String type, String name, ArrayList<Double> outputs) {
		super(type, name);
		for(Double d : outputs) {
			this.outputs.add(d);
		}
		organize();
	}
	
	public ArrayList<Double> getOutputs() {
		return this.outputs;
	}
	
	public void setOutputs(ArrayList<Double> outputs) {
		this.outputs = outputs;
	}
	
	public void organize() {
		Collections.sort(outputs);
	}
}