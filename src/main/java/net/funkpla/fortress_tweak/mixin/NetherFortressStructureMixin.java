package net.funkpla.fortress_tweak.mixin;

import net.minecraft.util.RandomSource;
import net.minecraft.world.level.levelgen.structure.Structure;
import net.minecraft.world.level.levelgen.structure.pieces.StructurePiecesBuilder;
import net.minecraft.world.level.levelgen.structure.structures.NetherFortressStructure;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(NetherFortressStructure.class)
public abstract class NetherFortressStructureMixin extends Structure {

	protected NetherFortressStructureMixin(StructureSettings settings) {
		super(settings);
	}


    @Redirect(
			method = "generatePieces(Lnet/minecraft/world/level/levelgen/structure/pieces/StructurePiecesBuilder;Lnet/minecraft/world/level/levelgen/structure/Structure$GenerationContext;)V",
			at = @At(
					value = "INVOKE",
					target = "Lnet/minecraft/world/level/levelgen/structure/pieces/StructurePiecesBuilder;moveInsideHeights(Lnet/minecraft/util/RandomSource;II)V"
			)

	)
	private static void fortress_tweak$modifyFortressLowerBound(StructurePiecesBuilder instance, RandomSource random, int i, int j){
				instance.moveInsideHeights(random, i+32, j+32);
	}
}