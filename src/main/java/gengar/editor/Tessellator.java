package gengar.editor;
/**
 * engine.Tessellator.java
 */

import java.util.ArrayList;

import org.lwjgl.opengl.GL11;
import org.lwjgl.util.glu.GLU;
import org.lwjgl.util.glu.GLUtessellator;
import org.lwjgl.util.glu.GLUtessellatorCallback;


/**
 * Wrapper for OpenGL tessellator.
 * @author Jani Salo
 */
public class Tessellator implements GLUtessellatorCallback {
	/**
	 * GL tessellator object.
	 */
	private GLUtessellator tessellator = GLU.gluNewTess();
	
	/**
	 * Output buffer for triangles.
	 */
	private ArrayList<Triangle<GL3DVertex>> triangleList;
	
	/**
	 * Output buffer for line loops.
	 */
	private ArrayList<ArrayList<GL3DVertex>> loopList;

	/**
	 * Current vertex in current triangle.
	 */
	private int currentTriangleVertex = 0;
	
	/**
	 * Constructor.
	 */
	public Tessellator() {
		// Set callbacks.
		tessellator.gluTessCallback(GLU.GLU_TESS_VERTEX,    this);
		tessellator.gluTessCallback(GLU.GLU_TESS_BEGIN,     this);
		tessellator.gluTessCallback(GLU.GLU_TESS_END,       this);
		tessellator.gluTessCallback(GLU.GLU_TESS_COMBINE,   this);
		tessellator.gluTessCallback(GLU.GLU_TESS_EDGE_FLAG, this);
		tessellator.gluTessCallback(GLU.GLU_TESS_ERROR,     this);
	}
		
	/**
	 * Sets the winding rule
	 * @param rule GLU winding rule
	 */
	public void setWindingRule(int rule) { tessellator.gluTessProperty(GLU.GLU_TESS_WINDING_RULE, rule); }
	
	/**
	 * Tessellates a list of contours.
	 * @param contourList list of contours defining the polygon
	 * @param triangleList list to output triangles to or null
	 * @param loopList list to output line loops to or null
	 */
	public void tessellate(
		ArrayList<Vertex> contourList, 
		ArrayList<Triangle<GL3DVertex>>  triangleList,
		ArrayList<ArrayList<GL3DVertex>> loopList
	) {	
		// Set internal lists.
		this.triangleList = triangleList;
		this.loopList     = loopList;
	
		if (triangleList != null) {
			System.out.println("Tessellator 1!");
			tessellator.gluTessProperty(GLU.GLU_TESS_BOUNDARY_ONLY, GL11.GL_FALSE);
			
			// Reset triangle vertex counter.
			currentTriangleVertex = 0;
			
			// Write polygon to tessellator.
			writePolygon(contourList);
		}
		
		if (loopList != null) {
			tessellator.gluTessProperty(GLU.GLU_TESS_BOUNDARY_ONLY, GL11.GL_TRUE);
			
			// Write polygon to tessellator.
			writePolygon(contourList);		
		}
	}	
	
	/**
	 * Writes a set of contours to GLU tessellator.
	 */
	private void writePolygon(ArrayList<Vertex> contour) {
		// Begin polygon.
		tessellator.gluBeginPolygon();

		// Loop trough contours.
		//for (Vector<Vertex> contour : contourList) {
			// New contour.
			tessellator.gluTessBeginContour();

			// Loop trough vertices belonging to the contour.
			for (Vertex v : contour) {
				// Write vertex to array.
				double vertex[] = new double[3];
				
				vertex[0] = (double) v.getX()*0.01f;
				vertex[1] = (double) v.getY()*0.01f;
				vertex[2] = 0;
				
				// Feed vertex to tesselator.
				tessellator.gluTessVertex(vertex, 0, new GL3DVertex(vertex));
			}

			// End contour.
			tessellator.gluTessEndContour();
		//}

		// End polygon.
		tessellator.gluTessEndPolygon();
	}
	
	/**
	 * Tessellator begin event.
	 */
	public void begin(int type) {
		System.out.println("Tessellator begin!");
		// For reading caller status.
		double data[] = new double[1];
		
		// Get current tessellation mode.
		tessellator.gluGetTessProperty(GLU.GLU_TESS_BOUNDARY_ONLY, data, 0);
	
		// If we're rendering loops, create new loop for each begin event.
		if (data[0] == GL11.GL_TRUE && type == GL11.GL_LINE_LOOP) {
			loopList.add(new ArrayList<GL3DVertex>());
		}
	}
	
	/**
	 * Tessellator end event.
	 */
	public void end() {
		System.out.println("Tessellator end!");
	}
	
	/**
	 * Tessellator vertex event.
	 * @param vertex new vertex
	 */
	public void vertex(Object vertex) {
		System.out.println("Tessellator vertex!");
		
		// Test input.
		if (!(vertex instanceof GL3DVertex)) return;

		// Cast input to vector.
		GL3DVertex v = (GL3DVertex) vertex;
		
		// For reading caller status.
		double data[] = new double[1];
		
		// Get current tessellation mode.
		tessellator.gluGetTessProperty(GLU.GLU_TESS_BOUNDARY_ONLY, data, 0);
		
		// Test whether we are generating triangles or loops.
		if (data[0] == GL11.GL_FALSE) {
			// Increase triangle count on first vertex
			if (currentTriangleVertex == 0) triangleList.add(new Triangle<GL3DVertex>());

			// Push vertex to triangle.
			triangleList.get(triangleList.size() - 1).setAt(currentTriangleVertex, v);

			// Increase vertex counter.
			currentTriangleVertex++;

			// Reset vertex counter if a new triangle begins.
			if (currentTriangleVertex > 2) currentTriangleVertex = 0;
		} else {
			// Push vertex to current loop.
			loopList.get(loopList.size() - 1).add(v);
		};
	}
	

	@Override
	public void beginData(int arg0, Object arg1) {
		// TODO Auto-generated method stub
		
	}

	/**
	 * Tessellator combine event callback.
	 * @param coordinates new vertex position
	 * @param data user vertex data
	 * @param weights interpolation weights for user vertex data
	 * @param out output
	 */
	@Override
	public void combine(double[] coordinates, Object[] data, float[] weights, Object[] out) {
		out[0] = new GL3DVertex((float) coordinates[0], (float) coordinates[1], (float) coordinates[2]);
	}

	@Override
	public void combineData(double[] arg0, Object[] arg1, float[] arg2,	Object[] arg3, Object arg4) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void edgeFlag(boolean arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void edgeFlagData(boolean arg0, Object arg1) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void endData(Object arg0) {
		// TODO Auto-generated method stub
		
	}
	
	/**
	 * Tessellator error event callback.
	 * @param type error type.
	 */
	public void error(int errCode) {
		if (errCode != GL11.GL_NO_ERROR) {
			 System.err.println("OpenGL Error: " + errCode);
		}
	}

	@Override
	public void errorData(int arg0, Object arg1) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void vertexData(Object arg0, Object arg1) {
		// TODO Auto-generated method stub
		
	}
	
	
	
}
