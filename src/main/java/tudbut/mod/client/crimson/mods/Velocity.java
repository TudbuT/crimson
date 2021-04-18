package tudbut.mod.client.crimson.mods;

import net.minecraftforge.event.entity.living.LivingKnockBackEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import tudbut.mod.client.crimson.CRIMSON;
import tudbut.mod.client.crimson.utils.Module;

public class Velocity extends Module {
    
    
    @SubscribeEvent
    public void onKnockBackDeal(LivingKnockBackEvent event) {
        if(enabled)
            if(event.getEntityLiving().getUniqueID().equals(CRIMSON.player.getUniqueID()))
                event.setCanceled(true);
    }
    
    @Override
    public void onEveryTick() {
        if(enabled)
            CRIMSON.mc.player.entityCollisionReduction = 1;
        else
            CRIMSON.mc.player.entityCollisionReduction = 0;
    }
    
    @Override
    public void onChat(String s, String[] args) {
    
    }
}
