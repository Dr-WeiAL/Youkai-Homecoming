package dev.xkmc.youkaishomecoming.content.item.curio.hat;

import dev.xkmc.l2library.util.math.MathHelper;
import dev.xkmc.youkaishomecoming.content.client.ReimuHairbandModel;
import dev.xkmc.youkaishomecoming.content.entity.danmaku.IYHDanmaku;
import dev.xkmc.youkaishomecoming.content.entity.reimu.ReimuModel;
import dev.xkmc.youkaishomecoming.init.YoukaisHomecoming;
import dev.xkmc.youkaishomecoming.init.data.YHDamageTypes;
import dev.xkmc.youkaishomecoming.init.data.YHLangData;
import net.minecraft.network.chat.Component;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.minecraftforge.client.extensions.common.IClientItemExtensions;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.UUID;
import java.util.function.Consumer;

public class ReimuHairbandItem extends TouhouHatItem {

	private static final UUID ID = MathHelper.getUUIDFromString("reimu_hairband");

	public ReimuHairbandItem(Properties properties) {
		super(properties, TouhouMat.REIMU_HAIRBAND);
	}

	public void initializeClient(Consumer<IClientItemExtensions> consumer) {
		consumer.accept(new ReimuHairbandModel(ReimuModel.HAT_LOCATION));
	}

	public String getArmorTexture(ItemStack stack, Entity entity, EquipmentSlot slot, String type) {
		return YoukaisHomecoming.MODID + ":textures/entities/reimu.png";
	}

	@Override
	public DamageSource modifyDamageType(ItemStack stack, LivingEntity le, IYHDanmaku danmaku, DamageSource type) {
		return YHDamageTypes.abyssal(danmaku);
	}

	@Override
	protected void tick(ItemStack stack, Level level, Player player) {
		FlyingToken.tickFlying(player);
	}

	@Override
	public void appendHoverText(ItemStack stack, @Nullable Level level, List<Component> list, TooltipFlag flag) {
		boolean obtain = showTooltip();
		if (obtain) {
			list.add(YHLangData.OBTAIN.get().append(YHLangData.OBTAIN_REIMU_HAIRBAND.get()));
			list.add(YHLangData.USAGE.get().append(YHLangData.USAGE_REIMU_HAIRBAND.get()));
		} else {
			list.add(YHLangData.OBTAIN.get().append(YHLangData.UNKNOWN.get()));
			list.add(YHLangData.USAGE.get().append(YHLangData.UNKNOWN.get()));
		}
	}

}
