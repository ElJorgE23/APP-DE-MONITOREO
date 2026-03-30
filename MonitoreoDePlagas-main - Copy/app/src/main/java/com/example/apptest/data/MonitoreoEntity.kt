package com.example.apptest.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "monitoreos")
data class MonitoreoEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val sesionId: String,
    val punto: String,
    val usuario: String,
    val agricola: String,
    val granja: String,
    val lote: String,
    val cultivo: String,
    val latitud: String,
    val longitud: String,
    val tipo: String,
    val nombre: String,
    val fase: String,
    val cantidad: String,
    val fecha: String,
    val hora: String
)