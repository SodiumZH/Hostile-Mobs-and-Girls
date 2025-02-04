package com.github.mechalopa.hmag.world.entity;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.util.RandomSource;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.SpawnGroupData;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.monster.WitherSkeleton;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraftforge.network.NetworkHooks;

public class WitherSkeletonGirlEntity extends WitherSkeleton
{
	public WitherSkeletonGirlEntity(EntityType<? extends WitherSkeletonGirlEntity> type, Level level)
	{
		super(type, level);
		this.xpReward = 10;
	}

	public static AttributeSupplier.Builder createAttributes()
	{
		return Monster.createMonsterAttributes()
				.add(Attributes.MAX_HEALTH, 36.0D)
				.add(Attributes.MOVEMENT_SPEED, 0.26D)
				.add(Attributes.ATTACK_DAMAGE, 4.5D)
				.add(Attributes.ARMOR, 4.0D)
				.add(Attributes.KNOCKBACK_RESISTANCE, 0.25D);
	}

	@Override
	protected void populateDefaultEquipmentSlots(RandomSource rand, DifficultyInstance difficulty)
	{
		super.populateDefaultEquipmentSlots(rand, difficulty);

		if (rand.nextFloat() < 0.05F)
		{
			this.setItemSlot(EquipmentSlot.MAINHAND, new ItemStack(Items.BOW));
		}
	}

	@Nullable
	@Override
	public SpawnGroupData finalizeSpawn(ServerLevelAccessor levelAccessor, DifficultyInstance difficulty, MobSpawnType spawnType, @Nullable SpawnGroupData spawnData, @Nullable CompoundTag dataTag)
	{
		SpawnGroupData spawngroupdata = super.finalizeSpawn(levelAccessor, difficulty, spawnType, spawnData, dataTag);
		this.getAttribute(Attributes.ATTACK_DAMAGE).setBaseValue(4.5D);
		this.reassessWeaponGoal();
		return spawngroupdata;
	}

	@Nonnull
	@Override
	public Packet<?> getAddEntityPacket()
	{
		return NetworkHooks.getEntitySpawningPacket(this);
	}
}