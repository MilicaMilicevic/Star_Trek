package src.client.ship;

import src.client.universe.Quadrant;

import src.client.JavaWorld;

public class KlingonShip extends Ship {
  private static int counter;//broj brodova koji pripadaju Klingoncima//NAPOMENA: Samo za davanje imena!
  
  {
    setName("KlingonShip-"+(++counter));
  }
  
  public KlingonShip(){
  quadrant=Quadrant.ALPHA;
  }
  
  public KlingonShip(boolean hasShield,int noStroke,int noMissile,int warpSpeed){
    super(hasShield,noStroke,noMissile,warpSpeed,Quadrant.ALPHA);
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