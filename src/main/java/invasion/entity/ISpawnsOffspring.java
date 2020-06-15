package invasion.entity;

import net.minecraft.entity.Entity;


public abstract interface ISpawnsOffspring
{
	public abstract Entity[] getOffspring(Entity paramEntity);
}