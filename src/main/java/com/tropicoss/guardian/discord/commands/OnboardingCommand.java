package com.tropicoss.guardian.discord.commands;

import com.tropicoss.guardian.config.Config;
import com.tropicoss.guardian.discord.Bot;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.entities.emoji.Emoji;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.components.buttons.Button;

import java.awt.*;
import java.time.Instant;

public class OnboardingCommand extends ListenerAdapter {
    @Override
    public void onSlashCommandInteraction(SlashCommandInteractionEvent event) {
        Config config = Config.getInstance();

        if (event.getName().equals("welcome")) {

            EmbedBuilder embedBuilder = new EmbedBuilder();

            embedBuilder
                    .setTitle("Beep Boop")
                    .setColor(Color.BLUE)
                    .setDescription(config.getConfig().getWelcome().getMessage())
                    .setTimestamp(Instant.now());

            TextChannel textChannel = event.getJDA().getTextChannelById(config.getConfig().getWelcome().getChannel());

            textChannel.sendMessageEmbeds(embedBuilder.build())
                    .addActionRow(
                            Button.primary(
                                    "Guardian:MEMBER_APPLY",
                                    "Apply"
                            ).withEmoji(Emoji.fromFormatted("\uD83D\uDCDC"))
                    )
                    .queue();
        }
    }
}