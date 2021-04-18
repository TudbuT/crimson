package tudbut.mod.client.crimson.mods;

import net.minecraft.client.Minecraft;
import org.lwjgl.input.Keyboard;
import tudbut.mod.client.crimson.CRIMSON;
import tudbut.mod.client.crimson.gui.GuiCRIMSON;
import tudbut.mod.client.crimson.utils.ChatUtils;
import tudbut.mod.client.crimson.utils.Module;
import tudbut.mod.client.crimson.utils.Utils;

import java.io.IOException;

public class ClickGUI extends Module {
    
    static ClickGUI instance;
    // TMP fix for mouse not showing
    public boolean mouseFix = false;
    
    public boolean flipButtons = false;
    
    public int themeID = 0;
    
    public GuiCRIMSON.Theme getTheme() {
        return GuiCRIMSON.Theme.values()[themeID];
    }
    
    private int confirmInstance = 0;
    
    {
        updateButtons();
    }
    
    public ClickGUI() {
        instance = this;
    }
    
    public static ClickGUI getInstance() {
        return instance;
    }
    
    @Override
    public void onEveryTick() {
        if(key.key == null) {
            key.key = Keyboard.KEY_M;
            updateButtons();
        }
    }
    private void updateButtons() {
        subButtons.clear();
        subButtons.add(new GuiCRIMSON.Button("Flip buttons: " + flipButtons, text -> {
            flipButtons = !flipButtons;
            text.set("Flip buttons: " + flipButtons);
        }));
        subButtons.add(new GuiCRIMSON.Button("Theme: " + getTheme(), text -> {
            if(Keyboard.isKeyDown(Keyboard.KEY_LSHIFT))
                themeID--;
            else
                themeID++;
        
            if(themeID < 0)
                themeID = GuiCRIMSON.Theme.values().length - 1;
            if(themeID > GuiCRIMSON.Theme.values().length - 1)
                themeID = 0;
        
            text.set("Theme: " + getTheme());
        }));
        subButtons.add(new GuiCRIMSON.Button("Reset layout", text -> {
            displayConfirmation = true;
            confirmInstance = 0;
        }));
        subButtons.add(new GuiCRIMSON.Button("Mouse fix: " + mouseFix, text -> {
            mouseFix = !mouseFix;
            text.set("Mouse fix: " + mouseFix);
        }));
        subButtons.add(new GuiCRIMSON.Button("Reset client", text -> {
            displayConfirmation = true;
            confirmInstance = 1;
        }));
    }
    
    @Override
    public void onEnable() {
        ChatUtils.print("§4[CRIMSON] §rShowing ClickGUI");
        CRIMSON.mc.displayGuiScreen(new GuiCRIMSON());
    }
    
    @Override
    public void onConfirm(boolean result) {
        if (result)
            switch (confirmInstance) {
                case 0:
                    // Reset ClickGUI by closing it, resetting its values, and opening it
                    enabled = false;
                    onDisable();
                    for (Module module : CRIMSON.modules) {
                        module.clickGuiX = null;
                        module.clickGuiY = null;
                    }
                    enabled = true;
                    onEnable();
                    break;
                case 1:
                    displayConfirmation = true;
                    confirmInstance = 2;
                    break;
                case 2:
                    enabled = false;
                    onDisable();
                    for (int i = 0; i < CRIMSON.modules.length; i++) {
                        Class<? extends Module> clazz = CRIMSON.modules[i].getClass();
                        try {
                            CRIMSON.modules[i].onDisable();
                            CRIMSON.modules[i].enabled = false;
                            CRIMSON.modules[i] = clazz.newInstance();
                            CRIMSON.cfg.put(clazz.getSimpleName(), CRIMSON.modules[i].saveConfig());
                        }
                        catch (InstantiationException | IllegalAccessException e) {
                            e.printStackTrace();
                        }
                    }
                    // Saving global config
                    CRIMSON.cfg.put("prefix", ",");
    
                    // Saving file
                    try {
                        CRIMSON.file.setContent(Utils.mapToString(cfg));
                    }
                    catch (IOException e) {
                        e.printStackTrace();
                    }
                    Minecraft.getMinecraft().shutdown();
                    break;
            }
    }
    
    @Override
    public void onDisable() {
        // Kill the GUI
        if (CRIMSON.mc.currentScreen != null && CRIMSON.mc.currentScreen.getClass() == GuiCRIMSON.class)
            CRIMSON.mc.displayGuiScreen(null);
    }
    
    @Override
    public void onChat(String s, String[] args) {
    
    }
    
    @Override
    public void loadConfig() {
        mouseFix = Boolean.parseBoolean(cfg.get("mouseFix"));
        flipButtons = Boolean.parseBoolean(cfg.get("flipButtons"));
        themeID = Integer.parseInt(cfg.get("theme"));
        
        if(key.key == null)
            key.key = Keyboard.KEY_COMMA;
        
        updateButtons();
    }
    
    @Override
    public void updateConfig() {
        cfg.put("mouseFix", String.valueOf(mouseFix));
        cfg.put("flipButtons", flipButtons + "");
        cfg.put("theme", themeID + "");
    }
    
    @Override
    public String saveConfig() {
        boolean b = enabled;
        enabled = false;
        
        return super.saveConfig() + ((enabled = b) ? "" : "");
    }
}
