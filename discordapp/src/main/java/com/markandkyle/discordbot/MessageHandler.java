package com.markandkyle.discordbot;

import com.fasterxml.jackson.module.afterburner.util.ClassName;
import com.markandkyle.discordbot.dataaccess.SessionDAO;
import com.markandkyle.discordbot.models.commands.VoteCommand.*;
import sx.blah.discord.api.IDiscordClient;
import sx.blah.discord.api.events.EventSubscriber;
import sx.blah.discord.handle.impl.events.guild.channel.message.MessageReceivedEvent;
import sx.blah.discord.handle.obj.IMessage;
import sx.blah.discord.util.DiscordException;
import sx.blah.discord.util.MessageBuilder;
import sx.blah.discord.util.MissingPermissionsException;


import java.util.logging.Level;
import java.util.logging.Logger;


public class MessageHandler {
    private static final Logger LOGGER = Logger.getLogger( ClassName.class.getName() );
    private IDiscordClient client;
    private SessionDAO sessionDAO;
    
    public MessageHandler(IDiscordClient client) {
        this.client = client;
    }
	
	@EventSubscriber
	public void OnMesageEvent(MessageReceivedEvent event) throws DiscordException, MissingPermissionsException{
            this.sessionDAO = new SessionDAO();
            IMessage message = event.getMessage();
            String msg = message.getContent().toLowerCase();
            System.out.println(message.getAuthor().getLongID());

            if(msg.startsWith("!ping")){
                    sendMessage("Pong!", event);
            }else if(msg.startsWith("!vote")){
                LOGGER.log(Level.INFO, "Vote Command Detected.");
                String[] commands = msg.split(" ");
                VoteCommand voteCommand = VoteCommandFactory.createCommand(commands[1], this, event);
                LOGGER.log(Level.INFO, "Vote Command Type: "+ voteCommand.getClass());
                voteCommand.execute(message.getContent());
            }
            
            // close off the connection
            this.sessionDAO.finalize();
	}
	
	public void sendMessage(String message, MessageReceivedEvent event) throws DiscordException, MissingPermissionsException{
		new MessageBuilder(this.client).appendContent(message).withChannel(event.getMessage().getChannel()).build();
	}

	public void directMessage(String message,  MessageReceivedEvent event) throws DiscordException, MissingPermissionsException {
        new MessageBuilder(this.client).appendContent(message).withChannel(event.getMessage().getAuthor().getOrCreatePMChannel()).build();
    }
}
