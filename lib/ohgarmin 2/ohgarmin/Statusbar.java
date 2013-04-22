package ohgarmin;

import java.awt.GridLayout;
import java.awt.Label;

import javax.swing.JPanel;

		//	Klasse Statusbar
		public class Statusbar extends JPanel{
			protected Label lbl_statusbar = null;
			
			public Statusbar(){
				//Label wird rechts eingefügt
				super(new GridLayout(1,1));
				lbl_statusbar = new Label("Koordinaten",Label.RIGHT);
				add(lbl_statusbar);
			}
			//Getter für LabelText
			public String getText(){
				return lbl_statusbar.getText();
			}
			//Setter für LabelText
			public void setText(String text){
				if (text==null) lbl_statusbar.setText("Koordinaten");
				else lbl_statusbar.setText(text);
			}		
		}