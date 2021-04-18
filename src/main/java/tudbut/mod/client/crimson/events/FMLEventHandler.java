package tudbut.mod.client.crimson.events;

import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.client.event.ClientChatEvent;
import net.minecraftforge.client.event.ClientChatReceivedEvent;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import net.minecraftforge.fml.common.network.FMLNetworkEvent;
import tudbut.mod.client.crimson.CRIMSON;
import tudbut.mod.client.crimson.mods.*;
import tudbut.mod.client.crimson.utils.ChatUtils;
import tudbut.mod.client.crimson.utils.ThreadManager;
import tudbut.mod.client.crimson.utils.Utils;

public class FMLEventHandler {
    
    private boolean isDead = true;
    
    // Fired when enter is pressed in chat
    @SubscribeEvent
    public void onChat(ClientChatEvent event) {
        // Only for CRIMSON commands
        if (event.getOriginalMessage().startsWith(CRIMSON.prefix)) {
            
            // Don't send
            event.setCanceled(true);
            ChatUtils.print("§4[CRIMSON] §rThis message was not sent to the server");
            // When canceled, the event blocks adding the message to the chat history,
            // so it'll cause confusion if this line doesn't exist
            ChatUtils.history(event.getOriginalMessage());
            
            // The command without the prefix
            String s = event.getOriginalMessage().substring(CRIMSON.prefix.length());
            
            try {
                // Toggle a module
                if (s.startsWith("t ")) {
                    for (int i = 0; i < CRIMSON.modules.length; i++) {
                        if (CRIMSON.modules[i].getClass().getSimpleName().equalsIgnoreCase(s.substring("t ".length()))) {
                            ChatUtils.print(String.valueOf(!CRIMSON.modules[i].enabled));
                            
                            if (CRIMSON.modules[i].enabled = !CRIMSON.modules[i].enabled)
                                CRIMSON.modules[i].onEnable();
                            else
                                CRIMSON.modules[i].onDisable();
                        }
                    }
                }
                
                // Ignore any commands and say something
                if (s.startsWith("say ")) {
                    CRIMSON.player.sendChatMessage(s.substring("say ".length()));
                    ChatUtils.history(event.getOriginalMessage());
                }
                
                if (s.equals("help")) {
                    String help = Utils.getRemote("help.chat.txt", false);
                    if (help == null) {
                        ChatUtils.print("§4[CRIMSON] §cUnable retrieve help message! Check your connection!");
                    } else {
                        help = help.replaceAll("%p", CRIMSON.prefix);
                        ChatUtils.print(help);
                    }
                }
                
                // Module-specific commands
                for (int i = 0; i < CRIMSON.modules.length; i++) {
                    if (s.toLowerCase().startsWith(CRIMSON.modules[i].getClass().getSimpleName().toLowerCase())) {
                        try {
                            String args = s.substring(CRIMSON.modules[i].getClass().getSimpleName().length() + 1);
                            if (CRIMSON.modules[i].enabled)
                                CRIMSON.modules[i].onChat(args, args.split(" "));
                            CRIMSON.modules[i].onEveryChat(args, args.split(" "));
                        } catch (StringIndexOutOfBoundsException e) {
                            String args = "";
                            if (CRIMSON.modules[i].enabled)
                                CRIMSON.modules[i].onChat(args, args.split(" "));
                            CRIMSON.modules[i].onEveryChat(args, args.split(" "));
                        }
                    }
                }
            }
            catch (Exception e) {
                ChatUtils.print("Command failed!");
                e.printStackTrace(ChatUtils.chatPrinterDebug());
            }
            
        }
        // A lil extra for the DM module
        else if (DM.getInstance().enabled) {
            event.setCanceled(true);
            ChatUtils.history(event.getOriginalMessage());
            ThreadManager.run(() -> {
                for (int i = 0; i < DM.getInstance().users.length; i++) {
                    CRIMSON.player.sendChatMessage("/tell " + DM.getInstance().users[i] + " " + event.getOriginalMessage());
                    try {
                        Thread.sleep(1000);
                    }
                    catch (InterruptedException e) {
                        e.printStackTrace();
                        e.printStackTrace(ChatUtils.chatPrinterDebug());
                    }
                }
            });
        }
        // A lil extra for the DMChat module
        else if (DMChat.getInstance().enabled) {
            event.setCanceled(true);
            ChatUtils.history(event.getOriginalMessage());
            ThreadManager.run(() -> {
                ChatUtils.print("<" + CRIMSON.player.getName() + "> " + event.getOriginalMessage());
                for (int i = 0; i < DMChat.getInstance().users.length; i++) {
                    CRIMSON.player.sendChatMessage("/tell " + DMChat.getInstance().users[i] + " " + event.getOriginalMessage());
                    try {
                        Thread.sleep(1000);
                    }
                    catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            });
        }
        // Don't add chatcolor to commands!
        else if (!event.getOriginalMessage().startsWith("/") && !event.getOriginalMessage().startsWith(".") && !event.getOriginalMessage().startsWith("#")) {
            event.setCanceled(true);
            CRIMSON.player.sendChatMessage(ChatColor.getInstance().get() + event.getMessage() + ChatSuffix.getInstance().get(ChatSuffix.getInstance().chance));
            
            ChatUtils.history(event.getOriginalMessage());
        }
    }
    
    // When a message is received, those will often require parsing
    @SubscribeEvent
    public void onServerChat(ClientChatReceivedEvent event) {
        // BayMax AC will ask you for a captcha when you chat too much or spam,
        // this will automatically solve it
        if (event.getMessage().getUnformattedText().startsWith("BayMax") && event.getMessage().getUnformattedText().contains("Please type '")) {
            String key = event.getMessage().getUnformattedText().substring("BayMax _ Please type '".length(), "BayMax _ Please type '".length() + 4);
            CRIMSON.player.sendChatMessage(key);
            ChatUtils.print("Auto-solved");
        }
        // Trigger module event for server chat, the modules can cancel display of the message
        for (int i = 0; i < CRIMSON.modules.length; i++) {
            if (CRIMSON.modules[i].enabled)
                if (CRIMSON.modules[i].onServerChat(event.getMessage().getUnformattedText(), event.getMessage().getFormattedText()))
                    event.setCanceled(true);
        }
    }
    
    // When the client joins a server
    @SubscribeEvent
    public void onJoinServer(FMLNetworkEvent.ClientConnectedToServerEvent event) {
        ChatUtils.print("§a§lCRIMSON has a Discord server: https://discord.gg/2WsVCQDpwy!");
        
        // Check for a new version
        ThreadManager.run(() -> {
            try {
                Thread.sleep(10000);
            }
            catch (InterruptedException e) {
                e.printStackTrace();
                e.printStackTrace(ChatUtils.chatPrinterDebug());
            }
            while (CRIMSON.mc.world != null) {
                if(false) {
                    String s = Utils.removeNewlines(Utils.getRemote("version.txt", true));
                    if (s == null) {
                        ChatUtils.print("§4[CRIMSON] §cUnable to check for a new version! Check your connection!");
                    }
                    else if (!s.equals(CRIMSON.VERSION)) {
                        ChatUtils.print(
                                "§a§lA new CRIMSON version was found! Current: " +
                                CRIMSON.VERSION +
                                ", New: " +
                                s +
                                "! Please consider updating at " +
                                "https://github.com/TudbuT/ttc/releases/tag/" +
                                s
                        );
                    }
                    try {
                        for (int i = 0; i < 60; i++) {
                            Thread.sleep(1000);
                            if (CRIMSON.mc.world == null)
                                break;
                        }
                    }
                    catch (InterruptedException e) {
                        e.printStackTrace();
                        e.printStackTrace(ChatUtils.chatPrinterDebug());
                    }
                }
            }
        });
    }
    
    // When any entity appears on screen, useful for setting player and world
    @SubscribeEvent
    public void onJoin(EntityJoinWorldEvent event) {
        // Setting player and world
        CRIMSON.player = Minecraft.getMinecraft().player;
        CRIMSON.world = Minecraft.getMinecraft().world;
    }
    
    // When the player dies, NOT called by FML
    public void onDeath(EntityPlayer player) {
        TPAParty.getInstance().enabled = false;
        TPAParty.getInstance().onDisable();
        BlockPos pos = player.getPosition();
        ChatUtils.print("§c§l§k|||§c§l You died at " + pos.getX() + " " + pos.getY() + " " + pos.getZ());
    }
    
    @SubscribeEvent
    public void onHUDRender(RenderGameOverlayEvent.Post event) {
        if(event.getType() == RenderGameOverlayEvent.ElementType.ALL)
            HUD.getInstance().renderHUD();
    }
    
    // Fired every tick
    @SubscribeEvent
    public void onSubTick(TickEvent event) {
        if(CRIMSON.mc.world == null || CRIMSON.mc.player == null)
            return;
        
        for (int i = 0; i < CRIMSON.modules.length; i++) {
            if (CRIMSON.modules[i].enabled)
                try {
                    CRIMSON.modules[i].onSubTick();
                }
                catch (Exception e) {
                    e.printStackTrace(ChatUtils.chatPrinterDebug());
                }
            CRIMSON.modules[i].onEverySubTick();
        }
    }
    
    // Fired every tick
    @SubscribeEvent
    public void onTick(TickEvent.PlayerTickEvent event) {
        if(CRIMSON.mc.world == null || CRIMSON.mc.player == null)
            return;
        
        if(event.phase != TickEvent.Phase.END)
            return;
    
        if(event.type != TickEvent.Type.PLAYER)
            return;
        
        EntityPlayerSP player = CRIMSON.player;
        if (player == null)
            return;
        if (player.getHealth() <= 0) {
            if (!isDead) {
                isDead = true;
                // >:(
                onDeath(player);
            }
        } else {
            isDead = false;
        }
        ParticleLoop.run();
        for (int i = 0; i < CRIMSON.modules.length; i++) {
            CRIMSON.modules[i].key.onTick();
            
            if (CRIMSON.modules[i].enabled) {
                try {
                    for (String key : CRIMSON.modules[i].customKeyBinds.keySet()) {
                        CRIMSON.modules[i].customKeyBinds.get(key).onTick();
                    }
                    CRIMSON.modules[i].onTick();
                }
                catch (Exception e) {
                    e.printStackTrace(ChatUtils.chatPrinterDebug());
                }
            }
            CRIMSON.modules[i].onEveryTick();
        }
    }
}
