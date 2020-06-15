package invasion.nexus;

import invasion.Invasion;
import invasion.entity.monster.EntityIMBurrower;
import invasion.entity.monster.MoulderingCreeperEntity;
import invasion.entity.monster.ImpEntity;
import invasion.entity.monster.InvadingEntity;
import invasion.entity.monster.PigEngyEntity;
import invasion.entity.monster.EntityIMSkeleton;
import invasion.entity.monster.EntityIMSpider;
import invasion.entity.monster.EntityIMThrower;
import invasion.entity.monster.InvadingZombieEntity;
import invasion.entity.monster.EntityIMZombiePigman;
import net.minecraft.world.World;


public class MobBuilder
{

	public InvadingEntity createMobFromConstruct(EntityConstruct mobConstruct, World world, Nexus nexus)
	{
		InvadingEntity mob = null;
		switch (mobConstruct.getMobType())
		{
			case ZOMBIE:
				InvadingZombieEntity zombie = new InvadingZombieEntity(world, nexus);
				zombie.setTexture(mobConstruct.getTexture());
				zombie.setFlavour(mobConstruct.getFlavour());
				zombie.setTier(mobConstruct.getTier());
				mob = zombie;
				break;
			case ZOMBIEPIGMAN:
				EntityIMZombiePigman zombiePigman = new EntityIMZombiePigman(world, nexus);
				zombiePigman.setTexture(mobConstruct.getTexture());
				zombiePigman.setTier(mobConstruct.getTier());
				mob = zombiePigman;
				break;
			case SPIDER:
				EntityIMSpider spider = new EntityIMSpider(world, nexus);
				spider.setTexture(mobConstruct.getTexture());
				spider.setFlavour(mobConstruct.getFlavour());
				spider.setTier(mobConstruct.getTier());
				mob = spider;
				break;
			case SKELETON:
				mob = new EntityIMSkeleton(world, nexus);
				break;
			case PIG_ENGINEER:
				mob = new PigEngyEntity(world, nexus);
				break;
			case THROWER:
				EntityIMThrower thrower = new EntityIMThrower(world, nexus);
				thrower.setTexture(mobConstruct.getTier());
				thrower.setTier(mobConstruct.getTier());
				mob = thrower;
				break;
			case BURROWER:
				mob = new EntityIMBurrower(world, nexus);
				break;
			case CREEPER:
				mob = new MoulderingCreeperEntity(world, nexus);
				break;
			case IMP:
				mob = new ImpEntity(world, nexus);
				break;
			default:
				Invasion.logger.warn("Missing mob type in MobBuilder: {}", mobConstruct.getMobType());
		}

		return mob;
	}

}