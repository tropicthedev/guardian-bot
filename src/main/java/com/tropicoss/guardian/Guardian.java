package com.tropicoss.guardian;


import com.tropicoss.guardian.config.ConfigurationManager;
import com.tropicoss.guardian.discord.commands.OnboardingCommand;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import net.fabricmc.api.DedicatedServerModInitializer;
import net.fabricmc.loader.api.FabricLoader;

import java.io.IOException;

public class Guardian implements DedicatedServerModInitializer {

    @Override
    public void onInitializeServer() {
        try {
            ConfigurationManager configManager = new ConfigurationManager(FabricLoader.getInstance().getConfigDir().resolve("guardian.json").toString());

            JDA api = JDABuilder.createDefault(configManager.getSetting("botToken"))
                    .build()
                    .awaitReady();

            api.addEventListener(new OnboardingCommand());

            Guild guild = api.getGuildById(configManager.getSetting("guildId"));

            if (guild != null) {
                guild.upsertCommand(
                        Commands.slash("welcome", "Creates a discord embed for new users to start onboarding")
                ).queue();
            }
        }

        catch (InterruptedException e ) {
            throw new RuntimeException(e);
        }
    }
}
