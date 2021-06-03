package com.example.sakshigupta.webview;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.WindowDecorActionBar;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.DownloadManager;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.webkit.CookieManager;
import android.webkit.DownloadListener;
import android.webkit.PermissionRequest;
import android.webkit.URLUtil;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceRequest;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.karumi.dexter.Dexter;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionDeniedResponse;
import com.karumi.dexter.listener.PermissionGrantedResponse;
import com.karumi.dexter.listener.single.PermissionListener;

public class MainActivity extends AppCompatActivity {
WebView webview;
private  String weburl ="https://google.com/";
ProgressBar progressbarweb;
ProgressDialog progressDialog;
RelativeLayout relativeLayout;
Button btnnointernet;


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.toolbar_menu,menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch( item.getItemId()){

            case R.id.nav_previous : onBackPressed(); break;
            case R.id.nav_next:if(webview.canGoForward()) webview.goForward(); break;
            case R.id.nav_reload: checkConnection(); break;


        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        if(webview.canGoBack()){
            webview.goBack();
        }else{
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setMessage("Are you sure you want to exit").setNegativeButton("No",null)
            .setPositiveButton("yes", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    finishAffinity();
                }
            }).show();


        }

    }

    public void checkConnection(){
        ConnectivityManager connectivityManager = (ConnectivityManager)
                this.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo wifi = connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
        NetworkInfo mobileNetwork = connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);


        if(wifi.isConnected() || mobileNetwork.isConnected()) {
            webview.loadUrl(weburl);
            webview.setVisibility(View.VISIBLE);
            relativeLayout.setVisibility(View.GONE);


        }
        else{

            webview.setVisibility(View.GONE);
            relativeLayout.setVisibility(View.VISIBLE);

        }

    }




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Window window= getWindow();
        window.addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN );


        setContentView(R.layout.activity_main);
        progressDialog =new ProgressDialog(this);
        progressDialog.setMessage("Loading please wait...");

        btnnointernet = (Button) findViewById(R.id.noconnectionbtn);
        relativeLayout = (RelativeLayout) findViewById(R.id.relativelayout);
         webview = (WebView) findViewById(R.id.mywebview);
         progressbarweb = (ProgressBar) findViewById(R.id.progressbar);
        

        if(savedInstanceState !=null){
            webview.restoreState(savedInstanceState);
        }
        else
        {
            webview.getSettings().setJavaScriptEnabled(true);
            webview.getSettings().setLoadWithOverviewMode(true);
            webview.getSettings().setUseWideViewPort(true);
            webview.getSettings().setDomStorageEnabled(true);
            webview.getSettings().setLoadsImagesAutomatically(true);

            checkConnection();

        }


//webview.getSettings().setJavaScriptEnabled(true);

         btnnointernet.setOnClickListener(new View.OnClickListener() {
             @Override
             public void onClick(View v) {
                 checkConnection();
             }
         });


        webview.setDownloadListener(new DownloadListener() {
            @Override
            public void onDownloadStart(final String s, final String s1, final String s2, final String s3, long l) {

                Dexter.withActivity(MainActivity.this)
                        .withPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE)
                        .withListener(new PermissionListener() {
                            @Override
                            public void onPermissionGranted(PermissionGrantedResponse response) {


                                DownloadManager.Request request = new DownloadManager.Request(Uri.parse(s));
                                request.setMimeType(s3);
                                String cookies = CookieManager.getInstance().getCookie(s);
                                request.addRequestHeader("cookie",cookies);
                                request.addRequestHeader("User-Agent",s1);
                                request.setDescription("Downloading File.....");
                                request.setTitle(URLUtil.guessFileName(s,s2,s3));
                                request.allowScanningByMediaScanner();
                                request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
                                request.setDestinationInExternalPublicDir(
                                        Environment.DIRECTORY_DOWNLOADS, URLUtil.guessFileName(
                                                s,s2,s3));
                                DownloadManager downloadManager = (DownloadManager) getSystemService(DOWNLOAD_SERVICE);
                                downloadManager.enqueue(request);
                                Toast.makeText(MainActivity.this, "Downloading File..", Toast.LENGTH_SHORT).show();

                            }

                            @Override
                            public void onPermissionDenied(PermissionDeniedResponse response) {

                            }

                            @Override
                            public void onPermissionRationaleShouldBeShown(com.karumi.dexter.listener.PermissionRequest permissionRequest, PermissionToken token) {
                                token.continuePermissionRequest();
                            }


                        }).check();

            }
        });
         webview.setWebViewClient(new WebViewClient(){

             @Override
             public boolean shouldOverrideUrlLoading(WebView view, String url) {
                  view.loadUrl(url);
                  return true;
             }
         });
      webview.setWebChromeClient(new WebChromeClient(){

          @Override
          public void onProgressChanged(WebView view, int newProgress) {
              progressbarweb.setVisibility(View.VISIBLE);
              progressbarweb.setProgress(newProgress);
              setTitle("Loading...");
              progressDialog.show();
              if(newProgress==100){
                  progressbarweb.setVisibility(View.GONE );
                  setTitle(view.getTitle());
                  progressDialog.dismiss();
              }

              super.onProgressChanged(view, newProgress);
          }
      });


    }
    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        webview.saveState(outState);
    }
}