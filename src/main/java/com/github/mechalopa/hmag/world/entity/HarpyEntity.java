package com.github.mechalopa.hmag.world.entity;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.github.mechalopa.hmag.ModConfigs;
import com.github.mechalopa.hmag.registry.ModSoundEvents;
import com.github.mechalopa.hmag.util.ModTags;
import com.github.mechalopa.hmag.world.entity.ai.goal.LeapAtTargetGoal2;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EntityDimensions;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.Pose;
import net.minecraft.world.entity.SpawnGroupData;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.FloatGoal;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import net.minecraft.world.entity.ai.goal.MeleeAttackGoal;
import net.minecraft.world.entity.ai.goal.RandomLookAroundGoal;
import net.minecraft.world.entity.ai.goal.WaterAvoidingRandomStrollGoal;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.common.ForgeMod;
import net.minecraftforge.network.NetworkHooks;

public class HarpyEntity extends Monster
{
	private static final EntityDataAccessor<Integer> DATA_VARIANT_ID = SynchedEntityData.defineId(HarpyEntity.class, EntityDataSerializers.INT);
	private int animationTick;
	private int animationTickO;

	public HarpyEntity(EntityType<? extends HarpyEntity> type, Level level)
	{
		super(type, level);
		this.xpReward = 12;
	}

	@Override
	protected void defineSynchedData()
	{
		super.defineSynchedData();
		this.entityData.define(DATA_VARIANT_ID, 0);
	}

	@Override
	protected void registerGoals()
	{
		this.goalSelector.addGoal(1, new FloatGoal(this));
		this.goalSelector.addGoal(3, new LeapAtTargetGoal2(this, 0.39F, 0.45F, 7.0F, 24));
		this.goalSelector.addGoal(4, new MeleeAttackGoal(this, 1.0D, false));
		this.goalSelector.addGoal(5, new WaterAvoidingRandomStrollGoal(this, 1.0D));
		this.goalSelector.addGoal(6, new LookAtPlayerGoal(this, Player.class, 8.0F));
		this.goalSelector.addGoal(6, new RandomLookAroundGoal(this));
		this.targetSelector.addGoal(1, new HurtByTargetGoal(this));
		this.targetSelector.addGoal(2, new NearestAttackableTargetGoal<>(this, Player.class, true).setUnseenMemoryTicks(120));
	}

	public static AttributeSupplier.Builder createAttributes()
	{
		return Monster.createMonsterAttributes()
				.add(Attributes.MAX_HEALTH, 40.0D)
				.add(Attributes.MOVEMENT_SPEED, 0.295D)
				.add(Attributes.ATTACK_DAMAGE, 7.0D)
				.add(Attributes.ATTACK_KNOCKBACK, 0.5D)
				.add(Attributes.FOLLOW_RANGE, 20.0D)
				.add(ForgeMod.STEP_HEIGHT_ADDITION.get(), 1.5D);
	}

	@Override
	public void tick()
	{
		if (this.level.isClientSide)
		{
			this.animationTickO = this.animationTick;
		}

		super.tick();

		if (this.level.isClientSide)
		{
			if (!this.isOnGround() && !this.isInWaterOrBubble() && !this.isPassenger())
			{
				if (this.animationTick < 10)
				{
					this.animationTick = Math.min(this.animationTick + 2, 10);
				}
			}
			else if (this.animationTick > 0)
			{
				this.animationTick = Math.max(this.animationTick - 5, 0);
			}
		}
	}

	@Override
	public void aiStep()
	{
		super.aiStep();

		Vec3 vec3 = this.getDeltaMovement();

		if (!this.onGround && vec3.y < 0.0D)
		{
			this.setDeltaMovement(vec3.multiply(1.0D, 0.6D, 1.0D));
		}
	}

	@Override
	public boolean causeFallDamage(float distance, float damageMultiplier, DamageSource source)
	{
		return false;
	}

	@Override
	@Nullable
	public SpawnGroupData finalizeSpawn(ServerLevelAccessor levelAccessor, DifficultyInstance difficulty, MobSpawnType spawnType, @Nullable SpawnGroupData spawnData, @Nullable CompoundTag dataTag)
	{
		spawnData = super.finalizeSpawn(levelAccessor, difficulty, spawnType, spawnData, dataTag);
		RandomSource randomsource = levelAccessor.getRandom();

		if (randomsource.nextDouble() < ModConfigs.cachedServer.PINK_HARPY_SPAWN_CHANCE)
		{
			this.setVariant(6);
		}
		else
		{
			Holder<Biome> holder = levelAccessor.getBiome(this.blockPosition());

			if (holder != null)
			{
				if (holder.is(ModTags.BiomeTags.IS_COLD))
				{
					this.setVariant(randomsource.nextInt(4) == 0 ? (randomsource.nextInt(3) + 2) : 5);
				}
				else if (holder.is(ModTags.BiomeTags.IS_BADLANDS))
				{
					this.setVariant(randomsource.nextInt(5) == 0 ? 3 : (randomsource.nextInt(3) == 0 ? 0 : (randomsource.nextInt(2) + 1)));
				}
				else if (holder.is(ModTags.BiomeTags.IS_SANDY))
				{
					this.setVariant(randomsource.nextInt(5) == 0 ? 5 : (randomsource.nextInt(3) == 0 ? 1 : (randomsource.nextBoolean() ? 0 : 2)));
				}
				else if (holder.is(ModTags.BiomeTags.IS_SAVANNA))
				{
					this.setVariant(randomsource.nextInt(6) == 0 ? 4 : randomsource.nextInt(3));
				}
				else if (holder.is(ModTags.BiomeTags.IS_PLAINS))
				{
					this.setVariant(randomsource.nextInt(3) == 0 ? (randomsource.nextBoolean() ? 1 : 4) : 3);
				}
				else
				{
					this.setVariant(randomsource.nextBoolean() ? 0 : 3);
				}
			}
			else
			{
				this.setVariant(randomsource.nextBoolean() ? 0 : 3);
			}
		}

		return spawnData;
	}

	public int getVariant()
	{
		return this.entityData.get(DATA_VARIANT_ID);
	}

	protected void setVariant(int type)
	{
		if (type < 0 || type >= 7)
		{
			type = this.getRandom().nextInt(6);
		}

		this.entityData.set(DATA_VARIANT_ID, type);
	}

	@Override
	public void readAdditionalSaveData(CompoundTag compound)
	{
		super.readAdditionalSaveData(compound);
		this.setVariant(compound.getInt("Variant"));
	}

	@Override
	public void addAdditionalSaveData(CompoundTag compound)
	{
		super.addAdditionalSaveData(compound);
		compound.putInt("Variant", this.getVariant());
	}

	@Override
	public int getMaxSpawnClusterSize()
	{
		return 3;
	}

	@Override
	public double getMyRidingOffset()
	{
		return -0.45D;
	}

	@Override
	protected float getStandingEyeHeight(Pose pose, EntityDimensions size)
	{
		return 1.74F;
	}

	@OnlyIn(Dist.CLIENT)
	public float getAnimationScale(float f)
	{
		return Mth.lerp(f, this.animationTickO / 10.0F, this.animationTick / 10.0F);
	}

	@Override
	protected SoundEvent getAmbientSound()
	{
		return ModSoundEvents.GIRL_MOB_AMBIENT.get();
	}

	@Override
	protected SoundEvent getHurtSound(DamageSource damageSource)
	{
		return ModSoundEvents.GIRL_MOB_HURT.get();
	}

	@Override
	protected SoundEvent getDeathSound()
	{
		return ModSoundEvents.GIRL_MOB_DEATH.get();
	}

	@Override
	protected void playStepSound(BlockPos pos, BlockState block)
	{
		this.playSound(SoundEvents.CHICKEN_STEP, 0.15F, 1.0F);
	}

	@Nonnull
	@Override
	public Packet<?> getAddEntityPacket()
	{
		return NetworkHooks.getEntitySpawningPacket(this);
	}
}