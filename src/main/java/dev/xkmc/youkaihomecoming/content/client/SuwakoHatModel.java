package dev.xkmc.youkaihomecoming.content.client;

import dev.xkmc.youkaihomecoming.init.YoukaiHomecoming;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.*;
import net.minecraft.resources.ResourceLocation;

public class SuwakoHatModel {
	public static final ModelLayerLocation LAYER_LOCATION = new ModelLayerLocation(new ResourceLocation(YoukaiHomecoming.MODID, "suwako_hat"), "main");

	public static LayerDefinition createBodyLayer() {
		MeshDefinition meshdefinition = HumanoidModel.createMesh(CubeDeformation.NONE, 0.0F);
		PartDefinition partdefinition = meshdefinition.getRoot();
		PartDefinition hat = partdefinition
				.addOrReplaceChild("head", CubeListBuilder.create()
								.texOffs(0, 50)
								.addBox(-4.75F, -9.0F, -4.75F, 9.5F, 4.0F, 9.5F)
								.texOffs(0, 0)
								.addBox(-4.5F, -12.0F, -4.5F, 9.0F, 3.5F, 9.0F)
								.texOffs(40, 0)
								.addBox(-6.0F, -13.8F, -2.35F, 2.5F, 2.5F, 2.5F)
								.texOffs(40, 5)
								.addBox(3.5F, -13.8F, -2.35F, 2.5F, 2.5F, 2.5F),
						PartPose.offset(0.0F, 0.0F, 0.0F));
		hat.addOrReplaceChild("cube_r1", CubeListBuilder.create()
						.texOffs(0, 14).addBox(-10.5F, 0.95F, -9.4F, 20.0F, 0.1F, 20.0F),
				PartPose.offsetAndRotation(0.4736F, -6.5562F, -0.525F, -0.0877F, 0.0056F, 0.0686F));
		return LayerDefinition.create(meshdefinition, 64, 64);
	}

}
