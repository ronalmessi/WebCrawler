package www.hjclass.com;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import com.csvreader.CsvWriter;

import edu.uci.ics.crawler4j.crawler.Page;
import edu.uci.ics.crawler4j.crawler.WebCrawler;
import edu.uci.ics.crawler4j.parser.HtmlParseData;
import edu.uci.ics.crawler4j.url.WebURL;

public class Crawler extends WebCrawler {
	private final static String URL_PATTERN = "http://www.hjclass.com/course/all\\?page=[0-9]+";
	private final static String CSV_PATH = "data/crawl/data.csv";
	private File csv;
	private CsvWriter cw;

	public Crawler() throws IOException {
		super();
		csv = new File(CSV_PATH);
		if (csv.isFile()) {
			csv.delete();
		}

		cw = new CsvWriter(new FileWriter(csv, true), ',');
		cw.write("课程类别");
		cw.write("课程名称");
		cw.write("价格");
		cw.endRecord();
		cw.close();
	}

	@Override
	public boolean shouldVisit(WebURL url) {
		return false;
	}

	@Override
	public void visit(Page page) {
		String url = page.getWebURL().getURL();
		System.out.println("url:" + url);
		if (page.getParseData() instanceof HtmlParseData) {
			HtmlParseData htmlParseData = (HtmlParseData) page.getParseData();
			String title = htmlParseData.getTitle();
			String html = htmlParseData.getHtml();
			Document doc = Jsoup.parse(html);
			Element parentElement = doc.select("div.course_list").first();
			Elements targetElements = parentElement
					.getElementsByClass("course_li");
			for (Element element : targetElements) {
				String courseName = element.select("div.course_title")
						.select("a[href]").attr("data-name");
				String courseDetailUrl = "http://www.hjclass.com"
						+ element.select("div.course_title").select("a[href]")
								.attr("href");
				System.out.println(courseDetailUrl);
				String courseType = null;
				try {
					Document document = Jsoup.parse(new URL(courseDetailUrl),
							5000);
					courseType = document.select("div.pnl_nav")
							.select("a[href]").get(1).text();
					System.out.println(courseType);
				} catch (MalformedURLException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				} catch (IOException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
				System.out.println(courseName);
				String coursePrice = element.getElementsByClass("course_price")
						.select("span.number").first().text();
				System.out.println(coursePrice);

				try {
					cw = new CsvWriter(new FileWriter(csv, true), ',');
					cw.write(courseType);
					cw.write(courseName);
					cw.write(coursePrice);
					cw.endRecord();
					cw.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}

		}

	}
}
