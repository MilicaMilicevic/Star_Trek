//(TACNO)//
package src.client.properties;

public interface Cybernetic{
//svi kibernetici se sastoje od bioloskog i elektronickog dijela.
Object biologicalPart=new Object();
Object electronicPart=new Object();
String description="CYBERNETIC [Have biological and electronic part. Able to do regeneration and assimilation.]";

void regenerate();//izvrsi regeneraciju!
void assimilate();//povezivanje u kolektiv

}