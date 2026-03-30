package com.example.apptest.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface OpcionDao {

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertar(opcion: OpcionEntity)

    @Query("SELECT * FROM opciones WHERE tipo = :tipo ORDER BY nombre ASC")
    suspend fun obtenerPorTipo(tipo: String): List<OpcionEntity>

    @Query("SELECT EXISTS(SELECT 1 FROM opciones WHERE tipo = :tipo AND nombre = :nombre)")
    suspend fun existe(tipo: String, nombre: String): Boolean

    @Query("DELETE FROM opciones WHERE tipo = :tipo AND nombre = :nombre")
    suspend fun eliminar(tipo: String, nombre: String)
}