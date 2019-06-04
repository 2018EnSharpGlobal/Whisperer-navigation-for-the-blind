package ensharp.yeey.whisperer;

import android.content.Context;
import android.content.Intent;
import android.util.Log;

import org.json.JSONObject;

import ensharp.yeey.whisperer.Activity.CommandActivity;
import ensharp.yeey.whisperer.Activity.HelpingActivity;

import static ensharp.yeey.whisperer.Constant.COMMAND_CALL;
import static ensharp.yeey.whisperer.Constant.COMMAND_GUIDE;
import static ensharp.yeey.whisperer.Constant.COMMAND_HELP;
import static ensharp.yeey.whisperer.Constant.COMMAND_ROUTE;
import static ensharp.yeey.whisperer.Constant.COMMAND_TIME;

public class CommandCenter {
    private String instruction, commandDescription, optionalDescription;

    Context commandActivityContext;

    public CommandCenter(Context commandActivityContext, String instruction, String commandDescription, String optionalDescription) {
        this.commandActivityContext = commandActivityContext;

        this.instruction = instruction;
        this.commandDescription = commandDescription;
        this.optionalDescription = optionalDescription;

        CategorizeCommand();
    }

    public void parseCommand(JSONObject result){

    }

    private void CategorizeCommand(){
        Log.e("instruction", instruction);
        switch(instruction){
            case COMMAND_GUIDE:
                break;

            case COMMAND_CALL:
                break;

            case COMMAND_HELP:
                ExecuteCommandActivity();
                break;

            case COMMAND_ROUTE:
                break;

            case COMMAND_TIME:
                break;
        }
    }

    private void ExecuteCommandActivity(){
        ((CommandActivity)commandActivityContext).startActivity(new Intent(commandActivityContext, HelpingActivity.class));
//        ((CommandActivity)commandActivityContext).overridePendingTransition(R.anim.anim_slide_in_right, R.anim.anim_slide_out_left);
//        ((CommandActivity)commandActivityContext).finish();
    }
}
