package AsyncHttpRequest;

import android.os.AsyncTask;
import android.util.Log;


import com.example.sony.smarteyeglass.extension.advancedlayouts.AdvancedLayoutsControl;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.mime.HttpMultipartMode;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.Charset;

import httpposttask.HttpPostTask;


public class AsyncHttpRequest extends AsyncTask<byte[], Void, String[]> {


    private AsyncCallback _asyncCallback = null;

    /**
     * Activiyへのコールバック用interface
     *
     *onPreExecute()非同期処理前に実行したい処理
     *onPostExecute()非同期処理完了時に実行したい処理
     *onProgressUpdate()非同期処理中に実行したい処理
     *onCancelled()キャンセル時に実行したい処理
     *
     * @author Koujides
     *
     */
    public interface AsyncCallback {
        void onPreExecute();
        void onPostExecute(String[] result);
        void onCancelled();
    }
    /**
     * コンストラクタ
     * @param asyncCallback
     */
    public AsyncHttpRequest(AsyncCallback asyncCallback) {
        this._asyncCallback = asyncCallback;
    }






    // このメソッドは必ずオーバーライドする必要があるよ
    // ここが非同期で処理される部分みたいたぶん。
    private byte[] data;
    private String json_string;
    private String[] strings = new String[2];

    public void setImage(byte[] data){
        this.data = data;
    }

    //Eye_etCameraControl eye_etCameraControl;
    AdvancedLayoutsControl advancedLayoutsControl;


    @Override
    protected String[] doInBackground(byte[]... builder) {

        //byte[] byteImage = builder[0];
        byte[] byteImage = data;

        String params = "image";
        String url = "http://yuji.website:3002/api/v1/meals";

        HttpClient client = new DefaultHttpClient();
        String str, str1, str2 = "0";

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

            str1 = new String(str);

            Log.i("HTTP status Line", httpResponse.getStatusLine().toString());
            Log.i("HTTP response", str1);

        } catch (ClientProtocolException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        json_string = str1;
//------------------------------------------------

        try {

            URL url2 = new URL("http://yuji.website:3002/api/v1/user_intake/1");
            HttpURLConnection con = (HttpURLConnection)url2.openConnection();
            str2 = InputStreamToString(con.getInputStream());
            Log.d("HTTP", str2);
        } catch(Exception ex) {
            System.out.println(ex);
        }

    //String[] strings = new String[2];


    strings[0] = str1;
    strings[1] = str2;

        return strings;
    }


    static String InputStreamToString(InputStream is) throws IOException {
        BufferedReader br = new BufferedReader(new InputStreamReader(is));
        StringBuilder sb = new StringBuilder();
        String line;
        while ((line = br.readLine()) != null) {
            sb.append(line);
        }
        br.close();
        return sb.toString();
    }
    /**
     * 以下は基本的にいじらない
     */

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        this._asyncCallback.onPreExecute();
    }


    @Override
    protected void onPostExecute(String result[]) {
        super.onPostExecute(result);
        this._asyncCallback.onPostExecute(strings);
    }

    @Override
    protected void onCancelled() {
        super.onCancelled();
        this._asyncCallback.onCancelled();
    }

}