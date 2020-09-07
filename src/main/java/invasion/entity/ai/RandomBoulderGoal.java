package invasion.entity.ai;

import invasion.entity.monster.EntityIMThrower;
import invasion.nexus.Nexus;
import net.minecraft.entity.ai.goal.Goal;


public class RandomBoulderGoal extends Goal {

    private final EntityIMThrower thrower;
    private int randomAmmo;
    private int timer = 180;

    public RandomBoulderGoal(EntityIMThrower entity, int ammo) {
        thrower = entity;
        randomAmmo = ammo;
    }

    @Override
    public boolean shouldExecute() {
        if ((thrower.isNexusBound()) && (randomAmmo > 0) && (thrower.canThrow())) {

            return --this.timer <= 0;
        }
        return false;
    }

    @Override
    public void startExecuting() {
        randomAmmo -= 1;
        timer = 240;
        Nexus nexus = thrower.getNexus();
        int d = (int) (thrower.findDistanceToNexus() * 0.37D);
        if (d == 0) d = 1;
        double d0 = nexus.getPos().getX() - d + this.thrower.getRNG().nextInt(2 * d);
        double d1 = nexus.getPos().getY() - 5 + this.thrower.getRNG().nextInt(10);
        double d2 = nexus.getPos().getZ() - d + this.thrower.getRNG().nextInt(2 * d);
        thrower.throwBoulder(d0, d1, d2);
    }

}