/*
 * VITacademics
 * Copyright (C) 2015  Aneesh Neelam <neelam.aneesh@gmail.com>
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package com.karthikb351.vitinfo2.api.utilities;

import android.content.Context;
import android.content.SharedPreferences;

import com.karthikb351.vitinfo2.Constants;
import com.karthikb351.vitinfo2.api.VITacademicsAPI;
import com.karthikb351.vitinfo2.api.contract.Friend;
import com.karthikb351.vitinfo2.api.event.LoginEvent;

import java.util.List;

import de.greenrobot.event.EventBus;

public class Network {

    private String campus;
    private String registerNumber;
    private String dateOfBirth;
    private String mobileNumber;

    private VITacademicsAPI viTacademicsAPI;

    public Network(Context context, String campus, String registerNumber, String dateOfBirth, String mobileNumber) {
        this.campus = campus;
        this.registerNumber = registerNumber;
        this.dateOfBirth = dateOfBirth;
        this.mobileNumber = mobileNumber;

        this.viTacademicsAPI = new VITacademicsAPI(context);

        EventBus.getDefault().register(this);
    }

    public Network(Context context) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(Constants.FILENAME_SHAREDPREFERENCES, Context.MODE_PRIVATE);
        this.campus = sharedPreferences.getString(Constants.KEY_CAMPUS, null);
        this.registerNumber = sharedPreferences.getString(Constants.KEY_REGISTERNUMBER, null);
        this.dateOfBirth = sharedPreferences.getString(Constants.KEY_DATEOFBIRTH, null);
        this.mobileNumber = sharedPreferences.getString(Constants.KEY_MOBILE, null);

        this.viTacademicsAPI = new VITacademicsAPI(context);
    }

    public void getAllFriends() {
        List<Friend> friends = Friend.listAll(Friend.class);
        for(Friend friend : friends) {
            viTacademicsAPI.share(friend.getCampus(), friend.getRegisterNumber(), friend.getDateOfBirth(), friend.getMobileNumber(), this.registerNumber);
        }
    }

    public void refreshAll() {
        viTacademicsAPI.system();
        viTacademicsAPI.refresh(campus, registerNumber, dateOfBirth, mobileNumber);
        viTacademicsAPI.grades(campus, registerNumber, dateOfBirth, mobileNumber);
        viTacademicsAPI.token(campus, registerNumber, dateOfBirth, mobileNumber);
        getAllFriends();
    }

    @Override
    protected void finalize() throws Throwable {
        EventBus.getDefault().unregister(this);
        super.finalize();
    }

    public void onEvent(LoginEvent loginEvent) {
        switch (loginEvent.path) {
            case Constants.EVENT_PATH_LOGIN_REFRESH:
                viTacademicsAPI.refresh(campus, registerNumber, dateOfBirth, mobileNumber);
                break;
            case Constants.EVENT_PATH_LOGIN_GRADES:
                viTacademicsAPI.grades(campus, registerNumber, dateOfBirth, mobileNumber);
                break;
        }
    }
}
