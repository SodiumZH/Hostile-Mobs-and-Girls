package com.github.mechalopa.hmag.block;

import net.minecraft.core.BlockPos;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.block.SoulSandBlock;
import net.minecraft.world.level.block.state.BlockState;

public class ThornSandBlock extends SoulSandBlock
{
	public ThornSandBlock(AbstractBlock.Properties properties)
	{
		super(properties);
	}

	@Override
	public void entityInside(BlockState state, World worldIn, BlockPos pos, Entity entityIn)
	{
		if (entityIn != null && entityIn instanceof LivingEntity)
		{
			entityIn.hurt(DamageSource.CACTUS, 1.0F);
		}
	}
}