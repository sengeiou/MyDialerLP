/*
 * Copyright (C) 2016 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.android.dialer.calllog;

import android.app.Fragment;
import android.app.FragmentManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import com.android.dialer.R;
import com.android.dialer.list.ListsFragment;


public class InternetContactFragment extends Fragment {
    private Context mContext;

    public InternetContactFragment() {

    }

    @Override
    public void onCreate(Bundle state) {
        super.onCreate(state);

    }

    @Override
    public void onStart() {
        super.onStart();
        changeToAnotherFragment();


    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
        ComponentName cn = new ComponentName("com.android.ldap", "com.android.ldap.Activity.MainActivity");
        Intent intent=new Intent();
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_NO_HISTORY);
        try {
            intent.setComponent(cn);
            startActivity(intent);
        } catch (Exception e) {

        }
    }

    private void changeToAnotherFragment(){
        FragmentManager fm = getActivity().getFragmentManager();
        Fragment fragment = new ListsFragment();
        fm.beginTransaction().replace(R.id.dialtacts_frame,fragment).commit();

    }
}
