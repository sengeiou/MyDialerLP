package org.linphone;
/*
HistoryListFragment.java
Copyright (C) 2015  Belledonne Communications, Grenoble, France

This program is free software; you can redistribute it and/or
modify it under the terms of the GNU General Public License
as published by the Free Software Foundation; either version 2
of the License, or (at your option) any later version.

This program is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU General Public License for more details.

You should have received a copy of the GNU General Public License
along with this program; if not, write to the Free Software
Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA.
*/
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;

import org.linphone.core.CallDirection;
import org.linphone.core.LinphoneAddress;
import org.linphone.core.LinphoneCallLog;
import org.linphone.core.LinphoneCallLog.CallStatus;
import org.linphone.core.LinphoneCoreException;
import org.linphone.core.LinphoneCoreFactory;
import org.linphone.wzb.Wlog;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.app.FragmentManager;

/**
 * @author Sylvain Berfini
 */
public class HistoryListFragment extends Fragment implements OnClickListener, OnItemClickListener, ContactsUpdatedListener {
	private ListView historyList;
	private LayoutInflater mInflater;
	private TextView noCallHistory, noMissedCallHistory;
	private ImageView missedCalls, allCalls, edit, selectAll, deselectAll, delete, cancel;
	private View allCallsSelected, missedCallsSelected;
	private LinearLayout editList, topBar;
	private boolean onlyDisplayMissedCalls, isEditMode;
	private List<LinphoneCallLog> mLogs;

	//add by wzb
	FragmentManager fragmentManger;
	View view;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
							 Bundle savedInstanceState) {
		mInflater = inflater;
		view = inflater.inflate(R.layout.history, container, false);

		noCallHistory = (TextView) view.findViewById(R.id.no_call_history);
		noMissedCallHistory = (TextView) view.findViewById(R.id.no_missed_call_history);

		historyList = (ListView) view.findViewById(R.id.history_list);
		historyList.setOnItemClickListener(this);

		delete = (ImageView) view.findViewById(R.id.delete);
		delete.setOnClickListener(this);

		editList = (LinearLayout) view.findViewById(R.id.edit_list);
		topBar = (LinearLayout) view.findViewById(R.id.top_bar);

		cancel = (ImageView) view.findViewById(R.id.cancel);
		cancel.setOnClickListener(this);

		allCalls = (ImageView) view.findViewById(R.id.all_calls);
		allCalls.setOnClickListener(this);

		allCallsSelected = view.findViewById(R.id.all_calls_select);

		missedCalls = (ImageView) view.findViewById(R.id.missed_calls);
		missedCalls.setOnClickListener(this);

		missedCallsSelected = view.findViewById(R.id.missed_calls_select);

		selectAll = (ImageView) view.findViewById(R.id.select_all);
		selectAll.setOnClickListener(this);

		deselectAll = (ImageView) view.findViewById(R.id.deselect_all);
		deselectAll.setOnClickListener(this);

		allCalls.setEnabled(false);
		onlyDisplayMissedCalls = false;

		edit = (ImageView) view.findViewById(R.id.edit);
		edit.setOnClickListener(this);

		//add by wzb 20180205
		initDetailView();
		notShowDetail(true);
		//end

		return view;
	}

	public void refresh() {
		mLogs = Arrays.asList(LinphoneManager.getLc().getCallLogs());
	}

	private void selectAllList(boolean isSelectAll){
		int size = historyList.getAdapter().getCount();
		for(int i=0; i<size; i++) {
			historyList.setItemChecked(i,isSelectAll);
		}
	}

	public void displayFirstLog(){
		if (mLogs != null && mLogs.size() > 0) {
			LinphoneCallLog log = mLogs.get(0);
			if (log.getDirection() == CallDirection.Incoming) {
				LinphoneActivity.instance().displayHistoryDetail(mLogs.get(0).getFrom().toString(), mLogs.get(0));
			} else {
				LinphoneActivity.instance().displayHistoryDetail(mLogs.get(0).getTo().toString(), mLogs.get(0));
			}
		} else {
			LinphoneActivity.instance().displayEmptyFragment();
		}
	}

	private void removeCallLogs(){
		int size = historyList.getAdapter().getCount();
		for(int i=0; i<size; i++) {
			if(historyList.isItemChecked(i)){
				LinphoneCallLog log = mLogs.get(i);
				LinphoneManager.getLc().removeCallLog(log);
			}
		}
	}

	public int getNbItemsChecked(){
		int size = historyList.getAdapter().getCount();
		int nb = 0;
		for(int i=0; i<size; i++) {
			if(historyList.isItemChecked(i)) {
				nb ++;
			}
		}
		return nb;
	}

	public void enabledDeleteButton(Boolean enabled){
		if(enabled){
			delete.setEnabled(true);
		} else {
			if (getNbItemsChecked() == 0){
				delete.setEnabled(false);
			}
		}
	}

	private void removeNotMissedCallsFromLogs() {
		if (onlyDisplayMissedCalls) {
			List<LinphoneCallLog> missedCalls = new ArrayList<LinphoneCallLog>();
			for (LinphoneCallLog log : mLogs) {
				if (log.getStatus() == CallStatus.Missed) {
					missedCalls.add(log);
				}
			}
			mLogs = missedCalls;
		}
	}

	private boolean hideHistoryListAndDisplayMessageIfEmpty() {
		removeNotMissedCallsFromLogs();
		if (mLogs.isEmpty()) {
			if (onlyDisplayMissedCalls) {
				noMissedCallHistory.setVisibility(View.VISIBLE);
			} else {
				noCallHistory.setVisibility(View.VISIBLE);
			}
			historyList.setVisibility(View.GONE);
			edit.setEnabled(false);
			return true;
		} else {
			noCallHistory.setVisibility(View.GONE);
			noMissedCallHistory.setVisibility(View.GONE);
			historyList.setVisibility(View.VISIBLE);
			edit.setEnabled(true);
			return false;
		}
	}

	@Override
	public void onResume() {
		super.onResume();
		ContactsManager.addContactsListener(this);


		//del by wzb
		/*
		if (LinphoneActivity.isInstanciated()) {
			LinphoneActivity.instance().selectMenu(FragmentsAvailable.HISTORY_LIST);
			LinphoneActivity.instance().hideTabBar(false);
			LinphoneActivity.instance().displayMissedCalls(0);
		}*/
		//end

		mLogs = Arrays.asList(LinphoneManager.getLc().getCallLogs());
		if (!hideHistoryListAndDisplayMessageIfEmpty()) {
			historyList.setChoiceMode(AbsListView.CHOICE_MODE_MULTIPLE);
			historyList.setAdapter(new CallHistoryAdapter(getActivity().getApplicationContext()));
		}
		//add by wzb 20180205
		notShowDetail(true);
		//end
	}
	
	@Override
	public void onPause() {
		ContactsManager.removeContactsListener(this);
		super.onPause();
	}

	@Override
	public void onContactsUpdated() {
		historyList.setAdapter(new CallHistoryAdapter(getActivity().getApplicationContext()));
	}

	@Override
	public void onClick(View v) {
		int id = v.getId();

		if (id == R.id.select_all) {
			deselectAll.setVisibility(View.VISIBLE);
			selectAll.setVisibility(View.GONE);
			enabledDeleteButton(true);
			selectAllList(true);
			return;
		}
		if (id == R.id.deselect_all) {
			deselectAll.setVisibility(View.GONE);
			selectAll.setVisibility(View.VISIBLE);
			enabledDeleteButton(false);
			selectAllList(false);
			return;
		}

		if (id == R.id.cancel) {
			quitEditMode();
			return;
		}

		if (id == R.id.delete) {
			if(historyList.getCheckedItemCount() == 0) {
				quitEditMode();
				return;
			}

			//final Dialog dialog = LinphoneActivity.instance().displayDialog(getString(R.string.delete_text));
			final Dialog dialog = displayDialog(getActivity(),getString(R.string.delete_text));//modify by wzb 20180205
			Button delete = (Button) dialog.findViewById(R.id.delete_button);
			Button cancel = (Button) dialog.findViewById(R.id.cancel);

			delete.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View view) {
					removeCallLogs();
					dialog.dismiss();
					quitEditMode();
				}
			});

			cancel.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View view) {
					dialog.dismiss();
					quitEditMode();
				}
			});
			dialog.show();
			return;
		}

		if (id == R.id.all_calls) {
			allCalls.setEnabled(false);
			allCallsSelected.setVisibility(View.VISIBLE);
			missedCallsSelected.setVisibility(View.INVISIBLE);
			missedCalls.setEnabled(true);
			onlyDisplayMissedCalls = false;
			refresh();
		}
		if (id == R.id.missed_calls) {
			allCalls.setEnabled(true);
			allCallsSelected.setVisibility(View.INVISIBLE);
			missedCallsSelected.setVisibility(View.VISIBLE);
			missedCalls.setEnabled(false);
			onlyDisplayMissedCalls = true;
		}

		if (id == R.id.edit) {
			topBar.setVisibility(View.GONE);
			editList.setVisibility(View.VISIBLE);
			enabledDeleteButton(false);
			isEditMode = true;
		}

		//add by wzb 20180205
		if(id==R.id.call){

			//goto call
			Intent intent=new Intent("com.custom.lp_GOTO_CALL");
			intent.putExtra("number",sipUri);
			intent.putExtra("name",displayName);
			getActivity().sendBroadcast(intent);

		}else if(id==R.id.chat){
			//goto char

		}else if (id == R.id.add_contact) {
			String uri = sipUri;
			try {
				LinphoneAddress addr = LinphoneCoreFactory.instance().createLinphoneAddress(sipUri);
				uri = addr.asStringUriOnly();
			} catch (LinphoneCoreException e) {
				Wlog.e(e.toString());
			}
			//LinphoneActivity.instance().displayContactsForEdition(uri);
		} else if (id == R.id.goto_contact) {
			//LinphoneActivity.instance().displayContact(contact, false);
		}

		//end

		if (!hideHistoryListAndDisplayMessageIfEmpty()) {
			historyList.setChoiceMode(AbsListView.CHOICE_MODE_MULTIPLE);
			historyList.setAdapter(new CallHistoryAdapter(getActivity().getApplicationContext()));
		}

		if(isEditMode){
			deselectAll.setVisibility(View.GONE);
			selectAll.setVisibility(View.VISIBLE);
		}

	}

	@Override
	public void onItemClick(AdapterView<?> adapter, View view, int position, long id) {
		if (isEditMode) {
			LinphoneCallLog log = mLogs.get(position);
			LinphoneManager.getLc().removeCallLog(log);
			mLogs = Arrays.asList(LinphoneManager.getLc().getCallLogs());
		} else {

			//modify by wzb 20180205
			/*
			if (LinphoneActivity.isInstanciated()) {
				LinphoneCallLog log = mLogs.get(position);
				LinphoneAddress address;
				if (log.getDirection() == CallDirection.Incoming) {
					address = log.getFrom();
				} else {
					address = log.getTo();
				}
				LinphoneActivity.instance().setAddresGoToDialerAndCall(address.asStringUriOnly(), address.getDisplayName(), null);
			}*/
			LinphoneCallLog log = mLogs.get(position);
			LinphoneAddress address;
			if (log.getDirection() == CallDirection.Incoming) {
				address = log.getFrom();
			} else {
				address = log.getTo();
			}

			Intent intent=new Intent("com.custom.lp_GOTO_CALL");
			intent.putExtra("number",address.asStringUriOnly());
			intent.putExtra("name",address.getDisplayName());
			getActivity().sendBroadcast(intent);

			//end
		}
	}

	public void quitEditMode(){
		isEditMode = false;
		editList.setVisibility(View.GONE);
		topBar.setVisibility(View.VISIBLE);

		refresh();
		if (!hideHistoryListAndDisplayMessageIfEmpty()) {
			historyList.setChoiceMode(AbsListView.CHOICE_MODE_MULTIPLE);
			historyList.setAdapter(new CallHistoryAdapter(getActivity().getApplicationContext()));
		}
		if (getResources().getBoolean(R.bool.isTablet)) {
			//displayFirstLog(); //del by wzb 20180205
		}
	}

	class CallHistoryAdapter extends  BaseAdapter {
		private class ViewHolder {
			public TextView contact;
			public ImageView detail;
			public CheckBox select;
			public ImageView callDirection;
			public ImageView contactPicture;
			
			public ViewHolder(View view) {
				contact = (TextView) view.findViewById(R.id.sip_uri);
				detail = (ImageView) view.findViewById(R.id.detail);
				select = (CheckBox) view.findViewById(R.id.delete);
				callDirection = (ImageView) view.findViewById(R.id.icon);
				contactPicture = (ImageView) view.findViewById(R.id.contact_picture);
			}
		}
		
		CallHistoryAdapter(Context aContext) {
		
		}
		
		public int getCount() {
			return mLogs.size();
		}

		public Object getItem(int position) {
			return mLogs.get(position);
		}

		public long getItemId(int position) {
			return position;
		}

		@SuppressLint("SimpleDateFormat")
		private String timestampToHumanDate(Calendar cal) {
			SimpleDateFormat dateFormat;
			if (isToday(cal)) {
				return getString(R.string.today);
			} else if (isYesterday(cal)) {
				return getString(R.string.yesterday);
			} else {
				dateFormat = new SimpleDateFormat(getResources().getString(R.string.history_date_format));
			}

			return dateFormat.format(cal.getTime());
		}

		private boolean isSameDay(Calendar cal1, Calendar cal2) {
			if (cal1 == null || cal2 == null) {
				return false;
			}

			return (cal1.get(Calendar.ERA) == cal2.get(Calendar.ERA) &&
					cal1.get(Calendar.YEAR) == cal2.get(Calendar.YEAR) &&
					cal1.get(Calendar.DAY_OF_YEAR) == cal2.get(Calendar.DAY_OF_YEAR));
		}

		private boolean isToday(Calendar cal) {
			return isSameDay(cal, Calendar.getInstance());
		}

		private boolean isYesterday(Calendar cal) {
			Calendar yesterday = Calendar.getInstance();
			yesterday.roll(Calendar.DAY_OF_MONTH, -1);
			return isSameDay(cal, yesterday);
		}

		public View getView(final int position, View convertView, ViewGroup parent) {
			View view = null;
			ViewHolder holder = null;
			
			if (convertView != null) {
				view = convertView;
				holder = (ViewHolder) view.getTag();
			} else {
				view = mInflater.inflate(R.layout.history_cell, parent,false);
				holder = new ViewHolder(view);
				view.setTag(holder);
			}

			final LinphoneCallLog log = mLogs.get(position);
			long timestamp = log.getTimestamp();
			LinphoneAddress address;

			holder.contact.setSelected(true); // For automated horizontal scrolling of long texts

			LinearLayout separator = (LinearLayout) view.findViewById(R.id.separator);
			TextView separatorText = (TextView) view.findViewById(R.id.separator_text);
			Calendar logTime = Calendar.getInstance();
			logTime.setTimeInMillis(timestamp);
			separatorText.setText(timestampToHumanDate(logTime));

			if (position > 0) {
				LinphoneCallLog previousLog = mLogs.get(position-1);
				long previousTimestamp = previousLog.getTimestamp();
				Calendar previousLogTime = Calendar.getInstance();
				previousLogTime.setTimeInMillis(previousTimestamp);

				if (isSameDay(previousLogTime, logTime)) {
					separator.setVisibility(View.GONE);
				} else {
					separator.setVisibility(View.VISIBLE);
				}
			} else {
				separator.setVisibility(View.VISIBLE);
			}

			if (log.getDirection() == CallDirection.Incoming) {
				address = log.getFrom();
				if (log.getStatus() == CallStatus.Missed) {
					holder.callDirection.setImageResource(R.drawable.call_status_missed);
				} else {
					holder.callDirection.setImageResource(R.drawable.call_status_incoming);
				}
			} else {
				address = log.getTo();
				holder.callDirection.setImageResource(R.drawable.call_status_outgoing);
			}

			LinphoneContact c = ContactsManager.getInstance().findContactFromAddress(address);
			String displayName = null;
			final String sipUri = address.asString();
			if (c != null) {
				displayName = c.getFullName();
				if (c.hasPhoto()) {
					Bitmap photo = c.getPhoto();
					if (photo != null) {
						holder.contactPicture.setImageBitmap(photo);
					} else {
						LinphoneUtils.setImagePictureFromUri(getActivity(), holder.contactPicture, c.getPhotoUri(), c.getThumbnailUri());
					}
				} else {
					LinphoneUtils.setImagePictureFromUri(getActivity(), holder.contactPicture, c.getPhotoUri(), c.getThumbnailUri());
				}
			} else {
				holder.contactPicture.setImageResource(R.drawable.avatar);
			}

			if (displayName == null) {
				holder.contact.setText(LinphoneUtils.getAddressDisplayName(sipUri));
			} else {
				holder.contact.setText(displayName);
			}

			if (isEditMode) {
				holder.select.setVisibility(View.VISIBLE);
				holder.select.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
					@Override
					public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
						historyList.setItemChecked(position, b);
						if(getNbItemsChecked() == getCount()){
							deselectAll.setVisibility(View.VISIBLE);
							selectAll.setVisibility(View.GONE);
							enabledDeleteButton(true);
						} else {
							if(getNbItemsChecked() == 0){
								deselectAll.setVisibility(View.GONE);
								selectAll.setVisibility(View.VISIBLE);
								enabledDeleteButton(false);
							} else {
								deselectAll.setVisibility(View.GONE);
								selectAll.setVisibility(View.VISIBLE);
								enabledDeleteButton(true);
							}
						}
					}
				});
				holder.detail.setVisibility(View.INVISIBLE);
				if(historyList.isItemChecked(position)) {
					holder.select.setChecked(true);
				} else {
					holder.select.setChecked(false);
				}
			} else {
				holder.select.setVisibility(View.GONE);
				holder.detail.setVisibility(View.VISIBLE);
				holder.detail.setOnClickListener(new OnClickListener() {
					@Override
					public void onClick(View v) {
						//modify by wzb 20180205
						//if (LinphoneActivity.isInstanciated()) {
						//	LinphoneActivity.instance().displayHistoryDetail(sipUri, log);
						//}
						showHistoryDetail(sipUri,log);
						//end
					}
				});
			}
			return view;
		}
	}


	//add by wzb 20180205

	private ImageView dialBack, chat, addToContacts, goToContact, back;

	private ImageView contactPicture, callDirection;
	private TextView contactName, contactAddress, time, date;
	private String sipUri, displayName, pictureUri;
	private LinphoneContact contact;

	private void initDetailView(){
		dialBack = (ImageView) view.findViewById(R.id.call);
		dialBack.setOnClickListener(this);
		chat = (ImageView) view.findViewById(R.id.chat);
		chat.setOnClickListener(this);
		if (getResources().getBoolean(R.bool.disable_chat))
			view.findViewById(R.id.chat).setVisibility(View.GONE);

		addToContacts = (ImageView) view.findViewById(R.id.add_contact);
		addToContacts.setOnClickListener(this);

		goToContact = (ImageView) view.findViewById(R.id.goto_contact);
		goToContact.setOnClickListener(this);

		contactPicture = (ImageView) view.findViewById(R.id.contact_picture);

		contactName = (TextView) view.findViewById(R.id.contact_name);
		contactAddress = (TextView) view.findViewById(R.id.contact_address);

		callDirection = (ImageView) view.findViewById(R.id.direction);

		time = (TextView) view.findViewById(R.id.time);
		date = (TextView) view.findViewById(R.id.date);

	}

	public Dialog displayDialog(Context context,String text){
		Dialog dialog = new Dialog(context);
		dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
		Drawable d = new ColorDrawable(ContextCompat.getColor(context, R.color.colorC));
		d.setAlpha(200);
		dialog.setContentView(R.layout.dialog);
		dialog.getWindow().setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.MATCH_PARENT);
		dialog.getWindow().setBackgroundDrawable(d);

		TextView customText = (TextView) dialog.findViewById(R.id.customText);
		customText.setText(text);
		return dialog;
	}

	private void notShowDetail(boolean notshow){
		int flag=View.INVISIBLE;
		if(notshow){
			flag=View.INVISIBLE;
		}else{
			flag=View.VISIBLE;
		}
		dialBack.setVisibility(flag);
		chat.setVisibility(flag);
		goToContact.setVisibility(flag);
		addToContacts.setVisibility(flag);
		contactPicture.setVisibility(flag);
		contactName.setVisibility(flag);
		contactAddress.setVisibility(flag);
		callDirection.setVisibility(flag);
		time.setVisibility(flag);
		date.setVisibility(flag);
	}

	private void showHistoryDetail(String sip, LinphoneCallLog log){
		sipUri=sip;
		String status;
		if (log.getDirection() == CallDirection.Outgoing) {
			status = getString(R.string.outgoing);
		} else {
			if (log.getStatus() == CallStatus.Missed) {
				status = getString(R.string.missed);
			} else {
				status = getString(R.string.incoming);
			}
		}

		String callTime = secondsToDisplayableString(log.getCallDuration());
		String callDate = String.valueOf(log.getTimestamp());


		notShowDetail(false);
		displayHistory(status,callTime,callDate);


	}

	private void displayHistory(String status, String callTime, String callDate) {
		if (status.equals(getResources().getString(R.string.missed))) {
			callDirection.setImageResource(R.drawable.call_missed);
		} else if (status.equals(getResources().getString(R.string.incoming))) {
			callDirection.setImageResource(R.drawable.call_incoming);
		} else if (status.equals(getResources().getString(R.string.outgoing))) {
			callDirection.setImageResource(R.drawable.call_outgoing);
		}

		time.setText(callTime == null ? "" : callTime);
		Long longDate = Long.parseLong(callDate);
		date.setText(LinphoneUtils.timestampToHumanDate(getActivity(),longDate,getString(R.string.history_detail_date_format)));
		LinphoneAddress lAddress = null;
		try {
			lAddress = LinphoneCoreFactory.instance().createLinphoneAddress(sipUri);
		} catch (LinphoneCoreException e) {
			Wlog.e(e.toString());
		}

		if (lAddress != null) {
			contactAddress.setText(lAddress.asStringUriOnly());
			contact = ContactsManager.getInstance().findContactFromAddress(lAddress);
			if (contact != null) {
				contactName.setText(contact.getFullName());
				LinphoneUtils.setImagePictureFromUri(view.getContext(),contactPicture,contact.getPhotoUri(),contact.getThumbnailUri());
				addToContacts.setVisibility(View.GONE);
				goToContact.setVisibility(View.VISIBLE);
			} else {
				contactName.setText(displayName == null ? LinphoneUtils.getAddressDisplayName(sipUri) : displayName);
				contactPicture.setImageResource(R.drawable.avatar);
				addToContacts.setVisibility(View.VISIBLE);
				goToContact.setVisibility(View.GONE);
			}
		} else {
			contactAddress.setText(sipUri);
			contactName.setText(displayName == null ? LinphoneUtils.getAddressDisplayName(sipUri) : displayName);
		}
	}

	@SuppressLint("SimpleDateFormat")
	private String secondsToDisplayableString(int secs) {
		SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm:ss");
		Calendar cal = Calendar.getInstance();
		cal.set(0, 0, 0, 0, 0, secs);
		return dateFormat.format(cal.getTime());
	}

	//end
}