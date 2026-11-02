package com.example.data.local

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.data.model.Student
import kotlinx.coroutines.flow.Flow

/**
 * Data Access Object (DAO) pour gérer la table des élèves (Student) dans CHIJU.
 * Fournit les opérations CRUD réactives (Flow) et suspendues pour :
 * - nom (lastName)
 * - prénom (firstName)
 * - niveau (niveau : CP1, CP2, CE1, CE2, CM1, CM2)
 */
@Dao
interface StudentDao {

    @Query("SELECT * FROM students ORDER BY lastName ASC, firstName ASC")
    fun getAllStudents(): Flow<List<Student>>

    @Query("SELECT * FROM students WHERE niveau = :niveau ORDER BY lastName ASC, firstName ASC")
    fun getStudentsByNiveau(niveau: String): Flow<List<Student>>

    @Query("SELECT * FROM students WHERE classId = :classId ORDER BY lastName ASC, firstName ASC")
    fun getStudentsForClass(classId: Long): Flow<List<Student>>

    @Query("SELECT * FROM students WHERE id = :id LIMIT 1")
    fun getStudentById(id: Long): Flow<Student?>

    @Query("SELECT * FROM students WHERE lastName LIKE '%' || :query || '%' OR firstName LIKE '%' || :query || '%' ORDER BY lastName ASC")
    fun searchStudentsByName(query: String): Flow<List<Student>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertStudent(student: Student): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertStudents(students: List<Student>): List<Long>

    @Update
    suspend fun updateStudent(student: Student)

    @Delete
    suspend fun deleteStudent(student: Student)

    @Query("DELETE FROM students WHERE id = :id")
    suspend fun deleteStudentById(id: Long)

    @Query("SELECT COUNT(*) FROM students WHERE niveau = :niveau")
    suspend fun countStudentsByNiveau(niveau: String): Int
}
