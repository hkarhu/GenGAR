package gengar.editor;
import java.io.Serializable;
import java.util.Vector;

public class Map implements Serializable{
	
	private Vector<Vector<FloorPolygon>> polygonData;
	private Vector<Vector<Item>> itemData;
	private Vector<Vector<Door>> doorData;
	private int floorNumber = 0;
	
	public Map() {
		polygonData = new Vector<Vector<FloorPolygon>>();
		polygonData.add(new Vector<FloorPolygon>());
		doorData = new Vector<Vector<Door>>();
		doorData.add(new Vector<Door>());
		itemData = new Vector<Vector<Item>>();
		itemData.add(new Vector<Item>());
	}
	
	public void addFloor(){
		polygonData.add(new Vector<FloorPolygon>());
		floorNumber = polygonData.size()-1;
	}
	
	public void setFloor(int n){
		if(polygonData.size() <= n){
			floorNumber = n;
		} else {
			System.out.println("Cannot set floor " + n + ", largest floor n. is " + polygonData.size());
		}
	}
	
	public int getFloor(){
		return floorNumber;
	}
	
	public void addVertexToPolygon(int id, Vertex p){
		polygonData.get(floorNumber).get(id).newVertex(p);
	}

	public Vector<FloorPolygon> getAllFloorPolygons() {
		return polygonData.get(floorNumber);
	}
	
	public FloorPolygon newPolygon(Vertex initialVertex, TextureListItem material) {
		FloorPolygon p = new FloorPolygon(floorNumber, material);
		polygonData.get(floorNumber).add(p);
		p.newVertex(initialVertex);
		return p;
	}

	public void cleanup() {
		for(int i=0; i < polygonData.get(floorNumber).size(); i++){
			if(polygonData.get(floorNumber).get(i).isDisposable()){
				polygonData.get(floorNumber).remove(i);
			}
		}
		
		for(int i=0; i < doorData.get(floorNumber).size(); i++){
			if(doorData.get(floorNumber).get(i).isDisposable()){
				doorData.get(floorNumber).remove(i);
			}
		}
		
		for(int i=0; i < itemData.get(floorNumber).size(); i++){
			if(itemData.get(floorNumber).get(i).isDisposable()){
				itemData.get(floorNumber).remove(i);
			}
		}
	}

	public FloorPolygon getClickedPolygon(Vertex vertex) {
		for(FloorPolygon p : polygonData.get(floorNumber)){
			if(p.hasVertex(vertex)) return p;
		}
		return null;
	}

	public FloorPolygon getPolygon(int id) {
		return polygonData.get(floorNumber).get(id);
	}

	public Vector<Door> getAllDoors() {
		return doorData.get(floorNumber);
	}
	
	public Door newDoor(Vertex initialVertex1, Vertex initialVertex2, TextureListItem material){
		Door d = new Door(initialVertex1, initialVertex2, material);
		doorData.get(floorNumber).add(d);
		return d;
	}

	public Vector<Item> getAllItems() {
		return itemData.get(floorNumber);
	}

}
