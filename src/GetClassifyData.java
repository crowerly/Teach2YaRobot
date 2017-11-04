import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.FileWriter;
import java.io.IOException;
import java.util.*;

/**
 * Created by TangChen on 17/11/4.
 */
public class GetClassifyData implements DataCallBack {
    private String[] provinces;
    private List<String> citys;
    private List<String> districts;
    private FileWriter fw = null;
    private int j = 0;
    private boolean isOnce = true;
    private Map<String, Map<String, Map<String, Set<String>>>> totalContent = new HashMap<>();
    private Map<String, Map<String, Set<String>>> cityContent;
    private Map<String, Set<String>> districtContent;
    private Set<String> schoolList, noMatchSchool;
    private String nowProvince, nowCity, nowDistrict;

    public GetClassifyData() throws IOException {
        noMatchSchool = new HashSet<>();
        citys = new ArrayList<>();
        districts = new ArrayList<>();
    }

    private void getCityContent(String url) throws IOException {
        Document doc = Jsoup.connect(url).userAgent(Main.agent).get();
        Elements allContent = doc.select("div.qylb");

        if (isOnce) {
            Elements c = allContent.first().getElementsByTag("a");
            provinces = new String[c.size() - 2];
            for (int i = 0; i < c.size() - 2; i++) {
                provinces[i] = c.get(i).text();
            }
            isOnce = false;
        }

        if (!allContent.first().select("a.xz").text().equals(provinces[j])) {
            if (allContent.size() != 3) {

                nowProvince = allContent.first().select("a.xz").text();
                //fw.write(nowProvince + "\n");
                System.out.println(nowProvince);

                for (Element city : allContent.get(1).getElementsByTag("a")) {
                    nowCity = city.text();

                    //fw.write("  " + nowCity + "\n");
                    System.out.println("  " + nowCity);
                    citys.add(changeString(city.text()));
                    String districtLink = city.attr("href");

                    if (checkHasChild(districtLink))
                        getCityContent(districtLink);
                }
                j++;

//                while (j <= provinces.length) {
//                    getCityContent(allContent.first().getElementsByTag("a").get(j).attr("href"));
//                }

            } else if (allContent.size() == 3) {
                for (Element district : allContent.last().getElementsByTag("a")) {
                    //fw.write("    " + district.text() + "\n");
                    nowDistrict = district.text();
                    districts.add(changeString(nowDistrict));
                    System.out.println("    " + district.text());
                    getSchoolContent(district.attr("href"));
                    //fw.write("\n");
                    System.out.println();
                }
            }
        }
    }

    private boolean checkHasChild(String url) throws IOException {
        return Jsoup.connect(url).userAgent(Main.agent).get().select("div.qylb").size() == 3;
    }

    void getSchoolContent(String url) throws IOException {
        Document doc = Jsoup.connect(url).userAgent(Main.agent).get();
        Elements pageContent = doc.select("span.zys");
        Elements allContent = doc.select("div.xxlb");
        int pageNum = Integer.parseInt(pageContent.first().text());

        if (pageNum > 1) {
            String pageUrl;
            for (int i = 1; i <= pageNum; i++) {
                pageUrl = i == 1 ? url : url + "&p=" + i;
                outSchoolContent(pageUrl);
            }
        } else {
            if (allContent.first().getElementsByTag("a").size() == 0) {
                saveEmptyContent();
            } else {
                outSchoolContent(url);
            }
        }
    }

    private void outSchoolContent(String url) throws IOException {
        Document doc = Jsoup.connect(url).userAgent(Main.agent).get();
        Elements allContent = doc.select("div.xxlb");
        for (Element school : allContent.first().getElementsByTag("a")) {
            fillHashMap(school.text());
            if (!school.text().equals("") && !school.text().isEmpty()) {
                //  fw.write("      " + school.text() + "\n");
                System.out.println("      " + school.text());
            }
        }
    }

    private void saveEmptyContent() {
        fillHashMap(null);
    }

    private void fillHashMap(String schoolName) { //填充散列Map
        if (!this.totalContent.containsKey(nowProvince)) { //若当前省不存在map中，即切换到新的省
            cityContent = new HashMap<>();
            districtContent = new HashMap<>();
            schoolList = new HashSet<>();
        } else { //在同一省内遍历
            cityContent = totalContent.get(nowProvince); //获取当前省份的下属城市map
            if (!cityContent.containsKey(nowCity)) { //循环在城市内移动，指向下一个新的区/县
                districtContent = new HashMap<>();
                schoolList = new HashSet<>();
            } else { //同一城市内移动
                districtContent = cityContent.get(nowCity);
                if (!districtContent.containsKey(nowDistrict)) {
                    schoolList = new HashSet<>();
                }
            }
        }
        schoolList.add(schoolName);
        districtContent.put(nowDistrict, schoolList);
        cityContent.put(nowCity, districtContent);
        totalContent.put(nowProvince, cityContent);
    }

    private void check(String... args) {
        String schoolName;
        String address;
        String tempContent;

        if (args.length == 1) {
            schoolName = args[0];
            address = null;
            tempContent = schoolName;
        } else {
            schoolName = args[0];
            address = args[1];
            tempContent = address;
        }

        String[] match = new String[3];

        for (int i = 0; i < match.length; i++) {
            match[i] = null;
        }

        if (tempContent.contains(" ")) {
            String[] s = tempContent.split(" ");//提取字符串
            tempContent = s[s.length - 1];//如果字符串内有空格分割，则取空格后的数据
        }

        if (address == null) {
            noMatchSchool.add(schoolName); //先把当前学校加入无匹配队列中
        }
        List<String> note = new ArrayList<>();
        tempContent = changeString(tempContent);

        for (String p : provinces) {
            if (tempContent.contains(p)) {
                note.add(p);
                tempContent = tempContent.replace(p, "");
                break;
            }
        }

        for (String c : citys) {
            if (tempContent.contains(c)) {
                note.add(c);
                tempContent = tempContent.replace(c, "");
                break;
            }
        }

        for (String d : districts) {
            if (tempContent.contains(d)) {
                note.add(d);
                break;
            }
        }

        System.out.println(tempContent);

        if (note.size() != 0) {
            for (String province : totalContent.keySet()) {
                cityContent = totalContent.get(province);
                match[0] = province;

                for (String city : cityContent.keySet()) {
                    districtContent = cityContent.get(city);
                    match[1] = city;

                    for (String district : districtContent.keySet()) {
                        match[2] = district;
                        schoolList = districtContent.get(district);

                        if (schoolList == null) {
                            schoolList = new HashSet<>();
                        }

                        if (note.contains(changeString(district))) {
                            System.out.println(note.contains(changeString(district)));
                            schoolList.add(schoolName);
                            noMatchSchool.remove(schoolName);

                            districtContent.put(district, schoolList);
                            cityContent.put(match[1], districtContent);
                            totalContent.put(match[0], cityContent);
                            return;
                        }
                    }
                }
            }
        }

        if (noMatchSchool.contains(schoolName) && address == null) {
            check(schoolName, getAddress());
        }

        if (match[2] == null && match[0] != null && match[1] != null) {
            cityContent = totalContent.get(match[0]);
            districtContent = cityContent.get(match[1]);

            schoolList = new HashSet<>();
            schoolList.add(schoolName);

            districtContent.put(note.get(note.size() - 1), schoolList);
            noMatchSchool.remove(schoolName);
            return;
        }
    }

    private String getAddress() {
        return sName_address;
    }

    public void init() throws IOException {
        fw = new FileWriter("new2.txt", true);
        getCityContent(Main.path);
        GetAllData getAllData = new GetAllData(this);
        getAllData.init();
    }

    private String changeString(String s) {
        s = s.replace("省", "");
        s = s.replace("地区", "");
        s = s.replace("区", "");
        s = s.replace("县", "");
        s = s.replace("市", "");
        return s;
    }

    String sName_address;

    @Override
    public void dataCallBack(Map<String, String> s) throws IOException {
        //while (!isFinish);

        for (String sName : s.keySet()) {
            sName_address = s.get(sName);
            check(sName);
        }

        for (String provinceKey : totalContent.keySet()) {
            System.out.println(provinceKey);
            fw.write(provinceKey + "\n");
            cityContent = totalContent.get(provinceKey);

            for (String cityKey : cityContent.keySet()) {
                System.out.println("  " + cityKey);
                fw.write("  " + cityKey + "\n");
                districtContent = cityContent.get(cityKey);

                for (String districtKey : districtContent.keySet()) {
                    System.out.println("    " + districtKey);
                    fw.write("     " + districtKey + "\n");
                    schoolList = districtContent.get(districtKey);

                    for (String schoolName : schoolList) {
                        if (schoolName == null) {
                            schoolName = "";
                        }
                        System.out.println("      " + schoolName);
                        fw.write("      " + schoolName + "\n");
                    }
                    fw.write("" + "\n");
                }
                fw.write("" + "\n");
            }
            fw.write("" + "\n");
        }

        fw.write("未排：" + "\n");
        for (String m : noMatchSchool) {
            fw.write(m + "\n");
        }
        fw.close();
    }
}
