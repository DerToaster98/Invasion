/*
package invasion.command;


import com.mojang.brigadier.CommandDispatcher;
import invasion.Invasion;
import invasion.entity.EntityIMLiving;
import invasion.entity.ally.EntityIMWolf;
import invasion.nexus.Nexus;
import net.minecraft.client.renderer.color.BlockColors;
import net.minecraft.command.CommandSource;
import net.minecraft.command.Commands;
import net.minecraft.command.ICommandSender;
import net.minecraft.command.ICommandSource;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;

import java.lang.reflect.Constructor;


public class InvasionCommand {

    public static void register(CommandDispatcher<CommandSource> dispatcher) {
        dispatcher.register(Commands.literal("invasion").
                        requires((commandSource)->commandSource.hasPermissionLevel(3))
                         );
    }
BlockColors

    @Override
    public void execute(MinecraftServer server, ICommandSource sender, String[] args) {
        String username = sender.toString();
        Nexus focusNexus = null;
        if (sender instanceof PlayerEntity) {
            focusNexus = Nexus.getNearest((EntityPlayer) sender, 50);
        }
        if (args.length <= 0 || args.length > 7) {
            this.sendMessage(sender, "Command not recognised, use /invasion help for a list of all the available commands", TextFormatting.RED);
            return;
        }
        switch (args[0].toLowerCase()) {
            case "help":
                if (args.length == 1) this.sendHelp(sender, 1);
                if (args.length == 2) {
                    try {
                        this.sendHelp(sender, Integer.parseInt(args[1]));
                    } catch (NumberFormatException e) {
                        this.sendMessage(sender, "Help page argument is not a number", TextFormatting.RED);
                    }
                }
                break;
            case "begin":
                if (focusNexus == null) {
                    this.sendMessage(sender, "No nexus detected", TextFormatting.RED);
                    return;
                }
                if (args.length == 2) {
                    int startWave = 1;
                    try {
                        startWave = Integer.parseInt(args[1]);
                    } catch (NumberFormatException e) {
                        if (args[1].equalsIgnoreCase("continuous") || args[1].equalsIgnoreCase("cont") || args[1].equalsIgnoreCase("c")) {
                            this.sendMessage(sender, "Starting continuous invasion");
                            focusNexus.debugStartContinuous();
                        } else {
                            this.sendMessage(sender, "Unknown start wave", TextFormatting.RED);
                            return;
                        }
                    }
                    this.sendMessage(sender, "Starting invasion at wave " + startWave);
                    focusNexus.debugStartInvasion(startWave);
                } else {
                    this.sendMessage(sender, "No start wave specified, starting at wave 1");
                    focusNexus.debugStartInvasion(1);
                }
                break;
            case "end":
                if (focusNexus != null) {
                    focusNexus.emergencyStop();
                    Invasion.broadcastToAll(username + " ended invasion");
                } else {
                    this.sendMessage(sender, "No invasion to end", TextFormatting.RED);
                }
                break;
            case "range":
                if (args.length == 2) {
                    try {
                        int radius = Integer.parseInt(args[1]);
                        this.changeRadius(sender, focusNexus, radius);
                    } catch (NumberFormatException e) {
                        this.sendMessage(sender, "Cannot set range to a value that is not a number", TextFormatting.RED);
                    }
                }
                break;
            case "nexusstatus":
                if (focusNexus != null) {
                    this.sendMessage(sender, "Nexus status sent to bound players");
                    focusNexus.debugStatus();
                } else {
                    this.sendMessage(sender, "No nexus detected", TextFormatting.RED);
                }
                break;
            case "bolt":
                this.spawnBolt(sender, focusNexus, args);
                break;
            case "status":
                if (focusNexus != null) {
                    this.sendMessage(sender, "Is invasion active: " + focusNexus.isActive());
                } else {
                    this.sendMessage(sender, "No nexus detected", TextFormatting.RED);
                }
                break;
            case "debug":
                if (args.length == 2) {
                    if (sender instanceof EntityPlayer) this.spawnDebugMob((EntityPlayer) sender, focusNexus, args[1]);
                } else {
                    this.sendMessage(sender, "No mob specified", TextFormatting.RED);
                }
                break;
            default:
                this.sendMessage(sender, "Command not recognised, use /invasion help for a list of all the available commands", TextFormatting.RED);
        }
    }

    private void begin() {

    }

    private void end() {

    }

    private void help() {

    }

    private void bolt() {

    }

    private void status() {

    }

    private void nexusstatus() {
        
    }

    private void debug() {

    }

    private void sendHelp(ICommandSender sender, int page) {
        switch (page) {
            case 1:
                this.sendMessage(sender, "--- Showing Main help page 1 of 2 ---", TextFormatting.GREEN);
                this.sendMessage(sender, "/invasion begin [x] - Starts an invasion at wave x (default: 1)");
                this.sendMessage(sender, "/invasion begin c - Starts a continuous invasion");
                this.sendMessage(sender, "/invasion end - Ends the invasion, if active");
                this.sendMessage(sender, "/invasion range <x> - Sets the mobs' spawn range (min: 32, max: 128)");
                this.sendMessage(sender, "/invasion nexusstatus - Displays debug status to bound players (only displayed if nexus is active)");
                break;
            case 2:
                this.sendMessage(sender, "--- Showing Main help page 2 of 2 ---", TextFormatting.GREEN);
                this.sendMessage(sender, "/invasion bolt [targX] [targY] [targZ] [durationTicks] - Spawns a lightning bolt to the specified coordinates for the specified amound of time in ticks");
                this.sendMessage(sender, "/invasion status - Returns whether an invasion is active");
                this.sendMessage(sender, "/invasion debug <IMEntityName> - Spawns an IM mob and binds it to an available nexus for debugging purposes");
                break;
            default:
                this.sendMessage(sender, "Unknown help page " + page, TextFormatting.RED);
        }
    }

    private void changeRadius(ICommandSender sender, Nexus focusNexus, int radius) {
        String username = sender.getName();
        if (focusNexus != null) {
            if ((radius >= 32) && (radius <= 128)) {
                if (focusNexus.setSpawnRadius(radius)) {
                    this.sendMessage(sender, "Set nexus range to " + radius);
                } else {
                    this.sendMessage(sender, username + ": Can't change range while nexus is active", TextFormatting.RED);
                }
            } else {
                this.sendMessage(sender, username + ": Range must be between 32 and 128", TextFormatting.RED);
            }
        } else {
            this.sendMessage(sender, username + ": Right-click the nexus first to set target for command", TextFormatting.RED);
        }
    }

    private void spawnBolt(ICommandSender sender, Nexus focusNexus, String[] args) {
        if (focusNexus != null) {
            int x = focusNexus.getPos().getX();
            int y = focusNexus.getPos().getY();
            int z = focusNexus.getPos().getZ();
            int time = 40;
            if (args.length >= 6) return;
            try {
                if (args.length >= 5) time = Integer.parseInt(args[4]);
                if (args.length >= 4) z += Integer.parseInt(args[3]);
                if (args.length >= 3) y += Integer.parseInt(args[2]);
                if (args.length >= 2) x += Integer.parseInt(args[1]);
            } catch (NumberFormatException e) {
                this.sendMessage(sender, "Bolt argument(s) must be integer(s)", TextFormatting.RED);
            }
            this.sendMessage(sender, "Spawning bolt at (" + x + ", " + y + ", " + z + ") (time=" + time + ")");
            focusNexus.createBolt(x, y, z, time);
        } else {
            this.sendMessage(sender, "Cannot spawn bolt because no nexus detected", TextFormatting.RED);
        }
    }

    private void spawnDebugMob(EntityPlayer sender, Nexus focusNexus, String entityName) {
        if (focusNexus != null) {
            try {
                Class<?> clazz = Class.forName(entityName);
                if (EntityIMLiving.class.isAssignableFrom(clazz)) {
                    Constructor<? extends EntityIMLiving> c = ((Class<? extends EntityIMLiving>) clazz).getConstructor(World.class, Nexus.class);
                    EntityIMLiving entity = c.newInstance(sender.world, focusNexus);
                    entity.setPosition(sender.posX, sender.posY, sender.posZ);
                    sender.world.spawnEntity(entity);
                } else if (EntityIMWolf.class.isAssignableFrom(clazz)) {
                    Constructor<? extends EntityIMWolf> c = ((Class<? extends EntityIMWolf>) clazz).getConstructor(World.class, Nexus.class);
                    EntityIMWolf entity = c.newInstance(sender.world, focusNexus);
                    entity.setPosition(sender.posX, sender.posY, sender.posZ);
                    sender.world.spawnEntity(entity);
                } else {
                    this.sendMessage(sender, "Entity is not an IM mob", TextFormatting.RED);
                }
            } catch (Exception e) {
                this.sendMessage(sender, "Unable to spawn entity", TextFormatting.RED);
                e.printStackTrace();
            }
        } else {
            this.sendMessage(sender, "No nexus detected", TextFormatting.RED);
        }
    }

    private void sendMessage(ICommandSender sender, String msg) {
        this.sendMessage(sender, msg, null);
    }

    private void sendMessage(ICommandSender sender, String msg, TextFormatting format) {
        TextComponentTranslation s = new TextComponentTranslation(msg);
        if (format != null) s.getStyle().setColor(format);
        sender.sendMessage(s);
    }

    @Override
    public String getName() {
        return "invasion";
    }

    @Override
    public String getUsage(ICommandSender icommandsender) {
        return "";
    }
}

 */