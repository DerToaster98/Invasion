package invmod.entity.ally;

import invmod.entity.EntityIMLiving;
import invmod.entity.ai.navigator.PathNode;
import invmod.entity.ai.navigator.PathfinderIM;
import invmod.tileentity.TileEntityNexus;

import java.util.List;

import net.minecraft.entity.ai.EntityAISwimming;
import net.minecraft.entity.passive.EntityVillager;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.registry.IForgeRegistry;
import net.minecraftforge.fml.common.registry.VillagerRegistry;
import net.minecraftforge.fml.common.registry.VillagerRegistry.VillagerProfession;
import scala.actors.threadpool.Arrays;

public class EntityIMVillager extends EntityIMLiving {
	
	private boolean nexusBound = false;
	
	private VillagerProfession originalProfession;

	public EntityIMVillager(World worldIn){
		this(worldIn, null);
	}
	
	public EntityIMVillager(World worldIn, EntityVillager villager){
		super(worldIn);
		if(villager != null){
			this.originalProfession = villager.getProfessionForge();
		} else {
			List<VillagerProfession> professions = VillagerRegistry.instance().getRegistry().getValues();
			this.originalProfession = professions.get(this.rand.nextInt(professions.size()));
		}
	}
	
	@Override
	protected void initEntityAI(){
		this.tasks.addTask(0, new EntityAISwimming(this));
	}

	@Override
	public void acquiredByNexus(TileEntityNexus nexus) {
		if (this.targetNexus == null) {
			this.targetNexus = nexus;
			this.nexusBound = true;
		}
	}

	@Override
	public float getBlockPathCost(PathNode prevNode, PathNode node, IBlockAccess terrainMap){
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public void getPathOptionsFromNode(IBlockAccess paramIBlockAccess, PathNode paramPathNode, PathfinderIM paramPathfinderIM){
		// TODO Auto-generated method stub
	}
	
	public static class IMVillageAssignment {
		
		public static final IForgeRegistry<VillagerProfession> REGISTRY = VillagerRegistry.instance().getRegistry();
		
		public static final IMVillageAssignment WARRIOR = new IMVillageAssignment("warrior",
				REGISTRY.getValue(new ResourceLocation("minecraft:butcher"))
		);
		
		public final String name;
		public final List<VillagerProfession> compatibleProfessions;
		
		public IMVillageAssignment(String name, VillagerProfession... compatibleProfessions){
			this.name = name.toLowerCase();
			this.compatibleProfessions = Arrays.asList(compatibleProfessions);
		}
		
	}

}
