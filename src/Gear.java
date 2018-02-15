import java.io.Serializable;




public class Gear implements Serializable{
   private int hp, mp, speed, power, accuracy, crit, type, code;
   private String  name;
   public Gear(int h, int m, int s, int p, int a, int c, int t, String n){
      hp=h;
      mp=m;
      speed=s;
      power=p;
      accuracy=a;
      crit=c;
      type=t;
      name=n;
      code=Game.getCode();
   }
   public String getName(){
      return name;
   }
   public int getType(){
      return type;
   }
   public int getHP(){
      return hp;
   }
   public int getMP(){
      return mp;
   }
   public int getSpeed(){
      return speed;
   }
   public int getPower(){
      return power;
   }
   public int getAccuracy(){
      return accuracy;
   }
   public int getCrit(){
      return crit;
   }
   public int getCode(){
      return code;
   }

}
