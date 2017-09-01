package gengar.editor;

import java.awt.Canvas;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.util.Vector;

import javax.swing.Timer;

import org.lwjgl.BufferUtils;
import org.lwjgl.LWJGLException;
import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.GL11;
import org.lwjgl.util.glu.GLU;

import fi.conf.ae.gl.GLGraphicRoutines;
import fi.conf.ae.gl.GLValues;
import fi.conf.ae.gl.texture.GLTextureManager;
import fi.conf.ae.thread.DrainableExecutorService;

public class MapViewPanel extends Canvas implements ComponentListener {
		
	private static final long serialVersionUID = 1L;
	private Timer glLoopTimer = null;
	private DrainableExecutorService textureExecService;
	private Map map;
	private Vector<TouchPoint> touchPoints = new Vector<>();
	
	private FloatBuffer fb = BufferUtils.createFloatBuffer(4);
	
	private float screenPanX = 0, screenPanY = 0, screenZoom = 1;
	
	private float lastDragStartX = 0;
	private float lastDragStartY = 0;
	
	public MapViewPanel() {
		textureExecService = new DrainableExecutorService();
		addComponentListener(this);
	}

	@Override
	public void paint(Graphics g) {glMainloop();}

	@Override
	public void update(Graphics g) {glMainloop();}
	
	public void setMap(Map map){
		this.map = map;
	}
	
	public void startGL(){
		
		GLValues.setScreenSize(getWidth(), getHeight());
		GLValues.calculateRatios();

		Keyboard.enableRepeatEvents(false);
		
		glInit();
		
		new GLTextureManager(textureExecService).initialize();
		
		glLoopTimer = new Timer(30, new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent arg0) {
				glMainloop();
				textureExecService.executePending();
			}
			
		});
		
		glLoopTimer.setRepeats(true);
		glLoopTimer.start();
		
	}
	
	public void stopGL() {
		if (glLoopTimer != null) glLoopTimer.stop();
	}
	
	private void glInit(){
		
		try {
			//Display.setDisplayModeAndFullscreen(new DisplayMode(1024,768));
			Display.setResizable(true);
			Display.setParent(this);
			Display.create();
		} catch (LWJGLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		GL11.glClearColor( 0.0f, 0.0f, 0.0f, 1.0f);
		
		GL11.glEnable( GL11.GL_BLEND );
		GL11.glEnable( GL11.GL_ALPHA_TEST );
		GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
		
		GL11.glEnable( GL11.GL_DEPTH_TEST );
		GL11.glDepthFunc( GL11.GL_LEQUAL );
		
		GL11.glEnable(GL11.GL_TEXTURE_2D);
		
		GL11.glEnable( GL11.GL_LIGHTING );
		GL11.glEnable(GL11.GL_COLOR_MATERIAL);

		GL11.glEnable( GL11.GL_DITHER );
		GL11.glEnable( GL11.GL_SMOOTH );
		GL11.glEnable( GL11.GL_POINT_SMOOTH );
		GL11.glEnable( GL11.GL_LINE_SMOOTH );
		GL11.glShadeModel (GL11.GL_FLAT);
		GL11.glEnable( GL11.GL_STENCIL_TEST );
		GL11.glDisable( GL11.GL_FOG );
		GL11.glHint(GL11.GL_POINT_SMOOTH_HINT, GL11.GL_NICEST);
		GL11.glHint(GL11.GL_LINE_SMOOTH_HINT, GL11.GL_NICEST);
		GL11.glHint(GL11.GL_PERSPECTIVE_CORRECTION_HINT, GL11.GL_NICEST);
		
		GL11.glShadeModel(GL11.GL_SMOOTH);

		GLValues.cameraTargetX = 0;
		GLValues.cameraTargetY = 0;
		GLValues.cameraTargetZ = 0;
		
		GLValues.cameraPositionX = 0;
		GLValues.cameraPositionY = 0;
		GLValues.cameraPositionZ = -10;
		
		GLValues.cameraRotationX = 0;
		GLValues.cameraRotationY = -1;
		GLValues.cameraRotationZ = 0;
		
	}
	
	long startTime = System.currentTimeMillis();
	long lastTime;
	
	public void glMainloop(){
		
		handleMouseEvents();
		
		handleTranslations();
		
		float t = (System.currentTimeMillis() - lastTime)/30f;
		lastTime = System.currentTimeMillis();
		
		GL11.glClear(
				GL11.GL_COLOR_BUFFER_BIT |
				GL11.GL_DEPTH_BUFFER_BIT |
				GL11.GL_ACCUM_BUFFER_BIT | 
				GL11.GL_STENCIL_BUFFER_BIT
				);
		
		GL11.glLoadIdentity();
		
		GL11.glTranslatef(0, 0, 0);
		
		if(map == null){
			GL11.glDisable( GL11.GL_LIGHTING );
			GLGraphicRoutines.initOrtho();
			GL11.glLineWidth(3.0f);
			GL11.glBegin(GL11.GL_LINE_STRIP);
				GL11.glColor4f(1, 0, 0, 1);
				GL11.glVertex2f(GLValues.glWidth*0.05f, GLValues.glHeight*0.05f);
				GL11.glVertex2f(GLValues.glWidth*0.05f, GLValues.glHeight*0.95f);
				GL11.glVertex2f(GLValues.glWidth*0.95f, GLValues.glHeight*0.95f);
				GL11.glVertex2f(GLValues.glWidth*0.95f, GLValues.glHeight*0.05f);
				GL11.glVertex2f(GLValues.glWidth*0.05f, GLValues.glHeight*0.05f);
				GL11.glVertex2f(GLValues.glWidth*0.95f, GLValues.glHeight*0.95f);
			GL11.glEnd();
			Display.update();
			return;
		} else {
			GL11.glEnable( GL11.GL_LIGHTING );
		}
		
		GLGraphicRoutines.initPerspective(40);
		GLGraphicRoutines.initCamera();
		
		GL11.glColor4f(1, 1, 1, 1);

		GL11.glPushMatrix();
			for(FloorPolygon p : map.getAllFloorPolygons()){
				p.glDraw();
			}
		GL11.glPopMatrix();
		
		GL11.glPushMatrix();
			for(Door d : map.getAllDoors()){
				d.glDraw();
			}
		GL11.glPopMatrix();

		GL11.glPushMatrix();
			for(Item i : map.getAllItems()){
				i.glDraw();
			}
		GL11.glPopMatrix();
		
		Display.update();
		
	}
	
	private void handleTranslations() {
		GLValues.cameraPositionX = screenPanX;
		GLValues.cameraPositionY = screenPanY;
		GLValues.cameraTargetY = GLValues.cameraPositionY;
		GLValues.cameraTargetX = GLValues.cameraPositionX;
		GLValues.cameraTargetZ = 0;
		
		fb.put(new float[] {0.0f, 0.0f, 0.0f, 1.0f}); fb.rewind(); GL11.glLight(GL11.GL_LIGHT0, GL11.GL_AMBIENT, fb);
		fb.put(new float[] {0.5f, 0.5f, 0.5f, 1.0f}); fb.rewind(); GL11.glLight(GL11.GL_LIGHT0, GL11.GL_DIFFUSE, fb);
		fb.put(new float[] {1.0f, 1.0f, 1.0f, 1.0f}); fb.rewind(); GL11.glLight(GL11.GL_LIGHT0, GL11.GL_SPECULAR, fb);
		fb.put(new float[] {GLValues.cameraTargetX, GLValues.cameraTargetY, 2.0f}); fb.rewind(); GL11.glLight(GL11.GL_LIGHT0, GL11.GL_POSITION, fb);
		GL11.glEnable(GL11.GL_LIGHT0);
		fb.clear();
		
	}

	@Override
	public void setSize(Dimension d) {
		setSize((int)d.getWidth(), (int)d.getHeight());
	}
	
	@Override
	public void setSize(int width, int height) {
		super.setSize(width, height);
		GLValues.setScreenSize(width, height);
		GLValues.calculateRatios();
	}
	
	private void handleMouseEvents(){
		
		while (Mouse.next()) {

			if ((Mouse.getDX() != 0) || (Mouse.getDY() != 0)) {
				pointMoved(0, Mouse.getX(), Mouse.getY());
			}
			
			if (Mouse.getEventButton() == -1) {
				continue;
			}
			
			if (Mouse.getEventButtonState() == true) {
				// button changed to down
				pointAppeared(0, Mouse.getX(), Mouse.getY(),  Mouse.getEventButton());
			} else {
				// button changed to up
				pointDisappeared(0, Mouse.getEventButton());
			}
	
		} // while
		
	}
	
	//Project 2D screen coordinates to 3D space
	private FloatBuffer translateTo3DSpace(int x, int y){
		
		FloatBuffer z =  BufferUtils.createFloatBuffer(1);
		IntBuffer viewport = BufferUtils.createIntBuffer(16);
		FloatBuffer modelviewMatrix = BufferUtils.createFloatBuffer(16);
		FloatBuffer projMatrix = BufferUtils.createFloatBuffer(16);
		FloatBuffer newPosition = BufferUtils.createFloatBuffer(3);
		
		GL11.glGetFloat( GL11.GL_MODELVIEW_MATRIX, modelviewMatrix );
		GL11.glGetFloat( GL11.GL_PROJECTION_MATRIX, projMatrix);
		GL11.glGetInteger( GL11.GL_VIEWPORT, viewport);
		
	    GL11.glReadPixels(x, y, 1, 1, GL11.GL_DEPTH_COMPONENT, GL11.GL_FLOAT, z);
		GLU.gluUnProject(x, y, z.get(), modelviewMatrix, projMatrix, viewport, newPosition);
		
		return newPosition;
		
	}

	@Override
	public void componentResized(ComponentEvent e) {
		GLValues.setScreenSize(getWidth(), getHeight());
		GLValues.calculateRatios();
		GL11.glMatrixMode(GL11.GL_PROJECTION);
		//GL11.glLoadIdentity();
		//GL11.glPixelZoom( 1.0f, 1.0f );
		GL11.glViewport(0, 0, GLValues.screenWidth, GLValues.screenHeight);
		GL11.glMatrixMode(GL11.GL_MODELVIEW);
	}

	@Override
	public void componentMoved(ComponentEvent e) {}

	@Override
	public void componentShown(ComponentEvent e) {}

	@Override
	public void componentHidden(ComponentEvent e) {}
	
	//------------- Handling Points
	
	private void pointDisappeared(int id, int eventButton) {
		for(int i=0; i<touchPoints.size(); i++){
			touchPoints.remove(i);
		}
	}

	private void pointAppeared(int id, int x, int y, int button) {
		FloatBuffer newPosition = translateTo3DSpace(x, y);
		touchPoints.add(new TouchPoint(id, newPosition.get(0), newPosition.get(1), newPosition.get(2), button));
		
		if(touchPoints.size() == 1){
			lastDragStartX = x;
			lastDragStartY = y;
		}
		
	}

	private void pointMoved(int id, int x, int y) {
		
		for(TouchPoint tp : touchPoints){
			if(tp.id == id){
				tp.move(translateTo3DSpace(x, y));
				System.out.println(touchPoints.get(id));
			}
		}
		
		if(touchPoints.size() == 1){
			screenPanX -= (x - lastDragStartX)*0.01f;
			screenPanY += (y - lastDragStartY)*0.01f;
			lastDragStartX = x;
			lastDragStartY = y;
		}
		
	}

}

 
