package dev.xkmc.youkaishomecoming.init.food;

import com.tterrag.registrate.util.entry.BlockEntry;
import com.tterrag.registrate.util.entry.ItemEntry;
import dev.xkmc.youkaishomecoming.content.block.plant.*;
import dev.xkmc.youkaishomecoming.init.YoukaisHomecoming;
import dev.xkmc.youkaishomecoming.init.registrate.YHItems;
import net.minecraft.advancements.critereon.StatePropertiesPredicate;
import net.minecraft.core.Direction;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.worldgen.BootstapContext;
import net.minecraft.data.worldgen.features.FeatureUtils;
import net.minecraft.data.worldgen.placement.PlacementUtils;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemNameBlockItem;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.BushBlock;
import net.minecraft.world.level.block.ComposterBlock;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.levelgen.blockpredicates.BlockPredicate;
import net.minecraft.world.level.levelgen.feature.ConfiguredFeature;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.configurations.RandomPatchConfiguration;
import net.minecraft.world.level.levelgen.feature.configurations.SimpleBlockConfiguration;
import net.minecraft.world.level.levelgen.feature.stateproviders.BlockStateProvider;
import net.minecraft.world.level.levelgen.placement.BiomeFilter;
import net.minecraft.world.level.levelgen.placement.InSquarePlacement;
import net.minecraft.world.level.levelgen.placement.PlacedFeature;
import net.minecraft.world.level.levelgen.placement.RarityFilter;
import net.minecraft.world.level.storage.loot.LootPool;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.entries.LootItem;
import net.minecraft.world.level.storage.loot.functions.ApplyBonusCount;
import net.minecraft.world.level.storage.loot.predicates.LootItemBlockStatePropertyCondition;
import org.jetbrains.annotations.Nullable;
import vectorwing.farmersdelight.common.tag.ModTags;

import java.util.Locale;
import java.util.function.BiFunction;

public enum YHCrops {
	SOYBEAN(PlantType.CROP, 16, null, "pods"),
	REDBEAN(PlantType.CROP, 16, null, null),
	COFFEA(PlantType.COFFEA, 8, "green_coffee_bean", "coffee_berries"),
	TEA(PlantType.TEA, 8, "tea_seeds", "tea_leaves"),
	UDUMBARA(PlantType.UDUMBARA, 16, "udumbara_seeds", "udumbara_flower"),
	MANDRAKE(PlantType.MANDRAKE, 8, "mandrake_root", null),
	;

	private final BlockEntry<? extends BushBlock> PLANT;
	private final BlockEntry<? extends BushBlock> WILD;
	public final ItemEntry<ItemNameBlockItem> seed;
	public final ItemEntry<? extends Item> fruits;

	public final ResourceKey<ConfiguredFeature<?, ?>> configKey;
	public final ResourceKey<PlacedFeature> placementKey;

	private final int rarity;

	YHCrops(PlantType type, int rarity, @Nullable String seedName, @Nullable String fruit) {
		String name = name().toLowerCase(Locale.ROOT);
		this.rarity = rarity;
		if (seedName == null) seedName = name;
		this.configKey = ResourceKey.create(Registries.CONFIGURED_FEATURE,
				new ResourceLocation(YoukaisHomecoming.MODID, name));
		this.placementKey = ResourceKey.create(Registries.PLACED_FEATURE,
				new ResourceLocation(YoukaisHomecoming.MODID, name));

		PLANT = type.plant(name, this);
		WILD = type.wild(name, this);

		seed = YHItems.crop(seedName, p -> new ItemNameBlockItem(getPlant(), p));

		fruits = fruit == null ? seed : YHItems.crop(fruit, Item::new);

	}

	public Block getPlant() {
		return PLANT.get();
	}

	public Block getWildPlant() {
		return WILD.get();
	}

	public Item getSeed() {
		return seed.get();
	}

	public Item getFruits() {
		return fruits.get();
	}

	public void registerComposter() {
		ComposterBlock.COMPOSTABLES.put(getSeed(), 0.3f);
		if (getSeed() != getFruits())
			ComposterBlock.COMPOSTABLES.put(getFruits(), 0.5f);
		ComposterBlock.COMPOSTABLES.put(getWildPlant().asItem(), 0.65f);
	}

	public void registerConfigs(BootstapContext<ConfiguredFeature<?, ?>> ctx) {
		FeatureUtils.register(ctx, configKey, Feature.RANDOM_PATCH,
				new RandomPatchConfiguration(12, 5, 3,
						PlacementUtils.filtered(Feature.SIMPLE_BLOCK, new SimpleBlockConfiguration(
										BlockStateProvider.simple(getWildPlant())),
								BlockPredicate.allOf(BlockPredicate.replaceable(), BlockPredicate.noFluid(),
										BlockPredicate.matchesBlocks(Direction.DOWN.getNormal(), Blocks.GRASS_BLOCK)))));
	}

	public void registerPlacements(BootstapContext<PlacedFeature> ctx) {
		PlacementUtils.register(ctx, placementKey, ctx.lookup(Registries.CONFIGURED_FEATURE).getOrThrow(configKey),
				RarityFilter.onAverageOnceEvery(rarity), InSquarePlacement.spread(), PlacementUtils.HEIGHTMAP, BiomeFilter.biome());
	}

	public BlockEntry<Block> createBag() {
		return createBag(getName());
	}

	public String getName() {
		return name().toLowerCase(Locale.ROOT);
	}

	public ResourceKey<PlacedFeature> getPlacementKey() {
		return placementKey;
	}

	public static BlockEntry<Block> createBag(String id) {
		return YoukaisHomecoming.REGISTRATE
				.block(id + "_bag", p -> new Block(BlockBehaviour.Properties.copy(Blocks.BROWN_WOOL)))
				.blockstate((ctx, pvd) -> pvd.simpleBlock(ctx.get(), pvd.models().cube(
						ctx.getName(),
						pvd.modLoc("block/" + ctx.getName() + "_bottom"),
						pvd.modLoc("block/" + ctx.getName() + "_top"),
						pvd.modLoc("block/" + ctx.getName() + "_side"),
						pvd.modLoc("block/" + ctx.getName() + "_side"),
						pvd.modLoc("block/" + ctx.getName() + "_side_tied"),
						pvd.modLoc("block/" + ctx.getName() + "_side_tied")
				).texture("particle", pvd.modLoc("block/" + ctx.getName() + "_top"))))
				.simpleItem().register();
	}

	public static void register() {

	}

	public enum PlantType {
		CROP((name, crop) -> YoukaisHomecoming.REGISTRATE.block(name, p ->
						new YHCropBlock(BlockBehaviour.Properties.copy(Blocks.WHEAT), crop::getSeed))
				.blockstate((ctx, pvd) -> YHCropBlock.buildPlantModel(ctx, pvd, name))
				.loot((pvd, block) -> YHCropBlock.buildPlantLoot(pvd, block, crop))
				.register(),
				(name, crop) -> YoukaisHomecoming.REGISTRATE.block("wild_" + name, p -> new BushBlock(BlockBehaviour.Properties.copy(Blocks.DANDELION)))
						.blockstate((ctx, pvd) -> YHCropBlock.buildWildModel(ctx, pvd, crop))
						.loot((ctx, pvd) -> YHCropBlock.buildWildLoot(ctx, pvd, crop))
						.item().tag(ModTags.WILD_CROPS_ITEM).model((ctx, pvd) -> pvd.generated(ctx, pvd.modLoc("block/wild_" + name))).build()
						.tag(ModTags.WILD_CROPS)
						.register()
		),
		COFFEA((name, crop) -> YoukaisHomecoming.REGISTRATE.block(name, p ->
						new CoffeaCropBlock(BlockBehaviour.Properties.copy(Blocks.WHEAT), crop::getSeed))
				.blockstate((ctx, pvd) -> CoffeaCropBlock.buildPlantModel(ctx, pvd, name))
				.loot((pvd, block) -> CoffeaCropBlock.buildPlantLoot(pvd, block, crop))
				.register(),
				(name, crop) -> YoukaisHomecoming.REGISTRATE.block("wild_" + name, p -> new WildCoffeaBlock(BlockBehaviour.Properties.copy(Blocks.DANDELION)))
						.blockstate((ctx, pvd) -> WildCoffeaBlock.buildWildModel(ctx, pvd, crop))
						.loot((ctx, pvd) -> WildCoffeaBlock.buildWildLoot(ctx, pvd, crop))
						.item().tag(ModTags.WILD_CROPS_ITEM).model((ctx, pvd) -> pvd.generated(ctx, pvd.modLoc("block/wild_" + name + "_top"))).build()
						.tag(ModTags.WILD_CROPS)
						.register()
		),
		TEA((name, crop) -> YoukaisHomecoming.REGISTRATE.block(name, p ->
						new TeaCropBlock(BlockBehaviour.Properties.copy(Blocks.WHEAT), crop::getSeed))
				.blockstate((ctx, pvd) -> TeaCropBlock.buildPlantModel(ctx, pvd, name))
				.loot((pvd, block) -> TeaCropBlock.buildPlantLoot(pvd, block, crop))
				.register(),
				(name, crop) -> YoukaisHomecoming.REGISTRATE.block("wild_" + name, p -> new BushBlock(BlockBehaviour.Properties.copy(Blocks.DANDELION)))
						.blockstate((ctx, pvd) -> YHCropBlock.buildWildModel(ctx, pvd, crop))
						.loot((ctx, pvd) -> TeaCropBlock.buildWildLoot(ctx, pvd, crop))
						.item().tag(ModTags.WILD_CROPS_ITEM).model((ctx, pvd) -> pvd.generated(ctx, pvd.modLoc("block/wild_" + name))).build()
						.tag(ModTags.WILD_CROPS)
						.register()
		),
		UDUMBARA((name, crop) -> YoukaisHomecoming.REGISTRATE.block(name, p ->
						new UdumbaraBlock(BlockBehaviour.Properties.copy(Blocks.WHEAT), crop::getFruits))
				.blockstate(YHBushBlock::buildModel).register(),
				(name, crop) -> YoukaisHomecoming.REGISTRATE.block("wild_" + name, p ->
								new YHBushBlock(BlockBehaviour.Properties.copy(Blocks.DANDELION),crop::getFruits))
						.blockstate((ctx, pvd) -> YHCropBlock.buildWildModel(ctx, pvd, crop))
						.loot((pvd, b) -> pvd.dropOther(b, crop.getSeed()))
						.item().tag(ModTags.WILD_CROPS_ITEM)
						.model((ctx, pvd) -> pvd.generated(ctx, pvd.modLoc("block/wild_" + name))).build()
						.tag(ModTags.WILD_CROPS)
						.register()),
		MANDRAKE((name, crop) -> YoukaisHomecoming.REGISTRATE.block(name, p ->
						new YHBushBlock(BlockBehaviour.Properties.copy(Blocks.WHEAT), crop::getFruits))
				.blockstate(YHBushBlock::buildModel).register(),
				(name, crop) -> YoukaisHomecoming.REGISTRATE.block("wild_" + name, p ->
								new YHBushBlock(BlockBehaviour.Properties.copy(Blocks.DANDELION),crop::getFruits))
						.blockstate((ctx, pvd) -> YHCropBlock.buildWildModel(ctx, pvd, crop))
						.loot((pvd, b) -> pvd.add(b, pvd.applyExplosionDecay(b,
								LootTable.lootTable().withPool(LootPool.lootPool().add(LootItem.lootTableItem(crop.getFruits())))
										.withPool(LootPool.lootPool()
												.when(LootItemBlockStatePropertyCondition.hasBlockStateProperties(b)
														.setProperties(StatePropertiesPredicate.Builder.properties().hasProperty(YHBushBlock.FLOWERING, true)))
												.add(LootItem.lootTableItem(crop.getFruits())
														.apply(ApplyBonusCount.addBonusBinomialDistributionCount(Enchantments.BLOCK_FORTUNE, 0.5714286F, 3)))))))
						.item().tag(ModTags.WILD_CROPS_ITEM)
						.model((ctx, pvd) -> pvd.generated(ctx, pvd.modLoc("block/wild_" + name))).build()
						.tag(ModTags.WILD_CROPS)
						.register()
		);

		private final BiFunction<String, YHCrops, BlockEntry<? extends BushBlock>> plant;
		private final BiFunction<String, YHCrops, BlockEntry<? extends BushBlock>> wild;

		PlantType(BiFunction<String, YHCrops, BlockEntry<? extends BushBlock>> plant,
				  BiFunction<String, YHCrops, BlockEntry<? extends BushBlock>> wild) {
			this.plant = plant;
			this.wild = wild;
		}

		public BlockEntry<? extends BushBlock> plant(String name, YHCrops crop) {
			return plant.apply(name, crop);
		}

		public BlockEntry<? extends BushBlock> wild(String name, YHCrops crop) {
			return wild.apply(name, crop);
		}

	}

}
