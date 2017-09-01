package gengar.editor;


import java.nio.FloatBuffer;

public class TouchPoint {

	int id;
	float x, y, z;
	int button;
	
	public TouchPoint(int id, FloatBuffer pos, int button) {
		this.id = id;
		this.x = pos.get(0);
		this.y = pos.get(1);
		this.z = pos.get(2);
		this.button = button;
	}
	
	public TouchPoint(int id, float x, float y, float z, int button) {
		this.id = id;
		this.x = x;
		this.y = y;
		this.z = z;
		this.button = button;
	}
	
	public float getX() {
		return x;
	}
	public void setX(float x) {
		this.x = x;
	}
	public float getY() {
		return y;
	}
	public void setY(float y) {
		this.y = y;
	}
	public float getZ() {
		return z;
	}
	public void setZ(float z) {
		this.z = z;
	}
	public int getButton() {
		return button;
	}
	public void setButton(int button) {
		this.button = button;
	}

	public void move(float x, float y, float z) {
		this.x = x;
		this.y = y;
		this.z = z;
	}
	
	public void move(FloatBuffer pos){
		this.x = pos.get(0);
		this.y = pos.get(1);
		this.z = pos.get(2);
	}
	
	public String toString(){
		return "TouchPoint "+ id + " - " + x + " " + y + " " + z;
	}
	
}
