package src.client.hero;

import src.client.properties.Warrior;

import java.util.Random;

public class Romulan extends Vulcan implements Warrior {
private static int counter;

  
  public Romulan(){
    super("Romulan-"+(++counter),new Random().nextInt(300)+100); //Vulkan(arg1,arg2)
  }
  

  @Override
  public void useLogic(){
    System.out.println("Since I don't know how to supress feelings, I use logic only sometimes.");
  }
  
  @Override 
  public boolean supressFeelings(){
    return false;
  }
  //TACNO)//
  @Override
  public String toString(){
    return super.toString()+"\n"+Warrior.description;
  }
  
 
}