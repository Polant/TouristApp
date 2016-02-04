package com.polant.touristapp.fragment;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.polant.touristapp.R;
import com.polant.touristapp.adapter.recycler.SearchMultiTypesAdapter;
import com.polant.touristapp.data.Database;
import com.polant.touristapp.fragment.base.recycler.BaseRecyclerFragment;
import com.polant.touristapp.interfaces.ISearchableFragment;
import com.polant.touristapp.model.database.Mark;
import com.polant.touristapp.model.database.UserMedia;
import com.polant.touristapp.model.search.SearchComplexItem;

import java.util.ArrayList;
import java.util.List;

/**
 * Фрагмент, содержащий RecyclerView, который служит для вывода результата поиска.
 * Текущий Адаптер позволяет выводить различные типы представлений одновременно в RecyclerView.
 */
public class SearchFragment extends BaseRecyclerFragment implements ISearchableFragment {

    private static final int LAYOUT = R.layout.fragment_search;

    protected View view;

    protected SearchMultiTypesAdapter mAdapter;

    protected Handler handler = new Handler();

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (!(context instanceof MarksFragment.MarksListener) || !(context instanceof PhotosFragment.PhotosListener) ) {
            throw new IllegalArgumentException("ACTIVITY MUST IMPLEMENT " +
                    "MarksFragment.MarksListener and PhotosFragment.PhotosListener");
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(LAYOUT, container, false);
        return view;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        initAdapter();
        initRecycledView();

        //Сначала вывожу все данные.
        search("");
    }

    protected void initAdapter(){
        mAdapter = new SearchMultiTypesAdapter(mActivity, null, this);
    }

    protected void initRecycledView(){
        RecyclerView recyclerView = (RecyclerView) view.findViewById(R.id.recyclerViewSearch);
        recyclerView.setLayoutManager(new LinearLayoutManager(mActivity));
        recyclerView.setAdapter(mAdapter);
    }

    //---------------------------RecyclerClickListener------------------------//

    @Override
    public void onItemClicked(int position) {
        SearchComplexItem clicked = mAdapter.getItem(position);
        if (clicked.isMark()) {
            ((MarksFragment.MarksListener) mActivity).showPhotosByMark(clicked.getMark());
        }
        else if(clicked.isUserMedia()){
            ((PhotosFragment.PhotosListener) mActivity).showSelectedPhoto(clicked.getMedia());
        }
    }

    @Override
    public boolean onItemLongClicked(int position) {
        return false;
    }

    //-------------------------------------------------------------------------//

    @Override
    public void notifyRecyclerView() {
        mAdapter.notifyDataSetChanged();
    }

    //------------------------------ISearchableFragment------------------------//

    @Override
    public void search(final String filter) {
        Thread t = new Thread(new Runnable() {
            @Override
            public void run() {
                Database db = getDatabase();
                List<Mark> marks = db.searchMarks(mUserId, filter);
                List<UserMedia> medias = db.searchUserMedia(mUserId, filter);

                List<SearchComplexItem> result = merge(marks, medias);
                mAdapter.changeItems(result);

                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        notifyRecyclerView();
                    }
                });
            }
        });
        t.start();
    }

    private List<SearchComplexItem> merge(List<Mark> marks, List<UserMedia> medias) {
        int resultSize = marks.size() + medias.size();
        List<SearchComplexItem> result = new ArrayList<>(resultSize);

        for (int i = 0; i < resultSize; i++) {
            if (i < marks.size()){
                result.add(new SearchComplexItem(i, marks.get(i)));
            }else {
                result.add(new SearchComplexItem(i, medias.get(i - marks.size())));
            }
        }
        return result;
    }
}
