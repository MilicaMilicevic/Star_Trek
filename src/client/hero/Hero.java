package src.client.hero;

import src.client.universe.Quadrant;
import src.client.properties.Moveable;

public abstract class Hero implements Moveable { //nema heroja da nije clan neke vrste!
  protected String name;
  protected int age;
  private Quadrant quadrant;  //dio svemira u kom junak zivi!
  
  private int[] position;  //position na kojoj je junak u svemiru, prije ukrcavanja u brod. Dakle, ako je position!=null, postavljen je
 

  //(TACNO)//
  public Hero(String arg1, int arg2,Quadrant arg3){
    name=arg1;
    age=arg2;
    quadrant=arg3;
  }
  //(TACNO)//
  public Hero(Quadrant arg){
    quadrant=arg;
  }
    
  //------------------------------------------------------GETERI-------------------------------------------------------
 
  public String getName(){
    return name;
  }
 
  public int getAge() {
    return age;
  }
 
  public Quadrant getQuadrant() {
    return quadrant;
  }

  public Object getPosition(){
    if(position!=null) 
      return position;
    else return "Not placed.";
  }

  
  //------------------------------------------------------SETERI-------------------------------------------------------
  //uredjeni par definise poziciju junaka u svemiru prije ukrcavanja na brod
   public void setPosition(int arg1,int arg2){
   position=new int[2];
   position[0]=arg1;
   position[1]=arg2;
  }   
   //------------------------------------------------------PRINTERI----------------------------------------------------
   //HELPER 
   private String positionToString(){ //ispisuje samo trenutnu poziciju junaka u svemiru, ukoliko je postavljen!
     if(position!=null)
       return "["+position[0]+","+position[1]+"]";
     else return "Not placed.";
   }
  
   //(TACNO)//
   @Override
   public String toString(){ //ispisuje detaljne infor. o Junaku
     return name+" [Age:"+age+"|"+quadrant+"] "+positionToString();
  }
   
   //----------------------------------------TACNO-----------------------------------------------------------------
   public boolean isPlaced(){
     if(getPosition().equals("Not placed.")) return false;
     return true;
   }
   
   
  
}