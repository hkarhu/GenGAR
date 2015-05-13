package gengar.items;


public class HexGrid extends VirtualItem {
	
	public static final float HEX_DETECTION_COVERAGE = 1.1f;
	public static float HEX_SIZE = 0.1638f;
	public static float HEX_LINE_WIDTH = 0.5f;
	
	public enum HexStyle { none, red, green, blue, yellow, cyan, magenta, point, hot, active, danger }
	
	private HexStyle[][] styles;
	
	public HexGrid(int width, int height) {
		styles = new HexStyle[width][height];
	}

	@Override
	public void glDraw(long time) {
		// TODO Auto-generated method stub
	}
	
}
