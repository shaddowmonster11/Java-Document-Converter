package DocumentConvertor.createpdf.util;

import android.app.Activity;

import com.afollestad.materialdialogs.MaterialDialog;

import DocumentConvertor.createpdf.R;

public class DialogUtils {

    private DialogUtils() {
    }

    private static class SingletonHolder {
        static final DialogUtils INSTANCE = new DialogUtils();
    }

    public static DialogUtils getInstance() {
        return DialogUtils.SingletonHolder.INSTANCE;
    }

    /**
     * Creates a material dialog with `Warning` title
     * @param activity - activity instance
     * @param content - content resource id
     * @return - material dialog builder
     */
    public MaterialDialog.Builder createWarningDialog(Activity activity,
                                                             int content) {
        return new MaterialDialog.Builder(activity)
                .title(R.string.warning)
                .content(content)
                .positiveText(android.R.string.ok)
                .negativeText(android.R.string.cancel);
    }

    /**
     * Creates a material dialog with `warning title` and overwrite message as content
     * @param activity - activity instance
     * @return - material dialog builder
     */
    public MaterialDialog.Builder createOverwriteDialog(Activity activity) {
        return new MaterialDialog.Builder(activity)
                .title(R.string.warning)
                .content(R.string.overwrite_message)
                .positiveText(android.R.string.ok)
                .negativeText(android.R.string.cancel);
    }

    /**
     * Creates a material dialog with given title & content
     * @param activity - activity instance
     * @param title - dialog title resource id
     * @param content - content resource id
     * @return - material dialog builder
     */
    public MaterialDialog.Builder createCustomDialog(Activity activity,
                                                            int title, int content) {
        return new MaterialDialog.Builder(activity)
                .title(title)
                .content(content)
                .positiveText(android.R.string.ok)
                .negativeText(android.R.string.cancel);
    }

    /**
     * Creates a material dialog with given title
     * @param activity - activity instance
     * @param title - dialog title resource id
     * @return - material dialog builder
     */
    public MaterialDialog.Builder createCustomDialogWithoutContent(Activity activity,
                                                            int title) {
        return new MaterialDialog.Builder(activity)
                .title(title)
                .positiveText(android.R.string.ok)
                .negativeText(android.R.string.cancel);
    }

    /**
     * Creates dialog with animation
     * @param activity - activity instance
     * @return - material dialog
     */
    public MaterialDialog createAnimationDialog(Activity activity) {
        return new MaterialDialog.Builder(activity)
                .customView(R.layout.lottie_anim_dialog, false)
                .build();
    }

    /**
     * Creates dialog with animation
     * @param activity - activity instance
     * @param title - dialog message
     * @return - material dialog
     */

   /* public MaterialDialog createCustomAnimationDialog(Activity activity, String title) {
        View view = LayoutInflater.from(activity).inflate(R.layout.lottie_anim_dialog, null);

        TextView dialogTitle = view.findViewById(R.id.textView);
        dialogTitle.setText(title);

        return new MaterialDialog.Builder(activity)
                .customView(view, false)
                .build();
                //Problem with MaterialDialog not showing properly
    }*/
}