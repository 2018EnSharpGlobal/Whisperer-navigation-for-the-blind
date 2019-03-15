package ensharp.yeey.whisperer;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.indooratlas.android.sdk.IALocationListener;
import com.indooratlas.android.sdk.IALocationManager;
import com.indooratlas.android.sdk.IARegion;

import ensharp.yeey.whisperer.Common.VO.PathVO;

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

    public void calculatePath(View view) {
        String departure = ((TextView)findViewById(R.id.departure)).getText().toString();
        String destination = ((TextView)findViewById(R.id.destination)).getText().toString();

        String departure1 = oDsayServiceManager.getStationCode(departure);
        String destination2 = oDsayServiceManager.getStationCode(destination);

        oDsayServiceManager.calculatePath("130", "328");
    }

    public void subwayInfo(View view) {
        oDsayServiceManager.getSubwayInfo("130");
    }

    public void subwayTimeTable(View view) {
        oDsayServiceManager.getSubwayTimeTable("120", "1");
    }
}
