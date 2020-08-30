package invasion.nexus;

import org.apache.commons.lang3.tuple.Pair;

import java.util.Collection;
import java.util.LinkedList;
import java.util.Queue;
import java.util.function.Consumer;
import java.util.stream.Collectors;

public class Wave_ implements ISpawnCount {
    private final Queue<Pair<Integer, Consumer<Nexus>>> entries;
    private final int totalTime;
    private final int breakTime;
    private int timeInWave;
    private int spawnCount;

    public Wave_(int waveTotalTime, int waveBreakTime, Collection<Pair<Integer, Consumer<Nexus>>> entriesUnsorted) {
        /*
        Set the entries to a linked list that is sorted by spawn time (the 'key' of the pair).
        If the Consumer spawns entities (implements ISpawnCount), the total spawn count is increased by that amount.
         */
        entries = entriesUnsorted.stream().sorted().peek(entry -> {
            Consumer<Nexus> spawn = entry.getValue();
            if (spawn instanceof ISpawnCount) spawnCount += ((ISpawnCount) spawn).spawnCount();
        }).collect(Collectors.toCollection(LinkedList::new));
        totalTime = waveTotalTime;
        breakTime = waveBreakTime;
    }

    public void doSpawns(int elapsed, Nexus nexus) {
        timeInWave += elapsed;
        while (!entries.isEmpty() && entries.peek().getKey() <= timeInWave) {
            entries.remove().getValue().accept(nexus);
        }
    }

    public void skip(int skipToTime) {
        timeInWave = skipToTime;
        while (!entries.isEmpty() && entries.peek().getKey() <= timeInWave) {
            entries.remove();
        }
    }

    public boolean isOver() {
        return timeInWave > totalTime;
    }

    @Override
    public int spawnCount() {
        return spawnCount;
    }
}
