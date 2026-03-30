package com.example.apptest.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "usuarios")
data class UserEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val nombre: String,
    val apellido: String,
    val celular: String,
    val correo: String,
    val password: String,
    val rol: String = "Monitoreador",
    val agricola: String = "Agricola Default"
)