package tudbut.mod.client.crimson;

import de.tudbut.tools.FileRW;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import org.apache.logging.log4j.Logger;
import tudbut.mod.client.crimson.events.FMLEventHandler;
import tudbut.mod.client.crimson.mods.*;
import tudbut.mod.client.crimson.utils.Module;
import tudbut.mod.client.crimson.utils.ThreadManager;
import tudbut.mod.client.crimson.utils.Utils;
import tudbut.parsing.TCN;

import javax.swing.*;
import java.io.IOException;
import java.util.*;

@Mod(modid = CRIMSON.MODID, name = CRIMSON.NAME, version = CRIMSON.VERSION)
public class CRIMSON {
    // FML stuff and version
    public static final String MODID = "crimson";
    public static final String NAME = "CRIMSON Client";
    public static final String VERSION = "vC1.8.0a";
    
    // Registered modules, will make an api for it later
    public static Module[] modules;
    // Player and current World(/Dimension), updated regularly in FMLEventHandler
    public static EntityPlayerSP player;
    public static World world;
    // Current Minecraft instance running
    public static Minecraft mc = Minecraft.getMinecraft();
    // Config
    public static FileRW file;
    public static TCN globalConfig;
    public static Map<String, String> cfg;
    // Prefix for chat-commands
    public static String prefix = ",";
    
    // Logger, provided by Forge
    public static Logger logger;
    
    private static CRIMSON instance;
    
    public static CRIMSON getInstance() {
        return instance;
    }
    
    {instance = this;}
    
    // Runs a slight moment after the game is started, not all mods are initialized yet
    @EventHandler
    public void preInit(FMLPreInitializationEvent event) {
        logger = event.getModLog();
        try {
            file = new FileRW("config/crimson.cfg");
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    // Runs when all important info is loaded and all mods are pre-initialized,
    // most game objects exist already when this is called
    @EventHandler
    public void init(FMLInitializationEvent event) {
        logger.info("CRIMSON by TudbuT");
        
        mc.gameSettings.autoJump = false; // Fuck AutoJump, disable it on startup
        
        long sa; // For time measurements
        
        System.out.println("Init...");
        sa = new Date().getTime();
        try {
            cfg = Utils.stringToMap(file.getContent().join("\n"));
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        try {
            globalConfig = TCN.read(Objects.requireNonNull(Utils.getRemote("global_config.tcn", true)));
        }
        catch (Exception e) {
            try {
                globalConfig = TCN.read("" +
                                        "\n" +
                                        "messages {\n" +
                                        "    update: true\n" +
                                        "}\n" +
                                        "\n" +
                                        "startup {\n" +
                                        "    show_credit: true\n" +
                                        "}");
            }
            catch (TCN.TCNException ignored) { }
        }
        Utils.trackLogin();
        if(globalConfig.getBoolean("startup#show_credit")) {
            // Show the "TTC by TudbuT" message
            ThreadManager.run(() -> {
                JOptionPane.showMessageDialog(null, "CRIMSON by TudbuT");
            });
        }
        sa = new Date().getTime() - sa;
        System.out.println("Done in " + sa + "ms");
        
        System.out.println("Constructing modules...");
        sa = new Date().getTime();
        // Constructing modules to be usable
        modules = new Module[] {
                new AutoTotem(),
                new TPAParty(),
                new Prefix(),
                new Team(),
                new Friend(),
                new TPATools(),
                new ChatSuffix(),
                new AutoConfig(),
                new ChatColor(),
                new Trap(),
                new PlayerLog(),
                new DMAll(),
                new DM(),
                new DMChat(),
                new Debug(),
                new AltControl(),
                new KillAura(),
                new CreativeFlight(),
                new ElytraFlight(),
                new ElytraBot(),
                new HUD(),
                new SeedOverlay(),
                new Velocity(),
                new Bright(),
                new Freecam(),
                new LSD(),
                new Bind(),
                new PopCount(),
                new PlayerSelector(),
                new Takeoff(),
                new Notifications(),
                new ClickGUI()
        };
        sa = new Date().getTime() - sa;
        System.out.println("Done in " + sa + "ms");
        
        // Registering event handlers
        MinecraftForge.EVENT_BUS.register(new FMLEventHandler());
        for (int i = 0; i < modules.length; i++) {
            MinecraftForge.EVENT_BUS.register(modules[i]);
        }
        
        System.out.println("Loading config...");
        sa = new Date().getTime();
    
        // Loading config from config/ttc.cfg
        loadConfig();
        
        sa = new Date().getTime() - sa;
        System.out.println("Done in " + sa + "ms");
        
        System.out.println("Starting threads...");
        sa = new Date().getTime();
        
        // Starting thread to regularly save config
        ThreadManager.run(() -> {
            while (true) {
                try {
                    Utils.trackPlay();
                    try {
                        // Only save if on main
                        if(AltControl.getInstance().mode != 1)
                            saveConfig();
                    }
                    catch (IOException e) {
                        e.printStackTrace();
                    }
                    Thread.sleep(1000);
                }
                catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        });
        sa = new Date().getTime() - sa;
        System.out.println("Done in " + sa + "ms");
    
        System.out.println("Initializing modules...");
        sa = new Date().getTime();
    
        for (int i = 0 ; i < modules.length ; i++) {
            modules[i].init();
        }
    
        sa = new Date().getTime() - sa;
        System.out.println("Done in " + sa + "ms");
    }
    
    public void saveConfig() throws IOException {
        setConfig();
        
        // Saving file
        file.setContent(Utils.mapToString(cfg));
    }
    
    public void setConfig() {
        // Saving config for modules
        for (int i = 0; i < modules.length; i++) {
            cfg.put(modules[i].getClass().getSimpleName(), modules[i].saveConfig());
        }
        // Saving global config
        cfg.put("prefix", prefix);
    }
    
    public void loadConfig() {
        // Loading config for modules
        for (int i = 0; i < modules.length; i++) {
            try {
                logger.info(modules[i].toString());
                modules[i].saveConfig();
                if (cfg.containsKey(modules[i].toString())) {
                    modules[i].loadConfig(Utils.stringToMap(cfg.get(modules[i].getClass().getSimpleName())));
                }
            }
            catch (Exception e) {
                logger.warn("Couldn't load config of module " + modules[i].toString() + "!");
                logger.warn(e);
            }
        }
        // Loading global config
        prefix = cfg.getOrDefault("prefix", ",");
    }
    
    public static boolean isIngame() {
        return mc.world != null && mc.player != null ;
    }
    
    public static void addModule(Module module) {
        List<Module> list = Arrays.asList(modules);
        list.add(module);
        modules = list.toArray(new Module[0]);
    }
    
    public static <T extends Module> T getModule(Class<? extends T> module) {
        for (int i = 0; i < modules.length; i++) {
            if(modules[i].getClass() == module) {
                return (T) modules[i];
            }
        }
        throw new RuntimeException();
    }
}
