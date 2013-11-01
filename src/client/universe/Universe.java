package src.client.universe;

import java.util.Random;
import java.util.ArrayList;
import java.util.List;
import java.util.Iterator;
import java.io.Serializable;
import java.io.ObjectOutputStream;
import java.io.ObjectInputStream;
import java.io.IOException;

import src.client.hero.*;
import src.client.ship.*;
import src.client.JavaWorld;


 //* Svemir sadrzi junake i brodove. Podijeljen je na kvadrante. 
 //* Sadrzi i generator SB, da izgenerise sve slucajnosti.

public class Universe implements Serializable {
  private Object[][] space;
  private Random generator; //jedan generator za sve slucaj.
  private Hero[][] heroes;
  private List<Ship> ships;//NAPOMENA: Nema potrebe za Vector -> ne mijenjamo kolekciju tokom borbe
  
  //sigurno thread safe!
  private static Universe uniqueUniverse=new Universe(); //pomocna varijabla koja cuva singleton instancu!Kreira se instanca, kad se ucita klasa!

  private Universe(){              //svemir je inicijalno bez junaka, bez brodova! Svemir je singleton, pa su singlton i generator i prostor 
    generator=new Random();      //samo se ovdje kreiraju!
    space=new Object[90][90];             
  }
  
//------------------------------------------TACNO--------------------------------------------------------------------------------  
  public void placeHero(Hero arg){
      int i,j; //koordinate
      while(!arg.isPlaced()){//ako nije vec smjesten
        i=uniqueUniverse.generator.nextInt((arg.getQuadrant()).getSize())+((arg.getQuadrant()).getBorder());
        j=uniqueUniverse.generator.nextInt((arg.getQuadrant()).getSize())+((arg.getQuadrant()).getBorder());
        if(!(uniqueUniverse.space[i][j] instanceof Hero)) {//ako na toj poziciji ne postoji neki drugi Junak (prostor[i][j]==null), postavi
          uniqueUniverse.space[i][j]=arg;
          arg.setPosition(i,j);
        }
      } 
  }  
  //---------------------------------------------TACNO-----------------------------
  public void createHeroes(){
  getHeroes(); //koliko god puta da s vana pozovemo createHeroes() promjenljiva junaci ostaje ista! Ne kreira se novi objekat!
  uniqueUniverse.heroes[0]=new Vulcan[15];
  for(int i=0;i<uniqueUniverse.heroes[0].length;i++)//1. vrsta
      uniqueUniverse.heroes[0][i]=new Vulcan();
  uniqueUniverse.heroes[1]=new Romulan[15];        //2.vrsta
  for(int i=0;i<uniqueUniverse.heroes[1].length;i++)
      uniqueUniverse.heroes[1][i]=new Romulan();
  uniqueUniverse.heroes[2]=new Borg[15];       //3. vrsta
  for(int i=0;i<uniqueUniverse.heroes[2].length;i++)
      uniqueUniverse.heroes[2][i]=new Borg();
  uniqueUniverse.heroes[3]=new Klingon[15];   //4. vrsta
  for(int i=0;i<uniqueUniverse.heroes[3].length;i++)
      uniqueUniverse.heroes[3][i]=new Klingon();
  }

//--------------------------------------TACNO-----------------------------------------------------------------------------------  
  public void createShips(){
    getShips();//samo se jednom inicijalizuje referenca brodovi!Bez obzira koliko puta pozovemo ovu metodu!
    Ship[][] ships={new BorgShip[3],new KlingonShip[3],new RomulanShip[3],new VulcanShip[3]};//pomocna referenca, niz
    for(int i=0;i<ships[0].length;i++)//kreiranje borgovskih brodova
      uniqueUniverse.ships.add(ships[0][i]=new BorgShip());
    for(int i=0;i<ships[1].length;i++)//kreiranje klingonskih brodova
      uniqueUniverse.ships.add(ships[1][i]=new KlingonShip());
    for(int i=0;i<ships[2].length;i++)//kreiranje romulanskih brodova
      uniqueUniverse.ships.add(ships[2][i]=new RomulanShip());
    for(int i=0;i<ships[3].length;i++)//kreiranje vulkanskih brodova
      uniqueUniverse.ships.add(ships[3][i]=new VulcanShip());
  }
 //--------------------------------------------TACNO--------------------------------------------------------------------------- 
  public void placeShip(Ship arg){
      int i,j;
      while(!arg.isPlaced()){
         i=uniqueUniverse.generator.nextInt((arg.getQuadrant()).getSize())+((arg.getQuadrant()).getBorder());
         j=uniqueUniverse.generator.nextInt((arg.getQuadrant()).getSize())+((arg.getQuadrant()).getBorder());
        if(!(uniqueUniverse.space[i][j] instanceof Ship)) { //ako na toj poziciji nije vec postavljen Brod
                               uniqueUniverse.space[i][j]=arg;
                               arg.setPosition(i,j);
        }
      }
    }

  //tokom izvodjenja simulacije, dio kretanja, prelazak na novo, i oslobadjanje straog mjesta.
  public void moveShip(Ship arg,int oldRow,int oldColumn){//POMJERi BROD
    int[] position=(int[])(arg.getPosition());//postavlja novi objekat u prostor
    uniqueUniverse.space[position[0]][position[1]]=arg;
    uniqueUniverse.space[oldRow][oldColumn]=null; //oslobodi prethodno mjesto u prostoru!
  }
//-------------------------------------------TACNO--------------------------------------------------------------------------------  
  @Override
  public String toString(){
    String tmp="";
    for(int i=0;i<uniqueUniverse.space.length;i++)
      for(int j=0;j<uniqueUniverse.space[i].length;j++)
      if(uniqueUniverse.space[i][j]!=null) tmp+=uniqueUniverse.space[i][j].toString()+"\n";
    return tmp;
  }
 //-----------------------------------------------TACNO---------------------------------------------------------------------------
  //GETERI// ----> Implemantirani tako da cuvaju singleton pattern!(Po funkcionalnosti su i inicijalizatori)
  public Object[][] getSpace(){//nema potrebe za sinhronizacijom!Jedna linija koda koja samo vraca referencu!
       return uniqueUniverse.space;
  }
  //----------------------------------------TACNO-----------------------------------------------------------------------
  public Hero[][] getHeroes(){ //koristi se prije pokretanja niti, thread safe!
    if(uniqueUniverse.heroes==null) 
      uniqueUniverse.heroes=new Hero[4][15];
    return uniqueUniverse.heroes;
  }
  //--------------------------------------------TACNO-------------------------------------------------------------------
  public ArrayList<Ship> getShips(){ //koristi se prije pokretanja niti, thread safe!
    if(uniqueUniverse.ships==null)
      uniqueUniverse.ships=new ArrayList<Ship>(12);
    return (ArrayList<Ship>)uniqueUniverse.ships;
  }
  //---------------------------------------------TACNO-----------------------------------------------------------------
  public static Universe getUniverse(){//Moze biti synchronized!!!Mada, potrebna je samo pri prvom pozivu, kasnije je overhead!Pa je bolje
       return uniqueUniverse;   //instancu kreirati pri ucitavanju klase!
  }
  
  //-------------------------------------------------------------------------------------------------------------------------
  
  //vraca brod za definisane koordinate, ukoliko postoji!
  public synchronized Ship getShip(int arg1, int arg2){
    if((uniqueUniverse.space[arg1][arg2] instanceof Ship)&&(uniqueUniverse.space[arg1][arg2]!=null)) return (Ship) uniqueUniverse.space[arg1][arg2];
    return null;
  }
 //---------------------------------------------------SERIJALIZACIJA---------------------------------------------------
  private void writeObject(ObjectOutputStream out) throws IOException {
      for(Ship tmp:ships) //nama su za formaciju bitne dvije info-ime i pocetna pozicija broda, nista drugo!
    {
      out.writeObject(tmp.getName());
      out.writeObject(tmp.getPosition());
    } 
  }
  //--------------------------------------------------DESERIJALIZACIJA-------------------------------------------------
  private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException {
    for(Ship tmp:getShips())
    {
      tmp.setName((String)in.readObject());
      tmp.setPosition(in.readObject());
    } 
  }
 
}

