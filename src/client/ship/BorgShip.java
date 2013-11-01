package src.client.ship;

import java.util.Random;
import src.client.universe.Quadrant;

import src.client.JavaWorld;

//---------------------------------------------------------------------------------------------------------------------
//Definise domen attr. oblik za Borgovske brodove!
enum Shape {
           CUBE,SPHERE,PYRAMID;
            //return slucajno odabrani oblik
           public static Shape getRandom() {
             return values()[new Random().nextInt(values().length)];
           } 
}
//---------------------------------------------------------------------------------------------------------------------



public class BorgShip extends Ship{
  private static int counter;
  
  {
    setName("BorgShip-"+(++counter));//poziva Thread::setName()
  }
  
  //setuje atribute koji uticu na borbu na random vrijednosti
  public BorgShip(){
  quadrant=Quadrant.DELTA;
  }
  //setuje attrs. koji uticu na borbu na predefinisane vrijednosti
  public BorgShip(boolean hasShield, int noStroke, int noMissile, int warpSpeed){
    super(hasShield,noStroke,noMissile,warpSpeed,Quadrant.DELTA);
    shape=Shape.getRandom(); //nama su poznati samo oblici BorgovskiBrod
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