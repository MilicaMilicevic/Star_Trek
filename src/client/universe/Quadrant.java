package src.client.universe; 
//izdvojeno zbog preglednosti koda! Moglo se definisati unutar klase Svemir!
//podjela svemira na kvadrante!
public enum Quadrant{ //ovaj tip koriste i Svemir, Junak, Brod
                         ALPHA(0),BETA(30),DELTA(60);   //inclusive
                         private int border;       //granica pojedinacnog kvadranta
                         private static final int size=30;       //"size" svakog kvadranta!
                         
                         Quadrant(int arg){
                           border=arg;
                         }
                          
                         public int getBorder(){
                           return border;
                         }
                         
                         public int getSize(){
                           return size;
                         }         
  }