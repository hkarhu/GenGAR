package gengar.editor;
import java.io.Serializable;
import java.util.ArrayList;

public class DataModel implements Serializable {
	
	private Map map;
	private ArrayList<Player> players;
	
	public DataModel() {
		map = new Map();
		players = new ArrayList<>();
	}

	public Map getMap() {
		return map;
	}
	
	public void setMap(Map map){
		this.map = map;
	}
	
}
