package com.effatheresoft.androidpractice.data.local

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.room.ColumnInfo
import androidx.room.Dao
import androidx.room.Database
import androidx.room.Entity
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.PrimaryKey
import androidx.room.Query
import androidx.room.Room
import androidx.room.RoomDatabase

@Dao
interface TaskDao {
    @Query("select * from tasks")
    fun getTasks(): LiveData<List<TaskEntity>>

    @Query("select * from tasks where id == :id")
    fun getTaskById(id: String): LiveData<TaskEntity>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun insertTasks(tasks: List<TaskEntity>)

    @Query("delete from tasks")
    fun deleteAllTasks()
}

@Entity("tasks")
data class TaskEntity(
    @PrimaryKey()
    val id: String,

    @field:ColumnInfo("title")
    val title: String,

    @field:ColumnInfo("completed")
    val completed: Boolean
)

@Database(entities = [TaskEntity::class], version = 1, exportSchema = false)
abstract class TaskDatabase: RoomDatabase() {
    abstract fun taskDao(): TaskDao

    companion object {
        @Volatile
        private var instance: TaskDatabase? = null

        fun getInstance(appContext: Context) = Room.databaseBuilder(appContext, TaskDatabase::class.java, "task.db").build()
    }
}