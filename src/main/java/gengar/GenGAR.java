package gengar;

import java.awt.EventQueue;
import java.util.concurrent.Exchanger;

import javax.swing.JFrame;
import javax.swing.UIManager;

import fi.conf.ae.gl.texture.GLTextureManager;
import fi.conf.tabare.ARDataProvider;

public class GenGAR {

	static ARDataProvider arDataProvider;
	static TableAugmentDemo tableAugmentor;
	
	public static void main(String[] args) {
		
		//Decoration stuff
		JFrame.setDefaultLookAndFeelDecorated(true);
		try {
			System.out.println("Setting look and feel");

			if (System.getProperty("os.name").toLowerCase().contains("windows")) {
				// windows-support so doesn't look ugly
				UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
			} else {
				// linux gtk only such nice-look(tm)
				UIManager.setLookAndFeel("com.sun.java.swing.plaf.gtk.GTKLookAndFeel");
			}
		} catch (Exception e) {
			System.err.println("Unable to set LookAndFeel");
		}

		final Exchanger<ARDataProvider> exchanger = new Exchanger<>();
		
        /* Create and display the form */
        EventQueue.invokeLater(new Runnable() {
            public void run() {
                ARDataProvider arDataProvider = new ARDataProvider();
                try {
					exchanger.exchange(arDataProvider);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
                arDataProvider.setVisible(true);
            }
        });
        
        try {
			arDataProvider = exchanger.exchange(null);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
        
        tableAugmentor = new TableAugmentDemo(arDataProvider);
        tableAugmentor.startGL("Table Augment Engine Demo");
        
        requestShutdown();
		
	}
	
	public static void requestShutdown(){
		arDataProvider.requestClose();
		tableAugmentor.requestClose();
		GLTextureManager.getInstance().requestShutdown();
	}

}
