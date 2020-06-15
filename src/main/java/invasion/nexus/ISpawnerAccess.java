package invasion.nexus;

public interface ISpawnerAccess
{
	boolean attemptSpawn(EntityConstruct paramEntityConstruct, int paramInt1, int paramInt2);

	int getNumberOfPointsInRange(int paramInt1, int paramInt2, SpawnType paramSpawnType);

	void sendSpawnAlert(String paramString);

	void noSpawnPointNotice();
}