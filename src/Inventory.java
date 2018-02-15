import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentLinkedDeque;
public class Inventory extends ConcurrentLinkedDeque implements Serializable{
   public Object[] getWeapon() {
      Object[] all= toArray();
      List<Gear> l=new ArrayList<Gear>();
      for(int i=0; i< all.length; i++){
         if(all[i] instanceof Gear){
            Gear g=(Gear) all[i];
            if(g.getType()==-1) l.add(g);
         }
      }
      return l.toArray();
      
   }
   

   public Object[] getHead() {
      Object[] all= toArray();
      List<Gear> l=new ArrayList<Gear>();
      for(int i=0; i< all.length; i++){
         if(all[i] instanceof Gear){
            Gear g=(Gear) all[i];
            if(g.getType()==0) l.add(g);
         }
      }
      return l.toArray();
      
   }
   public Object[] getBody() {
      Object[] all= toArray();
      List<Gear> l=new ArrayList<Gear>();
      for(int i=0; i< all.length; i++){
         if(all[i] instanceof Gear) {
            Gear g=(Gear) all[i];
            if(g.getType()==1) l.add(g);
         }
      }
      return l.toArray();
      
   }
   public Object[] getLegs() {
      Object[] all= toArray();
      List<Gear> l=new ArrayList<Gear>();
      for(int i=0; i< all.length; i++){
         if(all[i] instanceof Gear) {
            Gear g=(Gear) all[i];
            if(g.getType()==2) l.add(g);
         }
      }
      return l.toArray();
      
   }
   public Object[] getShoes() {
      Object[] all= toArray();
      List<Gear> l=new ArrayList<Gear>();
      for(int i=0; i< all.length; i++){
         if(all[i] instanceof Gear) {
            Gear g=(Gear) all[i];
            if(g.getType()==3) l.add(g);
         }
      }
      return l.toArray();
      
   }
   
   

}
