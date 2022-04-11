package DocumentConvertor.createpdf.model;

public class PDFOptions {

    private String mOutFileName;

    private String mPageSize;
    private int mBorderWidth;
    private int mPageColor;

    PDFOptions() {

    }

    PDFOptions(String mFileName, String mPageSize, int mBorderWidth, int pageColor) {
        this.mOutFileName = mFileName;
        this.mPageSize = mPageSize;

        this.mBorderWidth = mBorderWidth;
        this.mPageColor = pageColor;
    }

    public String getOutFileName() {
        return mOutFileName;
    }

    public String getPageSize() {
        return mPageSize;
    }


    public int getBorderWidth() {
        return mBorderWidth;
    }

    public void setOutFileName(String mOutFileName) {
        this.mOutFileName = mOutFileName;
    }

    public void setPageSize(String mPageSize) {
        this.mPageSize = mPageSize;
    }

    public void setBorderWidth(int mBorderWidth) {
        this.mBorderWidth = mBorderWidth;
    }


    public int getPageColor() {
        return mPageColor;
    }

    public void setPageColor(int pageColor) {
        this.mPageColor = pageColor;
    }
}
