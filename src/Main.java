import java.io.*;
import java.util.*;

/**
 * Created by TangChen on 17/10/30.
 */
public class Main{
    public static String agent = "Mozilla/5.0 (Windows NT 6.1; rv:30.0) Gecko/20100101 Firefox/30.0";
    public static String path = "http://www.ruyile.com/xuexiao/?a=7&t=2";

    public static void main(String[] args) throws IOException {
        GetClassifyData getClassifyData = new GetClassifyData();

        AllDataThread getAllData = new AllDataThread(getClassifyData);

        ClassifyData getClassifyData1 = new ClassifyData(getClassifyData);

        getClassifyData1.run();

        getAllData.run();
    }
}