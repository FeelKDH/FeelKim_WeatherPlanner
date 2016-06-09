package com.nhn.android.mapviewer;

import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.android.weatherplanner.R;

import net.htmlparser.jericho.Element;
import net.htmlparser.jericho.HTMLElementName;
import net.htmlparser.jericho.Source;
import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.Activity;
import android.os.Build;
import android.os.Bundle;
import android.os.StrictMode;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;

public class Map_Parser extends Activity implements OnClickListener {

	/** Called when the activity is first created. */
	static String html = null;
	boolean flag = false;
	Mythread th;

	@TargetApi(Build.VERSION_CODES.GINGERBREAD)
	@SuppressLint("NewApi")
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		th = new Mythread();
		th.start();

		setContentView(R.layout.activity_tab_maparser);

		if (android.os.Build.VERSION.SDK_INT > 9) {
			StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder()
					.permitAll().build();
			StrictMode.setThreadPolicy(policy);
		}

		findViewById(R.id.viewbus).setOnClickListener(this);
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		TextView tv = (TextView) findViewById(R.id.busView);
		// Log.i("test",""+html);
		tv.setText(html);
	}

	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		th.interrupt();
		super.onDestroy();
	}

	class Mythread extends Thread {

		@Override
		public void run() {
			// TODO Auto-generated method stub
			// while(1)
			// if(flag)
			try {
				// String busname = "�씤�젣���븰�썑臾�";
				String busname = "吏��궡�룞�궪嫄곕━";
				String busID = "";
				int b_index = 0;
				String[][] arrive = new String[50][50];
				String[] bstop = new String[100];
				String[] line_id = new String[100];
				String[] linenm = new String[100];

				String busstr = URLEncoder.encode(busname, "EUC-KR"); // url �씤肄붾뵫�쓣 �쐞�빐
																		
				String url = "http://121.174.75.12/02/021.html_ok.asp?m=1&m1=2&bstopnm="
						+ busstr;

				HashMap<String, String> hm = new HashMap<String, String>();

				URL link = new URL(url);
				InputStreamReader isr = new InputStreamReader(
						link.openStream(), "EUC-KR");
				Source source = new Source(isr);
				source.fullSequentialParse(); // �떆�옉遺��꽣 �걹源뚯� �깭洹몃뱾留�  parse

				Element P = source.getAllElements(HTMLElementName.P).get(1);
				List<Element> td = source.getAllElements(HTMLElementName.A);

				int A_count = P.getAllElements(HTMLElementName.A).size();
				int AA_count = 0;

				for (int i = 0; i < A_count; i++) {
					Element e = (Element) td.get(i);
					bstop[i] = e.getAttributeValue("href");

					b_index = bstop[i].indexOf("bstop_id");
					bstop[i] = bstop[i].substring(b_index + 9, b_index + 18);

					String nosun = "http://121.174.75.12/03/03.html_ok.asp?m=1&m1=2&bstop_id="
							+ bstop[i];
					link = new URL(nosun);
					isr = new InputStreamReader(link.openStream(), "EUC-KR");
					source = new Source(isr);
					source.fullSequentialParse(); // �떆�옉遺��꽣 �걹源뚯� �깭洹몃뱾留� parse\
					P = source.getAllElements(HTMLElementName.P).get(1);
					AA_count = P.getAllElements(HTMLElementName.A).size();
					td = source.getAllElements(HTMLElementName.A);

					for (int j = 0; j < AA_count; j++) {
						Element ee = (Element) td.get(j);
						line_id[j] = ee.getAttributeValue("href");
						linenm[j] = P.getAllElements(HTMLElementName.A).get(j)
								.getContent().toString();
						int l_index = line_id[j].indexOf("line_id");
						line_id[j] = line_id[j].substring(l_index + 8);

						String ar = "http://121.174.75.12/01/011.html.asp?m=1&m1=2&bstop_id="
								+ bstop[i] + "&line_id=" + line_id[j];

						link = new URL(ar);
						isr = new InputStreamReader(link.openStream(), "EUC-KR");
						source = new Source(isr);
						source.fullSequentialParse(); // �떆�옉遺��꽣 �걹源뚯� �깭洹몃뱾留�
														// parse\

						Element PP = source.getAllElements(HTMLElementName.P)
								.get(1);
						arrive[i][j] = PP.getContent().toString();

						Pattern p = Pattern.compile("<(?:.|\\s)*?>");

						// Pattern.compile("/<!--[^>](.*?)-->/g");
						Matcher m = p.matcher(arrive[i][j]);
						arrive[i][j] = m.replaceAll("").trim();
						// arrive[i][j] = arrive[i][j].replaceAll(" ",
						// "").trim();
						arrive[i][j] = arrive[i][j].replaceAll("<!--(.*)-->",
								"").trim();
						// arrive[i][j]=arrive[i][j].replaceAll("(/^\s|\s$/",
						// "");

						hm.put("踰꾩뒪�젙瑜섏옣 �끂�꽑0", arrive[1][4]);
						hm.put("踰꾩뒪�젙瑜섏옣 �끂�꽑6", arrive[1][5]);
						hm.put("踰꾩뒪�젙瑜섏옣 �끂�꽑7", arrive[1][6]);
						hm.put("踰꾩뒪�젙瑜섏옣 �끂�꽑8", arrive[1][7]);
						hm.put("踰꾩뒪�젙瑜섏옣 �끂�꽑9", linenm[0]);
						// hm.put("踰꾩뒪�젙瑜섏옣 �끂�꽑5",line_id[6]);
						// hm.put("踰꾩뒪�젙瑜섏옣 �끂�꽑6",arrive[7]);
						// hm.put("踰꾩뒪�젙瑜섏옣 �끂�꽑6",line_id[1]);
					}
				}
				Map_Parser.html = hm.toString();

			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
}