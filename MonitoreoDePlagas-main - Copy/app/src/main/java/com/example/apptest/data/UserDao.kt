package com.example.apptest.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query

@Dao
interface UserDao {

    @Insert
    suspend fun insertarUsuario(usuario: UserEntity)

    @Query("SELECT * FROM usuarios WHERE rol = 'Monitoreador' ORDER BY nombre, apellido")
    suspend fun obtenerMonitoreadores(): List<UserEntity>


    @Query("SELECT * FROM usuarios WHERE correo = :correo LIMIT 1")
    suspend fun obtenerUsuarioPorCorreo(correo: String): UserEntity?

    @Query("SELECT * FROM usuarios WHERE correo = :correo AND password = :password LIMIT 1")
    suspend fun login(correo: String, password: String): UserEntity?

    @Query("SELECT * FROM usuarios WHERE id = :id LIMIT 1")
    suspend fun obtenerUsuarioPorId(id: Int): UserEntity?
}