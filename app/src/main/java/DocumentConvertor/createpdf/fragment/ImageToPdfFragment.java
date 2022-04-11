package DocumentConvertor.createpdf.fragment;

import static DocumentConvertor.createpdf.util.Constants.DEFAULT_BORDER_WIDTH;
import static DocumentConvertor.createpdf.util.Constants.DEFAULT_COMPRESSION;
import static DocumentConvertor.createpdf.util.Constants.DEFAULT_IMAGE_BORDER_TEXT;
import static DocumentConvertor.createpdf.util.Constants.DEFAULT_IMAGE_SCALE_TYPE_TEXT;
import static DocumentConvertor.createpdf.util.Constants.DEFAULT_PAGE_COLOR;
import static DocumentConvertor.createpdf.util.Constants.DEFAULT_PAGE_SIZE;
import static DocumentConvertor.createpdf.util.Constants.DEFAULT_PAGE_SIZE_TEXT;
import static DocumentConvertor.createpdf.util.Constants.DEFAULT_QUALITY_VALUE;
import static DocumentConvertor.createpdf.util.Constants.IMAGE_SCALE_TYPE_ASPECT_RATIO;
import static DocumentConvertor.createpdf.util.Constants.OPEN_SELECT_IMAGES;
import static DocumentConvertor.createpdf.util.Constants.REQUEST_CODE_FOR_WRITE_PERMISSION;
import static DocumentConvertor.createpdf.util.Constants.RESULT;
import static DocumentConvertor.createpdf.util.Constants.STORAGE_LOCATION;
import static DocumentConvertor.createpdf.util.Constants.WRITE_PERMISSIONS;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.os.Parcelable;
import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.afollestad.materialdialogs.MaterialDialog;
import com.dd.morphingbutton.MorphingButton;
import com.github.danielnilsson9.colorpickerview.view.ColorPickerView;
import com.theartofdev.edmodo.cropper.CropImage;
import com.zhihu.matisse.Matisse;

import java.util.ArrayList;
import java.util.HashMap;

import DocumentConvertor.createpdf.R;
import DocumentConvertor.createpdf.activity.CropImageActivity;
import DocumentConvertor.createpdf.activity.ImageEditor;
import DocumentConvertor.createpdf.adapter.EnhancementOptionsAdapter;
import DocumentConvertor.createpdf.database.DatabaseHelper;
import DocumentConvertor.createpdf.interfaces.OnItemClickListener;
import DocumentConvertor.createpdf.interfaces.OnPDFCreatedInterface;
import DocumentConvertor.createpdf.model.EnhancementOptionsEntity;
import DocumentConvertor.createpdf.model.ImageToPDFOptions;
import DocumentConvertor.createpdf.util.Constants;
import DocumentConvertor.createpdf.util.CreatePdf;
import DocumentConvertor.createpdf.util.DialogUtils;
import DocumentConvertor.createpdf.util.FileUtils;
import DocumentConvertor.createpdf.util.ImageEnhancementOptionsUtils;
import DocumentConvertor.createpdf.util.ImageUtils;
import DocumentConvertor.createpdf.util.MorphButtonUtility;
import DocumentConvertor.createpdf.util.PermissionsUtils;
import DocumentConvertor.createpdf.util.StringUtils;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * ImageToPdfFragment fragment to start with creating PDF
 */
public class ImageToPdfFragment extends Fragment implements OnItemClickListener,
        OnPDFCreatedInterface {

    private static final int INTENT_REQUEST_APPLY_FILTER = 10;
    private static final int INTENT_REQUEST_PREVIEW_IMAGE = 11;
    private static final int INTENT_REQUEST_GET_IMAGES = 13;

    @BindView(R.id.pdfCreate)
    MorphingButton mCreatePdf;
    @BindView(R.id.pdfOpen)
    MorphingButton mOpenPdf;
    @BindView(R.id.enhancement_options_recycle_view)
    RecyclerView mEnhancementOptionsRecycleView;
    @BindView(R.id.tvNoOfImages)
    TextView mNoOfImages;

    private MorphButtonUtility mMorphButtonUtility;
    private Activity mActivity;
    public static ArrayList<String> mImagesUri = new ArrayList<>();
    private static final ArrayList<String> mUnarrangedImagesUri = new ArrayList<>();
    private String mPath;
    private SharedPreferences mSharedPreferences;
    private FileUtils mFileUtils;
    private int mPageColor;
    private boolean mIsButtonAlreadyClicked = false;
    private ImageToPDFOptions mPdfOptions;
    private MaterialDialog mMaterialDialog;
    private String mHomePath;
    private final int mMarginTop = 50;
    private final int mMarginBottom = 38;
    private final int mMarginLeft = 50;
    private final int mMarginRight = 38;
    private String mPageNumStyle;
    private int mChoseId;


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mActivity = (Activity) context;
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View root = inflater.inflate(R.layout.fragment_images_to_pdf, container, false);
        ButterKnife.bind(this, root);

        // Initialize variables
        mSharedPreferences = PreferenceManager.getDefaultSharedPreferences(mActivity);
        mMorphButtonUtility = new MorphButtonUtility(mActivity);
        mFileUtils = new FileUtils(mActivity);
        mPageColor = mSharedPreferences.getInt(Constants.DEFAULT_PAGE_COLOR_ITP,
                DEFAULT_PAGE_COLOR);
        mHomePath = mSharedPreferences.getString(STORAGE_LOCATION,
                StringUtils.getInstance().getDefaultStorageLocation());

        // Get default values & show enhancement options
        resetValues();

        // Check for the images received
        checkForImagesInBundle();

        if (mImagesUri.size() > 0) {
            mNoOfImages.setText(String.format(mActivity.getResources()
                    .getString(R.string.images_selected), mImagesUri.size()));
            mNoOfImages.setVisibility(View.VISIBLE);
            mMorphButtonUtility.morphToSquare(mCreatePdf, mMorphButtonUtility.integer());
            mCreatePdf.setEnabled(true);
            StringUtils.getInstance().showSnackbar(mActivity, R.string.successToast);
        } else {
            mNoOfImages.setVisibility(View.GONE);
            mMorphButtonUtility.morphToGrey(mCreatePdf, mMorphButtonUtility.integer());
        }

        return root;
    }

    /**
     * Adds images (if any) received in the bundle
     */
    private void checkForImagesInBundle() {
        Bundle bundle = getArguments();
        if (bundle == null)
            return;
        if (bundle.getBoolean(OPEN_SELECT_IMAGES))
            startAddingImages();
        ArrayList<Parcelable> uris = bundle.getParcelableArrayList(getString(R.string.bundleKey));
        if (uris == null)
            return;
        for (Parcelable p : uris) {
            String uriRealPath = mFileUtils.getUriRealPath((Uri) p);
            if (uriRealPath == null) {
                StringUtils.getInstance().showSnackbar(mActivity, R.string.whatsappToast);
            } else {
                mImagesUri.add(uriRealPath);
            }
        }
    }

    /**
     * Shows enhancement options
     */
    private void showEnhancementOptions() {
        GridLayoutManager mGridLayoutManager = new GridLayoutManager(mActivity, 2);
        mEnhancementOptionsRecycleView.setLayoutManager(mGridLayoutManager);
        ImageEnhancementOptionsUtils imageEnhancementOptionsUtilsInstance = ImageEnhancementOptionsUtils.getInstance();
        ArrayList<EnhancementOptionsEntity> list = imageEnhancementOptionsUtilsInstance.getEnhancementOptions(mActivity,
                mPdfOptions);
        EnhancementOptionsAdapter adapter =
                new EnhancementOptionsAdapter(this, list);
        mEnhancementOptionsRecycleView.setAdapter(adapter);
    }

    /**
     * Adding Images to PDF
     */
    @OnClick(R.id.addImages)
    void startAddingImages() {
        if (!mIsButtonAlreadyClicked) {
            if (PermissionsUtils.getInstance().checkRuntimePermissions(this, WRITE_PERMISSIONS)) {
                selectImages();
                mIsButtonAlreadyClicked = true;
            } else {
                getRuntimePermissions();
            }
        }
    }

    /**
     * Create Pdf of selected images
     */
    @OnClick(R.id.pdfCreate)
    void pdfCreateClicked() {
        createPdf(false);
    }

    /**
     * Opens the dialog to select a save name
     */
    private void createPdf(boolean isGrayScale) {
        String preFillName = mFileUtils.getLastFileName(mImagesUri);
        String ext = getString(R.string.pdf_ext);
        mFileUtils.openSaveDialog(preFillName, ext, filename -> save(isGrayScale, filename));
    }

    /**
     * Saves the PDF
     * @param isGrayScale if the images should be converted to grayscale before
     * @param filename the filename to save to
     */
    private void save(boolean isGrayScale, String filename) {
        mPdfOptions.setImagesUri(mImagesUri);
        mPdfOptions.setImageScaleType(ImageUtils.getInstance().mImageScaleType);
        mPdfOptions.setPageNumStyle(mPageNumStyle);
        mPdfOptions.setPageColor(mPageColor);
        mPdfOptions.setOutFileName(filename);
        new CreatePdf(mPdfOptions, mHomePath, ImageToPdfFragment.this).execute();
    }

    @OnClick(R.id.pdfOpen)
    void openPdf() {
        mFileUtils.openFile(mPath, FileUtils.FileType.e_PDF);
    }



    /**
     * Called after user is asked to grant permissions
     *
     * @param requestCode  REQUEST Code for opening permissions
     * @param permissions  permissions asked to user
     * @param grantResults bool array indicating if permission is granted
     */
    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        PermissionsUtils.getInstance().handleRequestPermissionsResult(mActivity, grantResults,
                requestCode, REQUEST_CODE_FOR_WRITE_PERMISSION, this::selectImages);
    }

    /**
     * Called after Matisse Activity is called
     *
     * @param requestCode REQUEST Code for opening Matisse Activity
     * @param resultCode  result code of the process
     * @param data        Data of the image selected
     */
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        mIsButtonAlreadyClicked = false;
        if (resultCode != Activity.RESULT_OK || data == null)
            return;

        switch (requestCode) {
            case INTENT_REQUEST_GET_IMAGES:
                mImagesUri.clear();
                mUnarrangedImagesUri.clear();
                mImagesUri.addAll(Matisse.obtainPathResult(data));
                mUnarrangedImagesUri.addAll(mImagesUri);
                if (mImagesUri.size() > 0) {
                    mNoOfImages.setText(String.format(mActivity.getResources()
                            .getString(R.string.images_selected), mImagesUri.size()));
                    mNoOfImages.setVisibility(View.VISIBLE);
                    StringUtils.getInstance().showSnackbar(mActivity, R.string.snackbar_images_added);
                    mCreatePdf.setEnabled(true);
                    mCreatePdf.unblockTouch();
                }
                mMorphButtonUtility.morphToSquare(mCreatePdf, mMorphButtonUtility.integer());
                mOpenPdf.setVisibility(View.GONE);
                break;

            case CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE:
                HashMap<Integer, Uri> croppedImageUris =
                        (HashMap) data.getSerializableExtra(CropImage.CROP_IMAGE_EXTRA_RESULT);

                for (int i = 0; i < mImagesUri.size(); i++) {
                    if (croppedImageUris.get(i) != null) {
                        mImagesUri.set(i, croppedImageUris.get(i).getPath());
                        StringUtils.getInstance().showSnackbar(mActivity, R.string.snackbar_imagecropped);
                    }
                }
                break;

            case INTENT_REQUEST_APPLY_FILTER:
                mImagesUri.clear();
                ArrayList<String> mFilterUris = data.getStringArrayListExtra(RESULT);
                int size = mFilterUris.size() - 1;
                for (int k = 0; k <= size; k++)
                    mImagesUri.add(mFilterUris.get(k));
                break;

            case INTENT_REQUEST_PREVIEW_IMAGE:
                mImagesUri = data.getStringArrayListExtra(RESULT);
                if (mImagesUri.size() > 0) {
                    mNoOfImages.setText(String.format(mActivity.getResources()
                            .getString(R.string.images_selected), mImagesUri.size()));
                } else {
                    mNoOfImages.setVisibility(View.GONE);
                    mMorphButtonUtility.morphToGrey(mCreatePdf, mMorphButtonUtility.integer());
                    mCreatePdf.setEnabled(false);
                }
                break;

        }
    }
    @Override
    public void onItemClick(int position) {

        if (mImagesUri.size() == 0) {
            StringUtils.getInstance().showSnackbar(mActivity, R.string.snackbar_no_images);
            return;
        }
        switch (position) {
            case 0:
                cropImage();
                break;
            case 1:
                startActivityForResult(ImageEditor.getStartIntent(mActivity, mImagesUri),
                        INTENT_REQUEST_APPLY_FILTER);
                break;
            case 2:
                setPageColor();
                break;

        }
    }

    private void setPageColor() {
        MaterialDialog materialDialog = new MaterialDialog.Builder(mActivity)
                .title(R.string.page_color)
                .customView(R.layout.dialog_color_chooser, true)
                .positiveText(R.string.ok)
                .negativeText(R.string.cancel)
                .onPositive((dialog, which) -> {
                    View view = dialog.getCustomView();
                    ColorPickerView colorPickerView = view.findViewById(R.id.color_picker);
                    CheckBox defaultCheckbox = view.findViewById(R.id.set_default);
                    mPageColor = colorPickerView.getColor();
                    if (defaultCheckbox.isChecked()) {
                        SharedPreferences.Editor editor = mSharedPreferences.edit();
                        editor.putInt(Constants.DEFAULT_PAGE_COLOR_ITP, mPageColor);
                        editor.apply();
                    }
                })
                .build();
        ColorPickerView colorPickerView = materialDialog.getCustomView().findViewById(R.id.color_picker);
        colorPickerView.setColor(mPageColor);
        materialDialog.show();
    }

    @Override
    public void onPDFCreationStarted() {
        mMaterialDialog = DialogUtils.getInstance().createAnimationDialog(mActivity);
        mMaterialDialog.show();
    }

    @Override
    public void onPDFCreated(boolean success, String path) {
        if (mMaterialDialog != null && mMaterialDialog.isShowing())
            mMaterialDialog.dismiss();

        if (!success) {
            StringUtils.getInstance().showSnackbar(mActivity, R.string.snackbar_folder_not_created);
            return;
        }
        new DatabaseHelper(mActivity).insertRecord(path, mActivity.getString(R.string.created));
        StringUtils.getInstance().getSnackbarwithAction(mActivity, R.string.snackbar_pdfCreated)
                .setAction(R.string.snackbar_viewAction,
                        v -> mFileUtils.openFile(mPath, FileUtils.FileType.e_PDF)).show();
        mOpenPdf.setVisibility(View.VISIBLE);
        mMorphButtonUtility.morphToSuccess(mCreatePdf);
        mCreatePdf.blockTouch();
        mPath = path;
        resetValues();
    }

    private void cropImage() {
        Intent intent = new Intent(mActivity, CropImageActivity.class);
        startActivityForResult(intent, CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE);
    }

    private void getRuntimePermissions() {
        PermissionsUtils.getInstance().requestRuntimePermissions(this,
                    WRITE_PERMISSIONS,
                    REQUEST_CODE_FOR_WRITE_PERMISSION);
    }

    /**
     * Opens Matisse activity to select Images
     */
    private void selectImages() {
        ImageUtils.selectImages(this, INTENT_REQUEST_GET_IMAGES);
    }

    /**
     * Resets pdf creation related values & show enhancement options
     */
    private void resetValues() {
        mPdfOptions = new ImageToPDFOptions();
        mPdfOptions.setBorderWidth(mSharedPreferences.getInt(DEFAULT_IMAGE_BORDER_TEXT,
                DEFAULT_BORDER_WIDTH));
        mPdfOptions.setQualityString(
                Integer.toString(mSharedPreferences.getInt(DEFAULT_COMPRESSION,
                        DEFAULT_QUALITY_VALUE)));
        mPdfOptions.setPageSize(mSharedPreferences.getString(DEFAULT_PAGE_SIZE_TEXT,
                DEFAULT_PAGE_SIZE));
        mImagesUri.clear();
        showEnhancementOptions();
        mNoOfImages.setVisibility(View.GONE);
        ImageUtils.getInstance().mImageScaleType = mSharedPreferences.getString(DEFAULT_IMAGE_SCALE_TYPE_TEXT,
                IMAGE_SCALE_TYPE_ASPECT_RATIO);
        mPdfOptions.setMargins(0, 0, 0, 0);
        mPageNumStyle = mSharedPreferences.getString (Constants.PREF_PAGE_STYLE, null);
        mPageColor = mSharedPreferences.getInt(Constants.DEFAULT_PAGE_COLOR_ITP,
                DEFAULT_PAGE_COLOR);
    }
}
