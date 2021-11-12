package vip.Resolute.command;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import vip.Resolute.Resolute;
import vip.Resolute.command.impl.*;
import vip.Resolute.events.impl.EventChat;

public class CommandManager {

    public List<Command> commands = new ArrayList<Command>();
    public String prefix = ".";

    public CommandManager() {
        setup();
    }

    public void setup() {
        commands.add(new Toggle());
        commands.add(new Bind());
        commands.add(new VClip());
        commands.add(new Hide());
        commands.add(new Unhide());
        commands.add(new Clientname());
        commands.add(new Configure());
        commands.add(new HClip());
        commands.add(new API());
        commands.add(new SpectatorAlt());
        commands.add(new NameProtect());
    }

    public void handleChat(EventChat event) {
        String message = event.getMessage();



        if(!message.startsWith(prefix))
            return;

        event.setCancelled(true);

        message = message.substring(prefix.length());

        boolean foundCommand = false;

        if(message.split(" ").length > 0) {
            String commandName = message.split(" ")[0];
            for(Command c : commands) {
                if(c.aliases.contains(commandName) || c.name.equalsIgnoreCase(commandName)) {
                    c.onCommand(Arrays.copyOfRange(message.split(" "), 1, message.split(" ").length), message);
                    foundCommand = true;
                    break;
                }
            }
        }

        if(!foundCommand) {
            Resolute.addChatMessage("Could not find command");
        }
    }
}
