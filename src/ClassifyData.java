class ClassifyData extends Thread {
    GetClassifyData getClassifyData;
    protected String name;

    public ClassifyData(GetClassifyData getClassifyData) {
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
