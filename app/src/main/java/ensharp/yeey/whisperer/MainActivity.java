package ensharp.yeey.whisperer;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.odsay.odsayandroidsdk.API;
import com.odsay.odsayandroidsdk.ODsayData;
import com.odsay.odsayandroidsdk.ODsayService;
import com.odsay.odsayandroidsdk.OnResultCallbackListener;

import org.json.JSONObject;
import android.support.v7.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    private ODsayServiceManager oDsayServiceManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initSubwayAPI();
    }

    /**
     * 지하철 운행정보를 가져오는 API를 호출하고 초기화합니다.
     */
    private void initSubwayAPI() {
        oDsayServiceManager = ODsayServiceManager.getInstance();
        oDsayServiceManager.setMainActivity(this);
        oDsayServiceManager.initAPI();
    }
}
