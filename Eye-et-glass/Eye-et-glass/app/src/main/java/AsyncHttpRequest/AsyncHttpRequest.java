package AsyncHttpRequest;

import android.app.Activity;
import android.net.Uri;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.TextView;

import com.gaasii.eye_et_grass.R;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.mime.HttpMultipartMode;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;

import java.io.IOException;
import java.net.URL;
import java.nio.charset.Charset;


public class AsyncHttpRequest extends AsyncTask<byte[], Void, String> {

    private Activity mainActivity;
    Integer integer;

    // このメソッドは必ずオーバーライドする必要があるよ
    // ここが非同期で処理される部分みたいたぶん。
    private byte[] data;
    public void setImage(byte[] data){
        this.data = data;
    }


    @Override
    protected String doInBackground(byte[]... builder) {

        //byte[] byteImage = builder[0];
        byte[] byteImage = data;

        String params = "image";
        String url = "http://yuji.website:3002/api/v1/meals";

        HttpClient client = new DefaultHttpClient();
        String str;

        MultipartEntityBuilder entity = MultipartEntityBuilder.create();
        entity.setMode(HttpMultipartMode.BROWSER_COMPATIBLE);
        entity.setCharset(Charset.forName("UTF-8"));
        try {
            // 画像をセット
            // 第一引数：パラメータ名
            // 第二引数：画像データ
            // 第三引数：画像のタイプ。jpegかpngかは自由
            // 第四引数：画像ファイル名。ホントはContentProvider経由とかで取って来るべきなんだろうけど、今回は見えない部分なのでパス
            entity.addBinaryBody("image", byteImage, ContentType.create("image/jpeg"), "image");
            //url = "http://example.com/image.json";

            // 画像以外のデータを送る場合はaddTextBodyを使う
            ContentType textContentType = ContentType.create("application/json","UTF-8");
            entity.addTextBody("auth_token", params, textContentType);
            ContentType textContentType2 = ContentType.create("application/json","UTF-8");
            entity.addTextBody("user_id", "1", textContentType2);

            HttpPost post = new HttpPost(url);
            post.setEntity(entity.build());

            HttpResponse httpResponse = client.execute(post);
            str = EntityUtils.toString(httpResponse.getEntity(), "UTF-8");
            Log.i("HTTP status Line", httpResponse.getStatusLine().toString());
            Log.i("HTTP response", new String(str));
        } catch (ClientProtocolException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        return str;

    }


    // このメソッドは非同期処理の終わった後に呼び出されます

}