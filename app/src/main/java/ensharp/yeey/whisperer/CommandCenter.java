package ensharp.yeey.whisperer;

import android.content.Context;
import android.content.Intent;
import android.util.Log;

import org.json.JSONObject;

import ensharp.yeey.whisperer.Activity.CommandActivity;
import ensharp.yeey.whisperer.Activity.HelpingActivity;
import ensharp.yeey.whisperer.Activity.InformationActivity;

import static ensharp.yeey.whisperer.Constant.COMMAND_CALL;
import static ensharp.yeey.whisperer.Constant.COMMAND_GUIDE;
import static ensharp.yeey.whisperer.Constant.COMMAND_HELP;
import static ensharp.yeey.whisperer.Constant.COMMAND_ROUTE;
import static ensharp.yeey.whisperer.Constant.COMMAND_TIME;

public class CommandCenter {
    private String instruction, commandDescription, optionalDescription;
    private ODsayServiceManager oDsayServiceManager;

    Context commandActivityContext;

    public CommandCenter(Context commandActivityContext, String instruction, String commandDescription, String optionalDescription) {
        oDsayServiceManager = ODsayServiceManager.getInstance();
        this.commandActivityContext = commandActivityContext;
        oDsayServiceManager.initAPI(commandActivityContext);

        this.instruction = instruction;
        this.commandDescription = commandDescription;
        this.optionalDescription = optionalDescription;

        Log.e("출발역", commandDescription);
        Log.e("도착역", optionalDescription);

        CategorizeCommand();
    }

    public void parseCommand(JSONObject result) {

    }

    private void CategorizeCommand() {
        Intent intent;
        Log.e("instruction", instruction);
        switch (instruction) {
            case COMMAND_GUIDE:
                break;

            case COMMAND_CALL:
                oDsayServiceManager.setSTTContext(commandActivityContext);
                oDsayServiceManager.findCloserStationCode(126.933361407195, 37.3643392278118);
                break;
            case COMMAND_HELP:
                intent = new Intent(commandActivityContext, HelpingActivity.class);
                ((CommandActivity) commandActivityContext).startActivity(new Intent(intent));
                break;
            case COMMAND_ROUTE:
                intent = new Intent(commandActivityContext, InformationActivity.class);
                intent.putExtra("instruction", instruction);
                intent.putExtra("departure", commandDescription);
                intent.putExtra("destination", optionalDescription);
                ((CommandActivity) commandActivityContext).startActivity(new Intent(intent));
                break;
            case COMMAND_TIME:
                intent = new Intent(commandActivityContext, InformationActivity.class);
                intent.putExtra("instruction", instruction);
                intent.putExtra("departure", commandDescription);
                intent.putExtra("destination", optionalDescription);
                ((CommandActivity) commandActivityContext).startActivity(new Intent(intent));
                break;
        }
    }

    private void ExecuteCommandActivity() {
        ((CommandActivity) commandActivityContext).startActivity(new Intent(commandActivityContext, HelpingActivity.class));
//        ((CommandActivity)commandActivityContext).overridePendingTransition(R.anim.anim_slide_in_right, R.anim.anim_slide_out_left);
//        ((CommandActivity)commandActivityContext).finish();
    }
}
