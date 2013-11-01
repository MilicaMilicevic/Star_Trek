package src.client.ship;

import src.client.universe.Quadrant;

import src.client.JavaWorld;

public class VulcanShip extends Ship {
   private static int counter;
  
  {
    setName("VulcanShip-"+(++counter));
  }
  
  
  public VulcanShip(){
  quadrant=Quadrant.BETA;
  }
  
  
  public VulcanShip(boolean hasShield,int noStroke,int noMissile,int warpSpeed){
    super(hasShield,noStroke,noMissile,warpSpeed,Quadrant.BETA);
  }
  
  @Override
  public void decCounter(){  
    counter--;
    if(counter==0)
      new JavaWorld().announceWinners();
  }
  
 
  public static int getCounter(){
    return counter;
  }
}