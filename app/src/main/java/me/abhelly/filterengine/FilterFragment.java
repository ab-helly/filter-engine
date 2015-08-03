package me.abhelly.filterengine;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
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
import me.abhelly.filterengine.model.Item;
import me.abhelly.filterengine.util.DividerItemDecoration;

/**
 * A placeholder fragment containing a simple view.
 */
public class FilterFragment extends Fragment {

    private final static int ITEM_COUNT = 100;

    @Bind(R.id.query_text_wrapper)
    TextInputLayout mTextInputLayout;

    @Bind(R.id.query_text)
    EditText mEditText;

    @Bind(R.id.result_view)
    RecyclerView mRecyclerView;

    private FilterAdapter mAdapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_filter, container, false);
        ButterKnife.bind(this, view);

        DataGenerator dg = DataGenerator.getInstance();
        ArrayList<Item> data = dg.generateDataList(ITEM_COUNT);
        mAdapter = new FilterAdapter(data, getActivity());
        mRecyclerView.setAdapter(mAdapter);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        Drawable divider = ContextCompat.getDrawable(getActivity(), R.drawable.divider);
        mRecyclerView.addItemDecoration(new DividerItemDecoration(divider));

        return view;
    }

    public static class FilterAdapter extends RecyclerView.Adapter<ItemViewHolder> {

        private final ArrayList<Item> mItems;

        private final DateFormat mDateFormat;

        private final String[] mPriorityColors;

        public FilterAdapter(ArrayList<Item> items, Context context) {
            mItems = items;
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
