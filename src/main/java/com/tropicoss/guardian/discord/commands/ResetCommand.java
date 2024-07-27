package com.tropicoss.guardian.discord.commands;

import com.tropicoss.guardian.utils.Cache;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;

import java.util.Objects;

import static com.tropicoss.guardian.Guardian.LOGGER;

public class ResetCommand extends ListenerAdapter {

    @Override
    public void onSlashCommandInteraction(SlashCommandInteractionEvent event) {
        try {
            if (Objects.requireNonNull(event.getUser()).isBot()) return;

            if (event.getName().equals("reset")) {
                OptionMapping optionMapping =  event.getOption("member");

                if(optionMapping == null) return;

                String userId = optionMapping.getAsUser().getId();

                Cache.getInstance().remove("timeout::" + userId);

                if(!Cache.getInstance().containsKey("timeout::" + userId)) {
                    event.reply("User's application timeout reset was successful").queue();

                    return;
                }

                event.reply("Unable to reset users application timeout reset").queue();
            }

        } catch (RuntimeException e) {
            LOGGER.error("An error occurred while trying to reset users application timeout {}", e.getMessage());

            event.reply("An error occurred while sending welcome embed, please try again")
                    .setEphemeral(true)
                    .queue();
        }
    }
}
