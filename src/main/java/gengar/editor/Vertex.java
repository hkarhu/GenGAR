package gengar.editor;
import java.io.Serializable;


public class Vertex implements Serializable {

	private int x;
	private int y;
	private float z;

	public Vertex(int x, int y) {
		this.x = x;
		this.y = y;
		z = 0;
	}

	public int getX() {
		return x;
	}
	public void setX(int x) {
		this.x = x;
	}
	public int getY() {
		return y;
	}
	public void setY(int y) {
		this.y = y;
	}

	public boolean equals(Vertex p) {
		if(p == null) return false;
		return p.getX() == x && p.getY() == y;
	}

	public Vertex getScaledInstance(float multiplier) {
		return new Vertex((int)(x*multiplier), (int)(y*multiplier));
	}

	public void moveTo(Vertex vertex) {
		this.x = vertex.getX();
		this.y = vertex.getY();
	}

	public float getDistanceToPoint(Vertex v){
		return (float)Math.sqrt(Math.pow(v.getX()-x, 2) + Math.pow(v.getY()-y, 2));
	}

	public float getDistanceToLineSegment(Vertex v1, Vertex v2){

		//If the vertex happens to be one of the line segments end-vertices
		if((v1.getX() == x && v1.getY() == y) || (v2.getX() == x && v2.getY() == y)) return 0;
		
//		if(v1.getY() == v2.getY()){ // If the line was horizontal
//			if(x < Math.min(v1.getX(), v2.getX())){ //if the point is left from the leftmost point
//				if(v1.getX() < v2.getX()){ //Return the distance to the leftmost point
//					return getDistanceToPoint(v1);
//				} else {
//					return getDistanceToPoint(v2);
//				}
//			} else if(x > Math.max(v1.getX(), v2.getX())){ //if the point is right from the rightmost point
//				if(v1.getX() > v2.getX()){ //Return the distance to the rightmost point
//					return getDistanceToPoint(v1);
//				} else {
//					return getDistanceToPoint(v2);
//				}
//			} else { //if the point is between the endpoints
//				return Math.abs(v1.getX() - x);
//			}
//		} else if(v1.getX() == v2.getX()){ // If the line was vertical
//			if(y < Math.min(v1.getY(), v2.getY())){ //if the point is above from the top point
//				if(v1.getY() < v2.getY()){ //Return the distance to the top point
//					return getDistanceToPoint(v1);
//				} else {
//					return getDistanceToPoint(v2);
//				}
//			} else if(y > Math.max(v1.getY(), v2.getY())){ //if the point is below from the lowest point
//				if(v1.getY() > v2.getY()){ //Return the distance to the lowest point
//					return getDistanceToPoint(v1);
//				} else {
//					return getDistanceToPoint(v2);
//				}
//			} else { //if the point is between the endpoints
//				return Math.abs(v1.getY() - y); 
//			}
//		}

		float a = (v2.getY()-v1.getY());
		float b = (v1.getX()-v2.getX());
		
		return (float)(Math.abs(a*x+b*y+(v1.getY()*v2.getX()-v1.getX()*v2.getY()))/Math.sqrt(Math.pow(a, 2) + Math.pow(b, 2)));

	}

}
