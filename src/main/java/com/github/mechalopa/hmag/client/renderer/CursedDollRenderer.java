package com.github.mechalopa.hmag.client.renderer;

import java.util.Map;

import com.github.mechalopa.hmag.HMaG;
import com.github.mechalopa.hmag.client.ModModelLayers;
import com.github.mechalopa.hmag.client.model.CursedDollModel;
import com.github.mechalopa.hmag.client.renderer.layers.CursedDollEyesLayer;
import com.github.mechalopa.hmag.world.entity.CommonOrUncommonVariant;
import com.github.mechalopa.hmag.world.entity.CursedDollEntity;
import com.google.common.collect.Maps;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;

import net.minecraft.Util;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class CursedDollRenderer extends AbstractGirlRenderer<CursedDollEntity, CursedDollModel<CursedDollEntity>>
{
	private static final Map<CommonOrUncommonVariant, ResourceLocation> TEXTURES = Util.make(Maps.newEnumMap(CommonOrUncommonVariant.class), p -> {
		p.put(CommonOrUncommonVariant.COMMON, new ResourceLocation(HMaG.MODID, "textures/entity/cursed_doll/cursed_doll_0.png"));
		p.put(CommonOrUncommonVariant.UNCOMMON, new ResourceLocation(HMaG.MODID, "textures/entity/cursed_doll/cursed_doll_1.png"));
	});

	public CursedDollRenderer(EntityRendererProvider.Context context)
	{
		super(context, new CursedDollModel<>(context.bakeLayer(ModModelLayers.CURSED_DOLL)), 0.375F);
		this.addLayer(new CursedDollEyesLayer<>(this));
	}

	@Override
	protected void scale(CursedDollEntity entity, PoseStack poseStack, float partialTickTime)
	{
		poseStack.scale(0.875F, 0.875F, 0.875F);
		super.scale(entity, poseStack, partialTickTime);
		float f = (float)entity.tickCount + partialTickTime;
		poseStack.translate(0.0F, -0.12F + Mth.sin(f * 0.06F) * 0.08F, 0.0F);
	}

	@Override
	protected void setupRotations(CursedDollEntity entity, PoseStack poseStack, float ageInTicks, float rotationYaw, float partialTicks)
	{
		super.setupRotations(entity, poseStack, ageInTicks, rotationYaw, partialTicks);
		poseStack.mulPose(Axis.XP.rotationDegrees(-10.0F));
	}

	@Override
	public ResourceLocation getTextureLocation(CursedDollEntity entity)
	{
		return TEXTURES.getOrDefault(entity.getVariant(), TEXTURES.get(CommonOrUncommonVariant.COMMON));
	}
}