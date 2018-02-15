import java.util.concurrent.atomic.AtomicInteger;


public class Warrior extends Player {
   protected  String[] techniques={"Sword Strike","Slice","Leap","Lunge","Spin Attack","Sword Throw","Swift Attack","Demon Lunge","Lightning Throw","Shield","Heal","Health Potion"};

   public Warrior(String string) {
      currenthp=new AtomicInteger();
      currentmp=new AtomicInteger();
      speed=new AtomicInteger();
      level=new AtomicInteger();
      armorBuff=new AtomicInteger(0);
      armor=new AtomicInteger(10);
      baseArmor=new AtomicInteger(10);
      accuracy=new AtomicInteger();
      level.set(1);
      attackType=string;
      power=40;
      speed.set(10);
      crit_chance=10;
      hpotion=1;
      currenthp.set(250);
      currentmp.set(100);
      maxhp=currenthp.get();
      maxmp=currentmp.get();
      accuracy.set(90);
      basePower=power;
      baseSpeed=20;
      baseCrit=crit_chance;
      baseHp=maxhp;
      baseMp=maxmp;
      baseAccuracy=accuracy.get();
      gear[0]=new Gear(0,0,0,0,0, 0, 0, "hat");
      gear[1]=new Gear(0,0,0,0,0, 0, 1, "shirt");
      gear[2]=new Gear(0,0,0,0,0, 0, 2, "pants");
      gear[3]=new Gear(0,0,0,0,0, 0, 3, "shoes");
      weapon=new Gear(0,0,0,0,0,0,-1,"sword");
   }

   
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
         case 10: return 120-speeder;
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
         case 6: return 20-speeder;
         case 7: return 20-speeder;
         case 8: return 20-speeder;
         case 9: return 30-speeder;
         case 10: return 120-speeder;
         }
      }
      return 0;
   }


   

}
