package src.client.hero;

import src.client.properties.Warrior;
import src.client.universe.Quadrant;
  
import java.util.Random;

public class Klingon extends Hero implements Warrior {
  private static int counter;
  
 
  
  //(TACNO)//
  public Klingon(){
    super("Klingon-"+(++counter),new Random().nextInt(500)+50,Quadrant.ALPHA);
  }

  
//-------------------------------------------PRINTERI------------------------------------------------------------------
  //(TACNO)//
  @Override
  public String toString(){
    return super.toString()+"\n"+Warrior.description;
  }  
 
  
}