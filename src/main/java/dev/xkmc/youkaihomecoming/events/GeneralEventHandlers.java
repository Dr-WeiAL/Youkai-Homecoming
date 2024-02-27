package dev.xkmc.youkaihomecoming.events;

import dev.xkmc.youkaihomecoming.content.block.furniture.LeftClickBlock;
import dev.xkmc.youkaihomecoming.init.YoukaiHomecoming;
import dev.xkmc.youkaihomecoming.init.data.YHModConfig;
import dev.xkmc.youkaihomecoming.init.data.YHTagGen;
import dev.xkmc.youkaihomecoming.init.registrate.YHEffects;
import dev.xkmc.youkaihomecoming.init.registrate.YHItems;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Items;
import net.minecraftforge.event.entity.living.*;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.event.entity.player.PlayerSleepInBedEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import vectorwing.farmersdelight.common.tag.ForgeTags;

@Mod.EventBusSubscriber(modid = YoukaiHomecoming.MODID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class GeneralEventHandlers {

	@SubscribeEvent
	public static void onLeftClickBlock(PlayerInteractEvent.LeftClickBlock event) {
		if (event.getItemStack().is(Items.DEBUG_STICK)) return;
		var level = event.getLevel();
		var pos = event.getPos();
		var state = level.getBlockState(pos);
		if (state.getBlock() instanceof LeftClickBlock block) {
			if (block.leftClick(state, level, pos, event.getEntity())) {
				event.setCanceled(true);
				event.setCancellationResult(InteractionResult.CONSUME);
			}
		}
	}

	@SubscribeEvent
	public static void onSleep(PlayerSleepInBedEvent event) {
		if (event.getEntity().hasEffect(YHEffects.SOBER.get())) {
			event.setResult(Player.BedSleepingProblem.OTHER_PROBLEM);
		}
	}

	@SubscribeEvent
	public static void onAttack(LivingAttackEvent event) {
		if (event.getSource().getEntity() instanceof LivingEntity le) {
			if (le.hasEffect(YHEffects.UNCONSCIOUS.get())) {
				le.removeEffect(YHEffects.UNCONSCIOUS.get());
			}
		}
		if (event.getSource().is(DamageTypeTags.IS_FIRE)) {
			if (event.getEntity().hasEffect(YHEffects.REFRESHING.get())) {
				event.setCanceled(true);
			}
		}
	}

	@SubscribeEvent
	public static void onDamage(LivingDamageEvent event) {
		if (event.getSource().is(DamageTypeTags.BYPASSES_EFFECTS) ||
				event.getSource().is(DamageTypeTags.BYPASSES_RESISTANCE) ||
				event.getSource().is(DamageTypeTags.BYPASSES_INVULNERABILITY))
			return;
		if (event.getEntity().hasEffect(YHEffects.THICK.get())) {
			event.setAmount(Math.max(0, event.getAmount() - 1));
		}
	}

	@SubscribeEvent
	public static void onHeal(LivingHealEvent event) {
		if (event.getEntity().hasEffect(YHEffects.SMOOTHING.get())) {
			event.setAmount((float) (event.getAmount() * YHModConfig.COMMON.smoothingHealingFactor.get()));
		}
	}

	@SubscribeEvent
	public static void onTick(LivingEvent.LivingTickEvent event) {
		var e = event.getEntity();
		if (e.hasEffect(YHEffects.THICK.get()) && e.hasEffect(MobEffects.WITHER)) {
			e.removeEffect(MobEffects.WITHER);
		}
		if (e.hasEffect(YHEffects.SMOOTHING.get()) && e.hasEffect(MobEffects.POISON)) {
			e.removeEffect(MobEffects.POISON);
		}
		if (e.hasEffect(YHEffects.REFRESHING.get()) && e.isOnFire()) {
			e.clearFire();
		}
		var tea = e.getEffect(YHEffects.TEA.get());
		if (tea != null) {
			if (e.tickCount % YHModConfig.COMMON.teaHealingPeriod.get() == 0)
				e.heal(1 << tea.getAmplifier());
		}
	}

	@SubscribeEvent
	public static void onEffectTest(MobEffectEvent.Applicable event) {
		if (event.getEffectInstance().getEffect() == MobEffects.WITHER) {
			if (event.getEntity().hasEffect(YHEffects.THICK.get())) {
				event.setCanceled(true);
			}
		}
		if (event.getEffectInstance().getEffect() == MobEffects.POISON) {
			if (event.getEntity().hasEffect(YHEffects.SMOOTHING.get())) {
				event.setCanceled(true);
			}
		}
	}

	@SubscribeEvent
	public static void collectBlood(LivingDeathEvent event) {
		if (!event.getEntity().getType().is(YHTagGen.FLESH_SOURCE)) return;
		if (event.getSource().getDirectEntity() instanceof LivingEntity le) {
			if (!le.getMainHandItem().is(ForgeTags.TOOLS_KNIVES)) return;
			if (!le.getOffhandItem().is(Items.GLASS_BOTTLE)) return;
			if (le.hasEffect(YHEffects.YOUKAIFIED.get()) || le.hasEffect(YHEffects.YOUKAIFYING.get())) {
				le.getOffhandItem().shrink(1);
				if (le instanceof Player player) {
					player.getInventory().add(YHItems.BLOOD_BOTTLE.asStack());
				} else {
					le.spawnAtLocation(YHItems.BLOOD_BOTTLE.asStack());
				}
			}
		}
	}

	public static boolean preventPhantomSpawn(ServerPlayer player) {
		return player.hasEffect(YHEffects.SOBER.get());
	}

}
