package com.tropicoss.guardian.minecraft.callback;

import com.google.gson.Gson;
import com.tropicoss.guardian.config.ConfigurationManager;
import com.tropicoss.guardian.discord.Bot;
import com.tropicoss.guardian.minecraft.event.EntityDeathEvents;
import com.tropicoss.guardian.minecraft.event.PlayerDeathEvents;
import com.tropicoss.guardian.networking.messaging.EntityDeathMessage;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.registry.RegistryKey;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.world.World;

import static com.tropicoss.guardian.Guardian.*;

public final class EntityDeathCallback extends ServerEventCallback implements PlayerDeathEvents, EntityDeathEvents {
    private final Bot bot;


    public EntityDeathCallback(String host, String serverName, String mode, String port, Bot bot) {
        super(host, serverName, mode, port);
        this.bot = bot;
    }

    @Override
    public void onPlayerDeath(ServerPlayerEntity player, DamageSource source) {
        String message = source.getDeathMessage(player).getString();

        RegistryKey<World> registry = player.getWorld().getRegistryKey();

        String dimension = registry.getValue().toString();

        // Type Casting to Int to remove decimal points
        String coordinates = String.format("*%s at %s, %s, %s*", dimension.replaceAll(".*:", ""), (int) player.getX(), (int) player.getY(), (int) player.getZ());

        EntityDeathMessage entityDeathMessage = new EntityDeathMessage(message, coordinates, this.getServerName());

        String json = new Gson().toJson(entityDeathMessage);

        switch (this.getMode()) {
            case "server" -> {
                SOCKET_SERVER.broadcast(json);

                this.bot.sendDeathMessage(this.getServerName(), message, coordinates);
            }

            case "client" -> SOCKET_CLIENT.send(json);

            case "standalone" ->  this.bot.sendDeathMessage(this.getServerName(), message, coordinates);
        }
    }

    @Override
    public void onEntityDeath(LivingEntity entity, DamageSource source) {

        if (!entity.hasCustomName()) return;

        String message = source.getDeathMessage(entity).getString();

        RegistryKey<World> registry = entity.getWorld().getRegistryKey();

        String dimension = registry.getValue().toString();

        String coordinates = String.format("*%s at %s, %s, %s*", dimension.replaceAll(".*:", ""), (int) entity.getX(), (int) entity.getY(), (int) entity.getZ());

        EntityDeathMessage entityDeathMessage = new EntityDeathMessage(message, coordinates, getServerName());

        String json = new Gson().toJson(entityDeathMessage);

        switch (this.getMode()) {
            case "server" -> {
                SOCKET_SERVER.broadcast(json);

                this.bot.sendDeathMessage(this.getServerName(), message, coordinates);
            }

            case "client" -> SOCKET_CLIENT.send(json);

            case "standalone" ->  this.bot.sendDeathMessage(this.getServerName(), message, coordinates);
        }
    }
}
