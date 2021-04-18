package tudbut.mod.client.crimson.gui;

import de.tudbut.type.Vector3d;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.entity.Entity;
import tudbut.mod.client.crimson.CRIMSON;
import tudbut.mod.client.crimson.mods.Notifications;
import tudbut.mod.client.crimson.mods.PlayerSelector;
import tudbut.mod.client.crimson.utils.FontRenderer;
import tudbut.mod.client.crimson.utils.Module;
import tudbut.obj.Vector2i;

public class GuiCRIMSONIngame extends Gui {
    
    static FontRenderer fontRenderer = new FontRenderer(6);
    
    public static void draw() {
        new GuiCRIMSONIngame().drawImpl();
    }
    
    public void drawImpl() {
        ScaledResolution sr = new ScaledResolution(CRIMSON.mc);
        Vector2i screenSize = new Vector2i(sr.getScaledWidth(), sr.getScaledHeight());
    
        int y = sr.getScaledHeight() - (5 + CRIMSON.mc.fontRenderer.FONT_HEIGHT);
        int x = screenSize.getX() - 5;
    
        if(!CRIMSON.isIngame())
            return;
    
        y = drawPos(CRIMSON.player, "Player", x, y);
        if(CRIMSON.mc.getRenderViewEntity() != CRIMSON.player)
            y = drawPos(CRIMSON.mc.getRenderViewEntity(), "Camera", x, y);
        
        y-=10;
        
        for (int i = 0; i < CRIMSON.modules.length; i++) {
            Module module = CRIMSON.modules[i];
            
            if(module.enabled && module.displayOnClickGUI()) {
                int color = 0x000000;
                
                switch (module.danger()) {
                    case 0:
                        color = 0x00ff00;
                        break;
                    case 1:
                        color = 0x80ff00;
                        break;
                    case 2:
                        color = 0xffff00;
                        break;
                    case 3:
                        color = 0xff8000;
                        break;
                    case 4:
                        color = 0xff0000;
                        break;
                    case 5:
                        color = 0xff00ff;
                        break;
                }
                
                
                drawString(module.toString(), x, y, color);
                y-=10;
            }
        }
    
        Notifications notifications = CRIMSON.getModule(Notifications.class);
        if(notifications.enabled) {
            x = sr.getScaledWidth() / 2 - (300 / 2);
            y = sr.getScaledHeight() / 4;
        
            Notifications.Notification[] notifs = Notifications.getNotifications().toArray(new Notifications.Notification[0]);
            for (int i = 0; i < notifs.length; i++) {
                drawRect(x, y, x + 300, y + 30, 0x80202040);
                drawStringL(notifs[i].text, x + 10, y + (15 - (9 / 2)), 0xffffffff);
                y -= 35;
            }
        }
    
        if(CRIMSON.getModule(PlayerSelector.class).enabled) {
            PlayerSelector.render();
        }
    }
    
    private void drawString(String s, int x, int y, int color) {
        drawString(
                CRIMSON.mc.fontRenderer,
                s,
                x - CRIMSON.mc.fontRenderer.getStringWidth(s),
                y,
                color
        );
    }
    
    private void drawStringL(String s, int x, int y, int color) {
        drawString(
                CRIMSON.mc.fontRenderer,
                s,
                x,
                y,
                color
        );
    }
    
    private int drawPos(Entity e, String s, int x, int y) {
        Vector3d p = new Vector3d(e.posX, e.posY, e.posZ);
        
        p.setX(Math.round(p.getX() * 10d) / 10d);
        p.setY(Math.round(p.getY() * 10d) / 10d);
        p.setZ(Math.round(p.getZ() * 10d) / 10d);
    
        if(CRIMSON.mc.world.provider.getDimension() == -1)
            drawString(
                    s + " Overworld " + Math.round(p.getX() * 8 * 10d) / 10d + " " + Math.round(p.getY() * 8 * 10d) / 10d + " " + Math.round(p.getZ() * 8 * 10d) / 10d,
                    x, y, 0xff00ff00
            );
        if(CRIMSON.mc.world.provider.getDimension() == 0)
            drawString(
                    s + " Nether " + Math.round(p.getX() / 8 * 10d) / 10d + " " + Math.round(p.getY() / 8 * 10d) / 10d + " " + Math.round(p.getZ() / 8 * 10d) / 10d,
                    x, y, 0xff00ff00
            );
        y -= 10;
        drawString(s + " " + p.getX() + " " + p.getY() + " " + p.getZ(), x, y, 0xff00ff00);
        return y - 10;
    }
}
