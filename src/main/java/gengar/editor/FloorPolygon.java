package gengar.editor;
import java.io.Serializable;
import java.util.ArrayList;

import org.lwjgl.opengl.GL11;
import org.lwjgl.util.glu.GLU;

import fi.conf.ae.gl.texture.GLTextureManager;

public class FloorPolygon implements Serializable {

	private ArrayList<Vertex> vertexData; //In pixel space
	private ArrayList<Triangle<GL3DVertex>> polygonData; //Tessellated triangles in vector space
	private ArrayList<ArrayList<GL3DVertex>> outlineData; //Outlines
	private boolean complete = false;
	private TextureListItem material;
	private int floor = 0;
	private Vertex centerPoint;
	private boolean destroy = false;

	public FloorPolygon(int floor, TextureListItem material) {
		this.floor = floor;
		this.material = material;
		vertexData = new ArrayList<Vertex>();
		polygonData = new ArrayList<>();
		outlineData = new ArrayList<>();
	}

	public void setFloor(int floor){
		this.floor = floor;
	}

	public int getFloor(){
		return floor;
	}

	public void setMaterial(TextureListItem material){
		this.material = material;
	}

	public TextureListItem getMaterial(){
		return material;
	}

	public void newVertex(Vertex p){
		vertexData.add(p);
	}

	public boolean hasVertex(Vertex p){
		for(Vertex op : vertexData){
			if(op.equals(p)) return true;
		}
		return false;
	}

	public void complete(){
		polygonData.clear();
		outlineData.clear();
		
		float centerX = 0, centerY = 0;
	
		//Clear identical vertices and calculate centerpoint
		for(int i=0; i < vertexData.size(); i++){
			centerX += vertexData.get(i).getX();
			centerY += vertexData.get(i).getY();
			for(int j=i+1; j < vertexData.size(); j++){
				if(vertexData.get(i).equals(vertexData.get(j))){
					vertexData.remove(i);
				}
			}
		}

		centerPoint = new Vertex((int)(centerX/vertexData.size()),(int)(centerY/vertexData.size()));

		//If after cleaning, the size is under three points, dispose this polygon
		if(vertexData.size() < 3){
			dispose();
			return;
		}
		
		Tessellator tessu = new Tessellator();
		System.out.println("Tesselating...");

		tessu.setWindingRule(GLU.GLU_TESS_WINDING_ODD);

		tessu.tessellate(vertexData, polygonData, outlineData);
		System.out.println("Done!");
		complete = true;
	}

	public ArrayList<Triangle<GL3DVertex>> getVectorData(){
		return polygonData;
	}

	public ArrayList<Vertex> getVertices(){
		return vertexData;
	}

	public boolean isComplete() {
		return complete;
	}

	public Vertex getVertex(Vertex p) {
		for(Vertex op : vertexData){
			if(op.equals(p)) return op;
		}
		return null;
	}

	public void glDraw(){

		if(complete){
			GL11.glColor4f(1,1,1,1);
			GLTextureManager.getInstance().bindTexture(material.getName());
			GL11.glBegin(GL11.GL_TRIANGLES);
			for (Triangle<GL3DVertex> t : polygonData) {
				GL11.glNormal3f(0, 0, 1);
				GL11.glTexCoord2f((float)t.getA().getX()*material.getMultiplier(), (float)t.getA().getY()*material.getMultiplier()); 
				GL11.glVertex3f((float)t.getA().getX(), (float)t.getA().getY(), (float)t.getA().getZ());
				
				GL11.glNormal3f(0, 0, 1);
				GL11.glTexCoord2f((float)t.getB().getX()*material.getMultiplier(), (float)t.getB().getY()*material.getMultiplier());
				GL11.glVertex3f((float)t.getB().getX(), (float)t.getB().getY(), (float)t.getB().getZ());
				
				GL11.glNormal3f(0, 0, 1);
				GL11.glTexCoord2f((float)t.getC().getX()*material.getMultiplier(), (float)t.getC().getY()*material.getMultiplier());
				GL11.glVertex3f((float)t.getC().getX(), (float)t.getC().getY(), (float)t.getC().getZ());	
			}
			GL11.glEnd();
			GLTextureManager.unbindTexture();
			GL11.glColor4f(Constants.mapLineColor[0],Constants.mapLineColor[1],Constants.mapLineColor[2],Constants.mapLineColor[3]);
			GL11.glLineWidth(Constants.mapWallWidth);
			GL11.glBegin(GL11.GL_LINE_LOOP);
			for (ArrayList<GL3DVertex> t : outlineData) {
				for(GL3DVertex v : t){
					GL11.glVertex3f((float)v.getX(), (float)v.getY(), (float)v.getZ());
				}
			}
			GL11.glEnd();	
		}

	}

	public boolean isStartingVertex(Vertex v) {
		return vertexData.get(0).equals(v);
	}

	public Vertex addVertexToClosestLine(Vertex v) {
		
		int cv = 0;
		float cd = vertexData.get(cv).getDistanceToPoint(v);
		
		//Find closest vertex
		for (int i=1; i < vertexData.size(); i++) {
			float d = vertexData.get(i).getDistanceToPoint(v);
			if(d < cd){
				cd = d;
				cv = i;
			}
		}
		
		Vertex nv = new Vertex((int)((vertexData.get(cv).getX() + v.getX())/2), (int)((vertexData.get(cv).getY() + v.getY())/2));
		
		vertexData.add(cv, nv);
		
		return nv;
		
	}

	public void addVertexOnIndex(Vertex v, int i) {
		vertexData.add(i, v);
	}
	
	public Vertex getCenterPoint(){
		return centerPoint;
	}

	public void dispose() {
		destroy = true;
	}
	
	public boolean isDisposable(){
		return destroy;
	}

}
