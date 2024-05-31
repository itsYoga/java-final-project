import org.json.simple.JSONObject;
import com.jacob.activeX.ActiveXComponent;
import com.jacob.com.Dispatch;
import com.jacob.com.Variant;

public class TextToSpeech {

    public static void tts(JSONObject data) {
        String i = (String)data.get("county") +
                (String)data.get("town") +
                "目前攝氏" + data.get("temperature") + "度，，，" +
                (String)data.get("weather_condition") +
                "，，，降水量" + data.get("humidity") + "毫米，，，風速  每秒" +
                data.get("windspeed") + "公尺";


       textToSpeech(i);
    }

    public static void textToSpeech(String data) {
        ActiveXComponent ax = null;
        try {
            ax = new ActiveXComponent("Sapi.SpVoice");

            // 運行時輸出語音內容
            Dispatch spVoice = ax.getObject();
            // 音量 0-100
            ax.setProperty("Volume", new Variant(100));
            // 語音朗讀速度 -10 到 +10
            ax.setProperty("Rate", new Variant(1));
            // 執行朗讀
            Dispatch.call(spVoice, "Speak", new Variant(data));



        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
