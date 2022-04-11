package DocumentConvertor.createpdf.providers.fragmentmanagement;

import android.content.Intent;
import android.os.Bundle;
import com.google.android.material.navigation.NavigationView;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import android.widget.Toast;

import java.util.Objects;

import DocumentConvertor.createpdf.R;
import DocumentConvertor.createpdf.fragment.HistoryFragment;
import DocumentConvertor.createpdf.fragment.HomeFragment;
import DocumentConvertor.createpdf.fragment.ImageToPdfFragment;
import DocumentConvertor.createpdf.fragment.PdfToImageFragment;
import DocumentConvertor.createpdf.fragment.ViewFilesFragment;
import DocumentConvertor.createpdf.fragment.texttopdf.TextToPdfFragment;
import DocumentConvertor.createpdf.util.FragmentUtils;

import static DocumentConvertor.createpdf.util.Constants.ACTION_SELECT_IMAGES;
import static DocumentConvertor.createpdf.util.Constants.ACTION_TEXT_TO_PDF;
import static DocumentConvertor.createpdf.util.Constants.ACTION_VIEW_FILES;
import static DocumentConvertor.createpdf.util.Constants.BUNDLE_DATA;
import static DocumentConvertor.createpdf.util.Constants.OPEN_SELECT_IMAGES;
import static DocumentConvertor.createpdf.util.Constants.PDF_TO_IMAGES;

/**
 * This is a fragment service that manages the fragments
 * mainly for the MainActivity.
 */
public class FragmentManagement implements IFragmentManagement {
    private final FragmentActivity mContext;
    private final NavigationView mNavigationView;
    private boolean mDoubleBackToExitPressedOnce = false;
    private final FragmentUtils mFragmentUtils;

    public FragmentManagement(FragmentActivity context, NavigationView navigationView) {
        mContext = context;
        mNavigationView = navigationView;
        mFragmentUtils = new FragmentUtils(mContext);
    }

    public Fragment checkForAppShortcutClicked() {
        Fragment fragment = new HomeFragment();
        if (mContext.getIntent().getAction() != null) {
            switch (Objects.requireNonNull(mContext.getIntent().getAction())) {
                case ACTION_SELECT_IMAGES:
                    fragment = new ImageToPdfFragment();
                    Bundle bundle = new Bundle();
                    bundle.putBoolean(OPEN_SELECT_IMAGES, true);
                    fragment.setArguments(bundle);
                    break;
                case ACTION_VIEW_FILES:
                    fragment = new ViewFilesFragment();
                    setNavigationViewSelection(R.id.nav_gallery);
                    break;
                case ACTION_TEXT_TO_PDF:
                    fragment = new TextToPdfFragment();
                    setNavigationViewSelection(R.id.nav_text_to_pdf);
                    break;
                default:
                    fragment = new HomeFragment(); // Set default fragment
                    break;
            }
        }
        if (areImagesReceived())
            fragment = new ImageToPdfFragment();

        mContext.getSupportFragmentManager().beginTransaction().replace(R.id.content, fragment).commit();

        return fragment;
    }

    public boolean handleBackPressed() {
        Fragment currentFragment = mContext.getSupportFragmentManager()
                .findFragmentById(R.id.content);
        if (currentFragment instanceof HomeFragment) {
            return checkDoubleBackPress();
        } else {
            if (mFragmentUtils.handleFragmentBottomSheetBehavior(currentFragment))
                return false;
        }
        handleBackStackEntry();
        return false;
    }

    public boolean handleNavigationItemSelected(int itemId) {
        Fragment fragment = null;
        FragmentManager fragmentManager = mContext.getSupportFragmentManager();
        Bundle bundle = new Bundle();

        switch (itemId) {
            case R.id.nav_home:
                fragment = new HomeFragment();
                break;
            case R.id.nav_camera:
                fragment = new ImageToPdfFragment();
                break;
            case R.id.nav_gallery:
                fragment = new ViewFilesFragment();
                break;
            case R.id.nav_text_to_pdf:
                fragment = new TextToPdfFragment();
                break;
            case R.id.nav_history:
                fragment = new HistoryFragment();
                break;
            case R.id.nav_pdf_to_images:
                fragment = new PdfToImageFragment();
                bundle.putString(BUNDLE_DATA, PDF_TO_IMAGES);
                fragment.setArguments(bundle);
                break;

        }

        try {
            if (fragment != null)
                fragmentManager.beginTransaction().replace(R.id.content, fragment).commit();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return itemId != R.id.action_done;
    }

    /**
     * Closes the app only when double clicked
     */
    private boolean checkDoubleBackPress() {
        if (mDoubleBackToExitPressedOnce) {
            return true;
        }
        mDoubleBackToExitPressedOnce = true;
        Toast.makeText(mContext, R.string.confirm_exit_message, Toast.LENGTH_SHORT).show();
        return false;
    }

    /**
     *  on clicking back, return back to previous instance
     */
    private void handleBackStackEntry() {
        int count = mContext.getSupportFragmentManager().getBackStackEntryCount();
        if (count > 0) {
            String s = mContext.getSupportFragmentManager().getBackStackEntryAt(count - 1).getName();
            mContext.setTitle(s);
            mContext.getSupportFragmentManager().popBackStack();
        } else {
            Fragment fragment = new HomeFragment();
            mContext.getSupportFragmentManager().beginTransaction().replace(R.id.content, fragment).commit();
            mContext.setTitle(R.string.app_name);
            setNavigationViewSelection(R.id.nav_home);
        }
    }

    private boolean areImagesReceived() {
        Intent intent = mContext.getIntent();
        String type = intent.getType();
        return type != null && type.startsWith("image/");
    }

    private void setNavigationViewSelection(int id) {
        mNavigationView.setCheckedItem(id);
    }
}
