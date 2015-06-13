/*
 * VITacademics
 * Copyright (C) 2015  Pulkit Juneja <pulkit.16296@gmail.com>
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

package com.karthikb351.vitinfo2.model;

public class CourseModel {
    public String courseName, courseCode;
    public int photoResourceId;

    CourseModel(String courseName, String courseCode, int photoResourceId) {
        this.courseCode = courseCode;
        this.courseName = courseName;
        this.photoResourceId = photoResourceId;
    }
}
