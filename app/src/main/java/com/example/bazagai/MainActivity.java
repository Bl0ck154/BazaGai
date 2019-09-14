package com.example.bazagai;

import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Editable;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.ScrollView;
import android.widget.TextView;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.InputStream;
import java.net.URI;
import java.util.ArrayList;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

public class MainActivity extends AppCompatActivity {
    public final String BAZA_URL = "https://baza-gai.com.ua";
    TextView tvTitle;
    TextView tvStolen;
    EditText editNum;
    Editable number;
    ProgressBar progressBar;
    ImageView imageView;
    ScrollView scrollView;
    TextView tvRegister;
    TextView tvFeatures;
    TextView tvOperation;
    TextView tvAddress;
    Button btnGo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        tvTitle = this.findViewById(R.id.tvTitle);
        editNum = this.findViewById(R.id.editNum);
        progressBar = this.findViewById(R.id.progressBar);
        imageView = this.findViewById(R.id.img);
        tvStolen = this.findViewById(R.id.tvStolenInfo);
        scrollView = this.findViewById(R.id.scrollViewInfo);
        tvRegister = this.findViewById(R.id.tvRegisterDate);
        tvFeatures = this.findViewById(R.id.tvFeatures);
        tvOperation = this.findViewById(R.id.tvOperation);
        tvAddress = this.findViewById(R.id.tvAddress);
        btnGo = this.findViewById(R.id.btnGo);

        editNum.setOnEditorActionListener(new EditText.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (event.getKeyCode() == KeyEvent.KEYCODE_ENTER) {
                    btnGo.performClick();
                    return true;
                }
                return false;
            }
        });
    }

    public void btnClick(View view) {
        try {
            number = editNum.getText();
            new NewThreed().execute();
            progressBar.setVisibility(ProgressBar.VISIBLE);
        }
        catch (Exception ex) {
            Log.e("ERROR", ex.getMessage());
        }
    }

    public class NewThreed extends AsyncTask<String, Void, String> {

        public Elements elements;
        String title = "";
        String stolen;
        Bitmap image;
        ArrayList<String> tableColumns = new ArrayList<>();

        @Override
        protected String doInBackground(String... strings) {

            try {
                Document doc = Jsoup.connect(BAZA_URL + "/nomer/" + number).get();
                elements = doc.select(".text-center.mb-3");
                if(!elements.isEmpty()) {
                    title = elements.first().text();
                    stolen = doc.select(".stolen-info").first().text();

                    elements = doc.select("tbody").select("td");
                    for(Element el : elements) {
                        tableColumns.add(el.text());
                    }

                    elements = doc.select(".card-img-top");
                    if (!elements.isEmpty()) {
                        String imgUrl = BAZA_URL + elements.first().attr("src");
                        image = ImageHelper.getRoundedCornerBitmap(ImageHelper.downloadByUrl(imgUrl), 16);
                    }
                }
            }
            catch (Exception ex) {
                Log.e("Error", ex.getMessage());
                title = ex.getMessage();
            }
            return null;
        }

        @Override
        protected void onPostExecute(String result) {
            progressBar.setVisibility(ProgressBar.GONE);
            if(title.isEmpty())
                return;

            tvTitle.setText(title);
            imageView.setImageBitmap(image);
            tvStolen.setText(stolen);
            tvRegister.setText(tableColumns.get(0));
            tvFeatures.setText(tableColumns.get(2));
            tvOperation.setText(tableColumns.get(3));
            tvAddress.setText(tableColumns.get(4));
            scrollView.setVisibility(View.VISIBLE);
        }
    }
}