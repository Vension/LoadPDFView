package com.vension.pdf;

import android.graphics.Canvas;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.github.barteksc.pdfviewer.PDFView;
import com.github.barteksc.pdfviewer.listener.OnDrawListener;
import com.github.barteksc.pdfviewer.listener.OnErrorListener;
import com.github.barteksc.pdfviewer.listener.OnLoadCompleteListener;
import com.github.barteksc.pdfviewer.listener.OnPageChangeListener;
import com.github.barteksc.pdfviewer.listener.OnPageScrollListener;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class MainActivity extends AppCompatActivity {

	private final String pdfUrl = "http://static02.121learn.com/1491898372044_f1dd1e8eadbee194fa71b44298c9f467.pdf";
	private PDFView _PdfView;
	private Button btn;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		_PdfView = (PDFView) findViewById(R.id.pdfView);
		btn = (Button) findViewById(R.id.getInputstream);
		btn.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {

				// 1、直接new 一个线程类，传入参数实现Runnable接口的对象（new Runnable），相当于方法二
				new Thread(new Runnable() {
					@Override
					public void run() {
						// 写子线程中的操作
						getInputStream();
					}
				}).start();
			}
		});

	}

	private void getInputStream() {

//		所以,这里只需要拿到流就行了
//		先开个子线程-->
		try {
			URL url = new URL(pdfUrl);
			HttpURLConnection connection = (HttpURLConnection)
					url.openConnection();
			connection.setRequestMethod("GET");//试过POST 可能报错
			connection.setDoInput(true);
			connection.setConnectTimeout(10000);
			connection.setReadTimeout(10000);
			//实现连接
			connection.connect();

			System.out.println("connection.getResponseCode()=" + connection.getResponseCode());
			if (connection.getResponseCode() == 200) {
				InputStream is = connection.getInputStream();
				//这里给过去就行了
				loadPDF(is);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private void loadPDF(InputStream is) {
		_PdfView.fromStream( is)
//.pages(0, 2, 1, 3, 3, 3) // all pages are displayed by default
				.enableSwipe(true)
				.swipeHorizontal(false)
				.enableDoubletap(true)
				.defaultPage(0)
				.onDraw(new OnDrawListener() {
					@Override
					public void onLayerDrawn(Canvas canvas, float pageWidth, float pageHeight, int displayedPage) {

					}
				})
				.onLoad(new OnLoadCompleteListener() {
					@Override
					public void loadComplete(int nbPages) {
						Toast.makeText(getApplicationContext(), "loadComplete", Toast.LENGTH_SHORT).show();
					}
				})
				.onPageChange(new OnPageChangeListener() {
					@Override
					public void onPageChanged(int page, int pageCount) {
						btn.setText(page + "/" + pageCount);
					}
				})
				.onPageScroll(new OnPageScrollListener() {
					@Override
					public void onPageScrolled(int page, float positionOffset) {
						Toast.makeText(getApplicationContext(), "第" +page +"页onPageScrolled" +  positionOffset + "positionOffset", Toast.LENGTH_SHORT).show();
					}
				})
				.onError(new OnErrorListener() {
					@Override
					public void onError(Throwable t) {
						Toast.makeText(getApplicationContext(), "error", Toast.LENGTH_SHORT).show();
					}
				})
				.enableAnnotationRendering(false)
				.password(null)
				.scrollHandle(null)
				.load();
	}

}
