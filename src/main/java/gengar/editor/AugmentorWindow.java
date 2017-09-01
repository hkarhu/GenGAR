package gengar.editor;
import java.awt.Dimension;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.JFrame;

import fi.conf.ae.gl.texture.GLTextureManager;


public class AugmentorWindow extends JFrame {

	private MapViewPanel mapview;
	
	public AugmentorWindow() {
		mapview = new MapViewPanel();
		add(mapview);
		setPreferredSize(new Dimension(1024, 768));
		setDefaultCloseOperation(DISPOSE_ON_CLOSE);
		pack();
		setVisible(true);
		
		mapview.startGL();
		
		addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosed(WindowEvent e) {
				mapview.stopGL();
				GLTextureManager.getInstance().requestShutdown();
			}
		});
	}

	public void setMap(Map map) {
		mapview.setMap(map);
	}
	
}
