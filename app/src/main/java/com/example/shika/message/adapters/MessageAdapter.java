package com.example.shika.message.adapters;

import java.util.Date;
import java.util.List;

import android.content.Context;
import android.text.format.DateUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.shika.message.R;
import com.example.shika.message.utils.UserConstant;
import com.parse.ParseObject;

public class MessageAdapter extends ArrayAdapter<ParseObject> {
	
	protected Context mContext;
	protected List<ParseObject> mMessages;
	
	public MessageAdapter(Context context, List<ParseObject> messages) {
		super(context, R.layout.message_item, messages);
		mContext = context;
		mMessages = messages;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder holder;
		
		if (convertView == null) {
			convertView = LayoutInflater.from(mContext).inflate(R.layout.message_item, null);
			holder = new ViewHolder();
			holder.iconImageView = (ImageView)convertView.findViewById(R.id.messageIcon);
			holder.nameLabel = (TextView)convertView.findViewById(R.id.senderLabel);
            holder.timeLabel = (TextView)convertView.findViewById(R.id.timeLabel);
            convertView.setTag(holder);
		}
		else {
			holder = (ViewHolder)convertView.getTag();
		}
		
		ParseObject message = mMessages.get(position);
        Date createAt=message.getCreatedAt();
        long now=new Date().getTime();
        String convertDate= DateUtils.getRelativeTimeSpanString(createAt.getTime(),now,DateUtils.SECOND_IN_MILLIS).toString();
        holder.timeLabel.setText(convertDate);

		
		if (message.getString(UserConstant.KEY_FILE_TYPE).equals(UserConstant.TYPE_IMAGE)) {
			holder.iconImageView.setImageResource(R.mipmap.ic_action_picture);
		}
		else {
			holder.iconImageView.setImageResource(R.drawable.ic_action_play_over_video);
		}
		holder.nameLabel.setText(message.getString(UserConstant.KEY_SENDER_NAME));
		
		return convertView;
	}
	
	private static class ViewHolder {
		ImageView iconImageView;
		TextView nameLabel;
        TextView timeLabel;
	}
}






