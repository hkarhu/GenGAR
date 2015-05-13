package gengar.items;

import fi.conf.tabare.TrackableObject;

public abstract class VirtualItem {
	
	private double x, y, z, r;
	private double xShift, yShift, zShift, rShift;
	private TrackableObject boundRealObject;
	
	public abstract void glDraw(long time);

	public TrackableObject getBoundObject() {
		return boundRealObject;
	}

	public void setBoundObject(TrackableObject boundRealObject) {
		this.boundRealObject = boundRealObject;
	}
	
}
