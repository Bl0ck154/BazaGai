package com.example.bazagai;

import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Editable;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.InputStream;
import java.net.URI;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

public class MainActivity extends AppCompatActivity {
    public final String BAZA_URL = "https://baza-gai.com.ua";
    TextView tv;
    EditText editNum;
    Editable number;
    ProgressBar progressBar;
    ImageView imageView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        tv = this.findViewById(R.id.tv);
        editNum = this.findViewById(R.id.editNum);
        progressBar = this.findViewById(R.id.progressBar);
        imageView = this.findViewById(R.id.img);
    }

    public void btnClick(View view) {
        try {
            number = editNum.getText();
            new NewThreed().execute();
            progressBar.setVisibility(ProgressBar.VISIBLE);
        }
        catch (Exception ex) {

        }
    }

    public class NewThreed extends AsyncTask<String, Void, String> {

        public Elements elements;
        String text = "";
        Bitmap image;

        @Override
        protected String doInBackground(String... strings) {

            try {
                Document doc = Jsoup.connect(BAZA_URL + "/nomer/" + number).get();
                elements = doc.select(".text-center.mb-3");
                for (Element element : elements) {
                    text += element.text();
                }
                elements = doc.select(".card-img-top");
                if(!elements.isEmpty()) {
                    String imgUrl = BAZA_URL + elements.first().attr("src");
                    image = this.imgDownload(imgUrl);
                }
            }
            catch (Exception ex) {
                Log.e("Error", ex.getMessage());
                text = ex.getMessage();
            }
            return null;
        }

        private Bitmap imgDownload(String url) {
            Bitmap mIcon11 = null;
            try {
                InputStream in = new java.net.URL(url).openStream();
                mIcon11 = BitmapFactory.decodeStream(in);
            } catch (Exception e) {
                Log.e("Error", e.getMessage());
                e.printStackTrace();
            }
            return mIcon11;
        }

        @Override
        protected void onPostExecute(String result) {

            tv.setText(text);
            progressBar.setVisibility(ProgressBar.GONE);
            imageView.setImageBitmap(image);
        }
    }
}