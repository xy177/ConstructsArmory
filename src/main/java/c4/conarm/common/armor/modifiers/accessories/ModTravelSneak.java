/*
 * Copyright (c) 2018-2020 C4
 *
 * This file is part of Construct's Armory, a mod made for Minecraft.
 *
 * Construct's Armory is free software: you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as published
 * by the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Construct's Armory is distributed in the hope that it will be useful, but
 * WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with Construct's Armory.  If not, see <https://www.gnu.org/licenses/>.
 */

package c4.conarm.common.armor.modifiers.accessories;

import c4.conarm.client.models.accessories.ModelCloak;
import c4.conarm.common.armor.modifiers.ArmorModifiers;
import c4.conarm.lib.modifiers.IAccessoryRender;
import c4.conarm.lib.tinkering.TinkersArmor;
import c4.conarm.lib.utils.ConstructUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.MobEffects;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import slimeknights.tconstruct.library.modifiers.IToolMod;
import slimeknights.tconstruct.library.utils.TagUtil;
import slimeknights.tconstruct.library.utils.TinkerUtil;
import slimeknights.tconstruct.library.utils.ToolHelper;

public class ModTravelSneak extends AbstractToggleAccessoryModifier implements IAccessoryRender {

    @SideOnly(Side.CLIENT)
    private static ModelCloak model;
    private static ResourceLocation texture = ConstructUtils.getResource("textures/models/accessories/travel_cloak.png");

    public ModTravelSneak() {
        super("travel_sneak", true);
        MinecraftForge.EVENT_BUS.register(this);
    }

    @SubscribeEvent
    public void onPlayerVisibility(PlayerEvent.Visibility evt) {
        EntityPlayer player = evt.getEntityPlayer();
        ItemStack stack = player.getItemStackFromSlot(EntityEquipmentSlot.CHEST);
        if (stack.getItem() instanceof TinkersArmor && !ToolHelper.isBroken(stack) && TinkerUtil.hasModifier(TagUtil.getTagSafe(stack), this.identifier)) {
            evt.modifyVisibility(0.5D);
        }
    }

    @Override
    public void onArmorTick(ItemStack armor, World world, EntityPlayer player) {

        if (getToggleData(armor).toggle) {
            player.addPotionEffect(new PotionEffect(MobEffects.INVISIBILITY, 100, -44, false, false));
        }

        super.onArmorTick(armor, world, player);
    }

    @Override
    public void onArmorRemoved(ItemStack armor, EntityPlayer player, int slot) {
        if (player.isPotionActive(MobEffects.INVISIBILITY)) {
            PotionEffect effect = player.getActivePotionEffect(MobEffects.INVISIBILITY);
            if (effect != null && effect.getAmplifier() == -44) {
                player.removePotionEffect(MobEffects.INVISIBILITY);
            }
        }
    }

    @Override
    public boolean canApplyCustom(ItemStack stack) {
        return EntityLiving.getSlotForItemStack(stack) == EntityEquipmentSlot.CHEST && super.canApplyCustom(stack);
    }

    @Override
    public boolean canApplyTogether(IToolMod otherModifier) {
        return otherModifier != ArmorModifiers.modConcealed;
    }

    @SideOnly(Side.CLIENT)
    @Override
    public void onAccessoryRender(EntityLivingBase entityLivingBaseIn, float limbSwing, float limbSwingAmount, float partialTicks, float ageInTicks, float netHeadYaw, float headPitch, float scale) {
        if (model == null) {
            model = new ModelCloak();
        }
        Minecraft.getMinecraft().getTextureManager().bindTexture(texture);
        model.render(entityLivingBaseIn, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch, scale);
    }

    @Override
    public boolean disableRendering(ItemStack armor, EntityLivingBase entityLivingBase) {
        return getToggleData(armor).toggle;
    }
}