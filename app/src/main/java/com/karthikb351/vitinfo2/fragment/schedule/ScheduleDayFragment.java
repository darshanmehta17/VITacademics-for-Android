/*
 * VITacademics
 * Copyright (C) 2015  Aneesh Neelam <neelam.aneesh@gmail.com>
 * Copyright (C) 2015  Saurabh Joshi <saurabhjoshi94@outlook.com>
 * Copyright (C) 2015  Gaurav Agerwala <gauravagerwala@gmail.com>
 * Copyright (C) 2015  Karthik Balakrishnan <karthikb351@gmail.com>
 * Copyright (C) 2015  Pulkit Juneja <pulkit.16296@gmail.com>
 * Copyright (C) 2015  Hemant Jain <hemanham@gmail.com>
 * Copyright (C) 2015  Darshan Mehta <darshanmehta17@gmail.com>
 *
 * This file is part of VITacademics.
 *
 * VITacademics is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * VITacademics is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with VITacademics.  If not, see <http://www.gnu.org/licenses/>.
 */

package com.karthikb351.vitinfo2.fragment.schedule;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.util.Pair;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ViewSwitcher;

import com.karthikb351.vitinfo2.MainApplication;
import com.karthikb351.vitinfo2.R;
import com.karthikb351.vitinfo2.activity.DetailsActivity;
import com.karthikb351.vitinfo2.activity.MainActivity;
import com.karthikb351.vitinfo2.contract.Course;
import com.karthikb351.vitinfo2.contract.Timing;
import com.karthikb351.vitinfo2.event.RefreshFragmentEvent;
import com.karthikb351.vitinfo2.model.Status;
import com.karthikb351.vitinfo2.utility.Constants;
import com.karthikb351.vitinfo2.utility.DateTimeCalender;
import com.karthikb351.vitinfo2.utility.RecyclerViewOnClickListener;
import com.karthikb351.vitinfo2.utility.ResultListener;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import de.greenrobot.event.EventBus;

public class ScheduleDayFragment extends Fragment {

    private SwipeRefreshLayout swipeRefreshLayout;
    private ScheduleListAdapter adapter;
    private RecyclerView recyclerview;
    private int dayOfWeek;
    private View rootView;
    private LayoutInflater layoutInflater;
    private ViewGroup viewGroup;
    private List<Course> courses;
    private ViewSwitcher viewSwitcher;

    public ScheduleDayFragment() {
    }

    public static ScheduleDayFragment newInstance(int dayOfWeek) {
        ScheduleDayFragment fragment = new ScheduleDayFragment();
        fragment.dayOfWeek = dayOfWeek;
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        layoutInflater = inflater;
        viewGroup = container;
        rootView = layoutInflater.inflate(R.layout.fragment_schedule_day, viewGroup, false);
        viewSwitcher = (ViewSwitcher) rootView.findViewById(R.id.view_switcher_schedule);
        swipeRefreshLayout = (SwipeRefreshLayout) rootView.findViewById(R.id.swipe_refresh_layout);
        initialize();
        return rootView;
    }

    void initialize() {
        recyclerview = (RecyclerView) rootView.findViewById(R.id.recycler_view_timetable);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getActivity());
        recyclerview.setLayoutManager(layoutManager);
        swipeRefreshLayout.setColorSchemeColors(ContextCompat.getColor(getContext(), R.color.colorAccent));
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                ((MainActivity) getActivity()).pullToRefresh(new ResultListener() {
                    @Override
                    public void onSuccess() {
                        swipeRefreshLayout.setRefreshing(false);
                    }

                    @Override
                    public void onFailure(Status status) {
                        swipeRefreshLayout.setRefreshing(false);
                    }
                });
            }
        });
        initializeData();
    }

    private void initializeData() {
        courses = ((MainApplication) getActivity().getApplication()).getDataHolderInstanceInitialized().getCourses();
        if (!(courses.isEmpty())) {
            new LoadDayTask().execute();
        }
    }


    void onListItemClicked(Course course) {
        Intent intent = new Intent(getActivity(), DetailsActivity.class);
        intent.putExtra(Constants.INTENT_EXTRA_CLASS_NUMBER, course.getClassNumber());
        startActivity(intent);
    }


    @Override
    public void onResume() {
        super.onResume();
        EventBus.getDefault().register(this);
    }

    @Override
    public void onPause() {
        EventBus.getDefault().unregister(this);
        super.onPause();
    }

    // This method will be called when a RefreshFragmentEvent is posted
    public void onEvent(RefreshFragmentEvent event) {
        initializeData();
    }

    private class LoadDayTask extends AsyncTask<Void, Void, List<Pair<Course, Timing>>> {

        @Override
        protected List<Pair<Course, Timing>> doInBackground(Void... params) {
            List<Pair<Course, Timing>> finalArray = new ArrayList<>();
            for (Course course : courses) {
                Timing lastTiming = new Timing();
                for (Timing timing : course.getTimings()) {
                    if (timing.getDay() == dayOfWeek && !(timing.equals(lastTiming))) {
                        finalArray.add(new Pair<>(course, timing));
                        lastTiming = timing;
                    }
                }
            }

            Collections.sort(finalArray, new Comparator<Pair<Course, Timing>>() {
                @Override
                public int compare(Pair<Course, Timing> lhs, Pair<Course, Timing> rhs) {
                    String lhsStartTime = "";
                    String rhsStartTime = "";
                    for (Timing timing : lhs.first.getTimings()) {
                        if (timing.getDay() == dayOfWeek) {
                            lhsStartTime = timing.getStartTime();
                        }
                    }
                    for (Timing timing : rhs.first.getTimings()) {
                        if (timing.getDay() == dayOfWeek) {
                            rhsStartTime = timing.getStartTime();
                        }
                    }
                    try {
                        return DateTimeCalender.compareTimes(lhsStartTime, rhsStartTime);
                    } catch (ParseException ex) {
                        ex.printStackTrace();
                        return 0;
                    }
                }
            });

            return finalArray;
        }

        @Override
        protected void onPostExecute(List<Pair<Course, Timing>> finalCourses) {
            if (finalCourses.size() == 0) {
                viewSwitcher.showNext();
            } else {
                adapter = new ScheduleListAdapter(getActivity(), finalCourses);
                adapter.setOnclickListener(new RecyclerViewOnClickListener<Course>() {
                    @Override
                    public void onItemClick(Course data) {
                        onListItemClicked(data);
                    }
                });
                recyclerview.setAdapter(adapter);
            }
        }

    }
}
