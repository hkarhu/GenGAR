package gengar.editor;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.Serializable;
import java.nio.file.Path;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;

import fi.conf.ae.gl.texture.GLTextureManager;

public class TextureListItem implements Serializable {

	private final int ICON_SIZE = 64;
	
	private ImageIcon icon;
	private String name;
	private float multiplier = 1;
	
	public TextureListItem(Path path) {
		
		BufferedImage image = new BufferedImage(ICON_SIZE, ICON_SIZE, BufferedImage.TYPE_USHORT_565_RGB);
		
		try {
			BufferedImage i = ImageIO.read(path.toFile());
			multiplier = 248.0f/Math.max(i.getWidth(), i.getHeight());
			Graphics g = image.getGraphics();
			System.out.println(multiplier);
			g.drawImage(i, 0, 0, ICON_SIZE, ICON_SIZE, null);
			g.setColor(Color.black);
			g.drawRect(0, 0, ICON_SIZE-1, ICON_SIZE-1);
			icon = new ImageIcon(image);
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		name = path.getFileName().toString().replaceAll("\\.(png|jpg)$", "");
		
		GLTextureManager.getInstance().deferredLoad(path, name);
		
	}

	public ImageIcon getIcon() {
		return icon;
	}

	public String getName() {
		return name;
	}

	public float getMultiplier() {
		return multiplier;
	}
	
}
