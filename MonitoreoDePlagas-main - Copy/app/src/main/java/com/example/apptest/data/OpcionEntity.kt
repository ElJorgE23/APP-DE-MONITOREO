package com.example.apptest.data

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "opciones",
    indices = [Index(value = ["tipo", "nombre"], unique = true)]
)
// e
data class OpcionEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val tipo: String,
    val nombre: String
)