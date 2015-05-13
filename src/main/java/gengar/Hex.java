package gengar;

import org.lwjgl.opengl.GL11;

import fi.conf.ae.gl.text.GLBitmapFontBlitter;
import fi.conf.ae.gl.text.GLBitmapFontBlitter.Alignment;
import fi.conf.ae.gl.texture.GLTextureManager;

public class Hex {
	
	public static final float HEX_DETECTION_COVERAGE = 1.1f;
	public static float HEX_SIZE = 0.1638f;
	public static float HEX_LINE_WIDTH = 0.5f;
	
	
	private int q, r;
	private float cR=0.5f, cB=0.5f, cG=0.5f, cA=1f;
	private float clR=1, clB=1, clG=1, clA=1;
	private String label;
	private long highlightTime = 0;
	
	public enum HexStyle { none, red, green, blue, yellow, cyan, magenta, point, hot, active, danger }
	private HexStyle style = HexStyle.none;

	public Hex(int q, int r) {
		this.q = q;
		this.r = r;
	}
	
	private void lineHex(){
		GL11.glBegin(GL11.GL_LINE_LOOP);		
		for(float a=(float) (Math.PI/6.0); a < 2*Math.PI; a += (2.0f*Math.PI)/6){
			GL11.glVertex3d(Math.sin(a)*HEX_SIZE, Math.cos(a)*HEX_SIZE, 0);
		}
		GL11.glEnd();
	}
	private void fillHex(){
		GL11.glBegin(GL11.GL_TRIANGLE_FAN);
		for(float a=(float) (Math.PI/6.0); a < 2*Math.PI; a += (2.0f*Math.PI)/6){
			GL11.glNormal3f(0, 0, -1.0f);
			GL11.glTexCoord2d((Math.sin(a))+0.0f, (Math.cos(a-(Math.PI/6.0))));
			GL11.glVertex3d(Math.sin(a)*HEX_SIZE, Math.cos(a)*HEX_SIZE, 0);
		}
		GL11.glEnd();	
	}
	
	public void glDraw(long time){
		
		GL11.glBindTexture(GL11.GL_TEXTURE_2D, -1);
		
		GL11.glPushMatrix();
		
			float xShift = HEX_SIZE*1.5f*q;
			float yShift = HEX_SIZE*(1.73206f)*r + (q%2 == 0 ? 0 : HEX_SIZE*(0.86603f));
			
			GL11.glTranslatef(xShift, yShift, 0);
			switch (style) {
			case point:
				GL11.glColor4f(1,1,1,1);
				GL11.glLineWidth(HEX_LINE_WIDTH);
				lineHex();
				GL11.glScalef(0.5f, 0.5f, 0);
				fillHex();
				break;
			case hot:
				GL11.glColor4f(1,0,0,(float)Math.sin(time*0.005f)*0.5f+0.8f);
				fillHex();
				//GL11.glRotatef((float) (Math.sin(time*0.001f)*30), 0, 0, 1);
				GLBitmapFontBlitter.drawString("HOT", "font_code", 0.07f, 0.1f, Alignment.CENTERED);
				break;
			case active:
				break;
			case danger:
				GL11.glColor4f(1, 1, 1, 1);
				GLTextureManager.getInstance().bindTexture("danger");
				fillHex();
				GL11.glColor4f(0,0,0,0.25f);
				GL11.glLineWidth(HEX_LINE_WIDTH*4);
				lineHex();
				break;
			case none:
				GL11.glColor4f(0.5f,0.5f,0.5f,1.0f);
				GL11.glLineWidth(HEX_LINE_WIDTH);
				lineHex();
				break;
			case red: GL11.glColor4f(1,0,0,1); fillHex(); break;
			case green: GL11.glColor4f(0,1,0,1); fillHex(); break;
			case blue: GL11.glColor4f(0,0,1,1); fillHex(); break;
			case yellow: GL11.glColor4f(1,1,0,1); fillHex(); break;
			case cyan: GL11.glColor4f(0,1,1,1); fillHex(); break;
			case magenta: GL11.glColor4f(1,0,1,1); fillHex(); break;
			default: break;
			}
				
			
		GL11.glPopMatrix();
		
	}

	public int getQ() {
		return q;
	}

	public void setQ(int q) {
		this.q = q;
	}

	public int getR() {
		return r;
	}

	public void setR(int r) {
		this.r = r;
	}
	
	public boolean collidesWith(double d, double e){
		float xShift = HEX_SIZE*1.5f*q;
		float yShift = HEX_SIZE*(1.73206f)*r + (q%2 == 0 ? 0 : HEX_SIZE*(0.86603f));
		return Math.sqrt(Math.pow(d-xShift, 2) + Math.pow(e-yShift, 2)) <= HEX_SIZE*HEX_DETECTION_COVERAGE;
	}

	public String getLabel() {
		return label;
	}

	public void setLabel(String label) {
		this.label = label;
	}
	
	public void setHighlight(long time, HexStyle style){
		this.style = style;
		highlightTime = time + 2000;
	}

	public HexStyle getStyle() {
		return style;
	}

}
