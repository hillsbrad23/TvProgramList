package tv.hillsbrad.com.tvprogramlist;

import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatDialog;
import android.support.v7.app.AppCompatDialogFragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ExpandableListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

import tv.hillsbrad.com.Utils;
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
    private HashSet<String> mCurrentSelectedChannel;
    /**
     * The variable is related to whether parse program data from web.
     * If value > 0 yes, otherwise no.
     */
    private int[] mSelectedCountByChannelType;

    private boolean[] mIsExpandWhenInitView;

    private ModelController mModelController;

    @Override
    public void onCreate(Bundle savedInstance) {
        super.onCreate(savedInstance);
        layoutInflater = (LayoutInflater) CustomChannelSettings.this.getContext()
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        mModelController = ModelController.getInstance();
        mCurrentSelectedChannel = new HashSet<>();
        mCurrentSelectedChannel.addAll(mModelController.getCustomSelectedChannels());
        mSelectedCountByChannelType = new int[YahooTvConstant.sChannelMapping.length];
        mIsExpandWhenInitView = new boolean[YahooTvConstant.sChannelMapping.length];
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
                String channelTitle = getString(YahooTvConstant.sChannelMapping[i][j]);
                channels.add(channelTitle);

                // init mSelectedCountByChannelType data
                if (mCurrentSelectedChannel.contains(channelTitle)) {
                    mSelectedCountByChannelType[i]++;
                }
            }
            channelsMap.put(mChannelTypeList.get(i), channels);
        }

        CustomExpandableListAdapter adapter = new CustomExpandableListAdapter(mChannelTypeList, channelsMap);
        mChannelListView.setAdapter(adapter);
    }

    @Override
    public void onClick(DialogInterface dialog, int which) {
        switch (which) {
            case DialogInterface.BUTTON_POSITIVE:
                mModelController.setCustomSelectedChannels(mCurrentSelectedChannel, mSelectedCountByChannelType);
                Utils.saveCustomSelectedChannels(getContext(), mCurrentSelectedChannel);
                break;
        }
    }

    static class ViewHolder {
        public TextView title;
        public CheckBox checkBox;
        public int groupIndex;
        public int childIndex;
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
            return groupPosition * 100 + childPosition;
        }

        @Override
        public boolean hasStableIds() {
            return true;
        }

        @Override
        public View getGroupView(final int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {
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
                        // set selected child count
                        int selectedChildCount = 0;
                        if (viewHolder.checkBox.isChecked()) {
                            selectedChildCount = YahooTvConstant.sChannelMapping[viewHolder.groupIndex].length;
                        }
                        if (viewHolder.groupIndex >= 0 && viewHolder.groupIndex < mSelectedCountByChannelType.length) {
                            mSelectedCountByChannelType[viewHolder.groupIndex] = selectedChildCount;
                        }

                        // add/remove channel title
                        int typeIndex = mChannelTypeList.indexOf(viewHolder.title.getText().toString());
                        if (typeIndex != -1 && typeIndex < YahooTvConstant.sChannelMapping.length) {
                            for (int i = 0; i < YahooTvConstant.sChannelMapping[typeIndex].length; i++) {
                                if (viewHolder.checkBox.isChecked()) {
                                    mCurrentSelectedChannel.add(getString(YahooTvConstant.sChannelMapping[typeIndex][i]));
                                } else {
                                    mCurrentSelectedChannel.remove(getString(YahooTvConstant.sChannelMapping[typeIndex][i]));
                                }
                            }
                        }

                        int position = viewHolder.groupIndex;
                        if (mChannelListView.isGroupExpanded(position)) {
                            mChannelListView.collapseGroup(position);
                            mChannelListView.expandGroup(position);
                        }
                    }
                });
            } else {
                viewHolder = (ViewHolder) convertView.getTag();
            }
            viewHolder.groupIndex = groupPosition;

            String channelType = mChannelList.get(groupPosition);
            viewHolder.title.setText(channelType);
            viewHolder.title.setTextSize(21);
            viewHolder.title.setTypeface(null, Typeface.BOLD);

            if (mSelectedCountByChannelType[viewHolder.groupIndex] ==
                    YahooTvConstant.sChannelMapping[viewHolder.groupIndex].length) {
                viewHolder.checkBox.setChecked(true);
            } else {
                viewHolder.checkBox.setChecked(false);
            }

            // expand if there is any selected channel
            if (!mIsExpandWhenInitView[groupPosition]) {
                mIsExpandWhenInitView[groupPosition] = true;
                if (mSelectedCountByChannelType[groupPosition] != 0) {
                    mChannelListView.expandGroup(groupPosition);
                }
            }

            return convertView;
        }

        @Override
        public View getChildView(final int groupPosition, final int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {
            final ViewHolder viewHolder;
            if (convertView == null) {
                convertView = layoutInflater.inflate(R.layout.custom_listview_item, null);
                viewHolder = new ViewHolder();
                viewHolder.title = (TextView) convertView.findViewById(R.id.custom_title);
                viewHolder.checkBox = (CheckBox) convertView.findViewById(R.id.custom_selected);
                convertView.setTag(viewHolder);

                viewHolder.checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                        if (isChecked) {
                            if (!mCurrentSelectedChannel.contains(viewHolder.title.getText().toString())) {
                                mCurrentSelectedChannel.add(viewHolder.title.getText().toString());
                                if (viewHolder.groupIndex >= 0 && viewHolder.groupIndex < mSelectedCountByChannelType.length) {
                                    mSelectedCountByChannelType[viewHolder.groupIndex]++;
                                }
                            }

                            if (mSelectedCountByChannelType[viewHolder.groupIndex] ==
                                    YahooTvConstant.sChannelMapping[viewHolder.groupIndex].length) {
                                mChannelListView.collapseGroup(viewHolder.groupIndex);
                                mChannelListView.expandGroup(viewHolder.groupIndex);
                            }
                        } else {
                            if (mCurrentSelectedChannel.contains(viewHolder.title.getText().toString())) {
                                mCurrentSelectedChannel.remove(viewHolder.title.getText().toString());
                                if (viewHolder.groupIndex >= 0 && viewHolder.groupIndex < mSelectedCountByChannelType.length) {
                                    mSelectedCountByChannelType[viewHolder.groupIndex]--;
                                }
                            }

                            if ((mSelectedCountByChannelType[viewHolder.groupIndex]+1) ==
                                    YahooTvConstant.sChannelMapping[viewHolder.groupIndex].length) {
                                mChannelListView.collapseGroup(viewHolder.groupIndex);
                                mChannelListView.expandGroup(viewHolder.groupIndex);
                            }
                        }
                    }
                });
            } else {
                viewHolder = (ViewHolder) convertView.getTag();
            }

            viewHolder.groupIndex = groupPosition;
            viewHolder.childIndex = childPosition;

            String channel = mChannelsMap.get(mChannelList.get(groupPosition)).get(childPosition);
            viewHolder.title.setText(channel);
            viewHolder.title.setTextSize(17);

            if (viewHolder.checkBox.isChecked() != mCurrentSelectedChannel.contains(viewHolder.title.getText().toString())) {
                viewHolder.checkBox.setChecked(mCurrentSelectedChannel.contains(viewHolder.title.getText().toString()));
            }

            return convertView;
        }

        @Override
        public boolean isChildSelectable(int groupPosition, int childPosition) {
            return true;
        }
    }

    @Override
    public void onPause() {
        super.onPause();
    }
}
