
package graph.objects;

import java.awt.geom.Point2D;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import nio.base.IPosInfo;

/**
 * Vertex alebo vrchol v grafe.
 * 
 * @author Lukas Sekerak
 */
@SuppressWarnings("serial")
public abstract class BaseVertex extends Point2D
		implements
		Serializable,
		IPosInfo
{
	// Zabera 40bytov
	private char	key;
	protected int	pos	= -1;
	protected int	valueHash;
	protected int[]	incoming;
	protected int[]	outcoming;
	protected float	x;
	protected float	y;
	protected char	size;
	private boolean	zvyrazneny; // neskor moze byt Color
	private boolean	packed;	// zabaleny
	private boolean	visible;

	public BaseVertex() {
		visible = true;
	}

	public Collection<Integer> getIncomingC() {
		if (incoming == null) return Collections.unmodifiableCollection(new ArrayList<Integer>(0));
		ArrayList<Integer> kolekcia = new ArrayList<Integer>(incoming.length);
		for (int i = 0; i < incoming.length; i++)
			kolekcia.add(new Integer(incoming[i]));
		return Collections.unmodifiableCollection(kolekcia);
	}
	public Collection<Integer> getOutcomingC() {
		if (outcoming == null) return Collections.unmodifiableCollection(new ArrayList<Integer>(0));
		ArrayList<Integer> kolekcia = new ArrayList<Integer>(outcoming.length);
		for (int i = 0; i < outcoming.length; i++)
			kolekcia.add(new Integer(outcoming[i]));
		return Collections.unmodifiableCollection(kolekcia);
	}
	public int[] getIncoming() {
		return outcoming;
	}
	public int[] getOutcoming() {
		return outcoming;
	}
	public double getX() {
		return (double) x;
	}
	public double getY() {
		return (double) y;
	}
	public long getPos() {
		return pos;
	}
	public int getSize() {
		return size;
	}
	public void setPos(long a) {
		pos = (int) a;
		long test = (int) a;
		if (test != a) { // Pretazenie
			throw new RuntimeException();
		}
	}
	public void setSize(int b) {
		size = (char) b;
		int test = (char) b;
		if (test != b) { // Pretazenie
			throw new RuntimeException();
		}
	}
	public void removeTarget(int edge) {
		if (outcoming == null) return;
		int[] newoutcoming = new int[outcoming.length - 1];
		int i, j;
		for (i = j = 0; j < outcoming.length; ++j) {
			if (edge != outcoming[j]) newoutcoming[i++] = outcoming[j];
		}
		outcoming = newoutcoming;
	}
	public void removeSource(int edge) {
		if (incoming == null) return;
		int[] newincoming = new int[incoming.length - 1];
		int i, j;
		for (i = j = 0; j < incoming.length; ++j) {
			if (edge != incoming[j]) newincoming[i++] = incoming[j];
		}
		incoming = newincoming;
	}
	public synchronized void AddOut(Integer edge) {
		if (outcoming == null) {
			outcoming = new int[1];
			outcoming[0] = edge;
		} else {
			outcoming = Arrays.copyOf(outcoming, outcoming.length + 1);
			outcoming[outcoming.length - 1] = edge;
		}
	}
	public synchronized void AddInc(Integer edge) {
		if (incoming == null) {
			incoming = new int[1];
			incoming[0] = edge;
		} else {
			incoming = Arrays.copyOf(incoming, incoming.length + 1);
			incoming[incoming.length - 1] = edge;
		}
	}
	public int getSusednychHran() {
		int size = 0;
		if (incoming != null) size += incoming.length;
		if (outcoming != null) size += outcoming.length;
		return size;
	}
	public abstract void setFPriority(int fpriority);
	public abstract int getFPriority();
	public int getPriority() {
		return getFPriority() * 10 + getSusednychHran();
	}
	/**
	 * hashCode( je to pretaze od Point2D. Bez toho performance
	 * klesne o 1000% a porovnavanie budu nepresne.
	 * 
	 * Aby kluce s 0 indexom nemali vsetke rovnake indexi
	 * (this.key + 1) * valueHash
	 */
	@Override
	public int hashCode() {
		return pos;
	}
	/**
	 * Porovnaj 2 Vertexi, spomaluje to nacitavanie zo suboru ale musi to tu byt
	 * Aby to bolo spravne !! Preto tento objekt je dedeny od Pointu a od neho
	 * dedi veci.
	 * 
	 * @param o
	 * @return
	 */
	@Override
	public boolean equals(Object obj) {
		if (obj.hashCode() != hashCode()) return false;
		return getKey() == ((BaseVertex) obj).getKey();
	}

	@Override
	public String toString() {
		return (int) getKey() + "=>" + pos + "_" + (int) size + " (" + x + ","
				+ y + ")";
	}

	public void setLocationNoUpdate(double x, double y) {
		this.x = (float) x;
		this.y = (float) y;
	}
	public void setZvyrazneny(boolean zvyrazneny) {
		this.zvyrazneny = zvyrazneny;
	}
	public boolean isZvyrazneny() {
		return zvyrazneny;
	}
	/*
	 * public void setKey(char key) {
	 * this.key = key;
	 * }
	 */
	public char getKey() {
		return key;
	}
	public int getValueHash() {
		return valueHash;
	}
	public void setKey(char key) {
		this.key = key;
	}
	public void setValueHash(int valueHash) {
		this.valueHash = valueHash;
	}
	public void setSize(char size) {
		this.size = size;
	}
	public boolean isPacked() {
		return packed;
	}
	public void setPacked(boolean packed) {
		this.packed = packed;
	}
	public boolean isVisible() {
		return visible;
	}
	public void setVisible(boolean visible) {
		this.visible = visible;
	}
}
