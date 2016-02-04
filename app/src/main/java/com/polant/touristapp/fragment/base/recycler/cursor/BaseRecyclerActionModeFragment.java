package com.polant.touristapp.fragment.base.recycler.cursor;

import android.content.Context;
import android.support.v7.app.AppCompatActivity;
import android.view.ActionMode;

import com.polant.touristapp.R;

/**
 * Базовый фрагмент для всех фрагментов, содержащих RecyclerView с множественным выбором
 * и работающими с БД через Cursor-ы, а также с реализацией ActionMode.
 */
public abstract class BaseRecyclerActionModeFragment extends BaseRecyclerMultiChoiceFragment {

    protected ActionMode mActionMode;

    protected ActionMode.Callback mActionModeCallback;


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (!(context instanceof AppCompatActivity)) {
            throw new IllegalArgumentException("ACTIVITY MUST will be a child of AppCompatActivity");
        }
    }

    protected abstract void startActionMode();

    protected void refreshActionMode(int selectedCount) {
        if (mActionMode != null) {
            if (selectedCount == 0) {
                mActionMode.finish();
            } else {
                mActionMode.setTitle(String.valueOf(getString(R.string.action_mode_overlay_text) + " " + selectedCount));
                mActionMode.invalidate();
            }
        }
    }

    @Override
    protected void toggleSelection(int position) {
        super.toggleSelection(position);
        int count = mAdapter.getSelectedItemCount();
        refreshActionMode(count);
    }

}
