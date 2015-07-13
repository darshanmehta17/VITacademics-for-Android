/*
 * VITacademics
 * Copyright (C) 2015  Aneesh Neelam <neelam.aneesh@gmail.com>
 * Copyright (C) 2015  Gaurav Agerwala <gauravagerwala@gmail.com>
 * Copyright (C) 2015  Pulkit Juneja <pulkit.16296@gmail.com>
 * Copyright (C) 2015  Hemant Jain <hemanham@gmail.com>
 *
 * This file is part of VITacademics.
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

package com.karthikb351.vitinfo2.fragment.timetable;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import com.karthikb351.vitinfo2.MainApplication;
import com.karthikb351.vitinfo2.R;
import com.karthikb351.vitinfo2.contract.Course;
import com.karthikb351.vitinfo2.contract.Timing;
import com.karthikb351.vitinfo2.event.RefreshFragmentEvent;
import com.karthikb351.vitinfo2.fragment.details.DetailsFragment;
import com.karthikb351.vitinfo2.utility.DateTime;
import com.karthikb351.vitinfo2.utility.RecyclerViewOnClickListener;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import de.greenrobot.event.EventBus;

public class TimetableDayFragment extends Fragment {

    private TimetableListAdapter adapter;
    private RecyclerView recyclerview;
    private ProgressBar loadProgress;
    private int dayOfWeek;
    private View rootView;

    public TimetableDayFragment() {
    }

    public static TimetableDayFragment newInstance(int dayOfWeek) {
        TimetableDayFragment fragment = new TimetableDayFragment();
        fragment.dayOfWeek = dayOfWeek ;
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_timetable_day, container, false);
        initialize();
        return rootView;
    }

    void initialize() {
        recyclerview = (RecyclerView) rootView.findViewById(R.id.recycler_view_timetable);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getActivity());
        recyclerview.setLayoutManager(layoutManager);
        loadProgress = (ProgressBar) rootView.findViewById(R.id.timeTableProgressBar);
        new LoadData().execute();
    }


    void onListItemClicked(Course course) {
        android.support.v4.app.Fragment frag = DetailsFragment.newInstance(course);
        android.support.v4.app.FragmentTransaction ft = getActivity().getSupportFragmentManager().beginTransaction();
        ft.replace(R.id.flContent,frag,course.getCourseCode()).addToBackStack(null).commit();
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
        initialize();
    }

    class LoadData extends AsyncTask<Void, Void, List<Course>> {
        @Override
        protected void onPreExecute() {
            loadProgress.setVisibility(View.VISIBLE);
        }

        @Override
        protected List<Course> doInBackground(Void... params) {
            List<Course> finalArray = new ArrayList<>();
            List<Course> courses = ((MainApplication)getActivity().getApplication()).getDataHolderInstance().getCourses();
            for (Course course : courses) {
                for (Timing timing : course.getTimings()) {
                    if (timing.getDay() == dayOfWeek) {
                        finalArray.add(course);
                    }
                }
            }

            Collections.sort(finalArray, new Comparator<Course>() {
                @Override
                public int compare(Course lhs, Course rhs) {
                    String lhsStartTime = "";
                    String rhsStartTime = "";
                    for (Timing timing : lhs.getTimings()) {
                        if (timing.getDay() == dayOfWeek) {
                            lhsStartTime = timing.getStartTime();
                        }
                    }
                    for (Timing timing : rhs.getTimings()) {
                        if (timing.getDay() == dayOfWeek) {
                            rhsStartTime = timing.getStartTime();
                        }
                    }
                    try {
                        return DateTime.compareTimes(lhsStartTime, rhsStartTime);
                    } catch (ParseException ex) {
                        ex.printStackTrace();
                        return 0;
                    }
                }
            });

            return finalArray;
        }

        @Override
        protected void onPostExecute(List<Course> finalCourses) {
            loadProgress.setVisibility(View.GONE);
            adapter = new TimetableListAdapter(getActivity(), finalCourses);
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