package invasion.util;

import invasion.Invasion;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;


public class RandomSelectionPool<T> implements ISelect<T> {
    private final List<Pair<ISelect<T>, Float>> pool;
    private float totalWeight;
    private final Random rand;

    public RandomSelectionPool() {
        this.pool = new ArrayList();
        this.totalWeight = 0.0F;
        this.rand = new Random();
    }

    public void addEntry(T entry, float weight) {
        SingleSelection selection = new SingleSelection(entry);
        this.addEntry(selection, weight);
    }

    public void addEntry(ISelect<T> entry, float weight) {
        this.pool.add(new Pair(entry, Float.valueOf(weight)));
        this.totalWeight += weight;
    }

    @Override
    public T selectNext() {
        float r = this.rand.nextFloat() * this.totalWeight;
        for (Pair entry : this.pool) {
            if (r < ((Float) entry.getVal2()).floatValue()) {
                return (T) ((ISelect) entry.getVal1()).selectNext();
            }

            r -= ((Float) entry.getVal2()).floatValue();
        }

        if (this.pool.size() > 0) {
            Invasion.logger.warn("RandomSelectionPool invalid setup or rounding error. Failing safe.");
            return (T) ((ISelect) ((Pair) this.pool.get(0)).getVal1()).selectNext();
        }
        return null;
    }

    @Override
    public RandomSelectionPool<T> clone() {
        RandomSelectionPool clone = new RandomSelectionPool();
        for (Pair entry : this.pool) {
            clone.addEntry((ISelect) entry.getVal1(), ((Float) entry.getVal2()).floatValue());
        }

        return clone;
    }

    @Override
    public void reset() {
    }

    @Override
    public String toString() {
        String s = "RandomSelectionPool@" + Integer.toHexString(this.hashCode()) + "#Size=" + this.pool.size();
        for (int i = 0; i < this.pool.size(); i++) {
            s = s + "\n\tEntry " + i + "   Weight: " + ((Pair) this.pool.get(i)).getVal2();
            s = s + "\n\t" + ((Pair) this.pool.get(i)).getVal1().toString();
        }
        return s;
    }
}