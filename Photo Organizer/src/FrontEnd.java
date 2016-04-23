
import java.io.*;

import javax.swing.*;

/**
 * The contains the main method and 
 * GUI. 
 * @author Kwesi Daniel
 *
 */
public class FrontEnd {
	public static void main(String[] args) throws Exception{
		JFileChooser chooser = new JFileChooser();
		chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY); //Takes only folders not individual files
		File folder = null;
		File dst = null;
		JOptionPane.showMessageDialog(null, "Choose picture folder.");
		if (chooser.showOpenDialog(chooser) == JFileChooser.APPROVE_OPTION) {
            folder = chooser.getSelectedFile();
		}
		else{
			JOptionPane.showMessageDialog(null, "No folder selected!");
			System.exit(0);
		}
		JOptionPane.showMessageDialog(null, "Choose output folder.");
		if (chooser.showOpenDialog(chooser) == JFileChooser.APPROVE_OPTION) {
            dst = chooser.getSelectedFile();
		}
		else{
			JOptionPane.showMessageDialog(null, "No folder selected!");
			System.exit(0);
		}
		
		Organizer organizer = null;
		try{organizer = new Organizer(folder,dst);}
		catch(Exception e){JOptionPane.showMessageDialog(null, e.getMessage()); System.exit(0);}
		organizer.getFiles(folder);
		
		JOptionPane.showMessageDialog(null, "Complete!");
		
		
	}
}
