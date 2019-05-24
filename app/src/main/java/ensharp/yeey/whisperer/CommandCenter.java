package ensharp.yeey.whisperer;

import org.json.JSONObject;

public class CommandCenter {
    private String instruction, commandDescription, optionalDescription;

    public CommandCenter(String instruction, String commandDescription, String optionalDescription) {
        this.instruction = instruction;
        this.commandDescription = commandDescription;
        this.optionalDescription = optionalDescription;
    }

    public void parseCommand(JSONObject result){

    }

    public void executeCommand(){

    }
}
