package com.falconxrobotics.discordbot.commands.test;

import com.github.raybipse.components.Command;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

/**
 * @author RayBipse
 */
public class EchoTo extends Command {

    protected EchoTo() {
        super("EchoTo", "echoto");
        setDescription("Repeats the parameters the user gives to a specific channel.");
        addExamples("social \"Hello World!\"");
        setSyntax("[channel id or channel name] [...things to be repeated]*");
        setParent(Test::getInstance);
    }

    @Override
    public EmbedBuilder getEmbedInfo() {
        EmbedBuilder builder = super.getEmbedInfo();
        builder.addField("Warning", "If you send the message in a private channel, then you can only supply the first argument with a channel id.", false);
        builder.addField("Note", "If your supply the first argument with a channel name, it will only send the message to the one of those channels.", false);
        builder.addField("Tip", "Add quotation marks around the message to echo a sentence without sending multiple messages.", false);
        return builder;
    }

    @Override
    public void onMessageReceived(MessageReceivedEvent event) {
        String messageContent = event.getMessage().getContentDisplay();
        if (event.getAuthor().isBot())
            return;
        if (!getInputValidity(messageContent))
            return;
        if (!event.getAuthor().getId().equals("590931976182169600")) {
            event.getChannel().sendMessage(getEmbedSimpleError("Permission Error", "You must be Ray.").build()).queue();
            return;
        }

        messageContent = trimInputBeginning(messageContent);
        String[] arguments = splitUserInput(messageContent);

        if (arguments.length < 2) {
            event.getChannel().sendMessage(getEmbedMissingArguments().build()).queue();
        } else {
            TextChannel channel;

            if (!event.isFromGuild()) { // if sent in a private channel
                try {
                    channel = event.getJDA().getTextChannelById(arguments[0]);
                } catch (NumberFormatException nfe) {
                    event.getChannel().sendMessage(getEmbedInvalidParameterError("In a private channel, the first parameter must be a channel id.").build()).queue();
                    return;
                } catch (IllegalStateException ise) {
                    event.getChannel().sendMessage(getEmbedInvalidParameterError("Channel not found.").build()).queue();
                    return;
                }
            } else { // if sent in guild
                try {
                    channel = event.getGuild().getTextChannelById(arguments[0]);
                } catch (NumberFormatException nfe) {
                    try {
                        channel = event.getGuild().getTextChannelsByName(arguments[0], true).get(0);
                    } catch (IllegalStateException | IndexOutOfBoundsException ise) {
                        event.getChannel().sendMessage(getEmbedInvalidParameterError("Channel not found.").build()).queue();
                        return;
                    }
                } catch (IllegalStateException ise) {
                    event.getChannel().sendMessage(getEmbedInvalidParameterError("Channel not found.").build()).queue();
                    return;
                }
            }

            if (channel.canTalk(event.getMember())) {
                for (int i = 1; i < arguments.length; i++) {
                    channel.sendMessage(arguments[i]).queue();
                }
            } else {
                event.getChannel().sendMessage(getEmbedInvalidParameterError("Permission Error").build());
            }
        }
    }
}