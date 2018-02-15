


public class Archenemy extends Enemy{
   public Archenemy(Game g, Player p, Interface f){
      super(g, p, f, 0);
      currenthp.set(currenthp.get()*10);
      //ehp[0]=currenthp;
   }
   public void run(){
      while(Game.inFight.get()){
         if(!Game.inFight.get()) return;
         if (game.getGodMode()) game.godModeActivate();
         eligibleToStun--;
         try {
            int speed=player.getSpeed()/3;
            if(random.nextInt(100) > speed){
               face.PassAlongMessage("Archenemy hits you with attack.\n");
               if(random.nextInt(10) >=9 && eligibleToStun <= 0) {
                  //stun for certain seconds
                  game.setPlayerStun(true);
                  eligibleToStun=10;
                  face.playerStun();
               }
               else if (random.nextInt(5) >=4){
                  game.setPlayerMiss(true);
               }
               else if (random.nextInt(5) >=4){
                  game.setPlayerDebuff(true);
               }
            }
            else {
               face.PassAlongMessage(" Archenemy attack Misses.\n");
               Thread.sleep(random.nextInt(3)*1000);
               return;
            }
            power=random.nextInt(game.getDifficulty()*25*player.getLevel());
            player.setCurrentHp(player.getCurrentHp()-power);
            face.updatePlayerHP(Integer.toString(player.getCurrentHp()));
            Thread.sleep(random.nextInt(3)*1000+1000);
     
         } catch (Exception e) {
            e.printStackTrace();
         }
      }
      if(game.getEHP(0) <=0) game.winGame();
   }

}
