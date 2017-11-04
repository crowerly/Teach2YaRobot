import java.io.IOException;

/**
 * Created by TangChen on 17/11/4.
 */
public class AllDataThread extends Thread {
    GetClassifyData getClassifyData;

    public AllDataThread(GetClassifyData getClassifyData) {
        this.getClassifyData = getClassifyData;
    }

    @Override
    public void run() {
        GetAllData getAllData = new GetAllData(getClassifyData);
        try {
            getAllData.init();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
