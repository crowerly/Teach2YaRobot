import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by TangChen on 17/11/1.
 */
public class GetAllData {
    Map<String, String> h = new HashMap<>();
    String url = "http://www.ruyile.com/xuexiao/?a=7&t=2";

    DataCallBack dataCallBack;
    public GetAllData(GetClassifyData listener) {
        this.dataCallBack = listener;
    }

    void init() throws IOException {
        Document doc = Jsoup.connect(url).userAgent(Main.agent).get();
        Elements pageContent = doc.select("span.zys");
        int pageNum = Integer.parseInt(pageContent.first().text());

        if (pageNum > 1) {
            String pUrl;
            for (int i = 1; i <= pageNum; i++) {
                pUrl = i == 1 ? url : url + "&p=" + i;
                outSchoolContent(pUrl);
            }
        } else outSchoolContent(url);

        dataCallBack.dataCallBack(h);
    }

    void outSchoolContent(String url) throws IOException {
        Document doc = Jsoup.connect(url).userAgent(Main.agent).get();
        Elements allContent = doc.select("div.xxlb");
//        for(Element address : allContent.first().getElementsContainingText("地址：")){
//            String a[] = address.html().split("地址：");
//            a = a[a.length - 1].split("<div");
//            System.out.println(a[0].split("\n")[0]);
//        }
        for (Element school : allContent.first().select("div.sk")) {
            //System.out.println(school);
            if (!school.text().equals("") && !school.text().isEmpty()) {
                String[] b, a = school.text().split("电话");
                String address, schoolName = a[0];
                if(a.length == 1){
                    address = schoolName;
                }else {
                    b = a[a.length - 1].split("地址：");
                    address = b[b.length - 1];
                }
                h.put(schoolName, address);
                System.out.println(schoolName + " " + address);
            }
        }
    }
}
