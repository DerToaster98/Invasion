package invasion;

import net.minecraft.entity.Entity;


public interface SparrowAPI {

    boolean isStupidToAttack();

    boolean doNotVaporize();

    boolean isPredator();

    boolean isHostile();

    boolean isPeaceful();

    boolean isPrey();

    boolean isNeutral();

    boolean isUnkillable();

    boolean isThreatTo(Entity paramEntity);

    boolean isFriendOf(Entity paramEntity);

    boolean isNPC();

    int isPet();

    Entity getPetOwner();

    String getName();

    Entity getAttackingTarget();

    float getSize();

    String getSpecies();

    int getTier();

    int getGender();

    String customStringAndResponse(String paramString);

    String getSimplyID();

}