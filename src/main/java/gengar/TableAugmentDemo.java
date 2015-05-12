package gengar;

import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.function.Consumer;

import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.DisplayMode;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.PixelFormat;

import fi.conf.ae.gl.GLGraphicRoutines;
import fi.conf.ae.gl.GLValues;
import fi.conf.ae.gl.core.DisplayModePack;
import fi.conf.ae.gl.core.GLCore;
import fi.conf.ae.gl.core.GLKeyboardListener;
import fi.conf.ae.gl.text.GLBitmapFontBlitter;
import fi.conf.ae.gl.text.GLBitmapFontBlitter.Alignment;
import fi.conf.ae.gl.texture.GLTextureManager;
import fi.conf.ae.routines.S;
import fi.conf.tabare.ARDataProvider;
import fi.conf.tabare.RealityChangeListener;
import fi.conf.tabare.TrackableBlob;
import fi.conf.tabare.TrackableObject;
import fi.conf.tabare.TrackableObject.ItemType;
import gengar.Hex.HexStyle;

public class TableAugmentDemo extends GLCore implements RealityChangeListener, GLKeyboardListener {
	
	private final int WINDOW_WIDTH = 1280;
	private final int WINDOW_HEIGHT = 800;
	private final int WINDOW_POS_X = 1280;
	private final int WINDOW_POS_Y = 1080;
	
	private final ARDataProvider arDataProvider;
	
	private final LinkedList<Hex> hexs;
	private HexStyle style = HexStyle.none;
	
	private double lastBlobX = 0, lastBlobY = 0;
	private long startTime = 0;
	
	public TableAugmentDemo(ARDataProvider arDataProvider) {
		this.arDataProvider = arDataProvider;
		this.keyboardListeners.add(this);
		
		GLValues.setScreenSize(WINDOW_WIDTH, WINDOW_HEIGHT);
		GLValues.calculateRatios();
		
		hexs = new LinkedList<Hex>();
		
		for(int r=0; r < GLValues.glHeight/Hex.HEX_SIZE; r++){
			for(int q=0; q < GLValues.glWidth/Hex.HEX_SIZE; q++){
				hexs.add(new Hex(q, r));
			}			
		}
	}
	
	@Override
	public boolean glInit() {
		new GLTextureManager(getExecutorService()).initialize();

		GL11.glClearColor( 0.0f, 0.0f, 0.0f, 1.0f);

		GL11.glEnable( GL11.GL_ALPHA_TEST );
		GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);

		//Enable depth testing
		GL11.glEnable( GL11.GL_DEPTH_TEST );
		GL11.glDepthFunc( GL11.GL_LEQUAL );

		//Enable blending
		GL11.glEnable( GL11.GL_BLEND );
		GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);

		//Enable textures and texture coloring
		GL11.glEnable(GL11.GL_TEXTURE_2D);
		GL11.glEnable(GL11.GL_COLOR_MATERIAL);

		GL11.glEnable(GL11.GL_NORMALIZE);
		GL11.glEnable( GL11.GL_CULL_FACE );
		GL11.glCullFace( GL11.GL_BACK );

		GL11.glDisable(GL11.GL_LIGHTING);
		GL11.glShadeModel(GL11.GL_FLAT);
		
		GL11.glEnable( GL11.GL_SMOOTH );
		GL11.glEnable( GL11.GL_POINT_SMOOTH );
		GL11.glEnable( GL11.GL_LINE_SMOOTH );
		
		GL11.glHint(GL11.GL_POINT_SMOOTH_HINT, GL11.GL_NICEST);
		GL11.glHint(GL11.GL_LINE_SMOOTH_HINT, GL11.GL_NICEST);
		GL11.glHint(GL11.GL_PERSPECTIVE_CORRECTION_HINT, GL11.GL_NICEST);
		GL11.glHint(GL11.GL_POLYGON_SMOOTH_HINT, GL11.GL_NICEST);

		GL11.glClear(
				GL11.GL_COLOR_BUFFER_BIT |
				GL11.GL_DEPTH_BUFFER_BIT |
				GL11.GL_ACCUM_BUFFER_BIT |
				GL11.GL_STENCIL_BUFFER_BIT
				);

		//Load all images from './data' to textures
		try (DirectoryStream<Path> stream = Files.newDirectoryStream(Paths.get("data"), "*.{jpg,png}")) {
			for (Path path : stream) {
				String identifier = path.getFileName().toString();
				identifier = identifier.substring(0,identifier.length()-4);
				S.debugFunc("Loading texture '%s' to identifier '%s'", path.toString(), identifier);
				GLTextureManager.getInstance().blockingLoad(path, identifier);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		Display.setLocation(WINDOW_POS_X, WINDOW_POS_Y);
		//Display.setSwapInterval(0);
		
		startTime = System.currentTimeMillis();
		return true;
	}

	@Override
	public DisplayModePack glPickDisplayMode() throws Exception {

		if ( Display.getDesktopDisplayMode().getBitsPerPixel() < 24 ) {
			throw new Exception( "Desktop bpp is too low." );
		}
		
		return new DisplayModePack(
				new DisplayMode(  GLValues.screenWidth,  GLValues.screenHeight ),
				new PixelFormat().withDepthBits( Display.getDesktopDisplayMode().getBitsPerPixel() ).withSamples( GLValues.antialiasSamples ),
				GLValues.fullScreen );
	}

	@Override
	public void glLoop() {
		
		long currentTime = System.currentTimeMillis()-startTime;
		
		GL11.glClear(
				GL11.GL_COLOR_BUFFER_BIT |
				GL11.GL_DEPTH_BUFFER_BIT |
				GL11.GL_ACCUM_BUFFER_BIT |
				GL11.GL_STENCIL_BUFFER_BIT
				);

		GL11.glLoadIdentity();
		GL11.glTranslatef(0, 0, 0);
		GLGraphicRoutines.initOrtho();

//		GL11.glBindTexture(GL11.GL_TEXTURE_2D, -1);
//		GL11.glColor4f(0.0f, 0.0f, 0.0f, 1);
//		GLGraphicRoutines.drawRepeatedBackgroundPlane(-GLValues.glWidth*0.49f, -GLValues.glHeight*0.49f, GLValues.glWidth*0.49f, GLValues.glHeight*0.49f);
		
		GL11.glPushMatrix();
		
			GL11.glColor4f(0.0f, 0.0f, 0.0f, 1.0f);
			GLGraphicRoutines.drawRepeatedBackgroundPlane(-GLValues.glWidth*0.49f, -GLValues.glHeight*0.49f, GLValues.glWidth*0.49f, GLValues.glHeight*0.49f);
	
			GL11.glColor4f(1,1,1,1);
			GL11.glTranslatef(0.02f, GLValues.glHeight*0.98f, 0);
			GLBitmapFontBlitter.drawString("Augment Engine Demo 0.0.1b ", "font_default", 0.12f, 0.2f, Alignment.LEFT);
	
		GL11.glPopMatrix();

		Iterator<TrackableBlob> i = (Iterator<TrackableBlob>) arDataProvider.getTrackedItemIterator(ItemType.blob);
		
		if(i != null)
		i.forEachRemaining(new Consumer<TrackableBlob>() {
			@Override
			public void accept(TrackableBlob t) {
				GL11.glPushMatrix();
				GL11.glTranslated(t.getX()*GLValues.glWidth, t.getY()*GLValues.glHeight, 0);
				for(Hex h : hexs){
					if(h.collidesWith(t.getX()*GLValues.glWidth, t.getY()*GLValues.glHeight)){
						h.setHighlight(currentTime, style);
					}
				}
				GLTextureManager.unbindTexture();
				GLGraphicRoutines.drawCircle(0.05f, 10);
				GL11.glPopMatrix();
			}
		});

		for(Hex h : hexs){
			h.glDraw(currentTime);
		}
		
		GLGraphicRoutines.drawLineRect(4.0f, GLValues.glWidth*0.01f, GLValues.glHeight*0.01f, GLValues.glWidth*0.99f, GLValues.glHeight*0.99f, -5);		
	}
	
	@Override
	public void glFocusChanged(boolean isFocused) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void glTerminate() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void objectAppeared(TrackableObject t) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void objectDisappeared(TrackableObject t) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void objectChanged(TrackableObject t) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void glKeyDown(int eventKey, char keyChar) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void glKeyUp(int eventKey, char keyChar) {
		System.out.println(eventKey + "");
		switch (eventKey) {
		case 74: Hex.HEX_SIZE -= 0.0001f; break; // Minus
		case 78: Hex.HEX_SIZE += 0.0001f; break; // Plus
		case 200:break; // Up
		case 203:break; // Left
		case 205:break; // Right
		case 208:break; // Down
		case 0: for(Hex h : hexs) if(h.getStyle().equals(style)) h.setHighlight(0, HexStyle.none); break;
		case 54: for(Hex h : hexs) h.setHighlight(0, HexStyle.none); break;
		case 2: style = HexStyle.none; break;
		case 3: style = HexStyle.red; break;
		case 4: style = HexStyle.green; break;
		case 5: style = HexStyle.blue; break;
		case 6: style = HexStyle.yellow; break;
		case 7: style = HexStyle.cyan; break;
		case 8: style = HexStyle.magenta; break;
		case 9: style = HexStyle.point; break;
		case 10: style = HexStyle.hot; break;
		case 11: style = HexStyle.danger; break;
		default:
			break;
		}
		
		System.out.println("Hex style:" + style);
	}

	
}
