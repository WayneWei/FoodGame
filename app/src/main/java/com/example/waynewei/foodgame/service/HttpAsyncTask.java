package com.example.waynewei.foodgame.service;

import android.os.AsyncTask;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class HttpAsyncTask extends AsyncTask<String, Integer, String> {
	private TaskCompleted mCallback;
	private String query;
//	private MaterialDialog mProgress;

	public HttpAsyncTask(TaskCompleted callback) {
		this.mCallback = callback;
	}

	public HttpAsyncTask(TaskCompleted callback, String query) {
		this.mCallback = callback;
		this.query = query;
	}

	@Override
	public void onPreExecute() {
//		mProgress = new MaterialDialog.Builder(mContext)
//				.content(R.string.please_wait)
//				.progress(true, 0)
//				.progressIndeterminateStyle(false)
//				.cancelable(false)
//				.show();
	}

	@Override
	protected void onProgressUpdate(Integer... params) {
		super.onProgressUpdate(params);


	}

	@Override
	protected String doInBackground(String... params) {
		String response = null;
			try {
				response = doHttpUrlConnectionAction(params[0]);
			} catch (Exception e) {
				e.printStackTrace();
			}

		return response;
	}

	private String doHttpUrlConnectionAction(String desiredUrl)
			throws Exception
	{
		BufferedReader reader = null;
		StringBuilder stringBuilder;

		try
		{
			// create the HttpURLConnection
			URL url = new URL(desiredUrl);
			HttpURLConnection connection = (HttpURLConnection) url.openConnection();

			// just want to do an HTTP GET here
			connection.setRequestMethod("POST");

			// uncomment this if you want to write output to this url
			connection.setDoOutput(true);
			// give it 10 seconds to respond
			// 获得一个输出流,向服务器写数据,默认情况下,系统不允许向服务器输出内容
			if(query!=null) {
				OutputStream out = connection.getOutputStream();// 获得一个输出流,向服务器写数据
				out.write(query.getBytes());
				out.flush();
				out.close();
			}

			connection.setReadTimeout(10*1000);
			connection.setConnectTimeout(10*1000);
			connection.connect();

			// read the output from the server
			reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
			stringBuilder = new StringBuilder();

			String line;
			while ((line = reader.readLine()) != null) {
				stringBuilder.append(line).append("\n");
			}
			return stringBuilder.toString();
		}
		catch (Exception e)
		{
			e.printStackTrace();
			throw e;
		}
		finally
		{
			// close the reader; this can throw an exception too, so
			// wrap it in another try/catch block.
			if (reader != null)
			{
				try {
					reader.close();
				}
				catch (IOException ioe) {
					ioe.printStackTrace();
				}
			}
		}
	}

	@Override
	protected void onPostExecute(String response) {
//		mProgress.dismiss();
		if(response!=null)
			mCallback.onTaskComplete(response);
	}
}
