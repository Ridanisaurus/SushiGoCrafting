package com.buuz135.sushigocrafting;

import com.buuz135.sushigocrafting.api.FoodHelper;
import com.buuz135.sushigocrafting.api.FoodType;
import com.buuz135.sushigocrafting.datagen.SushiBlockstateProvider;
import com.buuz135.sushigocrafting.datagen.SushiItemModelProvider;
import com.buuz135.sushigocrafting.datagen.SushiLangProvider;
import com.buuz135.sushigocrafting.proxy.SushiContent;
import com.buuz135.sushigocrafting.recipe.CombineAmountItemRecipe;
import com.buuz135.sushigocrafting.tile.machinery.RiceCookerTile;
import com.buuz135.sushigocrafting.tile.machinery.RollerTile;
import com.hrznstudio.titanium.event.handler.EventManager;
import com.hrznstudio.titanium.nbthandler.NBTManager;
import net.minecraft.block.Blocks;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.RenderTypeLookup;
import net.minecraft.inventory.InventoryHelper;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipeSerializer;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.gen.GenerationStage;
import net.minecraft.world.gen.feature.Features;
import net.minecraft.world.gen.feature.IFeatureConfig;
import net.minecraft.world.gen.placement.Placement;
import net.minecraft.world.gen.placement.TopSolidWithNoiseConfig;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.event.world.BiomeLoadingEvent;
import net.minecraftforge.event.world.PistonEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.event.lifecycle.GatherDataEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

@Mod(SushiGoCrafting.MOD_ID)
public class SushiGoCrafting {

    public static final String MOD_ID = "sushigocrafting";

    public static final ItemGroup TAB = new ItemGroup(MOD_ID) {
        @Override
        public ItemStack createIcon() {
            return new ItemStack(SushiContent.Items.RICE_SEED.get());
        }
    };

    //private static CommonProxy proxy;

    public SushiGoCrafting() {
        //proxy = DistExecutor.runForDist(() -> ClientProxy::new, () -> CommonProxy::new);
        SushiContent.Blocks.REGISTRY.register(FMLJavaModLoadingContext.get().getModEventBus());
        SushiContent.Items.REGISTRY.register(FMLJavaModLoadingContext.get().getModEventBus());
        SushiContent.Features.REGISTRY.register(FMLJavaModLoadingContext.get().getModEventBus());
        SushiContent.TileEntities.REGISTRY.register(FMLJavaModLoadingContext.get().getModEventBus());
        EventManager.mod(FMLClientSetupEvent.class).process(this::fmlClient).subscribe();
        EventManager.mod(FMLCommonSetupEvent.class).process(this::fmlCommon).subscribe();
        EventManager.mod(GatherDataEvent.class).process(this::dataGen).subscribe();
        EventManager.modGeneric(RegistryEvent.Register.class, IRecipeSerializer.class).process(register -> ((RegistryEvent.Register) register).getRegistry().register(CombineAmountItemRecipe.SERIALIZER.setRegistryName(new ResourceLocation(MOD_ID, "amount_combine_recipe")))).subscribe();
        for (FoodType value : FoodType.values()) {
            FoodHelper.generateFood(value).forEach(item -> SushiContent.Items.REGISTRY.register(FoodHelper.getName(item), () -> item));
        }
        NBTManager.getInstance().scanTileClassForAnnotations(RollerTile.class);
        NBTManager.getInstance().scanTileClassForAnnotations(RiceCookerTile.class);
        EventManager.forge(BiomeLoadingEvent.class).filter(biomeLoadingEvent -> biomeLoadingEvent.getCategory() == Biome.Category.OCEAN).process(biomeLoadingEvent -> {
            biomeLoadingEvent.getGeneration().getFeatures(GenerationStage.Decoration.VEGETAL_DECORATION).add(() ->
                    SushiContent.Features.SEAWEED.get().withConfiguration(IFeatureConfig.NO_FEATURE_CONFIG)
                            .withPlacement(Features.Placements.KELP_PLACEMENT.func_242728_a().withPlacement(Placement.field_242901_e.configure(new TopSolidWithNoiseConfig(80, 80.0D, 0.0D)))));
        }).subscribe();
        EventManager.forge(PistonEvent.Pre.class).process(pre -> {
            if (pre.getWorld().getBlockState(pre.getFaceOffsetPos()).getBlock().equals(SushiContent.Blocks.SEAWEED_BLOCK.get()) && pre.getWorld().getBlockState(pre.getPos().offset(pre.getDirection(), 2)).getBlock().equals(Blocks.IRON_BLOCK)) {
                pre.getWorld().destroyBlock(pre.getFaceOffsetPos(), false);
                NonNullList<ItemStack> list = NonNullList.create();
                list.add(new ItemStack(SushiContent.Items.NORI_SHEET.get(), 3));
                InventoryHelper.dropItems((World) pre.getWorld(), pre.getFaceOffsetPos().add(0.5, 0.5, 0.5), list);
            }
        }).subscribe();
    }

    public void fmlCommon(FMLCommonSetupEvent event) {

    }

    @OnlyIn(Dist.CLIENT)
    public void fmlClient(FMLClientSetupEvent event) {
        RenderTypeLookup.setRenderLayer(SushiContent.Blocks.RICE_CROP.get(), RenderType.getCutout());
    }

    public void dataGen(GatherDataEvent event) {
        //event.getGenerator().addProvider(new SushiModelProvider(event.getGenerator(), MOD_ID, event.getExistingFileHelper()));
        event.getGenerator().addProvider(new SushiBlockstateProvider(event.getGenerator(), MOD_ID, event.getExistingFileHelper()));
        event.getGenerator().addProvider(new SushiItemModelProvider(event.getGenerator(), MOD_ID, event.getExistingFileHelper()));
        event.getGenerator().addProvider(new SushiLangProvider(event.getGenerator(), MOD_ID, "en_us"));
    }

}
