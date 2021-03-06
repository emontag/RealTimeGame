import java.util.concurrent.atomic.AtomicInteger;


public class Archer extends Player {
   protected  String[] techniques={"Quick Shot","Hawk's Eye","Blazestrike","Boltstrike","Blizzstrike","Blightstrike","Sneakshot","Lethal Snipe","Fission Arrow Strike","Odyssey shot","Arrow Heal","Health Potion"};

   public Archer(String string) {
      currenthp=new AtomicInteger();
      currentmp=new AtomicInteger();
      speed=new AtomicInteger();
      level=new AtomicInteger();
      armorBuff=new AtomicInteger(0);
      armor=new AtomicInteger(5);
      baseArmor=new AtomicInteger(5);
      accuracy=new AtomicInteger();
      level.set(1);
      attackType=string;
      power=50;
      speed.set(10);
      crit_chance=10;
      hpotion=1;
      currenthp.set(200);
      currentmp.set(75);
      maxhp=currenthp.get();
      maxmp=currentmp.get();
      accuracy.set(95);
      basePower=power;
      baseSpeed=40;
      baseCrit=crit_chance;
      baseHp=maxhp;
      baseMp=maxmp;
      baseAccuracy=accuracy.get();
      gear[0]=new Gear(0,0,0,0,0, 0, 0, "hat");
      gear[1]=new Gear(0,0,0,0,0, 0, 1, "shirt");
      gear[2]=new Gear(0,0,0,0,0, 0, 2, "pants");
      gear[3]=new Gear(0,0,0,0,0, 0, 3, "shoes");
      weapon=new Gear(0,0,0,0,0,0,-1,"bow");
      
   }

   @Override
   public String[] getTechniques() {
      return techniques;
   }

   
   public int getCooldown(int s) {
      int speeder=getSpeed()/10+1;
      if(attackType=="Burst"){
         switch(s) {
         case 0: return 1;
         case 1: return 2;
         case 2: return 4;
         case 3: return 7;
         case 4: return 10-speeder;
         case 5: return 15-speeder;
         case 6: return 20-speeder;
         case 7: return 20-speeder;
         case 8: return 20-speeder;
         case 9: return 30-speeder;
         case 10: return 90-speeder;
         }
      }
      else if(attackType=="DOT"){
         switch(s){
         case 0: return 1;
         case 1: return 1;
         case 2: return 1;
         case 3: return 2;
         case 4: return 4;
         case 5: return 7;
         case 6: return 25-speeder;
         case 7: return 25-speeder;
         case 8: return 25-speeder;
         case 9: return 30-speeder;
         case 10: return 90-speeder;
         }
      }
      return 0;
   }

   

}
