package com.srgycheese.shearslime;

import com.mojang.logging.LogUtils;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.*;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import org.slf4j.Logger;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.monster.Slime;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;

@Mod(ShearSlime.MODID)
public class ShearSlime
{
    public static final String MODID = "shear_slime";

    public ShearSlime()
    {
        MinecraftForge.EVENT_BUS.register(this);
        ModLoadingContext.get().registerConfig(ModConfig.Type.COMMON, Config.SPEC);
    }

    @SubscribeEvent
    public void onPlayerInteract(PlayerInteractEvent.EntityInteract event) {
        Entity target = event.getTarget();
        Player player = event.getEntity();

        if (!(target instanceof Slime slimeTarget)) {
            return;
        }

        ItemStack heldItemStack = player.getItemInHand(event.getHand());

        if (heldItemStack.getItem() != Items.SHEARS) {
            return;
        }

        heldItemStack.hurtAndBreak(1, player, _player -> {});

        event.getLevel().playSound(null, event.getPos().getX(), event.getPos().getY(), event.getPos().getZ(), SoundEvents.SHEEP_SHEAR, SoundSource.BLOCKS, 1.0F, 1.0F);

        event.setCanceled(true);
        player.swing(event.getHand());

        var targetPosition = target.position();

        ItemEntity itemEntity = new ItemEntity(event.getLevel(), targetPosition.x, targetPosition.y, targetPosition.z, new ItemStack(Items.SLIME_BALL, 1));

        event.getLevel().addFreshEntity(itemEntity);

        if (slimeTarget.getSize() > 1) {
            slimeTarget.setSize(slimeTarget.getSize() - 1, true);
        } else {
            slimeTarget.kill();
        }
    }
}
