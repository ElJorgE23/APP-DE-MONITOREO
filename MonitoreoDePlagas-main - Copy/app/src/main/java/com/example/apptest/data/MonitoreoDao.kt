package com.example.apptest.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface MonitoreoDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertarMonitoreo(monitoreo: MonitoreoEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertarMonitoreos(monitoreos: List<MonitoreoEntity>)

    @Query("SELECT * FROM monitoreos ORDER BY id DESC")
    suspend fun obtenerTodos(): List<MonitoreoEntity>

    @Query("SELECT * FROM monitoreos WHERE sesionId = :sesionId ORDER BY id ASC")
    suspend fun obtenerPorSesion(sesionId: String): List<MonitoreoEntity>

    @Query("SELECT * FROM monitoreos WHERE usuario = :usuario ORDER BY sesionId DESC, id ASC")
    suspend fun obtenerPorUsuario(usuario: String): List<MonitoreoEntity>

    @Query("DELETE FROM monitoreos")
    suspend fun eliminarTodos()
}
