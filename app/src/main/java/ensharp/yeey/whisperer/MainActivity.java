package ensharp.yeey.whisperer;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.odsay.odsayandroidsdk.API;
import com.odsay.odsayandroidsdk.ODsayData;
import com.odsay.odsayandroidsdk.ODsayService;
import com.odsay.odsayandroidsdk.OnResultCallbackListener;

import org.json.JSONObject;

public class MainActivity extends AppCompatActivity {

    private ODsayService odsayService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initSubwayAPI();
    }

    /**
     * 지하철 운행정보를 가져오는 API를 호출합니다.
     * ODsayService 객체는 싱글톤으로 생성됩니다.
     */
    private void initSubwayAPI() {
        odsayService = ODsayService.init(MainActivity.this, getString(R.string.odsay_key));
        odsayService.setReadTimeout(5000);
        odsayService.setConnectionTimeout(5000);
    }
}
