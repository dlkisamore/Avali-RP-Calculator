/**
	This class takes in a string from WeaponProfiles.txt and creates the appropriate weapon (Ballistic, Ammunition, Explosive, Melee, Other)
*/

import java.util.ArrayList;

public class WeaponFactory {
	public static Weapon make(String line) {
		//separate line into parts
		ArrayList<String> parts = new ArrayList<String>();
		while(true) {
			if(line.contains(":::")) {
				parts.add(line.substring(0,line.indexOf(":::")).trim());
				line = line.substring(line.indexOf(":::") + 3);
			} else {
				parts.add(line.trim());
				break;
			}
		}
		while(line.contains(":::")) {
			parts.add(line.substring(0,line.indexOf(":::")).trim());
			line = line.substring(line.indexOf(":::") + 3);
			if(!line.contains(":::")) { //account for the last item in the line
				parts.add(line.trim());
			}
		}
		String type = parts.get(0);
		String name = parts.get(1);
		try {
			switch(parts.get(0)) {
				case "Ballistic":
					//process ammoTypes (,,, separator)
					ArrayList<String> ammoTypes = new ArrayList<String>();
					while(true) {
						if(parts.get(2).contains(",,,")) {
							ammoTypes.add(parts.get(2).substring(0, parts.get(2).indexOf(",,,")).trim());
							parts.set(2, parts.get(2).substring(parts.get(2).indexOf(",,,") + 3));
						} else {
							ammoTypes.add(parts.get(2).trim());
							break;
						}
					}
					ArrayList<Double> muzzleVelocities = new ArrayList<Double>();
					while(true) {
						if(parts.get(3).contains(",,,")) {
							muzzleVelocities.add(Double.valueOf(parts.get(3).substring(0, parts.get(3).indexOf(",,,")).trim()));
							parts.set(3, parts.get(3).substring(parts.get(3).indexOf(",,,") + 3));
						} else {
							muzzleVelocities.add(Double.valueOf(parts.get(3).trim()));
							break;
						}
					}
					ArrayList<Double> ratesOfFire = new ArrayList<Double>();
					while(true) {
						if(parts.get(4).contains(",,,")) {
							ratesOfFire.add(Double.valueOf(parts.get(4).substring(0, parts.get(4).indexOf(",,,")).trim()));
							parts.set(4, parts.get(4).substring(parts.get(4).indexOf(",,,") + 3));
						} else {
							ratesOfFire.add(Double.valueOf(parts.get(4).trim()));
							break;
						}
					}
					return new Ballistic(parts.get(0), parts.get(1), ammoTypes, muzzleVelocities, ratesOfFire);
				case "Ammunition":
					ArrayList<Double> masses = new ArrayList<Double>();
					while(true) {
						if(parts.get(2).contains(",,,")) {
							masses.add(Double.valueOf(parts.get(2).substring(0, parts.get(2).indexOf(",,,")).trim()));
							parts.set(2, parts.get(2).substring(parts.get(2).indexOf(",,,") + 3));
						} else {
							masses.add(Double.valueOf(parts.get(2).trim()));
							break;
						}
					}
					ArrayList<Double> velocities = new ArrayList<Double>();
					while(true) {
						if(parts.get(3).contains(",,,")) {
							velocities.add(Double.valueOf(parts.get(3).substring(0, parts.get(3).indexOf(",,,")).trim()));
							parts.set(3, parts.get(3).substring(parts.get(3).indexOf(",,,") + 3));
						} else {
							velocities.add(Double.valueOf(parts.get(3).trim()));
							break;
						}
					}
					return new Ammunition(parts.get(0), parts.get(1), masses, velocities);
				case "Explosive":
					ArrayList<Double> yields = new ArrayList<Double>();
					while(true) {
						if(parts.get(2).contains(",,,")) {
							yields.add(Double.valueOf(parts.get(2).substring(0, parts.get(2).indexOf(",,,")).trim()));
							parts.set(2, parts.get(2).substring(parts.get(2).indexOf(",,,") + 3));
						} else {
							yields.add(Double.valueOf(parts.get(2).trim()));
							break;
						}
					}
					ArrayList<Boolean> shaped = new ArrayList<Boolean>();
					while(true) {
						if(parts.get(3).contains(",,,")) {
							shaped.add(Boolean.valueOf(parts.get(3).substring(0, parts.get(3).indexOf(",,,")).trim()));
							parts.set(3, parts.get(3).substring(parts.get(3).indexOf(",,,") + 3));
						} else {
							shaped.add(Boolean.valueOf(parts.get(3).trim()));
							break;
						}
					}
					return new Explosive(parts.get(0), parts.get(1), yields, shaped);
				case "Melee":
					ArrayList<Double> weaponMasses = new ArrayList<Double>();
					while(true) {
						if(parts.get(2).contains(",,,")) {
							weaponMasses.add(Double.valueOf(parts.get(2).substring(0, parts.get(2).indexOf(",,,")).trim()));
							parts.set(2, parts.get(2).substring(parts.get(2).indexOf(",,,") + 3));
						} else {
							weaponMasses.add(Double.valueOf(parts.get(2).trim()));
							break;
						}
					}
					ArrayList<Double> speeds = new ArrayList<Double>();
					while(true) {
						if(parts.get(3).contains(",,,")) {
							speeds.add(Double.valueOf(parts.get(3).substring(0, parts.get(3).indexOf(",,,")).trim()));
							parts.set(3, parts.get(3).substring(parts.get(3).indexOf(",,,") + 3));
						} else {
							speeds.add(Double.valueOf(parts.get(3).trim()));
							break;
						}
					}
					return new Melee(parts.get(0), parts.get(1), weaponMasses, speeds);
				case "Other":
					ArrayList<Double> outputs = new ArrayList<Double>();
					while(true) {
						if(parts.get(2).contains(",,,")) {
							outputs.add(Double.valueOf(parts.get(2).substring(0, parts.get(2).indexOf(",,,")).trim()));
							parts.set(2, parts.get(2).substring(parts.get(2).indexOf(",,,") + 3));
						} else {
							outputs.add(Double.valueOf(parts.get(2).trim()));
							break;
						}
					}
					return new Other(parts.get(0), parts.get(1), outputs);
				default: //return an invalid weapon, type "INVALID", that will be discarded by the program
					return new Weapon("INVALID", "");
			}
		} catch(Exception e) { //handle incorrectly formatted lines in WeaponProfiles.txt
			return new Weapon("INVALID", "");
		}
	}
}