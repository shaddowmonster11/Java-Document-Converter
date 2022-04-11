package DocumentConvertor.createpdf.util;

import android.content.Context;

import androidx.fragment.app.Fragment;

import DocumentConvertor.createpdf.R;
import DocumentConvertor.createpdf.fragment.HistoryFragment;
import DocumentConvertor.createpdf.fragment.ImageToPdfFragment;
import DocumentConvertor.createpdf.fragment.PdfToImageFragment;
import DocumentConvertor.createpdf.fragment.texttopdf.TextToPdfFragment;

public class FragmentUtils {

    private final Context mContext;

    public FragmentUtils(Context mContext) {
        this.mContext = mContext;
    }

    public String getFragmentName(Fragment fragment) {
        String name = mContext.getString(R.string.app_name);
        if (fragment instanceof ImageToPdfFragment) {
            name = mContext.getString(R.string.images_to_pdf);
        } else if (fragment instanceof TextToPdfFragment) {
            name = mContext.getString(R.string.text_to_pdf);
        }
      else if (fragment instanceof HistoryFragment) {
            name = mContext.getString(R.string.history);
        }
         else if (fragment instanceof PdfToImageFragment) {
            name = mContext.getString(R.string.pdf_to_images);
        }
        return name;
    }

    public boolean handleFragmentBottomSheetBehavior(Fragment fragment) {
        boolean bottomSheetBehaviour = false;
            if (fragment instanceof PdfToImageFragment) {
            bottomSheetBehaviour = ((PdfToImageFragment) fragment).checkSheetBehaviour();
                if (bottomSheetBehaviour) ((PdfToImageFragment) fragment).closeBottomSheet();
        }
        return bottomSheetBehaviour;
    }


}
