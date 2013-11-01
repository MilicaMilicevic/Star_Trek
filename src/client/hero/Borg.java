package src.client.hero;

import src.client.properties.Warrior;
import src.client.properties.Cybernetic;
import src.client.properties.ShipCreator;

import src.client.ship.Ship;
import src.client.universe.Quadrant;

import java.util.Random;

//Borgovi su i Ratnici i Kibernetici.
public class Borg extends Hero implements Warrior, Cybernetic, ShipCreator {
  private static int counter;
  
  
  //(TACNO)//
  public Borg(){
    super("Borg-"+(++counter),new Random().nextInt(1500)+50,Quadrant.DELTA);
  }
  
 
  @Override
  public void regenerate(){
  System.out.println("Hull is regenerating ...");
  }
  
  
  @Override
  public void assimilate(){
    System.out.println("Preko elektronickog dijela povezujem se sa ostalima...");
    System.out.println("Sada imamo zajednicki um, zajednicku svijest.");
  }
   
 //-----------------------------------------------------------PRINTERI-------------------------------------------------
  //(TACNO)//
  @Override
  public String toString(){
    return super.toString()+"\n"+Warrior.description+"\n"+Cybernetic.description;
  }
  
  
}