package com.buuz135.sushigocrafting.block.crop;


import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.CropsBlock;
import net.minecraft.item.Item;
import net.minecraft.state.StateContainer;
import net.minecraft.util.IItemProvider;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockReader;

import java.util.Collections;
import java.util.function.Predicate;
import java.util.function.Supplier;

public class CustomCropBlock extends CropsBlock {

    private final int maxAge;
    private final Supplier<? extends Item> seedSupplier;
    private final Predicate<BlockState> groundPredicate;

    public CustomCropBlock(Properties builder, Supplier<? extends Item> seedSupplier, Predicate<BlockState> groundPredicate) {
        super(builder);
        this.maxAge = Collections.max(getAgeProperty().getAllowedValues());
        this.seedSupplier = seedSupplier;
        this.groundPredicate = groundPredicate;
    }

    @Override
    public int getMaxAge() {
        return maxAge;
    }

    @Override
    protected IItemProvider getSeedsItem() {
        return seedSupplier.get();
    }

    @Override
    protected boolean isValidGround(BlockState state, IBlockReader worldIn, BlockPos pos) {
        return groundPredicate.test(state);
    }

    @Override
    protected void fillStateContainer(StateContainer.Builder<Block, BlockState> builder) {
        builder.add(getAgeProperty());
    }
}
