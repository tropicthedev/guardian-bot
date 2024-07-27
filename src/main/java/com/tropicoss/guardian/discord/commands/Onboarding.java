package com.tropicoss.guardian.discord.commands;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.tropicoss.guardian.config.Config;
import com.tropicoss.guardian.database.dao.impl.ApplicationResponseDAOImpl;
import com.tropicoss.guardian.database.model.ApplicationResponse;
import com.tropicoss.guardian.database.model.Status;
import com.tropicoss.guardian.utils.Cache;
import com.tropicoss.guardian.database.dao.impl.ApplicationDAOImpl;
import com.tropicoss.guardian.database.model.Application;
import com.tropicoss.guardian.ids.ButtonIds;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.entities.UserSnowflake;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.entities.channel.concrete.ThreadChannel;
import net.dv8tion.jda.api.entities.emoji.Emoji;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.components.buttons.Button;
import net.dv8tion.jda.api.entities.MessageEmbed.Field;
import net.dv8tion.jda.api.interactions.components.buttons.ButtonInteraction;
import net.dv8tion.jda.api.requests.restaction.ThreadChannelAction;
import org.jetbrains.annotations.NotNull;

import static com.tropicoss.guardian.Guardian.LOGGER;

import java.awt.*;
import java.sql.SQLException;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

public class Onboarding extends ListenerAdapter {
    private final Config config = Config.getInstance();
    private final ApplicationDAOImpl applicationDAO = new ApplicationDAOImpl();
    private final ApplicationResponseDAOImpl applicationResponseDAO = new ApplicationResponseDAOImpl();
    private final Map<String, List<QuestionAnswers>> conversationState = new ConcurrentHashMap<>();

    public Onboarding() throws SQLException {
    }

    @Override
    public void onSlashCommandInteraction(@NotNull SlashCommandInteractionEvent event) {

        try {
            if (Objects.requireNonNull(event.getUser()).isBot()) return;

            if (event.getName().equals("welcome")) {

                EmbedBuilder embedBuilder = new EmbedBuilder();

                embedBuilder
                        .setTitle("Beep Boop")
                        .setColor(Color.BLUE)
                        .setDescription(config.getConfig().getWelcome().getMessage())
                        .setTimestamp(Instant.now());

                TextChannel textChannel = event.getJDA().getTextChannelById(config.getConfig().getWelcome().getChannel());

                if (textChannel == null) {
                    LOGGER.error("The text channel ID provided for the welcome channel does not seem to be valid, please ensure that the correct ID is used");

                    return;
                }

                textChannel.sendMessageEmbeds(embedBuilder.build())
                        .addActionRow(
                                Button.primary(
                                        ButtonIds.APPLY,
                                        "Apply"
                                ).withEmoji(Emoji.fromFormatted("\uD83D\uDCDC"))
                        )
                        .queue();
            }
        } catch (RuntimeException e) {
            LOGGER.error("An error occurred while sending welcome embed {}", e.getMessage());

            event.reply("An error occurred while sending welcome embed, please try again")
                    .setEphemeral(true)
                    .queue();
        }
    }

    @Override
    public void onButtonInteraction(ButtonInteractionEvent event) {
        if (event.getUser().isBot()) return;

        switch (event.getButton().getId()) {
            case ButtonIds.APPLY -> {
                handleApplyButtonInteraction(event);
            }
            case ButtonIds.ACCEPT -> {
                handleAcceptButtonInteraction(event);
            }
            case null, default -> {}
        }
    }

    private void handleApplyButtonInteraction(ButtonInteraction event) {
        Cache<Object, Object> cache = Cache.getInstance();

        if (cache.get("timeout::" + event.getUser().getId()) != null) {

            event.reply(String.format("Please wait for %s minutes after your application to apply again thank you.", config.getConfig().getApplication().getTimeout()))
                    .setEphemeral(true)
                    .queue();

            return;
        }

        try {
            if (applicationDAO.pendingUserApplication(event.getUser().getId())) {

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
    // TODO: Fix Webhook error with editing embed

    private void handleAcceptButtonInteraction(ButtonInteraction event) {
       try{
           Application application = applicationDAO.getApplicationByMessageId(event.getMessageId());

           if(application == null) {
               event.reply("Could not find application in the database").setEphemeral(true).queue();
           }

           Member member = Objects.requireNonNull(event.getGuild()).getMemberById(application.getDiscordId());

           Role role = event.getGuild().getRoleById(config.getConfig().getInterview().getRole());

           TextChannel interviewChannel = event.getGuild().getTextChannelById(config.getConfig().getInterview().getChannel());

           interviewChannel.createThreadChannel(String.format("%s's Interview", member.getUser().getName()), true).queue(channel -> {
               channel.addThreadMember(member).queue();
               channel.addThreadMember(event.getMember()).queue();

               ApplicationResponse applicationResponse = new ApplicationResponse(event.getMember().getIdLong(), application.getApplicationId(), "Member accepted", Status.ACCEPTED);

               try {
                   applicationResponseDAO.addApplicationResponse(applicationResponse);
               } catch (SQLException e) {
                   LOGGER.error("There was an error when storing application response {}", e.getMessage());
               }

               channel.sendMessage(config.getConfig().getInterview().getMessage().replaceAll("\\{member}", String.format("<@%s>", member.getUser().getName()))).queue();
           });

           member.getGuild().addRoleToMember(UserSnowflake.fromId(member.getId()), role).queue();

           member.getUser().openPrivateChannel().flatMap(channel -> channel.sendMessage(String.format("You have been accepted for an interview in <#%s>", interviewChannel.getId()))).queue();

           MessageEmbed embed = event.getMessage().getEmbeds().getFirst();

           EmbedBuilder embedBuilder = new EmbedBuilder(embed);

           embedBuilder.setColor(Color.green);

           event.getMessage().editMessageEmbeds(
                   embedBuilder.build()
           ).setActionRow(
                   Button.primary(ButtonIds.JOIN_THREAD, "Join Tread")
                           .withEmoji(Emoji.fromFormatted("🚀")),
                   Button.danger(ButtonIds.BAN, "Ban Applicant (Coming Soon)")
                           .withEmoji(Emoji.fromFormatted("🦵"))
                           .asDisabled()
           ).queue();

       } catch (Exception e) {
           LOGGER.error("There was an error when accepting member {}", e.getMessage());
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
            } else {

                EmbedBuilder embedBuilder = new EmbedBuilder();

                embedBuilder.setTitle("Application Received").setColor(Color.YELLOW).setTimestamp(Instant.now());

                Application application = new Application(getConversationStateAsString(state), null, userId);

                event.getChannel().sendMessageEmbeds(embedBuilder.build()).queue((message -> {

                    application.setMessageId(message.getId());

                    try {
                        // A message ID is created FOR NOW IT WILL BE CHANGED, this is in case the application is too
                        // long, and we need to fall back and render it on a website

                        int applicationId =  applicationDAO.addApplication(application);

                        application.setApplicationId(applicationId);


                    } catch (SQLException e) {
                        LOGGER.error("Error while adding data to User");
                        message.reply("Error storing your application please try again or contact one of the admins")
                                .queue();
                        throw new RuntimeException(e);
                    }
                }));

                conversationState.remove(userId);

                TextChannel applicationsChannel = event.getMessage().getJDA().getTextChannelById(config.getConfig().getApplication().getChannel());

                EmbedBuilder applicationEmbedBuilder = new EmbedBuilder();

                for (QuestionAnswers questionAnswers : state) {
                    Field field = new Field(questionAnswers.question, questionAnswers.answer, false);

                    applicationEmbedBuilder.addField(field);
                }

                applicationEmbedBuilder
                        .setColor(Color.YELLOW)
                        .setAuthor(event.getAuthor().getName(), null, event.getAuthor().getAvatarUrl())
                        .setTitle(String.format("%s has submitted an application", event.getAuthor().getName()))
                        .setThumbnail(event.getAuthor().getAvatarUrl())
                        .setTimestamp(Instant.now())
                ;

               try {
                   assert applicationsChannel != null;
                   applicationsChannel.sendMessageEmbeds(applicationEmbedBuilder.build()).addActionRow(
                           Button.primary(
                                   ButtonIds.ACCEPT,
                                   "Accept"
                           ).withEmoji(Emoji.fromFormatted("✅")),
                           Button.secondary(
                                   ButtonIds.DENY,
                                   "Deny"
                           ).withEmoji(Emoji.fromFormatted("❌")),
                           Button.danger(
                                   ButtonIds.BAN,
                                   "Ban"
                           ).withEmoji(Emoji.fromFormatted("🦵"))
                   ).queue((message -> {
                       application.setMessageId(message.getId());
                       try {
                           applicationDAO.updateApplication(application);
                       } catch (SQLException e) {
                           LOGGER.error("Error sending updating application with new message ID {}", e.getMessage());
                       }
                   }));

               } catch (RuntimeException e) {
                   LOGGER.error("Error sending application embed {}", e.getMessage());
               }
            }
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
}