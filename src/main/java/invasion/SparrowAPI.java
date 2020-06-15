package invasion;

import net.minecraft.entity.Entity;


public interface SparrowAPI
{

	public boolean isStupidToAttack();

	public boolean doNotVaporize();

	public boolean isPredator();

	public boolean isHostile();

	public boolean isPeaceful();

	public boolean isPrey();

	public boolean isNeutral();

	public boolean isUnkillable();

	public boolean isThreatTo(Entity paramEntity);

	public boolean isFriendOf(Entity paramEntity);

	public boolean isNPC();

	public int isPet();

	public Entity getPetOwner();

	public String getName();

	public Entity getAttackingTarget();

	public float getSize();

	public String getSpecies();

	public int getTier();

	public int getGender();

	public String customStringAndResponse(String paramString);

	public String getSimplyID();

}