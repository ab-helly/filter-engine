package me.abhelly.filterengine;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;

import butterknife.Bind;
import butterknife.ButterKnife;
import me.abhelly.filterengine.dummy.DataGenerator;
import me.abhelly.filterengine.filter.FilterEngine;
import me.abhelly.filterengine.filter.FilterResult;
import me.abhelly.filterengine.model.Item;
import me.abhelly.filterengine.util.DividerItemDecoration;

/**
 * A placeholder fragment containing a simple view.
 */
public class FilterFragment extends Fragment implements FilterEngine.FilterListener, TextWatcher {

    // generated data list count
    private final static int ITEM_COUNT = 1000;

    @Bind(R.id.query_text)
    EditText mEditText;

    @Bind(R.id.result_view)
    RecyclerView mRecyclerView;

    private FilterAdapter mAdapter;

    private FilterEngine mFilterEngine;

    // non-filtered data
    private ArrayList<Item> mData;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mFilterEngine = FilterEngine.getInstance();

        DataGenerator dg = DataGenerator.getInstance();
        mData = dg.generateDataList(ITEM_COUNT);
        mAdapter = new FilterAdapter(mData, getActivity());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_filter, container, false);
        ButterKnife.bind(this, view);

        mRecyclerView.setAdapter(mAdapter);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        Drawable divider = ContextCompat.getDrawable(getActivity(), R.drawable.divider);
        mRecyclerView.addItemDecoration(new DividerItemDecoration(divider));

        mEditText.addTextChangedListener(this);

        return view;
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {
    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {
    }

    @Override
    public void afterTextChanged(Editable s) {
        String query = mEditText.getText().toString();
        if (query.length() == 0) {
            resetData();
        } else {
            mFilterEngine.filter(query, mData, this, getActivity());
        }
    }

    @Override
    public void onFilterComplete(FilterResult result) {
        // change data set only if query is valid, otherwise just leave previous valid query results
        if (result != null && result.getItems() != null) {
            ActionBar actionBar = ((AppCompatActivity) getActivity()).getSupportActionBar();
            if (actionBar != null) {
                actionBar.setTitle(result.getSection().getTitle());
            }
            mAdapter = new FilterAdapter(result.getItems(), getActivity());
            mRecyclerView.swapAdapter(mAdapter, true);
        }
    }

    private void resetData() {
        ActionBar actionBar = ((AppCompatActivity) getActivity()).getSupportActionBar();
        if (actionBar != null) {
            actionBar.setTitle(getString(R.string.app_name));
        }
        mAdapter = new FilterAdapter(mData, getActivity());
        mRecyclerView.swapAdapter(mAdapter, false);
    }

    public static class FilterAdapter extends RecyclerView.Adapter<ItemViewHolder> {

        private final ArrayList<Item> mItems;

        private final DateFormat mDateFormat;

        private final String[] mPriorityColors;

        public FilterAdapter(ArrayList<Item> items, Context context) {
            mItems = (items != null) ? items : new ArrayList<Item>();
            mDateFormat = SimpleDateFormat.getDateInstance();
            mPriorityColors = context.getResources().getStringArray(R.array.priority_colors);
        }

        @Override
        public ItemViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View itemView = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.task_item, parent, false);
            return new ItemViewHolder(itemView);
        }

        @Override
        public void onBindViewHolder(ItemViewHolder holder, int position) {
            Item item = mItems.get(position);
            int color = Color.parseColor(mPriorityColors[item.getPriority() - 1]);
            holder.priority.setBackgroundColor(color);
            holder.content.setText(item.getContent());
            String dateString = mDateFormat.format(item.getDueDate());
            holder.dueDate.setText(dateString);
        }

        @Override
        public int getItemCount() {
            return mItems.size();
        }

        @Override
        public long getItemId(int position) {
            return mItems.get(position).getId();
        }
    }

    public static class ItemViewHolder extends RecyclerView.ViewHolder {

        @Bind(R.id.item_priority)
        View priority;

        @Bind(R.id.item_content)
        TextView content;

        @Bind(R.id.item_due_date)
        TextView dueDate;

        public ItemViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }
}
