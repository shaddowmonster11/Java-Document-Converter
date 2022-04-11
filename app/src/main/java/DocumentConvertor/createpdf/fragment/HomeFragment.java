package DocumentConvertor.createpdf.fragment;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.preference.PreferenceManager;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import org.json.JSONException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import DocumentConvertor.createpdf.R;
import DocumentConvertor.createpdf.activity.MainActivity;
import DocumentConvertor.createpdf.adapter.RecentListAdapter;
import DocumentConvertor.createpdf.customviews.MyCardView;
import DocumentConvertor.createpdf.fragment.texttopdf.TextToPdfFragment;
import DocumentConvertor.createpdf.model.HomePageItem;
import DocumentConvertor.createpdf.util.CommonCodeUtils;
import DocumentConvertor.createpdf.util.RecentUtil;

import static DocumentConvertor.createpdf.util.Constants.BUNDLE_DATA;
import static DocumentConvertor.createpdf.util.Constants.PDF_TO_IMAGES;
public class HomeFragment extends Fragment implements View.OnClickListener {

    private Activity mActivity;
    @BindView(R.id.images_to_pdf)
    MyCardView imagesToPdf;
    @BindView(R.id.text_to_pdf)
    MyCardView textToPdf;
    @BindView(R.id.view_files)
    MyCardView viewFiles;
    @BindView(R.id.view_history)
    MyCardView viewHistory;
    @BindView(R.id.pdf_to_images)
    MyCardView mPdfToImages;
    @BindView(R.id.recent_list)
    RecyclerView recentList;
    @BindView(R.id.recent_lbl)
    View recentLabel;
    @BindView(R.id.recent_list_lay)
    ViewGroup recentLayout;

    private Map<Integer, HomePageItem> mFragmentPositionMap;
    private RecentListAdapter mAdapter;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootview = inflater.inflate(R.layout.fragment_home, container, false);
        ButterKnife.bind(this, rootview);
        mFragmentPositionMap = CommonCodeUtils.getInstance().fillNavigationItemsMap(true);

        imagesToPdf.setOnClickListener(this);
        textToPdf.setOnClickListener(this);
        viewFiles.setOnClickListener(this);
        viewHistory.setOnClickListener(this);
        mPdfToImages.setOnClickListener(this);

        mAdapter =  new RecentListAdapter(this);
        recentList.setAdapter(mAdapter);
        return rootview;
    }

    @Override public void onViewCreated(
        @NonNull final View view, @Nullable final Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        try {
            LinkedHashMap<String, Map<String, String>> mRecentList = RecentUtil.getInstance()
                    .getList(PreferenceManager.getDefaultSharedPreferences(mActivity));
            if (!mRecentList.isEmpty()) {
                recentLabel.setVisibility(View.VISIBLE);
                recentLayout.setVisibility(View.VISIBLE);
                List<String> featureItemIds = new ArrayList<>(mRecentList.keySet());
                List<Map<String, String>> featureItemList = new ArrayList<>(mRecentList.values());
                mAdapter.updateList(featureItemIds, featureItemList);
                mAdapter.notifyDataSetChanged();
            } else {
                recentLabel.setVisibility(View.GONE);
                recentLayout.setVisibility(View.GONE);
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mActivity = (Activity) context;
    }

    @Override
    public void onClick(View v) {

        Fragment fragment = null;
        FragmentManager fragmentManager = getFragmentManager();
        Bundle bundle = new Bundle();

        highlightNavigationDrawerItem(mFragmentPositionMap.get(v.getId()).getNavigationItemId());
        setTitleFragment(mFragmentPositionMap.get(v.getId()).getTitleString());

        Map<String, String> feature = new HashMap<>();
        feature.put(
                String.valueOf(mFragmentPositionMap.get(v.getId()).getTitleString()),
                String.valueOf(mFragmentPositionMap.get(v.getId()).getmDrawableId()));

        try {
            RecentUtil.getInstance().addFeatureInRecentList(PreferenceManager
                    .getDefaultSharedPreferences(mActivity), v.getId(), feature);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        switch (v.getId()) {
            case R.id.images_to_pdf:
                fragment = new ImageToPdfFragment();
                break;
            case R.id.text_to_pdf:
                fragment = new TextToPdfFragment();
                break;
            case R.id.view_files:
                fragment = new ViewFilesFragment();
                break;
            case R.id.view_history:
                fragment = new HistoryFragment();
                break;
            case R.id.pdf_to_images:
                fragment = new PdfToImageFragment();
                bundle.putString(BUNDLE_DATA, PDF_TO_IMAGES);
                fragment.setArguments(bundle);
                break;
        }

        try {
            if (fragment != null && fragmentManager != null)
                fragmentManager.beginTransaction().replace(R.id.content, fragment).commit();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    /**
     * Highligh navigation drawer item
     * @param id - item id to be hjighlighted
     */
    private void highlightNavigationDrawerItem(int id) {
        if (mActivity instanceof MainActivity)
            ((MainActivity) mActivity).setNavigationViewSelection(id);
    }

    /**
     * Sets the title on action bar
     * @param title - title of string to be shown
     */
    private void setTitleFragment(int title) {
        if (title != 0)
            mActivity.setTitle(title);
    }
}
