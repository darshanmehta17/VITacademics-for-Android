/*
 * VITacademics
 * Copyright (C) 2015  Karthik Balakrishnan <karthikb351@gmail.com>
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


package com.karthikb351.vitinfo2.api;

import com.karthikb351.vitinfo2.utility.ResultListener;

import java.util.ArrayList;
import java.util.List;

public class RequestConfig {

    public final static int REQUEST_SYSTEM = 1;
    public final static int REQUEST_LOGIN = 2;
    public final static int REQUEST_REFRESH = 3;
    public final static int REQUEST_GRADES = 4;
    public final static int REQUEST_TOKEN = 5;
    public final static int REQUEST_FRIENDS = 6;

    List<Integer> requests;
    ResultListener resultListener;

    public RequestConfig(ResultListener resultListener) {
        requests = new ArrayList<Integer>();
        this.resultListener = resultListener;
    }

    public void addRequest(int request) {
        requests.add(request);
    }

    public void removeRequest(int request) {
        for (Integer r : requests) {
            if (request == r) {
                requests.remove(request);
            }
        }
    }

    public List<Integer> getRequests() {
        return requests;
    }

    public void setRequests(List<Integer> requests) {
        this.requests = requests;
    }
}