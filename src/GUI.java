import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.KeyEventDispatcher;
import java.awt.KeyboardFocusManager;
import java.awt.MouseInfo;
import java.awt.TextArea;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.File;
import java.io.FileOutputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.LinkedList;
import java.util.List;
import java.util.Observable;
import java.util.Observer;
import java.util.Random;
import java.util.concurrent.atomic.AtomicInteger;

import javafx.application.Platform;
import javafx.embed.swing.JFXPanel;
import javafx.stage.FileChooser;

import javax.swing.ButtonGroup;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JEditorPane;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.JTextPane;
import javax.swing.text.BadLocationException;
import javax.swing.text.DefaultCaret;
import javax.swing.text.DefaultStyledDocument;
import javax.swing.text.Document;
import javax.swing.text.MutableAttributeSet;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyledDocument;

public class GUI extends Interface implements ActionListener, Observer, MouseListener, KeyListener, Serializable{
   private  transient JFrame jframe;
   private  transient JLabel[][] board;
   private  transient Container content;
   private  Game game;
   private  Board bore;
   private  TextArea move_log, level_log;
   private  JLabel[] player_stats;
   private transient MyButtons[] player_moves;
   //private  TextArea gameText;
   private StyledDocument gameText;
   private JTextPane gamePane;
   private  JButton[] movementButtons;
   private int[] player_location= new int[2];
   private JPanel radioPanel;
   private int enemyTarget;
   JRadioButton[] radioButton;
   static int screenX=0, screenY=0, width, height;//need static for new game
   public GUI(Game g, Board b){
      currentGUI=this;
      bore=b;
      bore.addObserver(this);
      game=g;
      player=game.getPlayer();
      jframe=new JFrame();
      jframe.setSize(900,800);
      content=jframe.getContentPane();
      jframe.setLayout(new GridLayout(4,1));
      //jframe.setLocation(500,200);
      jframe.setTitle("The Game");
      jframe.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
      createFileMenu();
      JPanel hp_panel=new JPanel();
      move_log=new TextArea("",10,10,TextArea.SCROLLBARS_VERTICAL_ONLY );
      level_log=new TextArea("",10,10,TextArea.SCROLLBARS_VERTICAL_ONLY );
      level_log.setEditable(false);
      level_log.setFont(new Font("Arial", Font.PLAIN, 18));
      move_log.setFont(new Font("Arial", Font.PLAIN, 18));
      //player_stats=new TextArea("",10,10,TextArea.SCROLLBARS_VERTICAL_ONLY );
      player_stats= new JLabel[14];
      JPanel stat_panel=new JPanel();
      stat_panel.setLayout(new GridLayout(7,7));
      for(int i=0; i< player_stats.length;i++){
         player_stats[i]=new JLabel();
         player_stats[i].setEnabled(false);
         stat_panel.add(player_stats[i]);
         
      }
      player_stats[0].setText("HP");
      player_stats[2].setText("MP");
      //add power and speed and # heal pots
      player_stats[4].setText("Power");
      player_stats[6].setText("Speed");
      player_stats[8].setText("Accuracy");
      player_stats[10].setText("Critical Chance");
      player_stats[12].setText("Number of Health Potions");
      move_log.setEditable(false);
      hp_panel.setLayout(new GridLayout(1,2));
      hp_panel.add(stat_panel);
      hp_panel.add(level_log);
      content.add(hp_panel);
    //add game text
      //gameText=new TextArea("",10,10,TextArea.SCROLLBARS_VERTICAL_ONLY );
      //gameText.setEditable(false);
      //gameText.setText("");
      //gameText.setFont(new Font("Arial", Font.PLAIN, 15));
      gameText=new DefaultStyledDocument();
      gamePane=new JTextPane(gameText);
      gamePane.setFont(new Font("Arial", Font.PLAIN, 15));
      gamePane.setEditable(false);
      DefaultCaret caret = (DefaultCaret)gamePane.getCaret();
      caret.setUpdatePolicy(DefaultCaret.ALWAYS_UPDATE);
      JScrollPane scroll=new JScrollPane(gamePane);
      scroll.setWheelScrollingEnabled(true);
      scroll.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
      content.add(scroll);
      //create JLabels for techniques
      JPanel player_panel=new JPanel();
      createPlayermovelabels(player_panel);
      content.add(player_panel);
      //set up board
      setUpBoard();
      player_stats[1].setText(game.getCurrentHp());
      player_stats[3].setText(game.getCurrentMp());
      player_stats[5].setText(game.getPower());
      player_stats[7].setText(game.getSpeed());
      player_stats[9].setText(game.getAccuracy());
      player_stats[11].setText(game.getCrit());
      player_stats[13].setText(game.getNumPot());
      if(player_stats[13].getText()=="0")  player_moves[11].setEnabled(false);
      //jframe.setLocationRelativeTo(null);
      if(screenX==0 && screenY==0){
         jframe.setLocationRelativeTo(null);;
         jframe.setVisible(true);
      }
      else{
         jframe.setBounds(screenX, screenY, width, height);
         jframe.setVisible(true);
      }
      
   }
   public void swapButtonStatus(int[] arr){
      for(int i=0;i<arr.length;i++){
         if(player_moves[arr[i]].isEnabled()) player_moves[arr[i]].setEnabled(false);
         else if(!player_moves[arr[i]].isEnabled()) player_moves[arr[i]].setEnabled(true);
      }
   }

  private void setUpBoard() {
     //int[][] b=bore.getNumbers();
     JPanel panel= new JPanel(); 
     JPanel pane=new JPanel();
     panel.setLayout(new GridLayout(12,12));
     pane.setLayout(new GridLayout(1,2));
     board=new MyLabel[12][12];
     for(int i=0;i<board.length;i++){
         for(int j=0;j<board[i].length;j++){
            board[i][j]=new MyLabel("E");
            //board[i][j].setText(Integer.toString(b[i][j]));
            panel.add(board[i][j]);
         }
      }
     board[0][0].setText("P");
     board[11][11].setText("Dest");
     
           
     pane.add(panel);
     //instead use 4 buttons for direction 
     JPanel button_pane=new JPanel();
     button_pane.setLayout(new BorderLayout());
     movementButtons=new JButton[4];
     //map=new InputMap[4];
     for(int i=0;i<movementButtons.length;i++){
        movementButtons[i]=new JButton();
        movementButtons[i].addActionListener(this);
     }
     movementButtons[0].setText("up");
     movementButtons[1].setText("right");
     movementButtons[2].setText("down");
     movementButtons[3].setText("left");
     button_pane.add(movementButtons[0], BorderLayout.NORTH);
     button_pane.add(movementButtons[1], BorderLayout.EAST);
     button_pane.add(movementButtons[2], BorderLayout.SOUTH);
     button_pane.add(movementButtons[3], BorderLayout.WEST);
     button_pane.add(move_log, BorderLayout.CENTER);
     pane.add(button_pane);
     content.add(pane);
     updateLevelLog();
     //http://stackoverflow.com/questions/5344823/how-can-i-listen-for-key-presses-within-java-swing-across-all-components
     KeyboardFocusManager.getCurrentKeyboardFocusManager()
     .addKeyEventDispatcher(new KeyEventDispatcher() {
         @Override
         public boolean dispatchKeyEvent(KeyEvent e) {
            
           if(KeyEvent.KEY_PRESSED==e.getID()) keyPressed(e);
           return false;
         }
   });
     
   }

   private void createPlayermovelabels(JPanel panel) {
      panel.setLayout(new GridBagLayout());
      GridBagConstraints c=new GridBagConstraints();
      radioPanel=new JPanel();
      c.gridheight=10;
      c.gridwidth=10;
      c.anchor=GridBagConstraints.NORTHWEST;
      c.weightx=.1;
      c.weighty=.1;
      panel.add(radioPanel, c);
      
      
      JPanel pane=new JPanel();
      player_moves=new MyButtons[12];
      Player player=game.getPlayer();
      String [] techniques=player.getTechniques();
      pane.setLayout(new GridLayout(6,2));
      for(int i=0;i<player_moves.length;i++){
         player_moves[i]=new MyButtons(this, game, player);
         player_moves[i].setText(techniques[i]);
         player_moves[i].setName(Integer.toString(i));
         player_moves[i].addMouseListener(this);
         pane.add(player_moves[i]);
         player_moves[i].setEnabled(false); ;
      }
      //c.anchor=GridBagConstraints.CENTER;
      c.weightx=1;
      c.weighty=0;
      c.fill=GridBagConstraints.BOTH;
      panel.add(pane, c);
      //panel.setBackground(new Color(217,253,255));
   }


   public void actionPerformed(ActionEvent e) {
      String event=e.getActionCommand();
      if(event == "New Game"){
         //consider option pane to be sure
         Game.inFight.set(false);
         player.setCurrentHp(0);//ends all threads as will set match over in myThread
         screenX=jframe.getX();
         screenY=jframe.getY();
         width=jframe.getWidth();
         height=jframe.getHeight();
         jframe.dispose();
         new Game(2);
         return;
      }
      if(event=="Cheats"){
         String cheat=JOptionPane.showInputDialog(jframe,"Enter Cheat!", "Here!");
         if(cheat !=null && cheat.trim().equals("DEATH")) {
            game.setGodMode(true);
            game.godModeActivate();
         }
         if(cheat!=null && cheat.trim().equals("VISUAL")){
            showWalls();
         }
         if(cheat!=null && cheat.trim().equals("PESTILENCE")){
            showPotions();
         }
         if(cheat!=null && cheat.trim().equals("FAMINE")){
            showEnemies();
         }
         return;
         
      }
      //if(event=="Open"){
     //    JOptionPane.showMessageDialog(null,"Feature not yet implemented!");
         //open();
         //only allow load from outside in Pane
         //reconsruct board (5 is used spots)
     // }
      if(event=="Save"){
         //JOptionPane.showMessageDialog(null,"Feature not yet implemented!");
         save();
         return;
      }
      if(event=="up" || event=="right" || event=="down" || event=="left"){
         game.playerMovement(event);
         return;
      }
      if(event=="Armor"){
         if(Game.inFight.get() || game.isGameOver()) return;
         pullUpGearPane();
      }
      if(event=="Weapon"){
         if(Game.inFight.get() || game.isGameOver()) return;
         pullUpWeaponPane();
      }
      if(event=="Font"){
         getNewFontSize();
      }
      if(event=="Techniques"){
         setTechniques();
      }
      if(event=="Stats"){//only for testing
         setStats();
      }
      
   }

   

   private void pullUpWeaponPane() {
      Gear gear=player.getWeapon();
      JPanel panel=new JPanel();
      panel.setLayout(new GridLayout(1,3));
      JPanel gearPanel=new JPanel();
      gearPanel.setLayout(new GridLayout(1,2));
      JTextArea[] gearLabels=new JTextArea[2];
      for(int i=0; i< gearLabels.length; i++) {
         gearLabels[i]=new JTextArea();
         gearLabels[i].setEditable(false);
         gearLabels[i].setEnabled(false);
         gearPanel.add(gearLabels[i]);
      }
      gearLabels[0].setText("Weapon (" + gear.getName()+ "): ");
      gearLabels[1].setText(" HP: " + gear.getHP()+ "\n MP: "+ gear.getMP() +"\n Power: "+ gear.getPower()
         +"\n Speed: "+ gear.getSpeed()+ "\n Accuracy: " +gear.getAccuracy() + "\n Crit %: "+ gear.getCrit() );
      panel.add(gearPanel);
      JPanel inventoryPanel=new JPanel();
      Inventory inventory=player.getInventory();
      inventoryPanel.setLayout(new GridLayout(4,1));
      //weapon
      Object[] wm=inventory.getWeapon();
      String[] wmodes=new String[wm.length+1];
      wmodes[0]="";
      for(int i=1; i< wmodes.length; i++){
         Gear g=(Gear) wm[i-1];
         wmodes[i]=g.getName();
      }
      int count=0;
      for(int i=0; i< wmodes.length;i++){
         for(int j=i; j<wmodes.length;j++){
            if(wmodes[i].equals(wmodes[j]) && i!=0 && j!=0 && i!=j){
               String temp= " "+Integer.toString(++count);
               wmodes[j]+=temp;
            }
         }
      }
      JComboBox weapon_list=new JComboBox(wmodes);
      inventoryPanel.add(weapon_list);
      JTextArea wlabel=new JTextArea();
      weapon_list.addItemListener(new ItemListener(){
         public void itemStateChanged(ItemEvent e) {
            if(weapon_list.getSelectedIndex()<1) {
               wlabel.setText("");
               return;
            };
            String s=weapon_list.getSelectedItem().toString();
            Gear g=(Gear) wm[weapon_list.getSelectedIndex()-1];
            wlabel.setText("Weapon:\n HP: " + g.getHP()+ "\n MP: "+ g.getMP() +"\n Power: "+ g.getPower()
                  +"\n Speed: "+ g.getSpeed()+ "\n Accuracy: " +g.getAccuracy() + "\n Crit %: "+ g.getCrit() );
            
         }
      });
      panel.add(inventoryPanel);
      wlabel.setEditable(false);
      wlabel.setEnabled(false);
      JPanel anotherPanel=new JPanel();
      anotherPanel.setLayout(new GridLayout(1,1));
      anotherPanel.add(wlabel);
      panel.add(anotherPanel);
      int option = JOptionPane.showConfirmDialog(jframe, panel, "Weapon Inventory", JOptionPane.DEFAULT_OPTION);
      if (option == JOptionPane.OK_OPTION) {
         if(weapon_list.getSelectedIndex()>0){
            Gear g=(Gear) wm[weapon_list.getSelectedIndex()-1];
            player.removeFrominventory(g);
            if(!game.getGodMode()) g=player.replaceWeapon(g, true);
            else g=player.replaceWeapon(g, false);
            player.addToInventory(g);
         }
         
      }
      updatePlayerHP(Integer.toString(player.getCurrentHp()));
      updatePlayerMP(Integer.toString(player.getCurrentMp()));
      updatePlayerPower(Integer.toString(player.getPower()));
      updatePlayerSpeed(Integer.toString(player.getSpeed()));
      updatePlayerAccuracy(Integer.toString(player.getAccuracy()));
      updatePlayerCrit(Integer.toString(player.getCrit()));
      
      
      
   }
   private void pullUpGearPane() {
      Gear[] gear=player.getGear();
      JPanel panel=new JPanel();
      panel.setLayout(new GridLayout(1,3));
      JPanel gearPanel=new JPanel();
      gearPanel.setLayout(new GridLayout(4,2));
      JTextArea[] gearLabels=new JTextArea[8];
      for(int i=0; i< gearLabels.length; i++) {
         gearLabels[i]=new JTextArea();
         gearLabels[i].setEditable(false);
         gearLabels[i].setEnabled(false);
         //gearLabels[i].setPreferredSize(new Dimension(10,10));
         gearPanel.add(gearLabels[i]);
         //gearPanel.setPreferredSize(new Dimension(50,50));
      }
      gearLabels[0].setText("Head (" + gear[0].getName()+ "): ");
      gearLabels[1].setText(" HP: " + gear[0].getHP()+ "\n MP: "+ gear[0].getMP() +"\n Power: "+ gear[0].getPower()
         +"\n Speed: "+ gear[0].getSpeed()+ "\n Accuracy: " +gear[0].getAccuracy() + "\n Crit %: "+ gear[0].getCrit() );
      gearLabels[2].setText("Body (" + gear[1].getName()+ "): ");
      gearLabels[3].setText(" HP: " + gear[1].getHP()+ "\n MP: "+ gear[1].getMP() +"\n Power: "+ gear[1].getPower()
         +"\n Speed: "+ gear[1].getSpeed()+ "\n Accuracy: " +gear[1].getAccuracy() + "\n Crit %: "+ gear[1].getCrit() );
      gearLabels[4].setText("Legs (" + gear[2].getName()+ "): ");
      gearLabels[5].setText(" HP: " + gear[2].getHP()+ "\n MP: "+ gear[2].getMP() +"\n Power: "+ gear[2].getPower()
         +"\n Speed: "+ gear[2].getSpeed()+ "\n Accuracy: " +gear[2].getAccuracy() + "\n Crit %: "+ gear[2].getCrit() );
      gearLabels[6].setText("Feet (" + gear[3].getName()+ "): ");
      gearLabels[7].setText(" HP: " + gear[3].getHP()+ "\n MP: "+ gear[3].getMP() +"\n Power: "+ gear[3].getPower()
         +"\n Speed: "+ gear[3].getSpeed()+ "\n Accuracy: " +gear[3].getAccuracy() + "\n Crit %: "+ gear[3].getCrit() );
      
      panel.add(gearPanel);
      JPanel inventoryPanel=new JPanel();
      Inventory inventory=player.getInventory();
      inventoryPanel.setLayout(new GridLayout(4,1));
      //head
      Object[] hm=inventory.getHead();
      String[] hmodes=new String[hm.length+1];
      hmodes[0]="";
      for(int i=1; i< hmodes.length; i++){
         Gear g=(Gear) hm[i-1];
         hmodes[i]=g.getName();
      }
      int count=0;
      for(int i=0; i< hmodes.length;i++){
         for(int j=i; j<hmodes.length;j++){
            if(hmodes[i].equals(hmodes[j]) && i!=0 && j!=0 && i!=j){
               String temp= " "+Integer.toString(++count);
               hmodes[j]+=temp;
            }
         }
      }
      JComboBox head_list=new JComboBox(hmodes);
      inventoryPanel.add(head_list);
      JTextArea hlabel=new JTextArea();
      JTextArea blabel=new JTextArea();
      JTextArea llabel=new JTextArea();
      JTextArea slabel=new JTextArea();
      head_list.addItemListener(new ItemListener(){
         public void itemStateChanged(ItemEvent e) {
            if(head_list.getSelectedIndex()<1) {
               hlabel.setText("");
               return;
            };
            String s=head_list.getSelectedItem().toString();
            Gear g=(Gear) hm[head_list.getSelectedIndex()-1];
            hlabel.setText("Head:\n HP: " + g.getHP()+ "\n MP: "+ g.getMP() +"\n Power: "+ g.getPower()
                  +"\n Speed: "+ g.getSpeed()+ "\n Accuracy: " +g.getAccuracy() + "\n Crit %: "+ g.getCrit() );
            
         }
      });
      //body
      Object[] bm=inventory.getBody();
      String[] bmodes=new String[bm.length+1];
      bmodes[0]="";
      for(int i=1; i< bmodes.length; i++){
         Gear g=(Gear) bm[i-1];
         bmodes[i]=g.getName();
      }
      count=0;
      for(int i=0; i< bmodes.length;i++){
         for(int j=i; j<bmodes.length;j++){
            if(bmodes[i].equals(bmodes[j]) && i!=0 && j!=0 && i!=j){
               String temp= " "+Integer.toString(++count);
               bmodes[j]+=temp;
            }
         }
      }
      JComboBox body_list=new JComboBox(bmodes);
      inventoryPanel.add(body_list);
      body_list.addItemListener(new ItemListener(){
         public void itemStateChanged(ItemEvent e) {
            if(body_list.getSelectedIndex()<1) {
               blabel.setText("");
               return;
            }
            Gear g=(Gear) bm[body_list.getSelectedIndex()-1];
            blabel.setText("Body:\n HP: " + g.getHP()+ "\n MP: "+ g.getMP() +"\n Power: "+ g.getPower()
                  +"\n Speed: "+ g.getSpeed()+ "\n Accuracy: " +g.getAccuracy() + "\n Crit %: "+ g.getCrit() );
            
         }
      });
      
      //legs
      Object[] lm=inventory.getLegs();
      String[] lmodes=new String[lm.length+1];
      lmodes[0]="";
      for(int i=1; i< lmodes.length; i++){
         Gear g=(Gear) lm[i-1];
         lmodes[i]=g.getName();
      }
      count=0;
      for(int i=0; i< lmodes.length;i++){
         for(int j=i; j<lmodes.length;j++){
            if(lmodes[i].equals(lmodes[j]) && i!=0 && j!=0 && i!=j){
               String temp= " "+Integer.toString(++count);
               lmodes[j]+=temp;
            }
         }
      }
      JComboBox legs_list=new JComboBox(lmodes);
      inventoryPanel.add(legs_list);
      legs_list.addItemListener(new ItemListener(){
         public void itemStateChanged(ItemEvent e) {
            if(legs_list.getSelectedIndex()<1) {
               llabel.setText("");
               return;
            }
            Gear g=(Gear) lm[legs_list.getSelectedIndex()-1];
            llabel.setText("Legs:\n HP: " + g.getHP()+ "\n MP: "+ g.getMP() +"\n Power: "+ g.getPower()
                  +"\n Speed: "+ g.getSpeed()+ "\n Accuracy: " +g.getAccuracy() + "\n Crit %: "+ g.getCrit() );
            
         }
      });
      
      //feet
      
      Object[] sm=inventory.getShoes();
      String[] smodes=new String[sm.length+1];
      smodes[0]="";
      for(int i=1; i< smodes.length; i++){
         Gear g=(Gear) sm[i-1];
         smodes[i]=g.getName();
      }
      count=0;
      for(int i=0; i< smodes.length;i++){
         for(int j=0; j<smodes.length;j++){
            if(smodes[i]==smodes[j] && i!=0 && j!=0 && i!=j){
               String temp= " "+Integer.toString(++count);
               smodes[j]+=temp;
            }
         }
      }
      JComboBox shoe_list=new JComboBox(smodes);
      inventoryPanel.add(shoe_list);
      shoe_list.addItemListener(new ItemListener(){
         public void itemStateChanged(ItemEvent e) {
            if(shoe_list.getSelectedIndex()<1) {
               slabel.setText("");
               return;
            }
            Gear g=(Gear) sm[shoe_list.getSelectedIndex()-1];
            slabel.setText("Shoes:\n HP: " + g.getHP()+ "\n MP: "+ g.getMP() +"\n Power: "+ g.getPower()
                  +"\n Speed: "+ g.getSpeed()+ "\n Accuracy: " +g.getAccuracy() + "\n Crit %: "+ g.getCrit() );
            
         }
      });
      
      
      panel.add(inventoryPanel);
      hlabel.setEditable(false);
      hlabel.setEnabled(false);
      blabel.setEditable(false);
      blabel.setEnabled(false);
      llabel.setEditable(false);
      llabel.setEnabled(false);
      slabel.setEditable(false);
      slabel.setEnabled(false);
      JPanel anotherPanel=new JPanel();
      anotherPanel.setLayout(new GridLayout(4,1));
      anotherPanel.add(hlabel);
      anotherPanel.add(blabel);
      anotherPanel.add(llabel);
      anotherPanel.add(slabel);
      panel.add(anotherPanel);
      panel.setPreferredSize(new Dimension(500,500));
      int option = JOptionPane.showConfirmDialog(jframe, panel, "Armor Inventory", JOptionPane.DEFAULT_OPTION);
      if (option == JOptionPane.OK_OPTION) {
         if(head_list.getSelectedIndex()>0){
            Gear g=(Gear) hm[head_list.getSelectedIndex()-1];
            player.removeFrominventory(g);
            if(!game.getGodMode()) g=player.replaceHeadGear(g, true);
            else g=player.replaceHeadGear(g, false);
            player.addToInventory(g);
         }
         if(body_list.getSelectedIndex()>0){
            Gear g=(Gear) bm[body_list.getSelectedIndex()-1];
            player.removeFrominventory(g);
            if(!game.getGodMode()) g=player.replaceBodyGear(g, true);
            else g=player.replaceBodyGear(g, false);
            player.addToInventory(g);
         }
         if(legs_list.getSelectedIndex()>0){
            Gear g=(Gear) lm[legs_list.getSelectedIndex()-1];
            player.removeFrominventory(g);
            if(!game.getGodMode()) g=player.replaceLegGear(g, true);
            else g=player.replaceLegGear(g, false);
            player.addToInventory(g);
         }
         if(shoe_list.getSelectedIndex()>0){
            Gear g=(Gear) sm[shoe_list.getSelectedIndex()-1];
            player.removeFrominventory(g);
            if(!game.getGodMode()) g=player.replaceShoeGear(g, true);
            else g=player.replaceShoeGear(g, false);
            player.addToInventory(g);
         }
      }
      updatePlayerHP(Integer.toString(player.getCurrentHp()));
      updatePlayerMP(Integer.toString(player.getCurrentMp()));
      updatePlayerPower(Integer.toString(player.getPower()));
      updatePlayerSpeed(Integer.toString(player.getSpeed()));
      updatePlayerAccuracy(Integer.toString(player.getAccuracy()));
      updatePlayerCrit(Integer.toString(player.getCrit()));
   }
   
   private void open() {
      final FileChooser chooser=new FileChooser();
      chooser.setInitialDirectory(
            new File(System.getProperty("user.home"))
        ); 
      chooser.getExtensionFilters().addAll(
            new FileChooser.ExtensionFilter("*.txt", "*.txt")
        );
      new JFXPanel();
      Platform.runLater(new Runnable() {
         @Override
         public void run() {
           //javaFX operations should go here
           File file=null;
           try{
              file=chooser.showOpenDialog(null); 
           }catch(NullPointerException e){
              return;
           }
           
      
         }
      });
   }   
   
   public void save(){
      if(Game.inFight.get() || game.isGameOver()) return;
      List saver=game.doSave();
      final FileChooser chooser=new FileChooser();
     // chooser.setInitialDirectory(
       //     new File(System.getProperty("user.home"))
       // ); 
      new JFXPanel();
      Platform.runLater(new Runnable() {
         @Override
         public void run() {
           //javaFX operations should go here
            File f=null;
           try{
              f=chooser.showSaveDialog(null); 
              if(!f.getName().endsWith(".ser")){
                 String path=f.getAbsolutePath();
                 path+=".ser";
                 f=new File(path);
              }
           }catch(NullPointerException e){
              return;
           }
           
           try {
            FileOutputStream out= new FileOutputStream(f);
            ObjectOutputStream output = new ObjectOutputStream(out);
            output.writeObject(saver);
            out.close();
            output.close();
            
         } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(jframe, "Cannot save. Error");
            
         }
         
      
         }
      });
      
   }
   


   @Override
   public void update(Observable o, Object arg) {
      board[bore.getOldRow()][bore.getoldCol()].setText("T");
      player_location=bore.getCurrentSpot();
      int r=player_location[0];
      int c=player_location[1];
      board[r][c].setText("P");
      PassAlongLogMessage(player_location[0]+"   "+ player_location[1]+"\n");
      
   }
   public void createFileMenu() {
      JMenuBar menuBar = new JMenuBar();
      menuBar.setOpaque(true);
      menuBar.setBackground(new Color(217,253,255));
      JMenuItem item;//menu option
      JMenu fileMenu = new JMenu("File");//menu name
      fileMenu.setOpaque(true);
      fileMenu.setBackground(new Color(217,253,255));
      item = new JMenuItem("New Game"); //Open
      item.setBackground(new Color(217,253,255));
      item.addActionListener(this);
      fileMenu.add( item);
      item = new JMenuItem("Cheats"); 
      item.setBackground(new Color(217,253,255));
      item.addActionListener(this);
      fileMenu.add( item);
      item = new JMenuItem("Save"); 
      item.setBackground(new Color(217,253,255));
      item.addActionListener(this);
      fileMenu.add( item);
      jframe.setJMenuBar(menuBar);
      menuBar.add(fileMenu);
      JMenu editMenu = new JMenu("Edit");//menu name
      editMenu.setOpaque(true);
      editMenu.setBackground(new Color(217,253,255));
      item = new JMenuItem("Font"); 
      item.setBackground(new Color(217,253,255));
      item.addActionListener(this);
      editMenu.add( item);
      item = new JMenuItem("Techniques"); 
      item.setBackground(new Color(217,253,255));
      item.addActionListener(this);
      editMenu.add( item);
      item = new JMenuItem("Stats"); 
      item.setBackground(new Color(217,253,255));
      item.addActionListener(this);
      editMenu.add( item);
      menuBar.add(editMenu);
      JMenu gearMenu = new JMenu("Gear");//menu name
      gearMenu.setOpaque(true);
      gearMenu.setBackground(new Color(217,253,255));
      item = new JMenuItem("Armor"); 
      item.setBackground(new Color(217,253,255));
      item.addActionListener(this);
      gearMenu.add( item);
      item = new JMenuItem("Weapon"); 
      item.setBackground(new Color(217,253,255));
      item.addActionListener(this);
      gearMenu.add( item);
      menuBar.add(gearMenu);
   }
   
   

   
   public void howToPlay() {
      PassAlongMessage("For full details see how_to_play.docx.\n"
            + "Fight your way through the maze at the bottom of the window (P is player E is empty T is traversed and Dest is destination W is Wall) "
            + "as you meet enemies while traversing through.\n"
            + "Note that both clicks and arrows keys can be used for movement and in combat you may use 1-0 and - and = keys. \n"
            + "All fights are in real time and your enemies attack quickly. \n"
            + "Enemies may drop armor or weapons for you to equip. Find potions along the way used for healing or to increase stats. \n"
            + "Beat five rounds and face the champion enemy to win the game. Save the game constantly. \n"
            + "Enjoy playing. \n");
      
   }

   
  
   //http://www.java2s.com/Tutorial/Java/0240__Swing/SimpleAttributeBoldItalic.htm
   @Override
   public void PassAlongEnemyMessage(String m) {
      //gameText.append(m);
      EventQueue.invokeLater(new Runnable() {
         public void run() {
            //gameText.append(m);
            MutableAttributeSet mas=new SimpleAttributeSet();
            mas.addAttribute(StyleConstants.Bold, true);
            try {
               gameText.insertString(gameText.getLength(), m, mas);
            } catch (BadLocationException e) {
               e.printStackTrace();
            }
         }
     });
      
   }
   public void PassAlongMessage(String m) {
      //gameText.append(m);
      EventQueue.invokeLater(new Runnable() {
         public void run() {
            //gameText.append(m);
            MutableAttributeSet mas=new SimpleAttributeSet();
            mas.addAttribute(StyleConstants.Bold, false);
            try {
               gameText.insertString(gameText.getLength(), m, mas);
            } catch (BadLocationException e) {
               e.printStackTrace();
            }
         }
     });
      
   }
   @Override
   public void dispose() {
      jframe.dispose();
      
   }
   public void updatePlayerHP(String hp){
      //set all values can change all values but only change if values change
      player_stats[1].setText(hp);
   }
   public void updatePlayerMP(String mp){
      player_stats[3].setText(mp);
   }
   public void updatePlayerPower(String p){
      player_stats[5].setText(p);
   }
   public void updatePlayerSpeed(String s){
      player_stats[7].setText(s);
   }
   public void updatePlayerPotion(String p){
      player_stats[13].setText(p);
   }
   /*public void updateEnemyValues(int hp){
      enemy_labels.setText("Enemy Type: " + "Change\n"+ 
            "HP: " + hp;
            )
   }*/
   @Override
   
   public void mouseClicked(MouseEvent arg0) {
      //System.out.println("Mouse clicked " + arg0.getComponent().getName());
      /*int s=Integer.parseInt(arg0.getComponent().getName());
      if(!player_moves[s].isEnabled()) return;
      if(s*2*game.getDifficulty() > player.getCurrentMp() && s !=11) {
         PassAlongMessage("Move failed! Not enough MP.\n");
         return;
      }
      if(s==11 && player.getNumberHPotion()> 0){
         player.UseHPotion();
         updatePlayerHP(Integer.toString(player.getCurrentHp()));
         updatePlayerPotion(Integer.toString(player.getNumberHPotion()));
         if(player.getNumberHPotion()==0)  {
            player_moves[11].setEnabled(false);
            player_moves[11].setText("HP Potion Needed for use!");
         }
         return;
      }
      else{
         try{
            if(player_moves[s].isAlive()) return;//keeps exception away no move twice
         }catch(NullPointerException e){
            return;
         }
         //game.attack(s);
         
         int speed=player.getSpeed()/10;
         try {
            if(s==10){//heal
               player_moves[10].start(45-speed);
            }
            else if(s==9){
               player_moves[9].start(40-speed);
            }
            else if(s==8){
               player_moves[8].start(35-speed);
            }
            else if(s==7){
               player_moves[7].start(30-speed);
            }
            else if(s==6){
               player_moves[6].start(25-speed);
            }
            else if(s==5){
               player_moves[5].start(20-speed);
            }
            else if(s==4){
               player_moves[4].start(15);
            }
            else if(s==3){
               player_moves[3].start(10);
            }
            else if(s==2){
               player_moves[2].start(5);
            }
            else if(s==1){
               player_moves[1].start(3);
            }
            else if(s==0){
               player_moves[0].start(1);
            }
            //player_moves[s].interrupt();
         } catch (Exception e) {
            e.printStackTrace();
         }
      }*/
      
      
   }
   @Override
   public void mouseEntered(MouseEvent arg0) {
      //System.out.println("Mouse entered" + arg0.getComponent().getName());
      
   }
   @Override
   public void mouseExited(MouseEvent arg0) {
      //System.out.println("Mouse exited" + arg0.getComponent().getName());
      
   }
   @Override
   public void mousePressed(MouseEvent arg0) {
      attack(Integer.parseInt(arg0.getComponent().getName()));
   }
   private void attack(int s) {
      if(!player_moves[s].isEnabled() || game.getPlayerStun()) return;
      int mana_cost=(int) (player.getMaxMp()*.05*s+(3*player.getLevel()));
      if(s!=0 && mana_cost > player.getCurrentMp() && s !=11) {
         PassAlongMessage("Move failed! Not enough MP.\n");
         return;
      }
      if(s==11 && player.getNumberHPotion()> 0){
         player.UseHPotion();
         updatePlayerHP(Integer.toString(player.getCurrentHp()));
         updatePlayerPotion(Integer.toString(player.getNumberHPotion()));
         if(player.getNumberHPotion()==0)  {
            player_moves[11].setEnabled(false);
            player_moves[11].setText("HP Potion Needed for use!");
         }
         return;
      }
      else{
         try{
            if(player_moves[s].isAlive()) return;//keeps exception away no move twice
         }catch(NullPointerException e){
            return;
         }
         if(game.getEHP(enemyTarget) <1 ) {
            PassAlongMessage("Choose a new target! Target has been defeated. \n");
            return;
         }
         if(player.getAttackType()=="Burst") game.attackBurst(s, mana_cost, enemyTarget);
         else game.attackDOT(s, mana_cost, enemyTarget);
         
         //int speed=player.getSpeed()/10;
         try {
            if(s==10){//heal
               player_moves[10].start(player.getCooldown(s));
            }
            else if(s==9){
               player_moves[9].start(player.getCooldown(s));
            }
            else if(s==8){
               player_moves[8].start(player.getCooldown(s));
            }
            else if(s==7){
               player_moves[7].start(player.getCooldown(s));
            }
            else if(s==6){
               player_moves[6].start(player.getCooldown(s));
            }
            else if(s==5){
               player_moves[5].start(player.getCooldown(s));
            }
            else if(s==4){
               player_moves[4].start(player.getCooldown(s));
            }
            else if(s==3){
               player_moves[3].start(player.getCooldown(s));
            }
            else if(s==2){
               player_moves[2].start(player.getCooldown(s));
            }
            else if(s==1){
               player_moves[1].start(player.getCooldown(s));
            }
            else if(s==0){
               player_moves[0].start(player.getCooldown(s));
            }
            //player_moves[s].interrupt();
         } catch (Exception e) {
            e.printStackTrace();
         }
      
      }
   }
   @Override
   public void mouseReleased(MouseEvent arg0) {
      //System.out.println("Mouse released" + arg0.getComponent().getName());
      
   }
   @Override
   public void enablePotionUse() {
      player_moves[11].setEnabled(true);
      player_moves[11].setText("Health Potion");
      
   }
   @Override
   public void PassAlongLogMessage(String string) {
      move_log.append(string);
      
   }
   @Override
   public int askForUpgrade() {
      noKeys=true;
      //if(player.getSpeed() > 100) return 1;
      Object[] modes;
      List l= new LinkedList();
      l.add("");
      l.add("HP");
      l.add("MP");
      l.add("Power");
      if(player.getSpeed() < 100) l.add("Speed");
      if(player.getAccuracy() < 100) l.add("Accuracy");
      if(player.getCrit() < 100) l.add("Crit Chance");
      modes=l.toArray();
      JPanel panel=new JPanel();
      JOptionPane pane;
      //JPanel panel=new JPanel();
      panel.setLayout(new GridLayout(1,1));
      //DefaultComboBoxModel box = new DefaultComboBoxModel(options);
      JComboBox list=new JComboBox(modes);
      JLabel cLabel=new JLabel("Choose your stat to increase?");
      panel.add(cLabel);
      panel.add(list);
      //JOptionPane pane=new JOptionPane(panel);
      pane=new JOptionPane(panel);
      JDialog dialog = pane.createDialog(pane, "Potion");
      dialog.setLocationRelativeTo(jframe);
      dialog.setVisible(true);
      Object value=pane.getValue();
      noKeys=false;
      if (value == null) return 1;
      if(list.getSelectedItem()=="") {
         return -1;
      }
      if(list.getSelectedItem().toString()=="Power") return 1;
      else if (list.getSelectedItem().toString()=="Speed") return 2;
      else if (list.getSelectedItem().toString()=="HP") return 3;
      else if (list.getSelectedItem().toString()=="MP") return 4;
      else if (list.getSelectedItem().toString()=="Accuracy") return 5;
      else if (list.getSelectedItem().toString()=="Crit Chance") return 6;
      else return -1;
   
   }
   public void preventMovement() {
      for(int i=0; i< movementButtons.length; i++){
         movementButtons[i].setEnabled(false);
      }
      
   }
   public void allowMovement() {
      for(int i=0; i< movementButtons.length; i++){
         movementButtons[i].setEnabled(true);
      }
      
   }
   @Override
   public void disablePotionUse() {
      player_moves[11].setEnabled(false);
      
   }
   public void playerStun(){
      game.setPlayerStun(false);
      PassAlongMessage("You moves are on cooldown! Please wait until one becomes available!\n");
      for(int i=0; i< player_moves.length; i++){
         if(player_moves[i].isAlive()) continue;//keeps exception away no move twice
         player_moves[i].start(4);
      }
   }
   public void shutdown() {
      for(int i=0; i< player_moves.length; i++){
         player_moves[i].setEnabled(false);
      }
      
   }
   @Override
   public void keyPressed(KeyEvent arg0) {
      if(noKeys) return;//otherwise can use keys to move while potion
      int event=arg0.getKeyCode();
      if(!Game.inFight.get() && !game.isGameOver()){//arrows
         if(event==KeyEvent.VK_RIGHT){
            game.playerMovement("right");
         }
         else if (event == KeyEvent.VK_LEFT){
            game.playerMovement("left");
         }
         else if (event == KeyEvent.VK_UP){
            game.playerMovement("up");
         }
         else if (event == KeyEvent.VK_DOWN){
            game.playerMovement("down");
         }
      }
      else if (Game.inFight.get() && !game.isGameOver()){//1-12
         if(event==KeyEvent.VK_1 || event==KeyEvent.VK_2 || event==KeyEvent.VK_3 
               ||event==KeyEvent.VK_4 || event==KeyEvent.VK_5 
               || event==KeyEvent.VK_6 || event==KeyEvent.VK_7 ||
               event==KeyEvent.VK_8 || event==KeyEvent.VK_9) 
         { 
            attack(Integer.parseInt(KeyEvent.getKeyText(event))-1);
         }
         else if (event==KeyEvent.VK_0) attack(9);
         else if (KeyEvent.VK_MINUS==event) attack(10);
         else if (KeyEvent.VK_EQUALS==event) attack(11);
         else if (KeyEvent.VK_TAB==event) {
            if(enemyTarget<radioButton.length-1) {
               enemyTarget+=1;
            }
            else {
               enemyTarget=0;
            }
            radioButton[enemyTarget].setSelected(true);
         }
         else if(arg0.isShiftDown() && KeyEvent.VK_TAB==event){
            if(enemyTarget>0) {
               enemyTarget-=1;
            }
            else {
               enemyTarget=radioButton.length-1;
            }
            radioButton[enemyTarget].setSelected(true);
         }
      
      }
     
      
      
   }
   @Override
   public void keyReleased(KeyEvent arg0) {
      //System.out.println(arg0.getKeyChar());
   }
   @Override
   public void keyTyped(KeyEvent arg0) {
      //System.out.println(arg0.getKeyChar());
   }
   public void setUpArchenemy() {
      noKeys=true;
      JOptionPane.showMessageDialog(jframe, "You are now going to face your greatest enemy, the king of them all! Enemy is immune to most if not all of your effects. Good luck." );
      noKeys=false;
      
   }
   public void WinMessage(String string) {
      noKeys=true;
      JOptionPane.showMessageDialog(jframe, string);
      noKeys=false;
   }
   
   
   public void load() {
      board[0][0].setText("");//in setUpBoard sets it to P but that may not be true
      int[][] b=bore.getNumbers();
      for(int i=0; i< board.length; i++){
         for(int j=0; j< board[i].length; j++){
            if(b[i][j]== -1) board[i][j].setText("T");
            if(b[i][j]== 0) board[i][j].setText("E");
            if(b[i][j]== 9) board[i][j].setText("P");
         }
      }
   }
   @Override
   public void disableMoveButtons(int[] arr) {
      for(int i=0;i<arr.length;i++){
         if(player_moves[arr[i]].isEnabled()) player_moves[arr[i]].setEnabled(false);
      }
      EventQueue.invokeLater(new Runnable() { public void run() {
         if(!Game.inFight.get()) radioPanel.removeAll();
      }});
      
      
      
      //radioPanel.setVisible(false);
      
   }
   public void enableMoveButtons(int[] arr) {
      EventQueue.invokeLater(new Runnable() { public void run() {
         for(int i=0;i<arr.length;i++){
            if(!player_moves[arr[i]].isEnabled()) player_moves[arr[i]].setEnabled(true);
         }
      }});
      
      
   }
   
   public void updatePlayerAccuracy(String string) {
      player_stats[9].setText(game.getAccuracy());
      
      
   }
   
   public void updatePlayerCrit(String string) {
      player_stats[11].setText(game.getCrit());
      
   }
   
   public void callMerchant(Gear g) {
      Random r=new Random();
      int cost=player.level.get()*100+r.nextInt(100);
      if(player.getMoney()<cost) return;
      String str="";
      str+=("You currently have $" );
      str+=Integer.toString(player.getMoney());
      str+=". You may now buy a ";
      str+=g.getName();
      str+=" for $";
      str+=cost;
      str+=".";      
      str+="\n HP: " + g.getHP()+ "\n MP: "+ g.getMP() +"\n Power: "+ g.getPower()
            +"\n Speed: "+ g.getSpeed()+ "\n Accuracy: " +g.getAccuracy() + "\n Crit %: "+ g.getCrit(); 
      int choice = JOptionPane.showConfirmDialog(jframe, str, "Merchant", JOptionPane.YES_NO_OPTION);
      if (choice == JOptionPane.YES_OPTION) {
        player.addToInventory(g);
        player.setMoney(player.getMoney()-cost);
        PassAlongMessage("New "+ g.getName() +" has been added to your inventory!\n");
        PassAlongMessage("You have $"+ player.getMoney()+".\n");
      }
      updateLevelLog();
      return;
   }
   public void updateLevelLog(){
      level_log.setText("Level: "+player.getLevel()
            + "\nEXP: "+ player.getEXP()+ "/"+player.getXPLevel()+"\nMoney: $"+player.getMoney());
      
   }
   public void flipTechniqueButtons(){//flip based on mana
      for(int s=0; s< player_moves.length-1; s++){
         int cost=(int) (player.getMaxMp()*.05*s + (3*player.getLevel()));
         if(player.getCurrentMp()< cost ) player_moves[s].setEnabled(false);
         else {
            try{
               Integer.parseInt(player_moves[s].getText());
            }catch(Exception e){
               if(!game.getPlayerStun()) player_moves[s].setEnabled(true);
            }
         }
         if(!Game.inFight.get()) player_moves[s].setEnabled(false);   
         
      }
   }
   
   public void createEnemyList(List a) {
      radioBool=true;
      EventQueue.invokeLater(new Runnable() {
         public void run() {
            try{
               final Object[] array=a.toArray();
               radioButton=new JRadioButton[array.length];
               ButtonGroup group=new ButtonGroup();
               radioPanel.setLayout(new GridLayout(array.length,1));
               for(int i=0; i< radioButton.length; i++){
                  String s=(String) array[i];
                  radioButton[i]=new JRadioButton(s.toString());
                  group.add(radioButton[i]);
                  radioPanel.add(radioButton[i]);
                  final int n=i;
                  radioButton[i].addItemListener(new ItemListener() {
                     
                     public void itemStateChanged(ItemEvent e) {
                         int state = e.getStateChange();
                         if (state == ItemEvent.SELECTED) { 
                            enemyTarget=n;
                  
                         } 
                     }
                 });
               }
               radioButton[0].setSelected(true);
               radioPanel.setVisible(true);
            }catch(Exception e){
               e.printStackTrace();
            }finally{
               jframe.revalidate();
               jframe.repaint();
               radioBool=false;
            }
         }
      });
      
      
   }
   public boolean isRadioBool() {
      return radioBool;
   }
   public void setRadioBool(boolean b){
      radioBool=b;
   }
   public void markIfWall(String event) {
      int r=player_location[0];
      int c=player_location[1];
      if(event=="up") board[r-1][c].setText("W");
      if(event=="down") board[r+1][c].setText("W");
      if(event=="right") board[r][c+1].setText("W");
      if(event=="left") board[r][c-1].setText("W");
      
   }
   private void showWalls() {
      int[][] b=bore.getNumbers();
      for(int i=0; i< b.length; i++){
         for(int j=0; j< b[i].length; j++){
            if(b[i][j]==1) board[i][j].setText("W");
         }
      }
   }
   private void showPotions(){
      int[][] b=bore.getNumbers();
      for(int i=0; i< b.length; i++){
         for(int j=0; j< b[i].length; j++){
            if(b[i][j]==4) board[i][j].setText("B");
         }
      }
   }
   private void showEnemies(){
      int[][] b=bore.getNumbers();
      for(int i=0; i< b.length; i++){
         for(int j=0; j< b[i].length; j++){
            if(b[i][j]==3) board[i][j].setText("F");
         }
      }
   }
   private void getNewFontSize(){
      String string=JOptionPane.showInputDialog(jframe, "Set a new Font size (Integers between 1 and 30).");
      if(string==null) return;
      else {
         if(!string.matches("\\d+$")) return;
         int c=Integer.parseInt(string);
         if(c<1 || c > 30) return;
         //gameText.setFont(new Font("Arial", Font.PLAIN, c));
         gamePane.setFont(new Font("Arial", Font.PLAIN, c));
      }
   }
   private void setTechniques(){
      JPanel panel=new JPanel();
      JTextField[] labels=new JTextField[10];
      panel.setLayout(new GridLayout(5,2));
      for(int i=0; i< labels.length; i++){
         labels[i]=new JTextField();
         labels[i].setText(player_moves[i].getText());
         labels[i].setEnabled(true);
         labels[i].setEditable(true);
         panel.add(labels[i]);
      }
      JOptionPane pane=new JOptionPane(panel);
      JDialog dialog=pane.createDialog(jframe, "Change Names!");
      //dialog.setLocation(jframe.getLocation());
      dialog.setVisible(true);
      Object o=pane.getValue();
      if(o==null) return;
      for(int i=0; i< labels.length; i++){
         player_moves[i].setText(labels[i].getText());
      }
      
   }
   private void setStats(){
      if(Game.inFight.get()) {
         PassAlongMessage("Focus on the fight!\n");
         return;
      }
      JPanel panel=new JPanel();
      panel.setLayout(new GridLayout(1,2));
      String[] stats={"HP","MP", "Power", "Speed", "Accuracy", "Critical"};
      JComboBox list=new JComboBox(stats);
      panel.add(list);
      JTextField box=new JTextField();
      panel.add(box);
      JOptionPane pane=new JOptionPane(panel);
      JDialog dialog=pane.createDialog(jframe, "Change Stats");
      dialog.setVisible(true);
      Object o=pane.getValue();
      if(o==null) return;
      if(!box.getText().matches("\\d+$")) return;
      int s=0;
      try{
         s=Integer.parseInt(box.getText());
      }catch(Exception e){
         e.printStackTrace();
         return;
      }
      if(list.getSelectedItem().equals("HP")){
         if(s>10000) {
            PassAlongMessage("Invalid value passed\n");
         }
         else{
            player.setBaseHp(s);
            player.setMaxHp(player.getBaseHp());
            player.setCurrentHp(player.getMaxHp());
            updatePlayerHP(Integer.toString(player.getCurrentHp()));
         }
      }
      if(list.getSelectedItem().equals("MP")){
         if(s>10000) {
            PassAlongMessage("Invalid value passed\n");
            
         }
         else{
            player.setBaseMp(s);
            player.setMaxMp(player.getBaseMp());
            player.setCurrentMp(player.getMaxMp());
            updatePlayerMP(Integer.toString(player.getCurrentMp()));
         }
         
      }
      if(list.getSelectedItem().equals("Speed")){
         if(s>100) PassAlongMessage("Invalid value passed\n");
         else{
            player.setBaseSpeed(s);
            player.setSpeed(player.getBaseSpeed());
            updatePlayerSpeed(Integer.toString(player.getSpeed()));
         }              
      }
      if(list.getSelectedItem().equals("Accuracy")){
         if(s>100) PassAlongMessage("Invalid value passed\n");
         else{
            player.setBaseAccuracy(s);
            player.setAccuracy(player.getBaseAccuracy());
            updatePlayerAccuracy(Integer.toString(player.getAccuracy()));
         }
      }
      if(list.getSelectedItem().equals("Critical")){
         if(s>100) PassAlongMessage("Invalid value passed\n");
         else{
            player.setBaseCrit(s);
            player.setCrit(player.getBaseCrit());
            updatePlayerCrit(Integer.toString(player.getCrit()));
         }
      }
      if(list.getSelectedItem().equals("Power")){
         if(s>10000) PassAlongMessage("Invalid value passed\n");
         else{
            player.setBasePower(s);
            player.setPower(player.getBasePower());
            updatePlayerPower(Integer.toString(player.getPower()));
         }
      }
      
   }
      
   
}
   