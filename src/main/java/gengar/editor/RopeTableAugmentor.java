package gengar.editor;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import javax.swing.JFileChooser;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import javax.swing.filechooser.FileFilter;


public class RopeTableAugmentor {
	
	public static Constants constants;
	public static Map map;
	
	public static AugmentorWindow av;
	public static EditorWindow ev;
	
	private static File lastFile;
	private static JFileChooser fileChooser = new JFileChooser();;
	
	public RopeTableAugmentor() {
		fileChooser.setMultiSelectionEnabled(false);
		fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
		fileChooser.setFileFilter(new FileFilter() {
			@Override
			public String getDescription() { return "RoPe Augmented Map"; }
			@Override
			public boolean accept(File f) { return f.getName().endsWith(".ram"); }
		});
	}
	
	public static void saveMap(){
		if(lastFile == null) lastFile = new File("./maps/untitled.ram"); 
		fileChooser.setSelectedFile(lastFile);
		if (fileChooser.showSaveDialog(ev) == JFileChooser.APPROVE_OPTION) {
			try{
				FileOutputStream fileStream = new FileOutputStream(fileChooser.getSelectedFile());
				ObjectOutputStream os = new ObjectOutputStream(fileStream);
				os.writeObject(map);
				os.close();
			} catch (IOException ex) {
				ex.printStackTrace();
			}
		}
	}
	
	public static void loadMap(){
		if (fileChooser.showOpenDialog(ev) == JFileChooser.APPROVE_OPTION) {
			Map loadedMap = null;
			try {
				FileInputStream fileInputStream = new FileInputStream(fileChooser.getSelectedFile());
				ObjectInputStream ois = new ObjectInputStream(fileInputStream);	
				loadedMap = (Map)ois.readObject();
				ois.close();
			} catch (ClassNotFoundException e1) {
				e1.printStackTrace();
			} catch (IOException ex) {
				ex.printStackTrace();
			}
			
			if(loadedMap != null){
				map = loadedMap;
				av.setMap(map);
				ev.setMap(map);
			} else {
				System.err.println("Error while loading file...");
			}

		}
	}
	
	public static void main(String[] args) {
		
		fi.conf.ae.AE.setDebug(true);
		
		try {
			// Set System L&F
			//UIManager.setLookAndFeel("com.sun.java.swing.plaf.motif.MotifLookAndFeel");
			UIManager.setLookAndFeel(UIManager.getCrossPlatformLookAndFeelClassName());
			//UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		} catch (UnsupportedLookAndFeelException e) {
			System.out.println(e);
		} catch (ClassNotFoundException e) {
			System.out.println(e);
		} catch (InstantiationException e) {
			System.out.println(e);
		} catch (IllegalAccessException e) {
			System.out.println(e);
		}
		
		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {	
				av = new AugmentorWindow();
				ev = new EditorWindow();
			}
		});
		
	}

	public static void newMap() {
		System.out.println("Created new map");
		map = new Map();
		av.setMap(map);
		ev.setMap(map);
	}
	
}
