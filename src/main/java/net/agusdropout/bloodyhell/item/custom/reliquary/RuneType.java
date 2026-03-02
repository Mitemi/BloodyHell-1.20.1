package net.agusdropout.bloodyhell.item.custom.reliquary;

import net.agusdropout.bloodyhell.item.ModItems;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;

import java.util.function.Supplier;

public enum RuneType {

    RESTLESS_SLUMBER(
            ModItems.MARK_OF_THE_RESTLESS_SLUMBER,
            10,
            (level, player, pos) -> {
                /* Summon logic for the Restless Slumber entity is executed here. */
            }
    ),
    RAVENOUS_GAZE(
            ModItems.RUNE_OF_THE_RAVENOUS_GAZE,
            25,
            (level, player, pos) -> {
                /* Summon logic for the Ravenous Gaze entity is executed here. */
            }
    );

    private final Supplier<Item> itemSupplier;
    private final int capacityCost;
    private final RuneSummonAction summonAction;

    RuneType(Supplier<Item> itemSupplier, int capacityCost, RuneSummonAction summonAction) {
        this.itemSupplier = itemSupplier;
        this.capacityCost = capacityCost;
        this.summonAction = summonAction;
    }

    public Item getItem() {
        return this.itemSupplier.get();
    }

    public int getCapacityCost() {
        return this.capacityCost;
    }

    public void executeSummon(ServerLevel level, Player player, BlockPos pos) {
        this.summonAction.summon(level, player, pos);
    }

    /* Retrieves the corresponding RuneType for a given Item instance. Returns null if no match is found. */
    public static RuneType getByItem(Item item) {
        for (RuneType type : values()) {
            if (type.getItem().equals(item)) {
                return type;
            }
        }
        return null;
    }

    /* Functional interface to handle specific entity spawning logic per rune. */
    public interface RuneSummonAction {
        void summon(ServerLevel level, Player player, BlockPos pos);
    }
}