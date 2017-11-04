import java.io.IOException;

class ClassifyDataThread extends Thread {
    GetClassifyData getClassifyData;
    protected String name;

    public ClassifyDataThread(GetClassifyData getClassifyData) {
        this.getClassifyData = getClassifyData;
    }

    @Override
    public void run() {
        try {
            if(!Thread.currentThread().isInterrupted()) {
                getClassifyData.init();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
