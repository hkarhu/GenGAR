package gengar.editor;
import org.lwjgl.opengl.GL11;

import fi.conf.ae.gl.GLGraphicRoutines;
import fi.conf.ae.gl.text.GLBitmapFontBlitter;
import fi.conf.ae.gl.texture.GLTextureManager;

public class Player extends Item {
	
	private String name = "Unnamed Player";
	
	public void glDraw(){
		GLTextureManager.unbindTexture();
		GL11.glLineWidth(Constants.mapLineWidth);
		GLBitmapFontBlitter.drawString(name, "default_font", 0.2f, 0.2f, GLBitmapFontBlitter.Alignment.CENTERED);
		GLGraphicRoutines.drawCircle(0.5f, 10);
	}
	
}
