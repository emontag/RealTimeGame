import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.Semaphore;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;


public class Game implements Serializable{
   private Board board;
   private Player player;
   private  transient Interface face;
   private int  extraBoost;
   private AtomicInteger difficulty;
   private int round;
   private AtomicInteger[] ehp;
   private boolean loadSuccess=false;
   private Enemy[] enemy;
   static AtomicBoolean inFight;
   private volatile boolean[][] enemyDOT;
   private static int gear_code;
   //public static Semaphore playerSemaphore=new Semaphore(1);
   private  AtomicBoolean god_mode, playerStun,  playerMiss,  shielded, playerDebuff,  gameOver;
   public Game(int d){
      Pane.askForLoad(this);
      if(!loadSuccess){
         round=1;
         difficulty=new AtomicInteger();
         difficulty.set(d);;
         board=new Board();
         player=Pane.askForClass();
         face=new GUI(this, board);
         face.howToPlay(); //get to bottom right from top left
         board.PutEnemies(difficulty.get());
         inFight=new AtomicBoolean(false);
         god_mode=new AtomicBoolean(false);
         playerStun=new AtomicBoolean(false);
         playerMiss=new AtomicBoolean(false);
         shielded=new AtomicBoolean(false);
         playerDebuff=new AtomicBoolean(false);
         gameOver=new AtomicBoolean(false);
      }
      loadSuccess=false;
      //AtomicInteger ai=new AtomicInteger();
      
   }
   public Player getPlayer(){
      return player;
   }
   public int getDifficulty(){ return difficulty.get(); }
   public boolean getGodMode(){return god_mode.get();}
   public void setGodMode(boolean b){god_mode.set(b);;}
   public void win(){
      if(round == 5){
         //face.PassAlongMessage("You Win the game!");
         //face.disablePotionUse();
         //face.preventMovement();
         //gameOver=true;
         //return;
         faceArchenemy();
         return;
      }
      face.dispose();
      board=new Board();
      board.PutEnemies(difficulty.incrementAndGet());
      face=new GUI(this,board);
      face.updatePlayerPotion(Integer.toString(player.getNumberHPotion()));
      //if(player.getNumberHPotion() > 0) face.enablePotionUse();
      face.PassAlongMessage("Congratulations you win round "+ round++ + "!\n");
      face.allowMovement();
      if(god_mode.get()) return;
      int temp=player.getBaseCrit();
      player.setBaseCrit(player.getBaseCrit()+1);
      player.setCrit(player.getCrit()+player.getBaseCrit()-temp);
      temp=player.getBaseHp();
      player.setBaseHp((int) (player.getBaseHp()*1.25));
      player.setMaxHp(player.getMaxHp()+player.getBaseHp()-temp);
      player.setCurrentHp(player.getMaxHp());
      temp=player.getBaseMp();
      player.setBaseMp((int) (player.getBaseMp()*1.25));
      player.setMaxMp(player.getMaxMp()+player.getBaseMp()-temp);
      player.setCurrentMp(player.getMaxMp());
      temp=player.getBasePower();
      player.setBasePower((int) (player.getBasePower()+ player.getBasePower()*.25));
      player.setPower(player.getPower()+player.getBasePower()-temp);//gear takes effect but multiplication on base
      temp=player.getBaseAccuracy();
      player.setBaseAccuracy(player.getBaseAccuracy()+1);
      player.setAccuracy(player.getAccuracy()+player.getBaseAccuracy()-temp);
      temp=player.getBaseSpeed();
      player.setBaseSpeed((int) (player.getBaseSpeed()+player.getBaseSpeed()+5));
      player.setSpeed(player.getSpeed()+player.getBaseSpeed()-temp);
      face.updatePlayerHP(Integer.toString(player.getCurrentHp()));
      face.updatePlayerMP(Integer.toString(player.getCurrentMp()));
      face.updatePlayerPower(Integer.toString(player.getPower()));
      face.updatePlayerSpeed(Integer.toString(player.getSpeed()));
      face.updatePlayerAccuracy(Integer.toString(player.getAccuracy()));
      face.updatePlayerCrit(Integer.toString(player.getCrit()));
      
      
   }
   private void faceArchenemy() {
      Enemy[] enemy=new Archenemy[1];
      enemy[0]=new Archenemy(this, player, face);
      face.setUpArchenemy();
      enemy[0].setEnemyStun(false);
      int[] arr={0,1,2,3,4,5,6,7,8,9,10};
      face.enableMoveButtons(arr);
      System.out.println("ArchEnemy");
      ehp=new AtomicInteger[1];
      ehp[0]=new AtomicInteger(enemy[0].getCurrentHp().get());
      //enemy.setEHP(ehp);
      MyThread thread=new MyThread(this, player, ehp, face);
      thread.start();
      ManaRegeneration regen=new ManaRegeneration(player, face);
      regen.start();
      inFight.set(true);
      face.preventMovement();
      enemy[0].start();
      EnemyMonitor em= new EnemyMonitor(enemy, ehp);
      em.start();
      
   }
   public void winGame(){
    face.WinMessage("Congratulations. You Win the game!\n");
    face.disablePotionUse();
    face.preventMovement();
    gameOver.set(true);
    return;
   }
   public void playerMovement(String event){
      int check=board.checkFuturePosition(event);
      if(check!=0)  {
         face.PassAlongLogMessage("Invalid movement!\n");
         if(check==1) face.markIfWall(event);
         return;
      }
      int action=board.movePlayer();
      //if(markedForEnemy()) battle();
      int[] w=board.getCurrentSpot();
      if(action==5){//merchant. Placed here so after win round can go to merchant
         Random r=new Random();
         if(r.nextBoolean()){
            face.callMerchant(createArmor());
         }
         else{
            face.callMerchant(createWeapon());
         }
      }
      if(w[0]==11 && w[1]==11) {
         win();
         return;
      }
      if(action==4) {
         //potion
         //force use of potion unless health or stamina/mana etc.
         //all health and mana potions should raise 30%
         if(god_mode.get()) return;
         Random r=new Random();
         int n=r.nextInt(3);
         //System.out.println("Got potion");
         if(n==0 ) {
            player.AddHPotion();
            face.updatePlayerPotion(Integer.toString(player.getNumberHPotion()));
            //face.enablePotionUse();
            return;
         }
         else{
            int choice=face.askForUpgrade();
            if (choice==1) {
               int temp=player.getBasePower();
               player.setBasePower((int) (player.getBasePower()+ 5));
               player.setPower(player.getPower()+player.getBasePower()-temp);//gear takes effect but multiplication on base
               face.updatePlayerPower(Integer.toString(player.getPower()));
            }
            else if (choice==2){
               int temp=player.getBaseSpeed();
               player.setBaseSpeed((int) (player.getBaseSpeed()+3));
               player.setSpeed(player.getSpeed()+player.getBaseSpeed()-temp);
               face.updatePlayerSpeed(Integer.toString(player.getSpeed()));
            }
            else if (choice ==3){//hp
               int temp=player.getBaseHp();
               player.setBaseHp((int) (player.getBaseHp()+10));
               player.setMaxHp(player.getMaxHp()+player.getBaseHp()-temp);
               player.setCurrentHp(player.getMaxHp());
               face.updatePlayerHP(Integer.toString(player.getCurrentHp()));
            }
            else if (choice ==4){//mp
               int temp=player.getBaseMp();
               player.setBaseMp((int) (player.getBaseMp()+10));
               player.setMaxHp(player.getMaxHp()+player.getBaseMp()-temp);
               player.setCurrentHp(player.getMaxHp());
               face.updatePlayerMP(Integer.toString(player.getCurrentMp()));
            }
            else if (choice==5){//accuracy
               int temp=player.getBaseAccuracy();
               player.setBaseAccuracy(player.getBaseAccuracy()+1);
               player.setAccuracy(player.getAccuracy()+player.getBaseAccuracy()-temp);
               face.updatePlayerAccuracy(Integer.toString(player.getAccuracy()));
               
            }
            else if (choice==6){//crit
               int temp=player.getBaseCrit();
               player.setBaseCrit(player.getBaseCrit()+1);
               player.setCrit(player.getCrit()+player.getBaseCrit()-temp);
               face.updatePlayerCrit(Integer.toString(player.getCrit()));
            }
            else {
               face.PassAlongMessage("No bonus applied!\n");
            }
            return;
         }
      }
      if(action==3){
         //fight
         //make enemy, unlock buttons in gui
         //sleep until enemy gone or you die use volatile boolean
         //afterwards disable buttons and continue
         //switch from text to labels for player
         //use power instead of strength etc.
         int[] arr={0,1,2,3,4,5,6,7,8,9,10};
         face.enableMoveButtons(arr);
         if(player.getNumberHPotion() > 0) face.enablePotionUse();
         //System.out.println("Enemy");
         face.PassAlongMessage("Enemy encountered. Prepare to fight!\n");
         Random r=new Random();
         enemy=new Enemy[r.nextInt(difficulty.get()+1)+round];
         ehp=new AtomicInteger[enemy.length];
         List l=new ArrayList();
         for(int i=0; i< enemy.length; i++){
            enemy[i]=new Enemy(this, player, face, i);
            ehp[i]=new AtomicInteger(enemy[i].getCurrentHp().get());
            l.add(enemy[i].getType());
         }
         face.createEnemyList(l);
         
         MyThread thread=new MyThread(this, player, ehp, face);
         thread.start();
         ManaRegeneration regen=new ManaRegeneration(player, face);
         regen.start();
         inFight.set(true);;
         face.preventMovement();
         enemyDOT=new boolean[enemy.length][2];
         for(int i=0; i< enemyDOT.length; i++){
            for(int j=0; j<enemyDOT[i].length; j++)
            enemyDOT[i][j]=false;
         }
         for(int i=0; i< enemy.length; i++){
            enemy[i].start();
         }
         EnemyMonitor em= new EnemyMonitor(enemy, ehp);
         em.start();
         
      }
   }
   public void attackBurst(int s, int mana_cost, int num) {
      if(god_mode.get()) {
         godModeActivate();
         ehp[num].set(0);;
         return;
      }
      String[] tech=player.getTechniques();
      
    /*  if(s*2*difficulty > player.getCurrentMp()) {
         face.PassAlongMessage("Move failed! Not enough MP.\n");
         return;
      }*/
      //referenced in attack in gui and in flip technique buttons
      if(s!=0) player.setCurrentMp(player.getCurrentMp()-mana_cost);
      face.flipTechniqueButtons();//need here to turn off buttons when ManaRegeneration sleeps
      if (player.getCurrentMp() <0) player.setCurrentMp(0);
      face.updatePlayerMP(Integer.toString(player.getCurrentMp()));
      Random r=new Random();
      int p=player.getPower()/6;
      boolean AOE=false;
      face.PassAlongMessage(Player.name + " uses "+ tech[s] + ". \n");
      if(playerMiss.get() && s!=10) {
         face.PassAlongMessage("You miss!\n");
         playerMiss.set(false);
         return;
      }
      if(r.nextInt(100) >=player.getAccuracy() && s!=10) {
         face.PassAlongMessage("You miss!\n");
         return;
      }
      if(r.nextInt(100)> player.crit_chance) {
         p=(int) ( p*1.5);
         face.PassAlongMessage("Critical Strike!\n");
      }
      if(s==10) {
         player.heal();
         face.updatePlayerHP(Integer.toString(player.getCurrentHp()));
         return;
      }
      else if (s==9){
         enemy[num].setEnemyStun(true);
         p+=r.nextInt(10);
      }
      else if (s==8){
         enemy[num].setEnemyMiss(true);
         p+=r.nextInt(25);
      }
      else if (s==7){
         shielded.set(true);
         p+=r.nextInt(25);
      }
      else if (s==6){
         enemy[num].setEnemyDebuff(true);
         p+=r.nextInt(25);
      }
      else if (s==5){
         player.setArmorBuff(player.getArmorBuff().get()+5);
         AOE=true;
         p+=r.nextInt(35);
      }
      else if (s==4){
         player.setArmorBuff(player.getArmorBuff().get()+5);
         AOE=true;
         p+=r.nextInt(25);
      }
      else if (s==3){
         player.setArmorBuff(player.getArmorBuff().get()+2);
         setExtraBoost(getExtraBoost() + 1);
         p+=r.nextInt(13);
      }
      else if (s==2){
         player.setArmorBuff(player.getArmorBuff().get()+1);
         setExtraBoost(getExtraBoost() + 1);
         p+=r.nextInt(7);
      }
      else if (s==1){
         setExtraBoost(getExtraBoost() + 1);
         p+=r.nextInt(4);
      }
      else if (s==0){
         setExtraBoost(getExtraBoost() + 1);
         p+=r.nextInt(2);
      }
      //extraBoost gives more damage buildup if use first 4 then use greater move 
      if(s>3) {
         while(extraBoost > 0){
            p+=(p*.03);
            extraBoost-=1;
         }
      }
      else face.PassAlongMessage("Attack buff increase is at level " + extraBoost +".\n");
      if(playerDebuff.get()){
         playerDebuff.set(false);
         p=(int) (p*.75);
      }
      if(!AOE) {
         ehp[num].set(ehp[num].get()-p);
         face.PassAlongMessage("You have done "+ p + " damage to the "+ enemy[num].getType()+"!\n");
      }
      else{
         ehp[num].set(ehp[num].get()-p);
         face.PassAlongMessage("You have done "+ p + " damage to the "+ enemy[num].getType()+"!\n");
         for(int i=0; i< ehp.length; i++){
            if(ehp[i].get()<1) continue;
            if(r.nextBoolean() && i!=num) {
               ehp[num].set(ehp[num].get()-p/2);
               face.PassAlongMessage("You have done "+ p/2 + " damage to the "+ enemy[i].getType()+"!\n");
            }
         }
      }
      //System.out.println("Enemy " + num + " HP: "+ ehp[num]);
      if(ehp[num].get()<=0) {
         ehp[num].set(0);
         
      }
      face.PassAlongMessage(enemy[num].getType()+" has "+ ehp[num].get() + " HP left.\n");
      //if(ehp[0]<=0) face.PassAlongMessage("Enemy has no" + " HP left!\n");
      
      
   }
   
   public void attackDOT(int s, int mana_cost, int num) {
      if(god_mode.get()) {
         godModeActivate();
         ehp[num].set(0);;
         return;
      }
      String[] tech=player.getTechniques();
      
      //referenced in attack in gui and in flip technique buttons
      if(s!=0) player.setCurrentMp(player.getCurrentMp()-mana_cost);
      face.flipTechniqueButtons();//need here to turn off buttons when ManaRegeneration sleeps
      if (player.getCurrentMp() <0) player.setCurrentMp(0);
      face.updatePlayerMP(Integer.toString(player.getCurrentMp()));
      Random r=new Random();
      int p=player.getPower()/6;
      //boolean AOE=false;
      face.PassAlongMessage(Player.name + " uses "+ tech[s] + ". \n");
      if(playerMiss.get() && s!=10) {
         face.PassAlongMessage("You miss!\n");
         playerMiss.set(false);
         return;
      }
      if(r.nextInt(100) >=player.getAccuracy() && s!=10) {
         face.PassAlongMessage("You miss!\n");
         return;
      }
      if(r.nextInt(100)> player.crit_chance) {
         p=(int) ( p*1.5);
         face.PassAlongMessage("Critical Strike!\n");
      }
      if(s==10) {
         player.heal();
         face.updatePlayerHP(Integer.toString(player.getCurrentHp()));
         return;
      }
      else if (s==9){
         enemy[num].setEnemyStun(true);
         p+=r.nextInt(10);
      }
      else if (s==8){
         enemy[num].setEnemyMiss(true);
         p+=r.nextInt(15);
      }
      else if (s==7){
         shielded.set(true);
         p+=r.nextInt(15);
      }
      else if (s==6){
         enemy[num].setEnemyDebuff(true);
         p+=r.nextInt(15);
      }
      else if (s==5){
         //AOE=true;
         p+=r.nextInt(25);
      }
      else if (s==4){
         //AOE=true;
         p+=r.nextInt(15);
      }
      else if (s==3){
         
         p+=r.nextInt(13);
      }
      else if (s==2){
         p+=r.nextInt(7);
      }
      else if (s==1){
         if(!enemyDOT[num][1]){
            p/=2;
            DOT dot=new DOT(ehp[num], p+=r.nextInt(4), face,5,enemyDOT, num, 1, enemy[num].getType(), player);
            dot.start();
            return;
         }
         else{
            face.PassAlongMessage("This DOT is already applied to this enemy. \n");
            return;
         }
      }
      else if (s==0){
         if(!enemyDOT[num][0]){
            p/=2;
            DOT dot=new DOT(ehp[num], p+=r.nextInt(2), face,10,enemyDOT, num, 0, enemy[num].getType(), player);
            dot.start();
            return;
         }
         else{
            face.PassAlongMessage("This DOT is already applied to this enemy. \n");
            return;
         }
         
      }
      //extraBoost gives more damage buildup if use first 4 then use greater move 
      if(playerDebuff.get()){
         playerDebuff.set(false);
         p=(int) (p*.75);
      }
      //if(!AOE) {
      ehp[num].set(ehp[num].get()-p);
      face.PassAlongMessage("You have done "+ p + " damage to the "+ enemy[num].getType()+"!\n");
      //}
      //else{
        /* ehp[num].set(ehp[num].get()-p);
         face.PassAlongMessage("You have done "+ p + " damage to the "+ enemy[num].getType()+"!\n");
         for(int i=0; i< ehp.length; i++){
            if(ehp[i].get()<1) continue;
            if(r.nextBoolean() && i!=num) {
               ehp[num].set(ehp[num].get()-p/2);
               face.PassAlongMessage("You have done "+ p/2 + " damage to the "+ enemy[i].getType()+"!\n");
            }
         }
      }*/
      //System.out.println("Enemy " + num + " HP: "+ ehp[num]);
      if(ehp[num].get()<=0) {
         ehp[num].set(0);
         
      }
      face.PassAlongMessage(enemy[num].getType()+" has "+ ehp[num].get() + " HP left.\n");
      //if(ehp[0]<=0) face.PassAlongMessage("Enemy has no" + " HP left!\n");
      
      
   }
   
   public String getNumPot(){
      return Integer.toString(player.getNumberHPotion());
   }
   public String getCurrentHp(){
      return Integer.toString(player.getCurrentHp());
   }
   public String getMaxHp(){ 
      return Integer.toString(player.getMaxHp());
      }
   public String getCurrentMp(){ 
      return Integer.toString(player.getCurrentMp());
   }
   public String getMaxMp(){ 
      return Integer.toString(player.getMaxMp());
   }
   public String getPower(){ 
      return Integer.toString(player.getPower());
   }
   public String getSpeed(){ 
      return Integer.toString(player.getSpeed());
   }
   public void lose() {
      face.PassAlongMessage("\nYou lose!\n");
      face.disablePotionUse();
      face.shutdown();
      setGameOver(true);
   }
   public void godModeActivate(){
      player.setBaseHp(Integer.MAX_VALUE);
      player.setBaseMp(Integer.MAX_VALUE);
      player.setBaseSpeed(100);
      player.setPower(Integer.MAX_VALUE);
      player.setBaseCrit(100);
      player.setBaseAccuracy(100);
      player.setMaxHp(Integer.MAX_VALUE);
      player.setMaxMp(Integer.MAX_VALUE);
      player.setCurrentHp(Integer.MAX_VALUE);
      player.setCurrentMp(Integer.MAX_VALUE);
      player.setSpeed(100);
      player.setCrit(100);
      player.setAccuracy(100);
      player.setPower(Integer.MAX_VALUE);
      face.updatePlayerHP(Integer.toString(player.getCurrentHp()));
      face.updatePlayerMP(Integer.toString(player.getCurrentMp()));
      face.updatePlayerPower(Integer.toString(player.getPower()));
      face.updatePlayerSpeed(Integer.toString(player.getSpeed()));
      face.updatePlayerCrit(Integer.toString(player.getCrit()));
      face.updatePlayerAccuracy(Integer.toString(player.getAccuracy()));
      
   }

   
   public void setPlayerStun(boolean b) {
      playerStun.set(b);
      
   }
   public boolean getPlayerStun(){
      return playerStun.get();
   }
   public void setPlayerMiss(boolean b) {
      playerMiss.set(b);
      
   }
   
   
   public boolean getPlayerDebuff() {
      return playerDebuff.get();
   }
   public void setPlayerDebuff(boolean b){
      playerDebuff.set(b);
   }
   
   
   public boolean getShielded() {
      return shielded.get();
   }
   public void setShielded(boolean b){
      shielded.set(b);
   }
  /* public int[] getEHP(){
      return ehp;
   }*/
   public boolean getPlayerMiss(){
      return playerMiss.get();
   }
   public int getExtraBoost() {
      return extraBoost;
   }
   public void setExtraBoost(int e) {
      extraBoost = e;
   }
   public boolean isGameOver() {
      return gameOver.get();
   }
   public void setGameOver(boolean g) {
      gameOver.set(g);
   }
   public List doSave(){
      List saving=new ArrayList();
      
      saving.add(board);
      saving.add(this);
      //saving.add(face.getSubclass());
      saving.add(player);
      return saving;
   }
   public void doLoad(List l) {
      Object[] list=l.toArray();
      board=(Board) list[0];
      player=(Player) list[2];
      face=new GUI(this, board);
      face.load();
      Game g=(Game) list[1];
      difficulty.set(g.getDifficulty());
      round=g.getRound();
      god_mode.set(g.getGodMode());
      
   }
   private int getRound() {
      
      return round;
   }
   public void setLoadSuccess(boolean b) {
      loadSuccess=b;
      
   }
   public String getAccuracy() {
      return Integer.toString(player.getAccuracy());
      
   }
   public String getCrit() {
      return Integer.toString(player.getCrit());
   }
   public Inventory getInventory() {
      return player.getInventory();
   }

   public void removeFrominventory(Object o){
      if(player.getInventory().contains(o)) player.getInventory().remove(o);
   }
   public void addToInventory(Object o){
      player.getInventory().add(o);
   }
   static int getCode(){
      return gear_code;
   }
   static void addCode(){
      gear_code++;
   }
   public Gear createWeapon(){
      Random r=new Random();
      final String[] race={"Dwarf","Elf", "Gnome","Fairy","Goblin", "Gremlin", "Lich", 
            "Halfling", "Vampire","Werewolf", "Orc", "Goblin", "Giant", 
                   "Garwin", "Verpal", "Tarka", "Xiplo", "Hesla", 
                  "Levhow", "Grampus", "Julhow", "Rewdog", "Zaraganbah", "Iron", "Wooden", "Steel", "Dragonbone", "Cursed", "Fission"};
      String name="";
      name+=race[r.nextInt(race.length)];
      name+=" ";
      if(player instanceof Warrior) name+="sword";
      else if(player instanceof Archer) name+="bow";
      else if(player instanceof Mage) name+="staff";
      return new Gear(0, 0, r.nextInt(player.getLevel()+10), r.nextInt(player.getLevel()+5), r.nextInt(player.getLevel()+2), r.nextInt(player.getLevel()+2), -1, name);
   }
   public Gear createArmor(){
      Random r=new Random();
      final String[] race={"Dwarf","Elf", "Gnome","Fairy","Goblin", "Gremlin", "Lich", 
            "Halfling", "Vampire","Werewolf", "Orc", "Goblin", 
            "Giant", "Garwin", "Verpal", "Tarka", "Xiplo", "Hesla", "Levhow", 
            "Grampus", "Julhow", "Rewdog", "Zaraganbah, Mythril, Holy, Dragonplate"};
      final String[] t={"headgear", "shirt", "pants", "shoes"};
      String name="";
      name+=race[r.nextInt(race.length)];
      name+=" ";
      int tem=r.nextInt(t.length);
      name+=t[tem];
      return new Gear(player.getLevel()*10+r.nextInt(20), player.getLevel()*10+r.nextInt(20), r.nextInt(player.getLevel()+2), r.nextInt(player.getLevel()+2), r.nextInt(player.getLevel()+2), r.nextInt(player.getLevel()+2), tem, name);
   }
   public int getEHP(int n){
      return ehp[n].get();
   }
   public void setEHP(final int hp, int n){
      ehp[n].set(hp);
   }
   public Enemy[] getEnemies(){
      return enemy;
   }

}
