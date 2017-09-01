package gengar.editor;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import java.util.ArrayList;

import javax.swing.JPanel;

public class MapEditPanel extends JPanel implements MouseListener, MouseMotionListener, MouseWheelListener {

	private Map mapData;
	private boolean panning = false;
	private boolean editing = false;
	private int xCurrent = 0, yCurrent = 0;
	private int xStart = 0, yStart = 0;
	private float zoom = 1;
	private int xShift, yShift;

	private int gridSnap = 25;
	private int gridCursorSize = 6;

	private TextureListItem currentMaterial;

	private EditModes mode = EditModes.createPoly;

	private Vertex activeVertex;
	private FloorPolygon activePolygon;
	private Door activeDoor;
	private Item activeItem;

	public MapEditPanel() {
		addMouseListener(this);
		addMouseMotionListener(this);
		addMouseWheelListener(this);
	}
	
	@Override
	public void setBounds(int x, int y, int width, int height) {
		
		if(super.getBounds().getHeight() == 0){
			xShift = width/2;
			yShift = height/2;	
		}
		super.setBounds(x, y, width, height);
		
	}

	public void setMap(Map map) {
		interruptEdit();
		this.mapData = map;
	}
	
	private Vertex snapVertexToGrid(int x, int y){

		if(x-xShift <= 0){
			x=(int)(gridSnap*Math.floor((x-xShift-gridSnap/2)/gridSnap)*zoom);
		} else {
			x=(int)(gridSnap*Math.ceil((x-xShift+gridSnap/2)/gridSnap)*zoom);
		}

		if(y-yShift <= 0){
			y=(int)(gridSnap*Math.floor((y-yShift-gridSnap/2)/gridSnap)*zoom);
		} else {
			y=(int)(gridSnap*Math.ceil((y-yShift+gridSnap/2)/gridSnap)*zoom);
		}

		return new Vertex(x,y);

	}

	private Vertex translateToMap(int x, int y){

		x=(int)((x-xShift)*zoom);
		y=(int)((y-yShift)*zoom);

		return new Vertex(x,y);

	}

	@Override
	public void paint(Graphics g) {
		
		Graphics2D g2 = (Graphics2D) g;
				
		//Background
		g2.setColor(Color.darkGray);
		g2.fillRect(0, 0, getWidth(), getHeight());

		//Don't draw if there's no map yet
		if(mapData == null){
			
			g2.setFont(new Font("monospaced", Font.BOLD, 48));
			g2.translate(getWidth()*0.5f, getHeight()*0.5f);
			g2.setStroke(new BasicStroke(9, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
			g2.setColor(Color.getHSBColor(1.0f, 1, 0.8f));
			g2.fillOval(-120, -120, 240, 240);
			g2.setColor(Color.red);
			g2.drawLine(-82, -82, 82, 82);
			g2.drawOval(-120, -120, 240, 240);
			g2.setColor(Color.black);
			g2.drawString("No map!", -95, 15);
			
			return;
		}
		
		//Unit grid with panshift
		g2.setColor(Color.black);
		
		int m = (int)(100*zoom);
		
		for(int y=0; y < getHeight(); y += m){
			g2.drawLine(0, (int)((y+(yShift%m))*zoom), getWidth(), (int)((y+(yShift%m))*zoom));
		}
		for(int x=0; x < getWidth(); x += m){
			g2.drawLine((int)((x+(xShift%m))*zoom), 0, (int)((x+(xShift%m))*zoom), getHeight());
		}

		//Snap grid with panshift
		g2.setColor(Color.gray);
		for(int y=0; y < getHeight(); y += gridSnap*zoom){
			for(int x=0; x < getWidth(); x += gridSnap*zoom){
				g2.fillOval((int)((x+(xShift%(gridSnap*zoom)))*zoom)-1, (int)((y+(yShift%(gridSnap*zoom)))*zoom)-1, 2,2);
			}
		}
		
		//Text stuff
		g2.setFont(new Font("monospaced", Font.PLAIN, 10));
		g2.setColor(Color.cyan);
		g2.drawString("floor: " + mapData.getFloor() + ", material: " + currentMaterial.getName() + " zoom:" + zoom, 10, 15);

		if(panning){
			g2.translate((xShift+(xCurrent-xStart))*zoom, (yShift+(yCurrent-yStart))*zoom);
		} else {
			g2.translate(xShift*zoom, yShift*zoom);
		}

		//Grid Cursor
		Vertex c = snapVertexToGrid(xCurrent, yCurrent);
		g2.setColor(Color.green);
		g2.drawOval((int)(c.getX()*zoom)-gridCursorSize, (int)(c.getY()*zoom)-gridCursorSize, gridCursorSize*2, gridCursorSize*2);

		//Origin
		g2.setColor(Color.red);
		g2.fillOval(-2, -2, 4, 4);

		//Floor polys
		for(int j=0; j < mapData.getAllFloorPolygons().size(); j++){

			FloorPolygon p = mapData.getAllFloorPolygons().get(j);

			ArrayList<Vertex> v = p.getVertices();

			if(mode.equals(EditModes.createPoly) || mode.equals(EditModes.addVertex) || mode.equals(EditModes.moveVertex)){
				g2.setColor(Color.red);
				for(Vertex vt : v){
					g2.drawOval((int)(vt.getX()*zoom)-2, (int)(vt.getY()*zoom)-2, 5, 5);
				}
				
				if(p == activePolygon){
					g2.setColor(Color.yellow);
				} else {
					g2.setColor(Color.blue);
				}
			} else {
				g2.setColor(Color.gray);
			}

			for(int i=1; i < v.size(); i++){
				g2.drawLine((int)(v.get(i-1).getX()*zoom), (int)(v.get(i-1).getY()*zoom), (int)(v.get(i).getX()*zoom), (int)(v.get(i).getY()*zoom));
			}

			if(p.isComplete()){
				g2.drawLine((int)(v.get(0).getX()*zoom), (int)(v.get(0).getY()*zoom), (int)(v.get(v.size()-1).getX()*zoom), (int)(v.get(v.size()-1).getY()*zoom));
				if(zoom == 1){
					g2.drawRoundRect(p.getCenterPoint().getX()-42, p.getCenterPoint().getY()-20, 48+(int)g2.getFont().getStringBounds(p.getMaterial().getName(), g2.getFontMetrics().getFontRenderContext()).getWidth(), 38, 4, 4);
					g2.setColor(Color.lightGray);
					g2.drawString(p.getMaterial().getName(), p.getCenterPoint().getX(), p.getCenterPoint().getY()-4);
					g2.drawImage(p.getMaterial().getIcon().getImage(), p.getCenterPoint().getX()-38, p.getCenterPoint().getY()-16, 32, 32, null);
				}
			}
			
			if(activeVertex != null && mode.equals(EditModes.createPoly)){
				g2.setColor(Color.magenta);
				g2.drawLine((int)(activeVertex.getX()*zoom), (int)(activeVertex.getY()*zoom), (int)(c.getX()*zoom), (int)(c.getY()*zoom));
			}

		}
		
		//Doors
		for(int j=0; j < mapData.getAllDoors().size(); j++){
			
			Door d = mapData.getAllDoors().get(j);
			
			g2.setStroke(new BasicStroke(4, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
			
			if(mode.equals(EditModes.createDoor)) {
				g2.setColor(Color.red);
				g2.drawOval((int)(d.getVertex1().getX()*zoom)-2, (int)(d.getVertex1().getY()*zoom)-2, 5, 5);
				g2.drawOval((int)(d.getVertex2().getX()*zoom)-2, (int)(d.getVertex2().getY()*zoom)-2, 5, 5);
				g2.setColor(Color.green);
			} else {
				g2.setColor(Color.lightGray);
			}
			
			if(d.equals(activeDoor)){
				g2.setColor(Color.magenta);
				if(activeVertex != null && mode.equals(EditModes.createDoor)){
					g2.drawLine((int)(d.getVertex1().getX()*zoom), (int)(d.getVertex1().getY()*zoom), (int)(activeVertex.getX()*zoom), (int)(activeVertex.getY()*zoom));
				}
			} else {
				g2.drawLine((int)(d.getVertex1().getX()*zoom), (int)(d.getVertex1().getY()*zoom), (int)(d.getVertex2().getX()*zoom), (int)(d.getVertex2().getY()*zoom));
			}
			
		}
		
		//items
		for(int j=0; j < mapData.getAllItems().size(); j++){
			
			Item item = mapData.getAllItems().get(j);
		
			g2.drawRect((int)(item.getPixelX()*zoom), (int)(item.getPixelY()*zoom), (int)(item.getPixelWidth()*zoom), (int)(item.getPixelHeight()*zoom));
			
		}

	}

	public void setTexture(TextureListItem textureListItem) {
		if(textureListItem != null)	currentMaterial = textureListItem;
		repaint();
	}

	public void setMode(EditModes mode){
		this.mode = mode;
		interruptEdit();
		repaint();
	}

	public EditModes getMode(){
		return mode;
	}

	@Override
	public void mouseClicked(MouseEvent e) {}

	@Override
	public void mousePressed(MouseEvent e) {

		xStart = e.getX();
		yStart = e.getY();

		if(mapData == null) return;
		
		if(e.getButton() == 2){
			panning = true;
			repaint();
			return;
		}

		if(e.getButton() == 3){
			interruptEdit();
		} 

		if(e.getButton() == 1){
			
			if(mode.equals(EditModes.createPoly)){
				if(activePolygon == null){
					activeVertex = snapVertexToGrid(xStart, yStart);
					activePolygon = mapData.getClickedPolygon(activeVertex);
					if(activePolygon == null){
						activePolygon = mapData.newPolygon(activeVertex, currentMaterial);
					} else {
						activeVertex = activePolygon.getVertex(activeVertex);
						mode = EditModes.moveVertex;
					}
					editing = true;
				}
			} else if(mode.equals(EditModes.createDoor)){
				
				if(activeDoor == null){
					activeVertex = snapVertexToGrid(xStart, yStart);
					activeDoor = mapData.newDoor(snapVertexToGrid(xStart, yStart), activeVertex, currentMaterial);
					editing = true;
				}
				
			} else if(mode.equals(EditModes.addVertex)){

				if(activePolygon == null){
					activeVertex = snapVertexToGrid(xStart, yStart);
					activePolygon = mapData.getClickedPolygon(activeVertex);
				} else {
					activeVertex = translateToMap(xStart, yStart);
				}
				
				float shortestDistance = Float.MAX_VALUE;

				for(FloorPolygon p : mapData.getAllFloorPolygons()){

					ArrayList<Vertex> vertices = p.getVertices();

					float d = activeVertex.getDistanceToLineSegment(vertices.get(0), vertices.get(vertices.size()-1));

					int index = 0;

					if(d < shortestDistance){
						shortestDistance = d;
						activePolygon = p;
					}

					for(int i=1; i < vertices.size() ; i++) {
						d = activeVertex.getDistanceToLineSegment(vertices.get(i-1), vertices.get(i));
						if(d < shortestDistance){
							shortestDistance = d;
							activePolygon = p;
							index = i;
						}
					}

					if(activePolygon != null){
						activePolygon.addVertexOnIndex(activeVertex, index);
					}

				}

			}

		}

		repaint();

	}

	@Override
	public void mouseReleased(MouseEvent e) {
		
		panning = false;

		if(!editing) return;
		if(mapData == null) return;

		if(mode.equals(EditModes.createPoly)){

			activeVertex = snapVertexToGrid(e.getX(), e.getY());

			if(activePolygon.hasVertex(activeVertex)){
				interruptEdit();
			} else {
				activePolygon.newVertex(activeVertex);
				System.out.println("Added vertex");
			}

		} else if(mode.equals(EditModes.moveVertex)){

			interruptEdit();
			mode = EditModes.createPoly;

		} else if(mode.equals(EditModes.addVertex)){
			interruptEdit();
		} else if(mode.equals(EditModes.createDoor)){
			activeVertex.moveTo(snapVertexToGrid(e.getX(), e.getY()));
			interruptEdit();
		}

		repaint();

	}

	@Override
	public void mouseEntered(MouseEvent e) {
		// TODO Auto-generated method stub

	}

	@Override
	public void mouseExited(MouseEvent e) {
		// TODO Auto-generated method stub

	}

	@Override
	public void mouseDragged(MouseEvent e) {

		xCurrent = e.getX();
		yCurrent = e.getY();

		if(mapData == null) return;
		
		if(panning){
			xShift += (xCurrent - xStart);
			yShift += (yCurrent - yStart);
			xStart = e.getX();
			yStart = e.getY();
		}

		if((mode.equals(EditModes.moveVertex) || mode.equals(EditModes.addVertex) || mode.equals(EditModes.createDoor)) && activeVertex != null){
			activeVertex.moveTo(snapVertexToGrid(e.getX(), e.getY()));
		}

		repaint();
	}

	@Override
	public void mouseMoved(MouseEvent e) {
		xCurrent = e.getX();
		yCurrent = e.getY();

		if(mode.equals(EditModes.addVertex)){
			activeVertex = new Vertex(xStart, yStart);
		}
	}

	public void setGridSnap(int s){
		gridSnap = s;
		repaint();
	}

	public void interruptEdit(){

		if(activePolygon != null){
			if(activePolygon.getVertices().size() < 3){
				activePolygon.dispose();
			} else {
				activePolygon.complete();
			}
			activePolygon = null;
		}
		
		if(activeDoor != null){
			activeDoor.complete();
			activeDoor = null;
		}

		if(activeVertex != null){
			activeVertex = null;
		}

		editing = false;

		if(mapData != null)	mapData.cleanup();

		repaint();

	}

	@Override
	public void mouseWheelMoved(MouseWheelEvent e) {
		System.out.println(e.getPreciseWheelRotation());
		zoom += e.getPreciseWheelRotation()*0.1f;
		if(zoom < 0.25f){ zoom = 0.25f; } else if(zoom > 3f) zoom = 3f;
		repaint();
	}

}
