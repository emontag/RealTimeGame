import java.awt.GridLayout;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.ArrayList;
import java.util.List;

import javafx.application.Platform;
import javafx.embed.swing.JFXPanel;
import javafx.stage.FileChooser;

import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextArea;

public class Pane {
   static void askForLoad(Game g){
      Object[] choices={"Load Game", "New Game"};
      JOptionPane pane=new JOptionPane("What do you want to do? ");
      pane.setOptions(choices);
      JDialog d=pane.createDialog(pane, "Game");
      if(GUI.screenX!=0 && GUI.screenY !=0)d.setLocation(GUI.screenX, GUI.screenY);
      d.setVisible(true);
      /*int choice = JOptionPane.showOptionDialog(null,//parent container of JOptionPane
            "Choose an Option!",
            "Game",
            JOptionPane.YES_NO_CANCEL_OPTION,
            JOptionPane.QUESTION_MESSAGE,
            null,//do not use a custom Icon
            choices,//the titles of buttons
            "Game");//default button title
      if(choice==-1) System.exit(1);
      if(choice==1) return; 
      if(choice==0) g.setLoadSuccess(true);*/
      Object value=pane.getValue();
      if (value == null) System.exit(1);
      if(value.equals("New Game")) return;
      else if(value.equals("Load Game")) g.setLoadSuccess(true);
      
      final FileChooser chooser=new FileChooser();
      //chooser.setInitialDirectory(
      //      new File(System.getProperty("user.home"))
       // ); 
      chooser.getExtensionFilters().addAll(
            new FileChooser.ExtensionFilter("*.ser", "*.ser")
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
              e.printStackTrace();
              System.exit(1);
              
           }
           try {
              FileInputStream in = new FileInputStream(file);
              ObjectInputStream use = new ObjectInputStream(in);
              List list = (ArrayList) use.readObject();
              in.close();
              use.close();
              g.doLoad(list);
           }catch(Exception e) {
              //e.printStackTrace();
              System.exit(1);
              
           }
      
         }
      });
   }
   static Player askForClass() {
      JComboBox option_list;
      JPanel panel=new JPanel();
      JOptionPane pane;
      String[] modes = {"", "Warrior","Archer", "Mage"};
      String[] attackTypes={"Burst", "DOT"};
      DefaultComboBoxModel box = new DefaultComboBoxModel(attackTypes);
      //JPanel panel=new JPanel();
      panel.setLayout(new GridLayout(3,3));
      //DefaultComboBoxModel box = new DefaultComboBoxModel(options);
      JComboBox list=new JComboBox(modes);
      option_list=new JComboBox(box);
      /*list.addItemListener(new ItemListener(){
         public void itemStateChanged(ItemEvent e) {
            String s= e.getItem().toString();
            box.removeAllElements();
            if(s=="Warrior") {
               box.addElement("Berserker");
               box.addElement("Bladesman");
               box.addElement("Scytheweaver");
            }
            if(s=="Archer"){
               box.addElement("Slinger");
               box.addElement("Crossbowmen");
               box.addElement("Longbowmen");
               
            }
            if(s=="Mage"){
               box.addElement("Elementalist");
               box.addElement("Necromancer");
               box.addElement("Lich");
            }
            
         }
         
      });*/
      JLabel cLabel=new JLabel("Choose your class? (or you cannot play)");
      panel.add(cLabel);
      panel.add(list);
      JLabel nLabel=new JLabel("Choose your Spec?");
      panel.add(nLabel);
      panel.add(option_list);
      //JOptionPane pane=new JOptionPane(panel);
      JLabel nameLabel=new JLabel("What is your name? (less than 15 characters)");
      JTextArea text=new JTextArea();
      panel.add(nameLabel);
      panel.add(text);
      pane=new JOptionPane(panel);
      JDialog dialog = pane.createDialog(pane, "Game");
      if(GUI.screenX!=0 && GUI.screenY !=0)dialog.setLocation(GUI.screenX, GUI.screenY);
      dialog.setVisible(true);
      Object value=pane.getValue();
      if (value == null) System.exit(1);
      text.setText(text.getText().trim());
      if(text.getText().length()>15) System.exit(1);
      if(text.getText().isEmpty()) Player.name="Insert Name Here>";
      Player.name=text.getText();
      if(list.getSelectedItem()=="") {
         System.exit(1);
      }
      if(list.getSelectedItem().toString()=="Warrior") return new Warrior(box.getSelectedItem().toString());
      else if (list.getSelectedItem().toString()=="Archer") return new Archer(box.getSelectedItem().toString());
      else if(list.getSelectedItem().toString() == "Mage") return new Mage(box.getSelectedItem().toString());
      else return null;
   
   }
  
}
