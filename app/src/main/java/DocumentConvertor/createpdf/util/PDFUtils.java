package DocumentConvertor.createpdf.util;

import android.app.Activity;
import android.app.AlertDialog;

import androidx.annotation.WorkerThread;
import android.widget.TextView;

import com.itextpdf.text.pdf.PdfReader;

import java.io.File;
import java.io.IOException;

import DocumentConvertor.createpdf.R;

public class PDFUtils {

    private final Activity mContext;
    private final FileUtils mFileUtils;

    public PDFUtils(Activity context) {
        this.mContext = context;
        this.mFileUtils = new FileUtils(mContext);
    }

    /**
     * Creates a mDialog with details of given PDF file
     *
     * @param file - file name
     */
    public void showDetails(File file) {
        String name = file.getName();
        String path = file.getPath();
        String size = FileInfoUtils.getFormattedSize(file);
        String lastModDate = FileInfoUtils.getFormattedSize(file);

        TextView message = new TextView(mContext);
        TextView title = new TextView(mContext);
        message.setText(String.format
                (mContext.getResources().getString(R.string.file_info), name, path, size, lastModDate));
        message.setTextIsSelectable(true);
        title.setText(R.string.details);
        title.setPadding(20, 10, 10, 10);
        title.setTextSize(30);
        title.setTextColor(mContext.getResources().getColor(R.color.black));
        final AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
        final AlertDialog dialog = builder.create();
        builder.setView(message);
        builder.setCustomTitle(title);
        builder.setPositiveButton(mContext.getResources().getString(R.string.ok),
                (dialogInterface, i) -> dialog.dismiss());
        builder.create();
        builder.show();
    }

    /**
     * Check if a PDF at given path is encrypted
     *
     * @param path - path of PDF
     * @return true - if encrypted otherwise false
     */
    @WorkerThread
    public boolean isPDFEncrypted(String path) {
        boolean isEncrypted;
        PdfReader pdfReader = null;
        try {
            pdfReader = new PdfReader(path);
            isEncrypted = pdfReader.isEncrypted();
        } catch (IOException e) {
            isEncrypted = true;
        } finally {
            if (pdfReader != null) pdfReader.close();
        }
        return isEncrypted;
    }
    }

