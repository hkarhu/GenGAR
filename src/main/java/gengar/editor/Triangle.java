package gengar.editor;
import java.io.Serializable;

/**
 * vector.Triangle.java
 */


/**
 * Template for encapsulating data into triangles.
 * @author Jani Salo
 */
public final class Triangle<T> implements Serializable {
	/**
	 * Elements making up the triangle.
	 */
	private T a, b, c;

	/**
	 * @return a element
	 */
	public T getA() {
		return a;
	}

	/**
	 * @param a element to set a
	 */
	public void setA(T a) {
		this.a = a;
	}

	/**
	 * @return b element
	 */
	public T getB() {
		return b;
	}

	/**
	 * @param b element to set b
	 */
	public void setB(T b) {
		this.b = b;
	}

	/**
	 * @return c element
	 */
	public T getC() {
		return c;
	}

	/**
	 * @param c element to set as c
	 */
	public void setC(T c) {
		this.c = c;
	}
	
	/**
	 * @param i of the element to get
	 * @return element at given index
	 * @throws ArrayIndexOutOfBoundsException the index is outside [0, 2]
	 */
	public T getAt(int i) throws ArrayIndexOutOfBoundsException {
		switch(i) {
			case 0: return a;
			case 1: return b;
			case 2: return c;
			default: throw new ArrayIndexOutOfBoundsException();
		}
	}

	/**
	 * @param i index of the element to set
	 * @param t element to set the element to
	 * @throws ArrayIndexOutOfBoundsException the index is outside [0, 2]
	 */
	public void setAt(int i, T t) throws ArrayIndexOutOfBoundsException {
		switch(i) {
			case 0: a = t; return;
			case 1: b = t; return;
			case 2: c = t; return;
			default: throw new ArrayIndexOutOfBoundsException();
		}
	}
	
	/**
	 * Default constructor.
	 */
	public Triangle() {}
	
	/**
	 * Constructs the triangle from elements.
	 * @param a element to set as a
	 * @param b element to set as b
	 * @param c element to set as c
	 */
	public Triangle(T a, T b, T c) {
		this.a = a;
		this.b = b;
		this.c = c;
	}
	
	public Triangle(T a) {
		this.a = a;
	}
	
	/**
	 * @param obj object to test for equality
	 */
	public boolean equals(Object obj) {
		// See that the object is a triangle.
		if (obj instanceof Triangle) {
			return (
					a.equals(((Triangle<?>) obj).getA()) && 
					b.equals(((Triangle<?>) obj).getB()) && 
					c.equals(((Triangle<?>) obj).getC())
			);
		}
		
		// Different type of an object.
		return false;
	}

	/**
	 * @return sum of element hash codes
	 */
	public int hashCode() {
		return (int) (a.hashCode() + b.hashCode() + c.hashCode());
	}

	/**
	 * @return strings of the elements
	 */
	public String toString() {
		return a.toString() + "; " + b.toString() + "; " + c.toString();
	}
}
