package DocumentConvertor.createpdf.util;

import static DocumentConvertor.createpdf.R.drawable.ic_history_black_24dp;
import static DocumentConvertor.createpdf.R.drawable.ic_image_black_24dp;
import static DocumentConvertor.createpdf.R.drawable.ic_menu_camera;
import static DocumentConvertor.createpdf.R.drawable.ic_menu_gallery;
import static DocumentConvertor.createpdf.R.drawable.ic_text_format_black_24dp;
import static DocumentConvertor.createpdf.R.id.nav_camera;
import static DocumentConvertor.createpdf.R.id.nav_gallery;
import static DocumentConvertor.createpdf.R.id.nav_history;
import static DocumentConvertor.createpdf.R.id.nav_pdf_to_images;
import static DocumentConvertor.createpdf.R.id.nav_text_to_pdf;
import static DocumentConvertor.createpdf.R.id.pdf_to_images;
import static DocumentConvertor.createpdf.R.id.text_to_pdf;
import static DocumentConvertor.createpdf.R.id.view_files;
import static DocumentConvertor.createpdf.R.id.view_history;

import android.app.Activity;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.airbnb.lottie.LottieAnimationView;
import com.google.android.material.bottomsheet.BottomSheetBehavior;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import DocumentConvertor.createpdf.R;
import DocumentConvertor.createpdf.adapter.ExtractImagesAdapter;
import DocumentConvertor.createpdf.adapter.MergeFilesAdapter;
import DocumentConvertor.createpdf.model.HomePageItem;


public class CommonCodeUtils {

    Map<Integer, HomePageItem> mFragmentPositionMap;

    /**
     * updates the output recycler view if paths.size > 0
     * else give the main view
     */
    public void populateUtil(Activity mActivity, ArrayList<String> paths,
                             MergeFilesAdapter.OnClickListener onClickListener,
                             RelativeLayout layout, LottieAnimationView animationView,
                             RecyclerView recyclerView) {

        if (paths == null || paths.size() == 0) {
            layout.setVisibility(View.GONE);
        } else {
            // Init recycler view
            recyclerView.setVisibility(View.VISIBLE);
            MergeFilesAdapter mergeFilesAdapter = new MergeFilesAdapter(mActivity,
                    paths, false, onClickListener);
            RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(mActivity);
            recyclerView.setLayoutManager(mLayoutManager);
            recyclerView.setAdapter(mergeFilesAdapter);
            recyclerView.addItemDecoration(new ViewFilesDividerItemDecoration(mActivity));
        }
        animationView.setVisibility(View.GONE);
    }


    /**
     * sets the appropriate text to success Text View & display images in adapter
     */
    public void updateView(Activity mActivity, int imageCount, ArrayList<String> outputFilePaths,
                           TextView successTextView, LinearLayout options, RecyclerView mCreatedImages,
                           ExtractImagesAdapter.OnFileItemClickedListener listener) {

        if (imageCount == 0) {
            StringUtils.getInstance().showSnackbar(mActivity, R.string.extract_images_failed);
            return;
        }

        String text = String.format(mActivity.getString(R.string.extract_images_success), imageCount);
        StringUtils.getInstance().showSnackbar(mActivity, text);
        successTextView.setVisibility(View.VISIBLE);
        options.setVisibility(View.VISIBLE);
        ExtractImagesAdapter extractImagesAdapter = new ExtractImagesAdapter(mActivity, outputFilePaths, listener);
        // init recycler view for displaying generated image list
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(mActivity);
        successTextView.setText(text);
        mCreatedImages.setVisibility(View.VISIBLE);
        mCreatedImages.setLayoutManager(mLayoutManager);
        // set up adapter
        mCreatedImages.setAdapter(extractImagesAdapter);
        mCreatedImages.addItemDecoration(new ViewFilesDividerItemDecoration(mActivity));
    }

    /**
     * Closes the bottom sheet if it is expanded
     */

    public void closeBottomSheetUtil(BottomSheetBehavior sheetBehavior) {
        if (checkSheetBehaviourUtil(sheetBehavior))
            sheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
    }

    /**
     * Checks whether the bottom sheet is expanded or collapsed
     */
    public boolean checkSheetBehaviourUtil(BottomSheetBehavior sheetBehavior) {
        return sheetBehavior.getState() == BottomSheetBehavior.STATE_EXPANDED;
    }

    private void addFragmentPosition(boolean homePageItems, int iconA,
                                     int iconId, int drawableId, int titleString) {
        mFragmentPositionMap.put(homePageItems ? iconA : 0, new HomePageItem(iconId, drawableId, titleString));
    }

    public Map<Integer, HomePageItem> fillNavigationItemsMap(boolean homePageItems) {
        mFragmentPositionMap = new HashMap<>();
        addFragmentPosition(homePageItems, R.id.images_to_pdf, nav_camera, ic_menu_camera, R.string.images_to_pdf);
        addFragmentPosition(homePageItems, view_files,
                nav_gallery, ic_menu_gallery, R.string.viewFiles);
        addFragmentPosition(homePageItems, text_to_pdf,
                nav_text_to_pdf, ic_text_format_black_24dp, R.string.text_to_pdf);
        addFragmentPosition(homePageItems, view_history,
                nav_history, ic_history_black_24dp, R.string.history);
        addFragmentPosition(homePageItems, pdf_to_images,
                nav_pdf_to_images, ic_image_black_24dp, R.string.pdf_to_images);
        return mFragmentPositionMap;
    }

    private static class SingletonHolder {
        static final CommonCodeUtils INSTANCE = new CommonCodeUtils();
    }

    public static CommonCodeUtils getInstance() {
        return CommonCodeUtils.SingletonHolder.INSTANCE;
    }
}
