package gengar.editor;
import java.io.Serializable;

/**
 * vector.Vector3.java
 */


/**
 * Three dimensional vector for GL vertices.
 * @author Jani Salo
 * @author Harri Karhu
 */
public final class GL3DVertex implements Serializable {
	/**
	 * x component
	 */
	private float x;
	
	/**
	 * y component
	 */
	private float y;
	
	/**
	 * z component
	 */
	private float z;
	
	/**
	 * @return x component
	 */
	public float getX() { return x;	}

	/**
	 * @param x value to set as x
	 */
	public void setX(float x) {	this.x = x;	}

	/**
	 * @return y component
	 */
	public float getY() { return y; }

	/**
	 * @param y value to set as y
	 */
	public void setY(float y) {	this.y = y;	}

	/**
	 * @return z component
	 */
	public float getZ() { return z;	}

	/**
	 * @param z value to set as z
	 */
	public void setZ(float z) {	this.z = z;	}
	
	/**
	 * @return array containing the vector values
	 */
	public float [] toArray() { return new float [] {x, y, z}; }

	/**
	 * Default constructor.
	 */
	public GL3DVertex() {}
	
	/**
	 * Constructs the vector from components.
	 * @param x value to set x
	 * @param y value to set y
	 * @param z value to set z
	 */
	public GL3DVertex(float x, float y, float z) {
		this.x = x;
		this.y = y;
		this.z = z;
	}

	/**
	 * Constructs the vector from array. 
	 * @param array of values to construct the vector from
	 */
	public GL3DVertex(float [] array) {
		if (array.length > 0) x = array[0];
		if (array.length > 1) y = array[1];
		if (array.length > 2) z = array[2];
	}
	
	public GL3DVertex(double[] array) {
		if (array.length > 0) x = (float)array[0];
		if (array.length > 1) y = (float)array[1];
		if (array.length > 2) z = (float)array[2];
	}

	/**
	 * @param obj object to test for equality
	 */
	public boolean equals(Object obj) {
		// See that the object is a vector.
		if (obj instanceof GL3DVertex) {
			GL3DVertex vec = (GL3DVertex) obj;
			return (x == vec.x && y == vec.y && z == vec.z);
		}
		
		// Different type of an object.
		return false;
	}

	/**
	 * @return sum of components
	 */
	public int hashCode() {
		return (int) (x + y + z);
	}
	
	/**
	 * @return list of component values
	 */
	public String toString() {
		return x + ", " + y + ", " + z;
	}
	
	/**
	 * @param v right-hand operand
	 * @return sum of: this, v
	 */
	public GL3DVertex sum(GL3DVertex v) {
		return new GL3DVertex(x + v.x, y + v.y, z + v.z);
	}
	
	/**
	 * @param v right-hand operand
	 * @return difference of: this, v
	 */
	public GL3DVertex diff(GL3DVertex v) {
		return new GL3DVertex(x - v.x, y - v.y, z - v.z);
	}
	
	/**
	 * @param v vector to multiply with
	 * @return product of: this, v
	 */
	public GL3DVertex scalar(GL3DVertex v) {
		return new GL3DVertex(x * v.x, y * v.y, z * v.z);
	}
	
	/**
	 * @param f constant to multiply with
	 * @return product of: f, this
	 */
	public GL3DVertex scalar(float f) {
		return new GL3DVertex(f * x, f * y, f * z);
	}
	
	/**
	 * @param v right-hand operand
	 * @return cross product of: this, v
	 */
	public GL3DVertex cross(GL3DVertex v) {
		return new GL3DVertex(
			y * v.z - z * v.y,
			z * v.x - x * v.z,
			x * v.y - y * v.x
		);
	}
	
	/**
	 * @return dot product of: this, v
	 */
	public float dot(GL3DVertex v) {
		return x * v.x + y * v.y + z * v.z;
	}
	
	/**
	 * @return length of the vector
	 */
	public float length() {
		return (float) Math.sqrt(x * x + y * y + z * z);
	}
	
	/**
	 * @return normal of the vector
	 */
	public GL3DVertex normal() {
		float invLen = 1.0f / length();		
		return new GL3DVertex(invLen * x, invLen * y, invLen * z);
	}
}
