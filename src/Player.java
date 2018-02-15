import java.io.Serializable;
import java.util.concurrent.atomic.AtomicInteger;


public  abstract class Player implements Serializable{
   static String name;
   protected String attackType;
   protected int   maxhp, maxmp, power, exp=0, xplevel=100;
   protected AtomicInteger currenthp, currentmp, speed, level, accuracy;//only ones accesed more than 1 thread
   protected int baseHp, baseMp, basePower, baseSpeed, baseAccuracy, baseCrit;
   protected int crit_chance;
   protected int hpotion, money=0;
   protected Gear weapon;
   protected AtomicInteger armorBuff, armor, baseArmor;
   private Inventory inventory=new Inventory();
   protected Gear[] gear=new Gear[4];//head, body, pants, shoes
   public abstract String[] getTechniques();
   public int getCurrentHp(){ return currenthp.get();}
   public int getMaxHp(){ return maxhp;}
   public int getCurrentMp(){ return currentmp.get();}
   public int getMaxMp(){ return maxmp;}
   public int getPower(){ return power;}
   public int getSpeed(){ return speed.get();}
   public int getCrit(){ return crit_chance;}
   public int getBaseHp(){ return baseHp;}
   public int getBaseMp(){ return baseMp;}
   public int getBasePower(){ return basePower;}
   public int getBaseSpeed(){ return baseSpeed;}
   public int getBaseAccuracy(){return baseAccuracy;}
   public int getBaseCrit(){ return baseCrit;}
   public void setBaseHp(int num){
      baseHp=num ;
      if(baseHp < 0) baseHp=0;
   }
   public void setBaseMp(int num){ 
      if(baseMp <0 ) baseMp=0;
      baseMp=num ;
   }
   public void setBasePower(int num){ 
      basePower=num ;
      if(basePower <=0 ) basePower=1;
   }
   public void setBaseSpeed(int num)   { 
      if (baseSpeed < 100) baseSpeed=num ;
      if(baseSpeed >= 100) baseSpeed=100;
      if(baseSpeed <=0 ) baseSpeed=1;
   }
   public void setBaseCrit(int num){ 
      if(num>100) return;
      if(baseCrit <=0 ) baseCrit=1;
      baseCrit=num ;
   }
   public void setBaseAccuracy(int a){
      if(baseAccuracy==100 || a>100) return;
      if(baseAccuracy <=0 ) baseAccuracy=1;
      else baseAccuracy=a;
   }
   public void setCurrentHp(int num){
      currenthp.set(num); 
      if(currenthp.get() > maxhp) currenthp.set(maxhp);
      if(currenthp.get() < 0) currenthp.set(0);;
   }
   public void setCurrentMp(int num){ 
      currentmp.set(num); ;
      if(currentmp.get() > maxmp) currentmp.set(maxmp);
      if(currentmp.get() < 0) currentmp.set(0);
      }
   public void setMaxHp(int num){ 
      maxhp=num ;
      if(maxhp<0 ) maxhp=0;
   }
   public void setMaxMp(int num){ 
      if(maxmp <0 ) maxmp=0;
      maxmp=num ;
   }
   public void setPower(int num){ 
      power=num ;
      if(power <=0 ) power=1;
   }
   public void setSpeed(int num)   { 
      if (speed.get() < 100) speed.set(num);
      if(speed.get() >= 100) speed.set(100);;
      if(speed.get() <=0 ) speed.set(1);;
   }
   public void setCrit(int num){ 
      if(num>100) return;
      if(crit_chance <=0 ) crit_chance=1;
      else crit_chance=num ;
   }
   public void UseHPotion() {
      if(hpotion >0) {
         hpotion--;
         heal();
      }
   }
   public void AddHPotion() { hpotion++; }
   public int getNumberHPotion() { return hpotion; }
   public void heal() {
      currenthp.set((int) (currenthp.get()+(maxhp*.33)));
      if(currenthp.get() > maxhp) currenthp.set(maxhp);;
      
   }
   public int getAccuracy(){
      return accuracy.get();
   }
   public void setAccuracy(int a){
      if(accuracy.get()>=100 || a>100) return;
      if(accuracy.get() <=0 ) accuracy.set(1);
      else accuracy.set(a);
   }
   private void addGearStats(Gear g) {
      crit_chance+=g.getCrit();
      maxhp+=g.getHP();
      currenthp.set(maxhp);;
      maxmp+=g.getMP();
      currentmp.set(maxmp);;
      power+=g.getPower();
      setAccuracy(accuracy.get()+g.getAccuracy());
      setSpeed(speed.get()+g.getSpeed());
      
   }
   private void removeGearStats(Gear g) {
      crit_chance-=g.getCrit();
      maxhp-=g.getHP();
      currenthp.set(maxhp);;
      maxmp-=g.getMP();
      currentmp.set(maxmp);;
      power-=g.getPower();
      setAccuracy(accuracy.get()-g.getAccuracy());
      setSpeed(speed.get()-g.getSpeed());
      
   }
   public Inventory getInventory() {
      return inventory;
   }

   public void removeFrominventory(Object o){
      if(inventory.contains(o)) inventory.remove(o);
   }
   public void addToInventory(Object o){
      inventory.add(o);
   }
   public Gear replaceWeapon(Gear g, boolean b){//b for god modenot active if true
      Gear c=weapon;
      weapon=g;
      if(b){
         addGearStats(weapon);
         removeGearStats(c);
      }
      return c;
   }
   public Gear replaceHeadGear(Gear g, boolean b){//return old gear
      Gear c=gear[0];
      gear[0]=g;
      if(b){
         addGearStats(gear[0]);
         removeGearStats(c);
      }
      return c;
   }
   public Gear replaceBodyGear(Gear g, boolean b){//return old gear
      Gear c=gear[1];
      gear[1]=g;
      if(b){
         addGearStats(gear[1]);
         removeGearStats(c);
      }
      return c;
   }
   public Gear replaceLegGear(Gear g, boolean b){//return old gear
      Gear c=gear[2];
      gear[2]=g;
      if(b){
         addGearStats(gear[2]);
         removeGearStats(c);
      }
      return c;
   }
   public Gear replaceShoeGear(Gear g, boolean b){//return old gear
      Gear c=gear[3];
      gear[3]=g;
      if(b){
         addGearStats(gear[3]);
         removeGearStats(c);
      }
      return c;
   }
   public Gear[] getGear(){
      return gear;
   }
   public Gear getWeapon(){
      return weapon;
   }
   public void AddEXP(int e){
      exp+=e;
   }
   public int getEXP(){
      return exp;
   }
   public int getLevel(){
      return level.get();
   }
   public void addLevel(){
      level.incrementAndGet();
   }
   public int getXPLevel(){
      return xplevel;
   }
   public void setXPLevel(){
      exp-=xplevel;//reset exp as needed
      xplevel*=2;
   }
   /**
    * @return the money
    */
   public int getMoney() {
      return money;
   }
   /**
    * @param m the money to set
    */
   public void setMoney(int m) {
      money = m;
   }
   public String getAttackType(){
      return attackType;
   }
   public abstract int getCooldown(int s);
   public AtomicInteger getArmorBuff() {
      return armorBuff;
   }
   public void setArmorBuff( int i) {
      this.armorBuff.set(i);;
   }
   public AtomicInteger getArmor() {
      return armor;
   }
   public void setArmor(int i) {
      this.armor.set(i);
   }
   public int getBaseArmor() {
      return baseArmor.get();
   }
   public void setBaseArmor(int baseArmor) {
      this.baseArmor.set(baseArmor);;
   }
   

    

}
