package invasion.nexus;

import invasion.Invasion;
import invasion.entity.EntityIMLiving;
import invasion.entity.monster.InvadingZombieEntity;
import net.minecraft.util.text.StringTextComponent;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class WaveSpawner implements ISpawnerAccess {
    private final int MAX_SPAWN_TRIES = 20;
    private final int NORMAL_SPAWN_HEIGHT = 30;
    private final int MIN_SPAWN_POINTS_TO_KEEP = 15;
    private final int MIN_SPAWN_POINTS_TO_KEEP_BELOW_HEIGHT_CUTOFF = 20;
    private final int HEIGHT_CUTOFF = 35;
    private final float SPAWN_POINT_CULL_RATE = 0.3F;
    private final Nexus nexus;
    private final MobBuilder mobBuilder;
    private SpawnPointContainer spawnPointContainer;
    private final Random rand;
    private Wave currentWave;
    private boolean active;
    private boolean waveComplete;
    private boolean spawnMode;
    private boolean debugMode;
    private int spawnRadius;
    private final int currentWaveNumber;
    private int successfulSpawns;
    private long elapsed;

    public WaveSpawner(Nexus nexus, int radius) {
        this.nexus = nexus;
        active = false;
        waveComplete = false;
        spawnMode = true;
        debugMode = true;
        spawnRadius = radius;
        currentWaveNumber = 1;
        elapsed = 0L;
        successfulSpawns = 0;
        rand = new Random();
        spawnPointContainer = new SpawnPointContainer();
        mobBuilder = new MobBuilder();
    }

    public long getElapsedTime() {
        return elapsed;
    }

    public void setRadius(int radius) {
        if (radius > 8) {
            spawnRadius = radius;
        }
    }

    public void beginNextWave(int waveNumber) throws WaveSpawnerException {
        beginNextWave(WaveBuilder.generateMainInvasionWave(waveNumber));
    }

    public void beginNextWave(Wave wave) throws WaveSpawnerException {
        if (!active) {
            generateSpawnPoints();
        } else if (debugMode) {
            Invasion.logger.debug("Successful spawns last wave: {}", successfulSpawns);
        }

        wave.resetWave();
        waveComplete = false;
        active = true;
        currentWave = wave;
        elapsed = 0L;
        successfulSpawns = 0;

        if (debugMode)
            Invasion.logger.debug("Defined mobs this wave: {}", getTotalDefinedMobsThisWave());
    }

    public void spawn(int elapsedMillis) throws WaveSpawnerException {
        elapsed += elapsedMillis;
        if ((waveComplete) || (!active)) {
            return;
        }

        if (spawnPointContainer.getNumberOfSpawnPoints(SpawnType.HUMANOID) < 10) {
            generateSpawnPoints();
            if (spawnPointContainer.getNumberOfSpawnPoints(SpawnType.HUMANOID) < 10) {
                throw new WaveSpawnerException("Not enough spawn points for type " + SpawnType.HUMANOID);
            }
        }
        currentWave.doNextSpawns(elapsedMillis, this);
        if (currentWave.isComplete())
            waveComplete = true;
    }

    public int resumeFromState(Wave wave, long elapsedTime, int radius) throws WaveSpawnerException {
        spawnRadius = radius;
        stop();
        beginNextWave(wave);

        setSpawnMode(false);
        int numberOfSpawns = 0;
        for (; elapsed < elapsedTime; elapsed += 100L) {
            numberOfSpawns += currentWave.doNextSpawns(100, this);
        }
        setSpawnMode(true);
        return numberOfSpawns;
    }

    public void resumeFromState(int waveNumber, long elapsedTime, int radius) throws WaveSpawnerException {
        spawnRadius = radius;
        stop();
        beginNextWave(waveNumber);

        setSpawnMode(false);
        for (; elapsed < elapsedTime; elapsed += 100L) {
            currentWave.doNextSpawns(100, this);
        }
        setSpawnMode(true);
    }

    public void stop() {
        active = false;
    }

    public boolean isActive() {
        return active;
    }

    public boolean isReady() {
        if ((!active) && (nexus != null)) {
            return nexus.getWorld() != null;
        }

        return false;
    }

    public boolean isWaveComplete() {
        return waveComplete;
    }

    public int getWaveDuration() {
        return currentWave.getWaveTotalTime();
    }

    public int getWaveRestTime() {
        return currentWave.getWaveBreakTime();
    }

    public int getSuccessfulSpawnsThisWave() {
        return successfulSpawns;
    }

    public int getTotalDefinedMobsThisWave() {
        return currentWave.getTotalMobAmount();
    }

    public void askForRespawn(EntityIMLiving entity) {
        if (spawnPointContainer.getNumberOfSpawnPoints(SpawnType.HUMANOID) > 10) {
            SpawnPoint spawnPoint = spawnPointContainer.getRandomSpawnPoint(SpawnType.HUMANOID);
            entity.setLocationAndAngles(spawnPoint.getPos().getX(), spawnPoint.getPos().getY(), spawnPoint.getPos().getZ(), 0.0F, 0.0F);
        }
    }

    @Override
    public void sendSpawnAlert(String message) {
        if (debugMode) {
            Invasion.logger.debug(message);
        }
        nexus.sendMessageToBoundPlayers(new StringTextComponent(message));
    }

    @Override
    public void noSpawnPointNotice() {
    }

    public void debugMode(boolean isOn) {
        debugMode = isOn;
    }

    @Override
    public int getNumberOfPointsInRange(int minAngle, int maxAngle, SpawnType type) {
        return spawnPointContainer.getNumberOfSpawnPoints(type, minAngle, maxAngle);
    }

    public void setSpawnMode(boolean flag) {
        spawnMode = flag;
    }

    public void giveSpawnPoints(SpawnPointContainer spawnPointContainer) {
        this.spawnPointContainer = spawnPointContainer;
    }

    @Override
    public boolean attemptSpawn(EntityConstruct mobConstruct, int minAngle, int maxAngle) {
        if (nexus.getWorld() == null) {
            if (spawnMode) {
                return false;
            }
        }
        EntityIMLiving mob = mobBuilder.createMobFromConstruct(mobConstruct, nexus.getWorld(), nexus);
        if (mob == null) {
            Invasion.logger.warn("Invalid entity construct");
            return false;
        }

        int spawnTries = Math.min(getNumberOfPointsInRange(minAngle, maxAngle, SpawnType.HUMANOID), 20);

        for (int j = 0; j < spawnTries; j++) {
            SpawnPoint spawnPoint;
            if (maxAngle - minAngle >= 360)
                spawnPoint = spawnPointContainer.getRandomSpawnPoint(SpawnType.HUMANOID);
            else {
                spawnPoint = spawnPointContainer.getRandomSpawnPoint(SpawnType.HUMANOID, minAngle, maxAngle);
            }
            if (spawnPoint == null) {
                return false;
            }
            if (!spawnMode) {
                successfulSpawns += 1;
                if (debugMode) {
                    Invasion.logger.debug("[Spawn] Time: {} Type: {} Coords: {}  θ{}  Specified: {} - {}", currentWave.getTimeInWave() / 1000, mob.toString(), spawnPoint.getPos().toString(), spawnPoint.getAngle(), minAngle, maxAngle);
                }

                return true;
            }
            // TODO: increasing y coordinate fixes underground spawning, but I should probably fix this elsewhere.
            //DarthXenon: Appears to be fixed.
            mob.setLocationAndAngles(spawnPoint.getPos().getX(), spawnPoint.getPos().getY(), spawnPoint.getPos().getZ(), 0.0F, 0.0F);
            if (mob.getCanSpawnHere()) {
                successfulSpawns += 1;
                nexus.getWorld().spawnEntity(mob);
                if (debugMode) {
                    Invasion.logger.debug("[Spawn] Time: {}  Type: {}  Coords: {}  θ{}  Specified: {} - {}", currentWave.getTimeInWave() / 1000, mob.toString(), mob.getPosition().toString(), spawnPoint.getAngle(), minAngle, maxAngle);
                }

                return true;
            }
        }
        Invasion.logger.warn("Could not find valid spawn for '{}' after {} tries", EntityList.getEntityString(mob), spawnTries);
        return false;
    }

    private void generateSpawnPoints() {
        if (nexus.getWorld() == null) return;

        InvadingZombieEntity zombie = new InvadingZombieEntity(nexus.getWorld(), nexus);
        List<SpawnPoint> spawnPoints = new ArrayList<>();
        int x = nexus.getPos().getX();
        int y = nexus.getPos().getY();
        int z = nexus.getPos().getZ();
        for (int vertical = 0; vertical < 128; vertical = vertical > 0 ? vertical * -1 : vertical * -1 + 1) {
            if (y + vertical <= 252) {
                for (int i = 0; i <= spawnRadius * 0.7D + 1.0D; i++) {
                    int j = (int) Math.round(spawnRadius * Math.cos(Math.asin(i / spawnRadius)));

                    addValidSpawn(zombie, spawnPoints, x + i, y + vertical, z + j);
                    addValidSpawn(zombie, spawnPoints, x + j, y + vertical, z + i);

                    addValidSpawn(zombie, spawnPoints, x + i, y + vertical, z - j);
                    addValidSpawn(zombie, spawnPoints, x + j, y + vertical, z - i);

                    addValidSpawn(zombie, spawnPoints, x - i, y + vertical, z + j);
                    addValidSpawn(zombie, spawnPoints, x - j, y + vertical, z + i);

                    addValidSpawn(zombie, spawnPoints, x - i, y + vertical, z - j);
                    addValidSpawn(zombie, spawnPoints, x - j, y + vertical, z - i);
                }

            }

        }

        if (spawnPoints.size() > 15) {
            int i;
            int amountToRemove = (int) ((spawnPoints.size() - 15) * 0.3F);
            for (i = spawnPoints.size() - 1; i >= spawnPoints.size() - amountToRemove; i--) {
                if (Math.abs(spawnPoints.get(i).getPos().getY() - y) < 30) {
                    break;
                }
            }
            for (; i >= 20; i--) {
                SpawnPoint spawnPoint = spawnPoints.get(i);
                if (spawnPoint.getPos().getY() - y <= 35) {
                    spawnPointContainer.addSpawnPointXZ(spawnPoint);
                }

            }
            for (; i >= 0; i--) {
                spawnPointContainer.addSpawnPointXZ(spawnPoints.get(i));
            }

        }

        Invasion.logger.debug("Num. Spawn Points: {}", spawnPointContainer.getNumberOfSpawnPoints(SpawnType.HUMANOID));
    }

    private void addValidSpawn(EntityIMLiving entity, List<SpawnPoint> spawnPoints, int x, int y, int z) {
        entity.setLocationAndAngles(x, y, z, 0.0F, 0.0F);
        if (entity.getCanSpawnHere()) {
            int angle = (int) (Math.atan2(nexus.getPos().getZ() - z, nexus.getPos().getX() - x) * 180.0D / Math.PI);
            spawnPoints.add(new SpawnPoint(x, y, z, angle, SpawnType.HUMANOID));
        }
    }

    /* UNUSED
    private void checkAddSpawn(EntityIMLiving entity, int x, int y, int z) {
        entity.setLocationAndAngles(x, y, z, 0.0F, 0.0F);
        if (entity.getCanSpawnHere()) {
            int angle = (int) (Math.atan2(nexus.getPos().getZ() - z, nexus.getPos().getX() - x) * 180.0D / Math.PI);
            int angle = (int) (Math.atan2(nexus.getPos().getZ() - z, nexus.getPos().getX() - x) * 180.0D / Math.PI);
            spawnPointContainer.addSpawnPointXZ(new SpawnPoint(x, y, z, angle, SpawnType.HUMANOID));
        }
    }

     */
}