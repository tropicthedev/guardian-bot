package com.tropicoss.guardian.discord.commands;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.tropicoss.guardian.config.Config;
import com.tropicoss.guardian.database.DatabaseManager;
import com.tropicoss.guardian.model.*;
import com.tropicoss.guardian.model.ButtonId;
import com.tropicoss.guardian.model.ModalId;
import com.tropicoss.guardian.utils.Cache;
import com.tropicoss.guardian.utils.PlayerInfoFetcher;
import com.tropicoss.guardian.utils.PlayerInfoFetcher.Profile;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.*;
import net.dv8tion.jda.api.entities.MessageEmbed.Field;
import net.dv8tion.jda.api.entities.channel.ChannelType;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.entities.emoji.Emoji;
import net.dv8tion.jda.api.events.interaction.ModalInteractionEvent;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
import net.dv8tion.jda.api.events.interaction.component.StringSelectInteractionEvent;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import net.dv8tion.jda.api.interactions.components.ActionRow;
import net.dv8tion.jda.api.interactions.components.buttons.Button;
import net.dv8tion.jda.api.interactions.components.buttons.ButtonInteraction;
import net.dv8tion.jda.api.interactions.components.selections.SelectOption;
import net.dv8tion.jda.api.interactions.components.selections.StringSelectMenu;
import net.dv8tion.jda.api.interactions.components.text.TextInput;
import net.dv8tion.jda.api.interactions.components.text.TextInputStyle;
import net.dv8tion.jda.api.interactions.modals.Modal;
import org.jetbrains.annotations.NotNull;

import java.awt.*;
import java.sql.SQLException;
import java.time.Instant;
import java.util.*;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

import static com.tropicoss.guardian.Guardian.LOGGER;

public class Onboarding extends ListenerAdapter {
    private final Config config = Config.getInstance();
    private final DatabaseManager databaseManager;
    private final Map<String, List<QuestionAnswers>> conversationState = new ConcurrentHashMap<>();

    public Onboarding(DatabaseManager databaseManager) {
        this.databaseManager = databaseManager;
    }

    @Override
    public void onSlashCommandInteraction(@NotNull SlashCommandInteractionEvent event) {

        try {
            if (Objects.requireNonNull(event.getUser()).isBot()) return;

            if (!event.getMember().hasPermission(Permission.ADMINISTRATOR)) {
                event.reply("Insufficient Permissions").setEphemeral(true).queue();
                return;
            }

            if (event.getName().equals("welcome")) {
                handleWelcomeCommand(event);
            }

            if (event.getName().equals("accept")) {
                handleAcceptCommand(event);
            }

            if (event.getName().equals("deny")) {
                handleDenyCommand(event);
            }
        } catch (RuntimeException e) {
            LOGGER.error("An error occurred while running command {} {}", event.getName(), e.getMessage());

            event.reply("An error occurred while running command, please try again")
                    .setEphemeral(true)
                    .queue();
        }
    }

    private void handleAcceptCommand(SlashCommandInteractionEvent event) {

        if (event.getChannel().getType() != ChannelType.GUILD_PRIVATE_THREAD) {
            event.reply("This can only be ran in a private guild thread (Interview Thread)")
                    .setEphemeral(true)
                    .queue();
            return;
        }

        OptionMapping optionMapping = event.getOption("ign");

        if (optionMapping == null) return;

        try {

            Profile playerProfile = PlayerInfoFetcher.getProfile(optionMapping.getAsString());

            if (playerProfile == null) {
                event.reply("Member could not be found, is the IGN correct ?").setEphemeral(true).queue();
                return;
            }

            Guild guild = event.getGuild();

            String memberId = databaseManager.getMemberFromChannelId(event.getChannelId());

            Member member = event.getGuild().getMemberById(memberId);

            if (member == null) {
                event.reply("Member could not be found, are they still in the server ?").setEphemeral(true).queue();
                return;
            }

            TextChannel channel = guild.getChannelById(TextChannel.class, config.getConfig().getMember().getChannel());

            if (channel == null) {
                event.reply("The guild channel could not be found, ensure it exists and the correct id is present in the config file")
                        .setEphemeral(true)
                        .queue();
                return;
            }

            Role role = guild.getRoleById(config.getConfig().getMember().getRole());

            if (role == null) {
                event.reply("The member role could not be found, ensure it exists and the correct id is present in the config file")
                        .setEphemeral(true)
                        .queue();
                return;
            }

            guild.addRoleToMember(member.getUser(), role).queue();

            channel.sendMessage(config.getConfig().getMember().getMessage().replace("{member}", member.getAsMention())).queue();

            databaseManager.addInterviewResponse(UUID.randomUUID().toString(), event.getMember().getId(), event.getChannelId(),
                    "Member Accepted", Status.ACCEPTED);

            databaseManager.addMember(memberId.toString(), playerProfile.data.player.id, false);

            event.reply("Member Accepted").setEphemeral(true).queue();
        } catch (Exception e) {
            LOGGER.error("There was an error trying to accept member: {}",e.getMessage());
            event.reply("There was an error trying to accept member please try again. If the problem persists check the console for any errors that may occur.").setEphemeral(true).queue();
        }
    }

    private void handleWelcomeCommand(SlashCommandInteractionEvent event) {

        EmbedBuilder embedBuilder = new EmbedBuilder();

        embedBuilder
                .setTitle("Beep Boop")
                .setColor(Color.BLUE)
                .setDescription(config.getConfig().getWelcome().getMessage())
                .setTimestamp(Instant.now());

        TextChannel textChannel = event.getJDA().getTextChannelById(config.getConfig().getWelcome().getChannel());

        if (textChannel == null) {
            event.reply("Welcome channel could not be found, ensure that the channel id is correct and that it is a TEXT CHANNEL").setEphemeral(true).queue();

            LOGGER.error("Welcome channel could not be found, ensure that the channel id is correct and that it is a TEXT CHANNEL");

            return;
        }

        textChannel.sendMessageEmbeds(embedBuilder.build())
                .addActionRow(
                        Button.primary(
                                ButtonId.APPLY,
                                "Apply"
                        ).withEmoji(Emoji.fromFormatted("\uD83D\uDCDC"))
                )
                .queue();
    }

    private void handleDenyCommand(SlashCommandInteractionEvent event) {
        if (event.getChannel().getType() != ChannelType.GUILD_PRIVATE_THREAD) {
            event.reply("This can only be ran in a private guild thread (Interview Thread)").queue();
            return;
        }
        OptionMapping reasonMapping = event.getOption("reason");

        if (reasonMapping == null) return;

        try {

            String memberId = databaseManager.getMemberFromChannelId(event.getChannelId());

            Member member = event.getGuild().getMemberById(memberId);

            if (member == null) {
                event.reply("User could not be found, are they still in the server ?").setEphemeral(true).queue();
                return;
            }

            Objects.requireNonNull(event.getMember()).getUser().openPrivateChannel().flatMap(channel ->
                    channel.sendMessage("You have been denied for the reason: " + reasonMapping.getAsString())
            ).queue();

            databaseManager.addInterviewResponse(UUID.randomUUID().toString(),
                    event.getMember().getId(),
                    event.getChannelId(),
                    "Member Denied", Status.DENIED);

            event.reply("Member Denied").setEphemeral(true).queue();
        } catch (Exception e) {
            LOGGER.error("There was an error trying to deny member: {}", e.getMessage());
            event.reply("There was an error trying to deny member please try again. If the problem persists check the console for any errors that may occur.").setEphemeral(true).queue();
        }
    }

    @Override
    public void onButtonInteraction(ButtonInteractionEvent event) {
        if (event.getUser().isBot()) return;

        switch (event.getButton().getId()) {
            case ButtonId.APPLY -> handleApplyButtonInteraction(event);
            case ButtonId.ACCEPT -> handleAcceptButtonInteraction(event);
            case ButtonId.RESET -> handleResetButtonInteraction(event);
            case ButtonId.BAN -> handleBanButtonInteraction(event);
            case null, default -> {
            }
        }
    }

    @Override
    public void onMessageReceived(MessageReceivedEvent event) {
        if (event.getAuthor().isBot()) return;

        String userId = event.getAuthor().getId();

        List<QuestionAnswers> state = conversationState.get(userId);

        if (state != null && !state.isEmpty()) {
            QuestionAnswers currentQuestion = state.getLast();

            currentQuestion.answer = event.getMessage().getContentRaw();

            List<String> questions = config.getConfig().getApplication().getQuestions();

            if (state.size() < questions.size()) {
                String nextQuestion = questions.get(state.size());
                state.add(new QuestionAnswers(nextQuestion, null));

                event.getChannel().sendMessage(nextQuestion).queue();

                return;
            }

            EmbedBuilder embedBuilder = new EmbedBuilder();

            embedBuilder.setTitle("Application Received").setColor(Color.YELLOW).setTimestamp(Instant.now());

            Message applicationConfirmationMessage = event.getChannel().sendMessageEmbeds(embedBuilder.build()).complete();

            try {
                databaseManager.addApplication(applicationConfirmationMessage.getId(), getConversationStateAsString(state), userId);
            } catch (SQLException e) {
                LOGGER.error("Error storing application for userID: {} : {}", userId, e.getMessage());

                applicationConfirmationMessage.reply("Error storing your application please try again or contact one of the admins").queue();

                return;
            }

            conversationState.remove(userId);

            TextChannel applicationsChannel = event.getMessage().getJDA().getTextChannelById(config.getConfig().getApplication().getChannel());

            if (applicationsChannel == null) {
                LOGGER.error("Applications channel could not be found, ensure that the channel id is correct and that it is a TEXT CHANNEL");

                event.getChannel().sendMessage("There was an error while sending the application to the admins, please try again or contact and admin for further assistance").queue();

                return;
            }

            EmbedBuilder applicationEmbedBuilder = new EmbedBuilder();

            for (QuestionAnswers questionAnswers : state) {
                Field field = new Field(questionAnswers.question, questionAnswers.answer, false);

                applicationEmbedBuilder.addField(field);
            }

            applicationEmbedBuilder
                    .setColor(Color.YELLOW)
                    .setAuthor(event.getAuthor().getAsTag(), null, event.getAuthor().getAvatarUrl())
                    .setTitle(String.format("%s has submitted an application", event.getAuthor().getAsTag()))
                    .setThumbnail(event.getAuthor().getAvatarUrl())
                    .setTimestamp(Instant.now());

            List<SelectOption> selectOptions = new ArrayList<>();

            for (String response : config.getConfig().getApplication().getDenyReasons()) {
                selectOptions.add(SelectOption.of(response, response));
            }

            Message applicationEmbedMessage = applicationsChannel.sendMessageEmbeds(applicationEmbedBuilder.build())
                    .addActionRow(
                            StringSelectMenu.create(ButtonId.DENY)
                                    .setPlaceholder("Deny Application Reasons")
                                    .setMinValues(1)
                                    .setMaxValues(config.getConfig().getApplication().getDenyReasons().size())
                                    .addOptions(selectOptions)
                                    .addOption("Custom", "Custom", "THIS WILL OVERWRITE ALL OTHER SELECTED OPTIONS")
                                    .build()
                    )
                    .addActionRow(
                            Button.primary(ButtonId.ACCEPT, "Accept")
                                    .withEmoji(Emoji.fromFormatted("✅")),
                            Button.danger(ButtonId.BAN, "Ban")
                                    .withEmoji(Emoji.fromFormatted("🦵")))
                    .complete();

            try {
                databaseManager.updateApplication(applicationConfirmationMessage.getId(), applicationEmbedMessage.getId());
            } catch (SQLException e) {
                LOGGER.error("Error storing updated application with new message ID {}", e.getMessage());
            }
        }
    }

    private void handleApplyButtonInteraction(ButtonInteractionEvent event) {
        Cache<Object, Object> cache = Cache.getInstance();

        if (cache.get("timeout::" + event.getUser().getId()) != null) {

            event.reply(String.format("Please wait for %s minutes after your application to apply again thank you.", config.getConfig().getApplication().getTimeout()))
                    .setEphemeral(true)
                    .queue();

            return;
        }

        try {
            if (databaseManager.hasPendingApplication(event.getUser().getId())) {

                event.reply("You already have an application pending, please wait for a staff member to review your application.")
                        .setEphemeral(true)
                        .queue();

                return;
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        cache.put("timeout::" + event.getUser().getId(), "1", 5, TimeUnit.MINUTES);

        List<String> questions = config.getConfig().getApplication().getQuestions();

        event.reply("Check your direct messages").setEphemeral(true).queue();

        Objects.requireNonNull(event.getMember()).getUser().openPrivateChannel().flatMap(channel ->
                channel.sendMessage(questions.getFirst())
        ).queue();

        List<QuestionAnswers> questionAnswersList = new ArrayList<>();

        questionAnswersList.add(new QuestionAnswers(questions.getFirst(), null));

        conversationState.put(event.getUser().getId(), questionAnswersList);
    }

    // TODO: Prevent Duplicate Application Responses from being submitted

    private void handleAcceptButtonInteraction(ButtonInteraction event) {
        try {
            if (!event.getMember().hasPermission(Permission.ADMINISTRATOR)) {
                event.reply("Insufficient Permissions").queue();
                return;
            }

            Application application = databaseManager.getApplication(event.getMessageId());

            if (application == null) {
                event.reply("Could not find application in the database").setEphemeral(true).queue();

                LOGGER.error("Could not find application in the database");

                return;
            }

            ApplicationResponse existingApplicationResponse = databaseManager.getApplicationResponseByApplicationId(application.getApplicationId());

            if (existingApplicationResponse != null && !Objects.equals(existingApplicationResponse.getStatus(), Status.RESET)) {
                event.reply("This application has already been updated").setEphemeral(true).queue();

                LOGGER.error("This application has already been updated: {}", application.getApplicationId());

                return;
            }

            Member member = Objects.requireNonNull(event.getGuild()).getMemberById(application.getDiscordId());

            Role role = event.getGuild().getRoleById(config.getConfig().getInterview().getRole());

            TextChannel interviewChannel = event.getGuild().getTextChannelById(config.getConfig().getInterview().getChannel());

            if (member == null) {
                event.reply("Member could not be found, are they still apart of the server ?").setEphemeral(true).queue();

                LOGGER.error("Member could not be found, are they still apart of the server ?");

                return;
            }
            if (role == null) {
                event.reply("Role could not be found, ensure that the role id is correct in the config file").setEphemeral(true).queue();

                LOGGER.error("Role could not be found, ensure that the role id is correct in the config file");

                return;
            }

            if (interviewChannel == null) {
                event.reply("Interview channel could not be found, ensure that the channel id is correct and that it is a TEXT CHANNEL").setEphemeral(true).queue();

                LOGGER.error("Interview channel could not be found, ensure that the channel id is correct and that it is a TEXT CHANNEL");

                return;
            }

            interviewChannel.createThreadChannel(String.format("%s's Interview", member.getUser().getName()), true).queue(channel -> {

                channel.addThreadMember(member).queue();
                channel.addThreadMember(event.getMember()).queue();

                try {
                    databaseManager.upsertApplicationResponse(UUID.randomUUID().toString(), event.getMember().getId(), application.getApplicationId(), "", Status.ACCEPTED);
                    databaseManager.addInterview(channel.getId(), application.getApplicationId());
                } catch (SQLException e) {
                    event.reply("There was an error when storing application response").setEphemeral(true).queue();

                    LOGGER.error("There was an error when storing application response {}", e.getMessage());
                    return;
                }

                channel.sendMessage(config.getConfig().getInterview().getMessage().replaceAll("\\{member}", member.getUser().getAsTag())).queue();
            });

            member.getGuild().addRoleToMember(UserSnowflake.fromId(member.getId()), role).queue();

            member.getUser().openPrivateChannel().flatMap(channel -> channel.sendMessage(String.format("You have been accepted for an interview in %s", interviewChannel.getAsMention()))).queue();

            MessageEmbed embed = event.getMessage().getEmbeds().getFirst();

            EmbedBuilder embedBuilder = new EmbedBuilder(embed);

            embedBuilder.setColor(Color.green);

            event.deferEdit().queue();

            event.getMessage().editMessageEmbeds(
                    embedBuilder.build()
            ).setActionRow(
                    Button.success(ButtonId.JOIN_THREAD, "Join Tread")
                            .withEmoji(Emoji.fromFormatted("🚀")),
                    Button.primary(ButtonId.RESET, "Reset")
                            .withEmoji(Emoji.fromFormatted("🔙"))
            ).queue();

        } catch (Exception e) {
            LOGGER.error("There was an error when accepting member {}", e.getMessage());
        }
    }

    @Override
    public void onStringSelectInteraction(StringSelectInteractionEvent event) {
        if (event.getComponentId().equals(ButtonId.DENY) && !event.getMember().getUser().isBot()) {

            if (!event.getMember().hasPermission(Permission.ADMINISTRATOR)) {
                event.reply("Insufficient Permissions").queue();
                return;
            }

            List<String> selectedValues = event.getValues();

            String reason = null;

            if (selectedValues.contains("Custom")) {
                event.replyModal(Modal.create(ModalId.REASON, "Guardian - Deny Reason")
                        .addActionRow(TextInput.create("deny_reason", "Reason for Denial", TextInputStyle.PARAGRAPH)
                                .setRequired(true).build()).build()).queue();

                return;
            }

            StringBuilder denialReasons = new StringBuilder();

            for (String value : selectedValues) {
                denialReasons.append(value).append("\n");
            }
            reason = denialReasons.toString();
            try {
                handleDeny(event, reason);
            } catch (SQLException e) {
                LOGGER.error("Error handling denial: {}", e.getMessage());

                event.reply("An error occurred while processing the denial. Please try again later.").setEphemeral(true).queue();
            }
        }
    }

    @Override
    public void onModalInteraction(@NotNull ModalInteractionEvent event) {
        if (event.getModalId().equals(ModalId.REASON)) {

            if (!event.getMember().hasPermission(Permission.ADMINISTRATOR)) {
                event.reply("Insufficient Permissions").queue();
                return;
            }

            String customReason = event.getValue("deny_reason").getAsString();

            try {
                handleDeny(event, customReason);
            } catch (SQLException e) {
                LOGGER.error("Error handling denial: {}", e.getMessage());

                event.reply("An error occurred while processing the denial. Please try again later.").setEphemeral(true).queue();
            }
        }
    }

    private void handleDeny(StringSelectInteractionEvent event, String reason) throws SQLException {
        if (!event.getMember().hasPermission(Permission.ADMINISTRATOR)) {
            event.reply("Insufficient Permissions").queue();
            return;
        }

        Application application = databaseManager.getApplication(event.getMessageId());

        if (application == null) {
            LOGGER.error("Could not find application in the database");
            event.reply("Could not find application in the database").setEphemeral(true).queue();
            return;
        }

        try {
            ApplicationResponse existingApplicationResponse = databaseManager.getApplicationResponseByApplicationId(application.getApplicationId());

            if (existingApplicationResponse != null && existingApplicationResponse.getStatus() != Status.RESET) {
                event.reply("This application has already been updated").setEphemeral(true).queue();

                LOGGER.error("This application has already been updated");

                return;
            }
        } catch (SQLException ignored) {
        }

        Guild guild = event.getGuild();
        if (guild == null) {
            LOGGER.error("Guild could not be found, please try again");
            event.reply("Guild could not be found, please try again").setEphemeral(true).queue();
            return;
        }

        Member member = guild.getMemberById(application.getDiscordId());
        if (member == null) {
            LOGGER.error("Member could not be found, are they still in the server?");
            event.reply("Member could not be found, are they still in the server?").setEphemeral(true).queue();
            return;
        }

        databaseManager.upsertApplicationResponse(UUID.randomUUID().toString() ,event.getMember().getId(), application.getApplicationId(), reason, Status.DENIED);

        member.getUser().openPrivateChannel().flatMap(channel -> channel.sendMessage("Your application has been denied for the following reason(s):\n" + reason)).queue();

        MessageEmbed embed = event.getMessage().getEmbeds().getFirst();

        EmbedBuilder embedBuilder = new EmbedBuilder(embed).setColor(Color.RED).setFooter(
                String.format("User Denied By %s \nReason %b\n", event.getMember().getUser().getAsTag(), reason));

        event.deferEdit().queue();

        event.getMessage().editMessageEmbeds(embedBuilder.build()).setActionRow(
                Button.primary(
                        ButtonId.RESET,
                        "Reset"
                ).withEmoji(Emoji.fromFormatted("🔙"))
        ).queue();
    }

    private void handleDeny(ModalInteractionEvent event, String reason) throws SQLException {
        if (!event.getMember().hasPermission(Permission.ADMINISTRATOR)) {
            event.reply("Insufficient Permissions").queue();
            return;
        }

        String messageId = event.getMessage().getId();

        Application application = databaseManager.getApplication(messageId);

        if (application == null) {
            LOGGER.error("Could not find application in the database");

            event.reply("Could not find application in the database").setEphemeral(true).queue();

            return;
        }

        try {
            ApplicationResponse existingApplicationResponse = databaseManager.getApplicationResponseByApplicationId(application.getApplicationId());

            if (existingApplicationResponse != null && !Objects.equals(existingApplicationResponse.getStatus(), Status.RESET)) {
                event.reply("This application has already been updated").setEphemeral(true).queue();

                LOGGER.error("This application has already been updated");

                return;
            }
        } catch (SQLException ignored) {
        }

        Guild guild = event.getGuild();
        if (guild == null) {
            LOGGER.error("Guild could not be found, please try again");

            event.reply("Guild could not be found, please try again").setEphemeral(true).queue();

            return;
        }

        Member member = guild.getMemberById(application.getDiscordId());

        if (member == null) {
            LOGGER.error("Member could not be found, are they still in the server?");

            event.reply("Member could not be found, are they still in the server?").setEphemeral(true).queue();

            return;
        }

        databaseManager.upsertApplicationResponse(UUID.randomUUID().toString(), event.getMember().getId(), application.getApplicationId(), reason, Status.DENIED);

        member.getUser().openPrivateChannel().flatMap(channel -> channel.sendMessage("Your application has been denied for the following reason(s):\n" + reason)).queue();

        MessageEmbed embed = event.getMessage().getEmbeds().getFirst();

        EmbedBuilder embedBuilder = new EmbedBuilder(embed).setColor(Color.RED).setFooter(
                String.format("User Denied By %s \nReason %b\n", event.getMember().getUser().getAsTag(), reason));

        event.deferEdit().queue();

        event.getMessage().editMessageEmbeds(embedBuilder.build()).setActionRow(
                Button.primary(
                        ButtonId.RESET,
                        "Reset"
                ).withEmoji(Emoji.fromFormatted("🔙"))
        ).queue();

        event.reply("Application has been denied and the user has been notified.").setEphemeral(true).queue();
    }

    private void handleResetButtonInteraction(ButtonInteractionEvent event) {
        ApplicationResponse applicationResponse;
        Application application;
        Member member;

        if (!Objects.requireNonNull(event.getMember()).hasPermission(Permission.ADMINISTRATOR)) {
            event.reply("Insufficient Permissions").setEphemeral(true).queue();
            return;
        }

        try {
            applicationResponse = databaseManager.getApplicationResponseByMessageId(event.getMessageId());
            application = databaseManager.getApplication(event.getMessageId());
            member = Objects.requireNonNull(event.getGuild()).getMemberById(application.getDiscordId());
            databaseManager.resetApplication(event.getMessageId());

        } catch (Exception e) {
            LOGGER.error("There was an error while getting the application response: {}", e.getMessage());
            event.reply("There was an error while trying to get the application response").setEphemeral(true).queue();
            return;
        }

        if (member == null) {
            LOGGER.error("There was an error getting member, are they still in the server ?");
            event.reply("There was an error getting member, are they still in the server ?").setEphemeral(true).queue();
            return;
        }

        if (Objects.equals(applicationResponse.getStatus(), Status.ACCEPTED)) {
            List<Role> memberRoles = member.getRoles();

            for (Role memberRole : memberRoles) {
                member.getGuild().removeRoleFromMember(member.getUser(), memberRole).queue();
            }
        }

        member.getUser().openPrivateChannel().flatMap(privateChannel -> privateChannel.sendMessage("Your application is being reconsidered, once a decision is made you will be updated")).queue();

        List<SelectOption> selectOptions = new ArrayList<>();

        for (String response : config.getConfig().getApplication().getDenyReasons()) {
            selectOptions.add(SelectOption.of(response, response));
        }

        MessageEmbed embed = event.getMessage().getEmbeds().getFirst();

        EmbedBuilder embedBuilder = new EmbedBuilder(embed).setColor(Color.YELLOW).setFooter(
                String.format("Application Reset by %s", member.getAsMention()));

        event.deferEdit().queue();

        event.getMessage().editMessageEmbeds(embedBuilder.build()).setComponents(
                ActionRow.of(StringSelectMenu.create(ButtonId.DENY)
                        .setPlaceholder("Deny Application Reasons")
                        .setMinValues(1)
                        .setMaxValues(config.getConfig().getApplication().getDenyReasons().size())
                        .addOptions(selectOptions)
                        .addOption("Custom", "Custom", "THIS WILL OVERWRITE ALL OTHER SELECTED OPTIONS")
                        .build()),
                ActionRow.of(Button.primary(ButtonId.ACCEPT, "Accept")
                                .withEmoji(Emoji.fromFormatted("✅")),
                        Button.danger(ButtonId.BAN, "Ban")
                                .withEmoji(Emoji.fromFormatted("🦵")))
        ).queue();
    }

    private void handleBanButtonInteraction(ButtonInteractionEvent event) {
        Application application;

        if (!event.getMember().hasPermission(Permission.ADMINISTRATOR)) {
            event.reply("Insufficient Permissions").queue();
            return;
        }


        try {
            application = databaseManager.getApplication(event.getMessageId());
        } catch (SQLException e) {
            LOGGER.error("There was an error while getting the application");

            event.reply("There was an error while trying to get the application").setEphemeral(true).queue();
            return;
        }

        Member member = event.getGuild().getMemberById(application.getDiscordId());

        if (member == null) {
            LOGGER.error("There was an error while getting the member to ban, are they still in the server ?");

            event.reply("There was an error while getting the member to ban, are they still in the server ?").setEphemeral(true).queue();
            return;
        }

        member.ban(0, TimeUnit.MINUTES).queue();

        try {
            databaseManager.upsertApplicationResponse(UUID.randomUUID().toString(), event.getMember().getId(), application.getApplicationId(), "Member Banned", Status.BANNED);
        } catch (SQLException e) {
            LOGGER.error("There was an error while storing application response for banned user");

            event.reply("There was an error while storing application response for banned user").setEphemeral(true).queue();
            return;
        }

        member.getUser().openPrivateChannel().flatMap(privateChannel -> privateChannel.sendMessage("You have been banned")).queue();

        MessageEmbed embed = event.getMessage().getEmbeds().getFirst();

        EmbedBuilder embedBuilder = new EmbedBuilder(embed).setColor(Color.RED).setFooter(
                String.format("Application Banned by %s", member.getAsMention()));

        event.deferEdit().queue();

        event.getMessage().editMessageEmbeds(embedBuilder.build()).setComponents().queue();
    }

    private String getConversationStateAsString(List<QuestionAnswers> state) {
        if (state.isEmpty()) return null;

        try {
            ObjectMapper mapper = new ObjectMapper();
            ArrayNode arrayNode = mapper.createArrayNode();

            for (QuestionAnswers qa : state) {
                ObjectNode qaNode = mapper.createObjectNode();
                qaNode.put("question", qa.question);
                qaNode.put("answer", qa.answer != null ? qa.answer : "No answer yet");
                arrayNode.add(qaNode);
            }

            return mapper.writerWithDefaultPrettyPrinter().writeValueAsString(arrayNode);

        } catch (Exception e) {
            LOGGER.error("Error while parsing Application Conversation {}", e.getMessage());
            return null;
        }
    }

    private class QuestionAnswers {
        public String question;
        public String answer;

        public QuestionAnswers(String question, String answer) {
            this.question = question;
            this.answer = answer;
        }
    }
}