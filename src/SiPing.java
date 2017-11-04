import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by TangChen on 17/11/2.
 */
public class SiPing {
    String url = "http://www.50xiao.com/siping/xx_pm/";
    String baseUrl = "http://www.50xiao.com";
    Map<String, String> areas;

    public SiPing() {
        this.areas = new HashMap<>();
    }

    int loop = 0;

    void init() throws IOException {
        Document doc = Jsoup.connect(url).userAgent(Main.agent).get();
        Elements contents = doc.select("p.label_list");

        Elements areas = contents.first().getElementsByTag("a");

        for(Element e : areas) {
            if(!e.text().contains("全部")){
                if(!this.areas.containsKey(e.text())) {
                    this.areas.put(e.text(), e.attr("href"));
                }
            }
        }

        System.out.println("四平市");

        for(String areasName : this.areas.keySet()) {
            System.out.println("   " + areasName);
            iterator(baseUrl + this.areas.get(areasName));
        }

        System.out.println(loop);
    }

    void iterator(String url) throws IOException {
        Document doc = Jsoup.connect(url).userAgent(Main.agent).get();
        Elements pages = doc.select("div.search_list").first().select("p.search_more");
        String str = pages.first().text().trim();
        StringBuilder str2 = new StringBuilder();

        if(!"".equals(str)){
            for(int i=0;i<str.length();i++){
                if(str.charAt(i)>=48 && str.charAt(i)<=57){
                    str2.append(str.charAt(i));
                }
            }
        }

        int baseNum = Integer.valueOf(String.valueOf(str2));
        int pageNum = baseNum % 30 == 0 ? baseNum / 30 : baseNum / 30 + 1;

        for (int i = 1; i <= pageNum; i++) {
            if (i == 1) {
                outSchoolName(url);
            }else {
                outSchoolName(url + "/index_" + i + ".html");
            }
        }
    }

    void outSchoolName(String url) throws IOException {
        Document doc = Jsoup.connect(url).userAgent(Main.agent).get();
        Elements contents = doc.select("div.search_list").first().select("dl");
        for(Element school : contents) {
            System.out.println("      "+school.getElementsByTag("table").first().getElementsByTag("a").text());
            loop++;
        }
    }
}
