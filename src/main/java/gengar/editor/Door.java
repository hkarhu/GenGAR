package gengar.editor;
import java.io.Serializable;

import org.lwjgl.opengl.GL11;

import fi.conf.ae.gl.GLGraphicRoutines;
import fi.conf.ae.gl.texture.GLTextureManager;

public class Door implements Serializable {
	
	private Vertex v1, v2;
	private GL3DVertex centerpoint;
	private float angle;
	private float lenght;
	private TextureListItem material;
	private boolean complete = false;
	private boolean dispose = false;
	
	public Door(Vertex v1, Vertex v2, TextureListItem material) {
		this.v1 = v1;
		this.v2 = v2;
		this.material = material;
	}
	
	public void complete(){
		
		if(v1.equals(v2)){
			dispose = true;
			return;
		}
		
		if(v2.getY() < v1.getY()){
			angle = -180*(float)(Math.atan((float)(v2.getX()-v1.getX())/(float)(v2.getY()-v1.getY()))/Math.PI)+90;
		} else if(v2.getY() > v1.getY()){
			angle = -180*(float)(Math.atan((float)(v2.getX()-v1.getX())/(float)(v2.getY()-v1.getY()))/Math.PI)+270;
		} else {
			angle = 180;
		}
		
		lenght = (float)Math.sqrt(Math.pow(v2.getY()-v1.getY(), 2)+Math.pow(v2.getX()-v1.getX(), 2))*0.005f;
		centerpoint = new GL3DVertex((v2.getX()+v1.getX())*0.005f, (v2.getY()+v1.getY())*0.005f, 0);
		
		complete = true;
	}
	
	public void glDraw(){
		
		if(!complete) return;
		
		GL11.glColor4f(1,1,1,1);
		GLTextureManager.getInstance().bindTexture(material.getName());
		
		GL11.glPushMatrix();
			GL11.glTranslatef(centerpoint.getX(), centerpoint.getY(), 0);
			GL11.glRotatef(angle, 0, 0, 1);
			GLGraphicRoutines.draw2DRect(-lenght, -Constants.mapDoorWidth, lenght, Constants.mapDoorWidth, 0);
			
			GLTextureManager.unbindTexture();
			GL11.glColor4f(Constants.mapLineColor[0],Constants.mapLineColor[1],Constants.mapLineColor[2],Constants.mapLineColor[3]);
			GL11.glLineWidth(3.0f);
			GL11.glBegin(GL11.GL_LINE_STRIP);
				GL11.glVertex3f(-lenght, -Constants.mapDoorWidth, 0);
				GL11.glVertex3f(-lenght, Constants.mapDoorWidth, 0);
			GL11.glEnd();
			GL11.glBegin(GL11.GL_LINE_STRIP);
				GL11.glVertex3f(lenght, -Constants.mapDoorWidth, 0);
				GL11.glVertex3f(lenght, Constants.mapDoorWidth, 0);
			GL11.glEnd();
		GL11.glPopMatrix();
		
		complete();
		
	}
	
	public Vertex getVertex1() {
		return v1;
	}

	public Vertex getVertex2() {
		return v2;
	}
	
	public boolean isDisposable(){
		return dispose;
	}

}
