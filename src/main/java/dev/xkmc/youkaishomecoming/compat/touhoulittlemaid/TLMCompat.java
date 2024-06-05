package dev.xkmc.youkaishomecoming.compat.touhoulittlemaid;

import com.github.tartaricacid.touhoulittlemaid.init.InitItems;
import com.github.tartaricacid.touhoulittlemaid.item.ItemGarageKit;
import dev.xkmc.youkaishomecoming.content.entity.youkai.GeneralYoukaiEntity;
import dev.xkmc.youkaishomecoming.content.spell.game.TouhouSpellCards;
import dev.xkmc.youkaishomecoming.content.spell.spellcard.SpellCardWrapper;
import dev.xkmc.youkaishomecoming.init.registrate.YHEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import org.jetbrains.annotations.Nullable;

public class TLMCompat {


	@SubscribeEvent
	public static void onInteract(PlayerInteractEvent.EntityInteract event) {
		if (event.getTarget() instanceof GeneralYoukaiEntity e && event.getEntity().isCreative()) {
			if (event.getItemStack().getItem() instanceof ItemGarageKit) {
				if (!event.getTarget().level().isClientSide()) {
					String id = ItemGarageKit.getMaidData(event.getItemStack()).getString("ModelId");
					e.spellCard = new SpellCardWrapper();
					e.spellCard.modelId = id;
					TouhouSpellCards.setSpell(e, id);
					e.syncModel();
				}
				event.setCancellationResult(InteractionResult.SUCCESS);
				event.setCanceled(true);
			}
		}
	}

	public static boolean summonReimu(LivingEntity sp) {
		BlockPos center = BlockPos.containing(sp.position().add(sp.getForward().scale(8)).add(0, 5, 0));
		GeneralYoukaiEntity e = YHEntities.GENERAL_YOUKAI.create(sp.level());
		if (e == null) return false;
		BlockPos pos = getPos(sp, e, center, 16, 8, 5);
		if (pos == null) {
			center = sp.blockPosition().above(5);
			pos = getPos(sp, e, center, 16, 16, 5);
		}
		if (pos == null) return false;
		e.moveTo(pos, 0, 0);
		e.setTarget(sp);
		String id = "touhou_little_maid:hakurei_reimu";
		e.spellCard = new SpellCardWrapper();
		e.spellCard.modelId = id;
		TouhouSpellCards.setSpell(e, id);
		e.setItemInHand(InteractionHand.MAIN_HAND, new ItemStack(InitItems.HAKUREI_GOHEI.get(), 1));
		e.syncModel();
		sp.level().addFreshEntity(e);
		return true;
	}

	@Nullable
	private static BlockPos getPos(LivingEntity sp, Entity e, BlockPos center, int trial, int range, int dy) {
		for (int i = 0; i < trial; i++) {
			BlockPos pos = center.offset(
					sp.getRandom().nextInt(-range, range),
					sp.getRandom().nextInt(-dy, dy),
					sp.getRandom().nextInt(-range, range)
			);
			e.moveTo(pos, 0, 0);
			if (sp.level().noCollision(e)) {
				return pos;
			}
		}
		return null;
	}

}
