package org.psywerx.car;

public interface DataListener {
	public int dataStream = 0;
	public void updateData(float[] data);
}
