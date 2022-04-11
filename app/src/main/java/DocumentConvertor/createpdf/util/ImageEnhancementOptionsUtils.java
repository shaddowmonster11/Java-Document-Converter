package DocumentConvertor.createpdf.util;

import android.content.Context;

import java.util.ArrayList;

import DocumentConvertor.createpdf.R;
import DocumentConvertor.createpdf.model.EnhancementOptionsEntity;
import DocumentConvertor.createpdf.model.ImageToPDFOptions;

public class ImageEnhancementOptionsUtils {

    public ImageEnhancementOptionsUtils() {
    }

    /**
     * Singleton Implementation
     */
    private static class SingletonHolder {
        private static final ImageEnhancementOptionsUtils INSTANCE = new ImageEnhancementOptionsUtils();
    }

    public static ImageEnhancementOptionsUtils getInstance() {
        return SingletonHolder.INSTANCE;
    }

    public ArrayList<EnhancementOptionsEntity> getEnhancementOptions(Context context,
                                                                     ImageToPDFOptions pdfOptions) {
        ArrayList<EnhancementOptionsEntity> options = new ArrayList<>();

        options.add(new EnhancementOptionsEntity(
                context, R.drawable.baseline_crop_rotate_24, R.string.edit_images_text));

        options.add(new EnhancementOptionsEntity(
                context, R.drawable.ic_photo_filter_black_24dp, R.string.filter_images_Text));

        options.add(new EnhancementOptionsEntity(
                context, R.drawable.ic_page_color, R.string.page_color));

        return options;
    }
}
