//NAPOMENA: Koristiti primitivne gdje god je moguce (umjesto wrapper!)

package src.client.ship;

import java.util.Random;
import java.util.Set;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Vector;
import java.util.List;
import java.io.IOException;

import src.client.universe.*;
import src.client.hero.*;
import src.client.JavaWorld;

//event-driven
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;


//---------------------------------------------------------------------------------------------------------------------
enum Control {
               LEFT,RIGHT,UP,DOWN;
               
               public synchronized static Control getRandom(){  //mora biti sinhronizovana, nema svaka nit kopiju!
                 return values()[new Random().nextInt(values().length)];
               }
}
//---------------------------------------------------------------------------------------------------------------------
public abstract class Ship extends Thread implements ActionListener {
 //attrs koji se setuju prilikom kreiranja
 private volatile boolean hasShield;
 private volatile int strikes; //kad se nekoj niti promjeni pod. (pri pucanju) sigurni smo da vidi posljednju promjenu
 private volatile int projectiles;  //broj projektila
 private volatile int warpSpeed;    //broj polja koje brod predje kroz svemir u 1 sec
 protected Object shape;  //svi brodovi, bez obzira kojoj vrsti pripadaju imaju neki oblik! Specif. su zadati samo oblici Borgovskih brodova
 private Sensor sensor;
 protected Quadrant quadrant; //RomulanskiShip se nalazi u Beta kvadrantu itd.
 
 //attrs koji se setuju u nekoj fazi izvodjenja programa
 private int[] position;  //cuva  position broda kad se postavi u svemis
 private Set<Hero> crew;    //crew broj 5 clanova! Setuje se tek prilikom ukrcavanja!NIJE NAM BITAN REDOSLIJED!
 private Control currentControl;
 private volatile boolean destroyed;
 //--------------------------------------------------------------------------------------------------------------------
 private class Sensor implements ActionListener {//svaki brod ima SAMO JEDNU INSTANCU klase Senzor!
   int range;                                                       
   boolean on;//senzor je inicijalno iskljucen
   int[] positionEnemy; //senzor moze u nekom trenutku uociti samo jednog neprijatelja!
   Ship ship;//Brod osluskuje pripadajuci senzor
   
   //konstrukotri i metode imaju private access -> samo ih sadrzavajuca klasa moze instancirati
   Sensor(int arg){
     range=arg;
   }
   
   Sensor(){ 
     range=2;
   }
   //registarcija pripadajuciBroda
   public void addActionListener(Ship arg){
     ship=arg;
   }
   
   //kad ga Brod obavijesti da je promijenio poziciju, on odmah treba da skenira range!
   @Override 
   public void actionPerformed(ActionEvent arg){
         detectingEnemy();
     }
   
//---------------//(TACNO)//----------------------------------------------------------------------------------------------
   //kad otkrije neprijatelja, setuje njegove koordinate.
   public void detectingEnemy(){ //NAPOMENA: Brod nece pucati kraj samog sebe!To je vec slucaj sudara!
     this.addActionListener(Ship.this);//registruj pripadajuciBrod
      //1. SLUCAJ: pogledaj u istom redu, nalijevo
     lookLeft();
     //2.SLUCAJ: pogledaj u istoj koloni, ali ka gore
     lookUp();
     //3.SLUCAJ: pogledaj u istom redu, ali udesno
     lookRight();
     //4.SLUCAJ:pogledaj u istoj koloni, nadole
      lookDown();
     }
  //--------------------------------------------HELPERI--------------------------------------------------------------- 
   private void lookLeft(){ 
   if(position[1]>1){//ako si u prve dvije kolone, ne mozes gledati nalijevo.
       if((((Universe.getUniverse()).getSpace())[position[0]][position[1]-range] instanceof Ship)
            &&(!(Ship.this).equals(((Universe.getUniverse()).getSpace())[position[0]][position[1]-range])))//ne pripadas mom timu
       {
         positionEnemy=new int[2];
         positionEnemy[0]=position[0];
         positionEnemy[1]=position[1]-range;
         notifyShip();
       }     
   }
   }
   
   private void lookUp(){
     if(position[0]>1){//ako se nalazis u prva dva redu, ne mozes gledati ka gore
     if((((Universe.getUniverse()).getSpace())[position[0]-range][position[1]] instanceof Ship)
          &&(!(Ship.this).equals(((Universe.getUniverse()).getSpace())[position[0]-range][position[1]])))
     {
       positionEnemy=new int[2];
       positionEnemy[0]=position[0]-range;
       positionEnemy[1]=position[1];
       notifyShip();
     }
     }
   }
   
   private void lookRight(){
     if(position[1]<((Universe.getUniverse()).getSpace()).length-range){//ako se nalazis u posljednje dvije kolone, ne mozes gledati nadesno
     if((((Universe.getUniverse()).getSpace())[position[0]][position[1]+range] instanceof Ship)
          &&(!(Ship.this).equals(((Universe.getUniverse()).getSpace())[position[0]][position[1]+range])))
     {
       positionEnemy=new int[2];
       positionEnemy[0]=position[0];
       positionEnemy[1]=position[1]+range;
       notifyShip();
   }
     }
      }
   
   
   private void lookDown(){
   if(position[0]<((Universe.getUniverse()).getSpace()).length-range){//ako se nalazis u posljednjem redu, ne mozes gledati ka dole
      if((((Universe.getUniverse()).getSpace())[position[0]+range][position[1]] instanceof Ship)
           &&(!(Ship.this).equals(((Universe.getUniverse()).getSpace())[position[0]+range][position[1]])))
     {
       positionEnemy=new int[2];
       positionEnemy[0]=position[0]+range;
       positionEnemy[1]=position[1];
       notifyShip();
     }
   } 
   }
//HELPER ----------------------------------TACNO--------------------------------------------------------------------------
 private void notifyShip(){
   ActionEvent event=new ActionEvent(this,0,"");
   ship.actionPerformed(event);
 } 
 }//END
//--------------------------------------------------INSTANCIRANJE(BROD)------------------------------------------------------ 
//koji god nacin kreiranja da odaberemo, senzor mora postojati
 {
   sensor=new Sensor();//moramo koristiti instancu Brod da kreiramo instancu Senzor!
   shape=new Object(); //nije oblik definisan
 }

 //inicijalizuje attrs. ali na random vrijednosti
 public Ship(){
   Random generator=new Random();
   if(hasShield=generator.nextBoolean())
     strikes=generator.nextInt(6)+1; //ukoliko brod ne posjeduje stiti brUdara=0;
   else strikes=0;
   projectiles=generator.nextInt(15)+1;
   warpSpeed=generator.nextInt(10)+1;
 }
 
 //Inicijalizuje attr. koji uticu na borbu na predefinisane vrijednosti
 public Ship(boolean arg1,int arg2,int arg3,int arg4,Quadrant arg5){
   hasShield=arg1;
   strikes=arg2;
   projectiles=arg3;
   warpSpeed=arg4;
   quadrant=arg5;
 }
 //--------------------------------------------EVENT_DRIVEN------------------------------------------------------------------------
 public void addActionListener(Sensor arg){
   sensor=arg;
 }
 
  //kad te senzor obavjesti da je neprijatelj u blizini pucaj!
 @Override
 public void actionPerformed(ActionEvent arg){
   shoot();
 }
 
 //------------------------------------------------------------------------------------------------------------------------------
 //kretanje broda, provjera da li ima neprijatelja u blizini, pucanje!
 @Override
 public void run(){
   addActionListener(sensor);
   sensor.on=true;
   System.out.println(this.getName()+": Starting from "+positionToString());
   try{
     while(!JavaWorld.isEnd()){//mozemo stopirati i pomocu ThreadGroup (interrupt()) 
     currentControl=Control.getRandom();//generisi kontrolu
     switch(currentControl){
         case RIGHT:
           moveRight(); break;
         case LEFT:
           moveLeft(); break;
         case DOWN:
           moveDown(); break; 
         case UP:
           moveUp(); break;   
       }
   }
   }
   catch(InterruptedException e) { //NAPOMENA: Kad jednom dodje do ove linije ide samo naprijed!
     synchronized(Universe.getUniverse()){
       destroyed=true;
       ((Universe.getUniverse()).getSpace())[position[0]][position[1]]=null;
       System.err.println(this.getName()+" is destroyed.");
       decCounter();
     }
   }
   }//ovdje je vec u dead state!
 
 //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
 private void moveRight() throws InterruptedException { //prvo provjeri da li na zeljenoj poziciji vec postoji brod!
   boolean crashed=false;
   while((position[1]<((Universe.getUniverse()).getSpace()).length-1)&&(!crashed)){ //pomjeraj se udesno dok mozes
     if((Universe.getUniverse()).getShip(position[0],position[1]+1)==null) {
       synchronized(Universe.getUniverse()){
         position[1]++; 
         (Universe.getUniverse()).moveShip(this,position[0],position[1]-1);
         notifySensor();  //oabvijesti senzor o promjeni pozicije!
       }
       if(warpSpeed!=0) sleep(1000/warpSpeed); //jer neka nit moze doci do ove linije a da je vec warpSpeed=0;
       }
     else if((Universe.getUniverse()).getShip(position[0],position[1]+1) instanceof Ship)
     {
       crash((Universe.getUniverse()).getShip(position[0],position[1]+1)); 
       crashed=true;
     }
   }
 }
 //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
 private void moveLeft() throws InterruptedException {
   boolean crashed=false;
   while((position[1]>0)&&(!crashed)){
     if((Universe.getUniverse()).getShip(position[0],position[1]-1)==null) {
       synchronized(Universe.getUniverse()){
         position[1]--;
         (Universe.getUniverse()).moveShip(this,position[0],position[1]+1);
         notifySensor();
       }
       if(warpSpeed!=0) sleep(1000/warpSpeed);
       }
     else if((Universe.getUniverse()).getShip(position[0],position[1]-1) instanceof Ship)
     {
         crash((Universe.getUniverse()).getShip(position[0],position[1]-1));
         crashed=true;
       }
   }
 }
/////////////////////////////////////////////////////////////////////////////////////////////////////////////////////// 
 private void moveDown() throws InterruptedException {
   boolean crashed=false;
   while((position[0]<((Universe.getUniverse()).getSpace()).length-1)&&(!crashed)){
     if((Universe.getUniverse()).getShip(position[0]+1,position[1])==null) {
       synchronized(Universe.getUniverse()){
         position[0]++;
         (Universe.getUniverse()).moveShip(this,position[0]-1,position[1]);
         notifySensor();
       }
       if(warpSpeed!=0) sleep(1000/warpSpeed);                     
       } 
     else if((Universe.getUniverse()).getShip(position[0]+1,position[1]) instanceof Ship)    
     {
       crash((Universe.getUniverse()).getShip(position[0]+1,position[1]));   
       crashed=true;
       }
   }
 }
////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
 private void moveUp() throws InterruptedException {
   boolean crashed=false;  //pomocna koja sluzi za promjenu smijera kretanja
   while((position[0]>0)&&(!crashed)){
     if((Universe.getUniverse()).getShip(position[0]-1,position[1])==null) {
       synchronized(Universe.getUniverse()){
                                     position[0]--;                              
                                     (Universe.getUniverse()).moveShip(this,position[0]+1,position[1]);                                 
                                     notifySensor();
                                       }
       if(warpSpeed!=0) sleep(1000/warpSpeed);
       }                            
     else if((Universe.getUniverse()).getShip(position[0]-1,position[1]) instanceof Ship)//dodje do sudara ako zelim preci na poziciju na kojoj vec postoji neki brod   
     {
       crash(((Universe.getUniverse()).getShip(position[0]-1,position[1])));   
       crashed=true;
     }
    }
 }
//////////////////////////////////////////////////////////////////////////////////////////////////////////////////////// 
 
private void notifySensor(){
  ActionEvent event=new ActionEvent(this,0,"");
  sensor.actionPerformed(event);
}
///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
 public void crash(Ship arg) throws InterruptedException { //unistava se slabiji
   if((!arg.destroyed) && (!destroyed)){
       System.out.println(this+" and "+arg+" have collided.");
       if(!arg.hasShield)   //1.slucaj -> arg nema stit
       {
         System.out.println(new JavaWorld().translate(arg.getName()+" is without shield."));
         arg.interrupt();//unisti ga!
         arg.join();
       }
       if(!hasShield) //2.slucaj -> this nema stit
       {
         System.out.println(new JavaWorld().translate(getName()+" is without shield."));
         interrupt();
         this.join();
       }
       if(arg.hasShield && hasShield) {//3.slucaj -> oba broda posjeduju stit
         if(strikes>arg.strikes)
         {
           System.out.println(new JavaWorld().translate(arg.getName()+" has less strikes than "+getName()));
           arg.interrupt();
           arg.join(); //blokiraj this dok arg ne zavrsi sa inzvrsavanjem , tj dok ne dodje u DEAD STATE
         }
         else if(strikes<arg.strikes)
         {
           System.out.println(new JavaWorld().translate(getName()+" has less strikes than "+arg.getName()));
           interrupt();
           join();
         }
         else { //imamo jednak broj udara
           if(projectiles>arg.projectiles)
           {
             System.out.println(new JavaWorld().translate(arg.getName()+" has less projectiles than "+getName()));
             arg.interrupt();
             arg.join();
           }
           else if(projectiles<arg.projectiles)
           {
             System.out.println(new JavaWorld().translate(getName()+" has less projectiles than "+arg.getName()));
             interrupt();
             join();
                 }
           else { //jednak broj projektila
             if(warpSpeed>arg.warpSpeed) 
             {
               System.out.println(new JavaWorld().translate(arg.getName()+" is slower than "+getName()));
               arg.interrupt();
               arg.join();
             }
             else if(warpSpeed<arg.warpSpeed)
             {
               System.out.println(new JavaWorld().translate(getName()+" is slower than "+arg.getName()));
               interrupt();
               join();
             }
             else {
               System.out.println(new JavaWorld().translate(getName()+" and "+arg.getName()+" are with same characteristics."));
               arg.interrupt();
               arg.join();
               interrupt();
               join();
             }
           } 
         }
   } 
   }
 }
 //--(TACNO)-----------------------------------------------------------------------------------------------------------------------
 //metoda ceka od senzora notifikaciju da je neprijatelj uocen i koordinate neprijatelja
 public void shoot(){
   if(!destroyed&&projectiles>0){  //mogu gadjati samo ako imam dovoljno projekt.
   Ship enemy=((Universe.getUniverse()).getShip(sensor.positionEnemy[0],sensor.positionEnemy[1]));
   if(!enemy.destroyed)
   {
     System.out.println(this+":  Shooting on "+enemy);
     if(!enemy.hasShield) 
        enemy.interrupt();
     else {//ukoliko posjeduje stit
       enemy.decWarpSpeed();
       enemy.decStrikes();
       }                                 
     projectiles--;
   }
 }
 }

 //------------------------------------------------TACNO---------------------------------------------------------------------------------------------
 public void boardCrew(){
   crew=new HashSet<Hero>();
   Object[][] space=(Universe.getUniverse()).getSpace();  //pomocna-->CITLJIVIJI KOD
   for(int i=quadrant.getBorder();i<quadrant.getSize()+quadrant.getBorder();i++)
     for(int j=quadrant.getBorder();j<quadrant.getSize()+quadrant.getBorder();j++)
     if((crew.size()<5)&&(space[i][j] instanceof Hero)&&(isCompatible((Hero)space[i][j]))){
     {
       crew.add((Hero)space[i][j]);//dodaj u kolekciju.
       space[i][j]=null;   //oslobodi mjesto u svemiru
     } //dodatno: ((Hero)space[i][j]).setPosition(null);      
   }     
}
 
 //HELPER---------------------------------TACNO--------------------------------------------------------------------------------
 private boolean isCompatible(Hero arg){
   if((this instanceof BorgShip)&&(arg instanceof Borg))
         return true;
   if((this instanceof RomulanShip)&&(arg instanceof Romulan))
         return true;  
   if((this instanceof VulcanShip)&&(arg instanceof Vulcan))
         return true;
   if((this instanceof KlingonShip)&&(arg instanceof Klingon))
         return true;
   return false;
 }

 //-----------------------------------------------------------------------------------------------------------------------
 // Brod provjerava da li je odredjeni Brod neprijatelj.
  //@return false ako je arg instanca klase i ako je u suprotnom timu.
 @Override 
 public boolean equals(Object arg){
     if(arg instanceof KlingonShip){
        if(this instanceof KlingonShip) return true;
        else if(this instanceof VulcanShip) return true;
     }
     else if(arg instanceof BorgShip){
       if(this instanceof BorgShip) return true;
       else if(this instanceof RomulanShip) return true;
     }
     else if(arg instanceof RomulanShip) {
       if(this instanceof RomulanShip) return true;
       if(this instanceof BorgShip) return true;
     }
     else {
       if(this instanceof VulcanShip) return true;
       else if(this instanceof KlingonShip) return true;
     }
   return false;
 }


 //--------------------------------------------------GETERI(BROD)------------------------------------------------------------
//(TACNO)
 public boolean hasShield(){
   return hasShield;
 }
 //(TACNO)//
 public int getStrikes(){
   return strikes;
 }
 //(TACNO)//
 public int getProjectiles(){
   return projectiles;
 }
 //(TACNO)//
 public int getWarpSpeed(){
   return warpSpeed;
 }
 //(TACNO)//
 public Object getShape(){
   return shape;
 }
 //(TACNO)//
 public Object getPosition(){
   if(position!=null)
     return position;
   else return "Not placed.";
 }
 

 //-------------------------------------------------TACNO--------------------------------------------------------------
  public Object getCrew(){
   if(crew!=null) 
     return crew;
   else return "The crew is not boarded.";
 }

 //(TACNO)//
 public Object getSensor(){
   return sensor;
 }
 
 //(TACNO)//
 public Quadrant getQuadrant(){
   return quadrant;
 }
 
 public boolean isDestroyed(){
   return destroyed;
 }

 //----------------------------------------------SETERI(BROD)----------------------------------------------------------------------
 //(TACNO)//
 public void setPosition(int arg1,int arg2){
   position=new int[2];
   position[0]=arg1;
   position[1]=arg2;
 }
 
 public void setPosition(Object arg){ //ova metoda se koristi samo pri serijalizaciji objekta
   if(arg instanceof int[])
     position=(int[])arg;
 }
//NAPOMENA: Posada se ne moze setovato "izvana".   
 
 //-------------------------------------------------PRINTERI(BROD)--------------------------------------------------------------------
   public String positionToString(){ //ispisuje samo trenutnu poziciju broda u svemiru, ukoliko je postavljen!
     if(position!=null)
       return "["+position[0]+","+position[1]+"]";
     else return "Not placed.";
   }
 
 //--------------------------------------------TACNO-------------------------------------------------------------------
 @Override
 public String toString(){
   String tmp=getName()+" [";
   if(hasShield) tmp+="SHIELD|Strikes:"+strikes+"|";
   tmp+="Projectiles:"+projectiles+"|Speed:"+warpSpeed+"]";
   return tmp+=" "+positionToString();
 }
 //------------------------------------------TACNO----------------------------------------------------------------------
 public String crewToString(){
   String tmp="CREW:\n";
   Iterator<Hero> iterator=crew.iterator();
   while(iterator.hasNext()){
     Hero i=iterator.next();
     tmp+=i.toString()+"\n";
   }
   return tmp;
 }

 //------------------------------------------------------------------------------------------------------------------------
 //(TACNO)//
 public void decWarpSpeed(){
   warpSpeed--;
   if(warpSpeed==0) 
     interrupt();
  }
 //(TACNO)//
 public void decStrikes(){
   strikes--;
   if(strikes==0)  
     interrupt(); 
 }
 
 public boolean isPlaced(){
   if(getPosition().equals("Not placed.")) return false;
   return true;
 }
  
 public abstract void decCounter(); //NAPOMENA: Mora biti non-static!
}