package net.funkpla.fortress_tweak.mixin;

import me.shedaniel.autoconfig.AutoConfig;
import net.funkpla.fortress_tweak.FortressTweakConfig;
import net.minecraft.advancements.critereon.LocationPredicate;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseFireBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.structure.Structure;
import net.minecraft.world.level.portal.PortalShape;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Optional;

@Mixin(BaseFireBlock.class)
public abstract class BaseFireBlockMixin {

    @Inject(
            method = "onPlace",
            at = @At("HEAD"),
            cancellable = true
    )
    public void fortress_tweak$tweakOnPlace(BlockState state, Level level, BlockPos pos, BlockState oldState, boolean movedByPiston, CallbackInfo ci) {
        FortressTweakConfig config = AutoConfig.getConfigHolder(FortressTweakConfig.class).getConfig();
        System.out.println("I LIVE");
        Optional<PortalShape> portalShape = PortalShape.findEmptyPortalShape(level, pos, Direction.Axis.X);
        if (portalShape.isPresent()) {
            boolean structurePresent = false;
            if (level.dimension() == Level.OVERWORLD) {
                Registry<Structure> reg = level.registryAccess().registry(Registries.STRUCTURE).get();
                for (String name : config.portalStructures) {
                    ResourceLocation loc = ResourceLocation.of(name, ':');
                    Optional<Structure> struct = reg.getOptional(loc);
                    if (struct.isPresent()) {
                        ResourceKey<Structure> key = reg.getResourceKey(struct.get()).get();
                        if (LocationPredicate.inStructure(key).matches((ServerLevel) level, pos.getX(), pos.getY(), pos.getZ())) {
                            structurePresent = true;
                            break;
                        }
                    }
                }
            } else if (level.dimension() == Level.NETHER) {
                structurePresent = true;
            }

            if (structurePresent) {
                portalShape.get().createPortalBlocks();
            }
        }

        if (!state.canSurvive(level, pos)) {
            level.removeBlock(pos, false);
        }
        ci.cancel();
    }
}