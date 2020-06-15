package invasion.nexus;

import invasion.Invasion;
import invasion.util.ISelect;

import java.util.*;

public class WaveEntry {
    private final int timeBegin;
    private final int timeEnd;
    private final int amount;
    private final int granularity;
    private final ISelect<IEntityIMPattern> mobPool;
    private final List<EntityConstruct> spawnList;
    private final Map<Integer, String> alerts;
    private int amountQueued;
    private int elapsed;
    private int toNextSpawn;
    private int minAngle;
    private int maxAngle;
    private int minPointsInRange;
    private int nextAlert;

    public WaveEntry(int timeBegin, int timeEnd, int amount, int granularity, ISelect<IEntityIMPattern> mobPool) {
        this(timeBegin, timeEnd, amount, granularity, mobPool, -180, 180, 1);
    }

    public WaveEntry(int timeBegin, int timeEnd, int amount, int granularity, ISelect<IEntityIMPattern> mobPool, int angleRange, int minPointsInRange) {
        this(timeBegin, timeEnd, amount, granularity, mobPool, 0, 0, minPointsInRange);
        minAngle = (new Random().nextInt(360) - 180);
        maxAngle = (minAngle + angleRange);
        while (maxAngle > 180)
            maxAngle -= 360;
    }

    public WaveEntry(int timeBegin, int timeEnd, int amount, int granularity, ISelect<IEntityIMPattern> mobPool, int minAngle, int maxAngle, int minPointsInRange) {
        spawnList = new ArrayList<>();
        alerts = new HashMap<>();
        this.timeBegin = timeBegin;
        this.timeEnd = timeEnd;
        this.amount = amount;
        this.granularity = granularity;
        this.mobPool = mobPool;
        this.minAngle = minAngle;
        this.maxAngle = maxAngle;
        this.minPointsInRange = minPointsInRange;
        amountQueued = 0;
        elapsed = 0;
        toNextSpawn = 0;
        nextAlert = 2147483647;
    }

    public int doNextSpawns(int elapsedMillis, ISpawnerAccess spawner) {
        toNextSpawn -= elapsedMillis;
        if (nextAlert <= elapsed - toNextSpawn) {
            sendNextAlert(spawner);
        }

        if (toNextSpawn <= 0) {
            elapsed += granularity;
            toNextSpawn += granularity;
            if (toNextSpawn < 0) {
                elapsed -= toNextSpawn;
                toNextSpawn = 0;
            }

            int amountToSpawn = Math.round(amount * elapsed / (timeEnd - timeBegin)) - amountQueued;
            if (amountToSpawn > 0) {
                if (amountToSpawn + amountQueued > amount) {
                    amountToSpawn = amount - amountQueued;
                }
                while (amountToSpawn > 0) {
                    IEntityIMPattern pattern = mobPool.selectNext();
                    if (pattern != null) {
                        EntityConstruct mobConstruct = pattern.generateEntityConstruct(minAngle, maxAngle);
                        if (mobConstruct != null) {
                            amountToSpawn--;
                            amountQueued += 1;
                            spawnList.add(mobConstruct);
                        }
                    } else {
                        Invasion.logger.warn("A selection pool in wave entry {} returned empty", toString());
                        Invasion.logger.warn("Pool: {}", mobPool.toString());
                    }
                }
            }
        }

        if (spawnList.size() > 0) {
            int numberOfSpawns = 0;
            if (spawner.getNumberOfPointsInRange(minAngle, maxAngle, SpawnType.HUMANOID) >= minPointsInRange) {
                for (int i = spawnList.size() - 1; i >= 0; i--) {
                    if (spawner.attemptSpawn(spawnList.get(i), minAngle, maxAngle)) {
                        numberOfSpawns++;
                        spawnList.remove(i);
                    }
                }
            } else {
                reviseSpawnAngles(spawner);
            }
            return numberOfSpawns;
        }
        return 0;
    }

    public void resetToBeginning() {
        elapsed = 0;
        amountQueued = 0;
        mobPool.reset();
    }

    public void setToTime(int millis) {
        elapsed = millis;
    }

    public int getTimeBegin() {
        return timeBegin;
    }

    public int getTimeEnd() {
        return timeEnd;
    }

    public int getAmount() {
        return amount;
    }

    public int getGranularity() {
        return granularity;
    }

    public void addAlert(String message, int timeElapsed) {
        alerts.put(timeElapsed, message);
        if (timeElapsed < nextAlert)
            nextAlert = timeElapsed;
    }

    @Override
    public String toString() {
        return "WaveEntry@" + Integer.toHexString(hashCode()) + "#time=" + timeBegin + "-" + timeEnd + "#amount=" + amount;
    }

    private void sendNextAlert(ISpawnerAccess spawner) {
        String message = alerts.remove(nextAlert);
        if (message != null) {
            spawner.sendSpawnAlert(message);
        }
        nextAlert = 2147483647;
        if (alerts.size() > 0) {
            for (Integer key : alerts.keySet()) {
                if (key < nextAlert)
                    nextAlert = key;
            }
        }
    }

    private void reviseSpawnAngles(ISpawnerAccess spawner) {
        int angleRange = maxAngle - minAngle;
        while (angleRange < 0)
            angleRange += 360;
        if (angleRange == 0) {
            angleRange = 360;
        }
        List<Integer> validAngles = new ArrayList<>();

        for (int angle = -180; angle < 180; angle += angleRange) {
            int nextAngle = angle + angleRange;
            if (nextAngle >= 180)
                nextAngle -= 360;
            if (spawner.getNumberOfPointsInRange(angle, nextAngle, SpawnType.HUMANOID) >= minPointsInRange) {
                validAngles.add(angle);
            }
        }
        if (validAngles.size() > 0) {
            minAngle = validAngles.get(new Random().nextInt(validAngles.size()));
            maxAngle = (minAngle + angleRange);
            while (maxAngle >= 180) {
                maxAngle -= 360;
            }
        }

        if (minPointsInRange > 1) {
            Invasion.logger.info("Can't find a direction with enough spawn points: {}. Lowering requirement.", minPointsInRange);

            minPointsInRange = 1;
        } else if (maxAngle - minAngle < 360) {
            Invasion.logger.info("Can't find a direction with enough spawn points: {}. Switching to 360 degree mode for this entry", minPointsInRange);

            minAngle = -180;
            maxAngle = 180;
        } else {
            Invasion.logger.warn("Wave entry cannot find a single spawn point");
            spawner.noSpawnPointNotice();
        }
    }
}