package tv.hillsbrad.com.tvprogramlist;

import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatDialog;
import android.support.v7.app.AppCompatDialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.CheckBox;
import android.widget.ExpandableListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.Map;

import tv.hillsbrad.com.yahoo.YahooTvConstant;

/**
 * Created by alex on 6/30/16.
 */
public class CustomChannelSettings extends AppCompatDialogFragment
        implements DialogInterface.OnClickListener {

    private LayoutInflater layoutInflater;

    private View mRootView;
    private ExpandableListView mChannelListView;

    private ArrayList<String> mChannelTypeList;
    private LinkedHashSet<String> mCheckedSet;

    @Override
    public void onCreate(Bundle savedInstance) {
        super.onCreate(savedInstance);
        layoutInflater = (LayoutInflater) CustomChannelSettings.this.getContext()
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        mCheckedSet = new LinkedHashSet<>();
    }

    @Override
    public AppCompatDialog onCreateDialog(Bundle savedInstanceState) {
        mRootView = LayoutInflater.from(getContext())
                .inflate(R.layout.custom_channels_layout, null);
        mChannelListView = (ExpandableListView) mRootView.findViewById(R.id.custom_channel_listview);
        initView();

        return new AlertDialog.Builder(getContext())
                .setView(mRootView)
                .setNegativeButton(R.string.cancel, this)
                .setPositiveButton(R.string.okay, this)
                .create();
    }

    private void initView() {
        mChannelTypeList = new ArrayList<>();
        HashMap<String, ArrayList<String>> channelsMap = new HashMap<>();

        for (int i = 0; i < YahooTvConstant.sChannelMapping.length; i++) {
            mChannelTypeList.add(getString(YahooTvConstant.CHANNEL_TYPE[i]));

            ArrayList<String> channels = new ArrayList<>();
            for (int j = 0; j < YahooTvConstant.sChannelMapping[i].length; j++) {
                channels.add(getString(YahooTvConstant.sChannelMapping[i][j]));
            }
            channelsMap.put(mChannelTypeList.get(i), channels);
        }


        CustomExpandableListAdapter adapter = new CustomExpandableListAdapter(mChannelTypeList, channelsMap);
        mChannelListView.setAdapter(adapter);
    }

    @Override
    public void onClick(DialogInterface dialog, int which) {

    }

    static class ViewHolder {
        public TextView title;
        public CheckBox checkBox;
        public int groupIndex;
    }

    class CustomExpandableListAdapter extends BaseExpandableListAdapter {

        private ArrayList<String> mChannelList;
        private Map<String, ArrayList<String>> mChannelsMap;

        public CustomExpandableListAdapter(ArrayList<String> channelList, Map<String,
                ArrayList<String>> channelsMap) {
            mChannelList = channelList;
            mChannelsMap = channelsMap;
        }

        @Override
        public int getGroupCount() {
            return mChannelList.size();
        }

        @Override
        public int getChildrenCount(int groupPosition) {
            return mChannelsMap.get(mChannelList.get(groupPosition)).size();
        }

        @Override
        public Object getGroup(int groupPosition) {
            return mChannelList.get(groupPosition);
        }

        @Override
        public Object getChild(int groupPosition, int childPosition) {
            return mChannelsMap.get(mChannelList.get(groupPosition)).get(childPosition);
        }

        @Override
        public long getGroupId(int groupPosition) {
            return groupPosition;
        }

        @Override
        public long getChildId(int groupPosition, int childPosition) {
            return childPosition;
        }

        @Override
        public boolean hasStableIds() {
            return false;
        }

        @Override
        public View getGroupView(final int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {
            final ViewHolder viewHolder;
            if (convertView == null) {
                convertView = layoutInflater.inflate(R.layout.custom_listview_item, null);
                viewHolder = new ViewHolder();
                viewHolder.title = (TextView) convertView.findViewById(R.id.custom_title);
                viewHolder.checkBox = (CheckBox) convertView.findViewById(R.id.custom_selected);
                viewHolder.groupIndex = groupPosition;
                convertView.setTag(viewHolder);

                final View forInnerUsed = convertView;
                viewHolder.checkBox.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (viewHolder.checkBox.isChecked()) {
                            mCheckedSet.add(viewHolder.title.getText().toString());
                        } else {
                            mCheckedSet.remove(viewHolder.title.getText().toString());
                        }

                        int typeIndex = mChannelTypeList.indexOf(viewHolder.title.getText().toString());
                        if (typeIndex != -1 && typeIndex < YahooTvConstant.sChannelMapping.length) {
                            for (int i = 0; i < YahooTvConstant.sChannelMapping[typeIndex].length; i++) {
                                if (viewHolder.checkBox.isChecked()) {
                                    mCheckedSet.add(getString(YahooTvConstant.sChannelMapping[typeIndex][i]));
                                } else {
                                    mCheckedSet.remove(getString(YahooTvConstant.sChannelMapping[typeIndex][i]));
                                }
                            }
                        }

                        //FIXME  position not correct sometimes
                        int position = mChannelListView.getPositionForView(forInnerUsed);
                        if (mChannelListView.isGroupExpanded(position)) {
                            mChannelListView.collapseGroup(position);
                            mChannelListView.expandGroup(position);
                        }
                    }
                });
            } else {
                viewHolder = (ViewHolder) convertView.getTag();
            }

            String channelType = mChannelList.get(groupPosition);
            viewHolder.title.setText(channelType);
            viewHolder.title.setTextSize(21);
            viewHolder.title.setTypeface(null, Typeface.BOLD);
            viewHolder.checkBox.setChecked(mCheckedSet.contains(viewHolder.title.getText().toString()));
            return convertView;
        }

        @Override
        public View getChildView(int groupPosition, final int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {
            final ViewHolder viewHolder;
            if (convertView == null) {
                convertView = layoutInflater.inflate(R.layout.custom_listview_item, null);
                viewHolder = new ViewHolder();
                viewHolder.title = (TextView) convertView.findViewById(R.id.custom_title);
                viewHolder.checkBox = (CheckBox) convertView.findViewById(R.id.custom_selected);
                convertView.setTag(viewHolder);

                viewHolder.checkBox.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (viewHolder.checkBox.isChecked()) {
                            mCheckedSet.add(viewHolder.title.getText().toString());
                        } else {
                            mCheckedSet.remove(viewHolder.title.getText().toString());
                        }
                    }
                });
            } else {
                viewHolder = (ViewHolder) convertView.getTag();
            }

            String channel = mChannelsMap.get(mChannelList.get(groupPosition)).get(childPosition);
            viewHolder.title.setText(channel);
            viewHolder.title.setTextSize(17);
            viewHolder.checkBox.setChecked(mCheckedSet.contains(viewHolder.title.getText().toString()));
            return convertView;
        }

        @Override
        public boolean isChildSelectable(int groupPosition, int childPosition) {
            return true;
        }
    }
}
