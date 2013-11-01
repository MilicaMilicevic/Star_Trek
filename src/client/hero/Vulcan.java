package src.client.hero;

import src.client.properties.Humanoid;

import src.client.universe.Quadrant;

import java.util.Random;

//Vulkanci dolaze sa planeta Vulkana.
public class Vulcan extends Hero implements Humanoid {
  private static int counter;
  
 
  //(TACNO)//
  public Vulcan(){
    super("Vulcan-"+(++counter),new Random().nextInt(500)+100,Quadrant.BETA);
  }
  
  public Vulcan(String arg1,int arg2){
    super(Quadrant.BETA);
    name=arg1;
    age=arg2;
  }
  
 
  @Override
  public void useLogic(){
    System.out.println("I always use logic.");
  }
  
  
  @Override
  public boolean supressFeelings(){
    System.out.println("I have feelings, but I've learned how to suppress them.");
    return true;
  }
  
  //----------------------------------------------------------PRINTERI-------------------------------------------------
  //(TACNO)//
  @Override
  public String toString(){
    return super.toString()+"\n"+Humanoid.description;
  }
  
 
}