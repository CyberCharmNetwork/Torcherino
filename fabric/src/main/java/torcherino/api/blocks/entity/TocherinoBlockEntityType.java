package torcherino.api.blocks.entity;

import com.mojang.datafixers.types.Type;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntityType;
import torcherino.api.TierSupplier;

import java.util.function.Supplier;

// todo: nuke this
@SuppressWarnings("SpellCheckingInspection")
public class TocherinoBlockEntityType extends BlockEntityType<TorcherinoBlockEntity> {
    public TocherinoBlockEntityType(Supplier<TorcherinoBlockEntity> supplier, Type type) {
        super(supplier, null, type);
    }

    @Override
    public boolean isValid(Block block) {
        return block instanceof TierSupplier;
    }
}
