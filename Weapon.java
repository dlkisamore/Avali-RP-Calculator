/**
	Weapons must be stored in the following format:
		Ballistic
			Type:::Name:::AmmunitionType:::RateOfFire
		Directed Energy
			
		Explosive
			
		Melee / Improvised
			Type:::Object:::Mass:::Speed
	Weapons consist of the following traits:
		
*/

public class Weapon {
	protected String type;
	protected String name;
	
	public Weapon(String type, String name) {
		this.type = type;
		this.name = name;
	}
	
	public String getType() {
		return this.type;
	}
	
	public String getName() {
		return this.name;
	}
}