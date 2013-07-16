package spells;

import java.util.ArrayList;

import entities.Agent;

public class Spell {

	
	private String name;
	private ArrayList<SpellEffect> effects;
	
	public Spell(){
		
	}
	
	public void applyEffect(Agent target){
		for(SpellEffect effect : effects){
			effect.applyEffect(target);
		}
	}
	
	public String getName(){
		return name;
	}
	
	public ArrayList<SpellEffect> getEffects(){
		return effects;
	}
}
