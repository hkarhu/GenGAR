package gengar.editor;
import java.io.Serializable;
import java.util.Vector;

import javax.swing.ImageIcon;


public class Item implements Serializable {

	private int px = 0;
	private int py = 0;
	private int pwidth = 0;
	private int pheigth = 0;
	private float glWidth, glHeight, glX, glY;
	private boolean animated = false;
	private Vector<String> imageIDs;
	private String name = "unnamed_item";
	
	public boolean isTouched(float x, float y){
		return false;
	}

	public void glDraw() {
		// TODO Auto-generated method stub
		
	}

	public void complete(){
		
	}
	
	public void addImage(){
		
	}
	
	public ImageIcon getIcon(){
		return null;
	}
	
	public boolean isDisposable() {
		return false;
	}

	public int getPixelX() {
		return px;
	}
	
	public int getPixelY() {
		return py;
	}

	public int getPixelWidth() {
		return pwidth;
	}

	public int getPixelHeight() {
		return pheigth;
	}
	
}
