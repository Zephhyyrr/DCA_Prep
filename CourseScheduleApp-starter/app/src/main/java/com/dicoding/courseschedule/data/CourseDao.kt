package com.dicoding.courseschedule.data

import androidx.lifecycle.LiveData
import androidx.paging.PagingSource
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.RawQuery
import androidx.sqlite.db.SimpleSQLiteQuery

//TODO 2 : Define data access object (DAO)
@Dao
interface CourseDao {
    @RawQuery(observedEntities = [Course::class])
    fun getNearestSchedule(query: SimpleSQLiteQuery): LiveData<Course?>

    @RawQuery(observedEntities = [Course::class])
    fun getAll(query: SimpleSQLiteQuery): PagingSource<Int, Course>

    @RawQuery(observedEntities = [Course::class])
    fun getCourse(query: SimpleSQLiteQuery): PagingSource<Int, Course>

    @Query("SELECT * FROM course WHERE day = :day ORDER BY startTime ASC")
    fun getTodaySchedule(day: Int): List<Course>

    @Insert(onConflict = androidx.room.OnConflictStrategy.REPLACE)
    fun insert(course: Course)

    @Delete
    fun delete(course: Course)

    @Query("SELECT * FROM course WHERE id = :courseId")
    fun getCourseById(courseId: Int): LiveData<Course?>
}