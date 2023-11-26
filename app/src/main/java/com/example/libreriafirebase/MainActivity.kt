package com.example.libreriafirebase

import Libro
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.ListView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.FirebaseApp
import com.google.firebase.database.*
import java.util.UUID

class MainActivity : AppCompatActivity() {
    // Declaración de variables
    private lateinit var listLibros: MutableList<Libro>
    private lateinit var arrayAdapterLibro: ArrayAdapter<Libro>
    private lateinit var nombreLibro: EditText
    private lateinit var autorLibro: EditText
    private lateinit var editorialLibro: EditText
    private lateinit var listV_libros: ListView
    private lateinit var databaseReference: DatabaseReference
    private lateinit var libroSelected: Libro

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Inicialización de vistas
        nombreLibro = findViewById(R.id.inputTitulo)
        autorLibro = findViewById(R.id.inputAutor)
        editorialLibro = findViewById(R.id.inputEditorial)
        listV_libros = findViewById(R.id.listaLibros)

        // Inicialización de botones
        val btnAdd: Button = findViewById(R.id.botonAgregar)
        val btnEdit: Button = findViewById(R.id.botonEditar)
        val btnDelete: Button = findViewById(R.id.botonEliminar)

        // Inicialización de listas y adaptador
        listLibros = mutableListOf()
        arrayAdapterLibro = ArrayAdapter(this, android.R.layout.simple_list_item_1, listLibros)
        listV_libros.adapter = arrayAdapterLibro

        // Inicialización de Firebase y carga de datos
        inicializarFirebase()
        listarDatos()

        // Manejo de eventos al hacer clic en un elemento de la lista
        listV_libros.setOnItemClickListener { parent, view, position, id ->
            libroSelected = parent.getItemAtPosition(position) as Libro
            nombreLibro.setText(libroSelected.Nombre)
            autorLibro.setText(libroSelected.Autor)
            editorialLibro.setText(libroSelected.Editorial)
        }

        // Manejo de eventos para el botón de agregar
        btnAdd.setOnClickListener {
            val nombre = nombreLibro.text.toString().trim()
            val autor = autorLibro.text.toString().trim()
            val editorial = editorialLibro.text.toString().trim()

            if (nombre.isNotEmpty() && autor.isNotEmpty() && editorial.isNotEmpty()) {
                val libro = Libro(
                    UUID.randomUUID().toString(),
                    nombre,
                    autor,
                    editorial
                )
                // Agregar libro a Firebase
                databaseReference.child("Libro").child(libro.id).setValue(libro)
                Toast.makeText(this, "Libro agregado", Toast.LENGTH_LONG).show()
                Limpiar()
            } else {
                Validar()
            }
        }

        // Manejo de eventos para el botón de editar
        btnEdit.setOnClickListener {
            val nombre = nombreLibro.text.toString().trim()
            val autor = autorLibro.text.toString().trim()
            val editorial = editorialLibro.text.toString().trim()

            if (nombre.isNotEmpty() && autor.isNotEmpty() && editorial.isNotEmpty()) {
                val libro = Libro(
                    libroSelected.id,
                    nombre,
                    autor,
                    editorial
                )
                // Actualizar libro en Firebase
                databaseReference.child("Libro").child(libro.id).setValue(libro)
                Toast.makeText(this, "Libro actualizado", Toast.LENGTH_LONG).show()
                Limpiar()
            } else {
                Validar()
            }
        }

        // Manejo de eventos para el botón de eliminar
        btnDelete.setOnClickListener {
            val libro = Libro(
                libroSelected.id,
                nombreLibro.text.toString().trim(),
                autorLibro.text.toString().trim(),
                editorialLibro.text.toString().trim()
            )
            // Eliminar libro de Firebase
            databaseReference.child("Libro").child(libro.id).removeValue()
            Toast.makeText(this, "Libro eliminado", Toast.LENGTH_LONG).show()
            Limpiar()
        }
    }

    // Función para limpiar las cajas de texto
    private fun Limpiar() {
        nombreLibro.setText("")
        autorLibro.setText("")
        editorialLibro.setText("")
    }

    // Función para realizar la validación de campos
    private fun Validar() {
        if (nombreLibro.text.toString().trim().isEmpty()) {
            nombreLibro.error = "Required"
        } else if (autorLibro.text.toString().trim().isEmpty()) {
            autorLibro.error = "Required"
        } else if (editorialLibro.text.toString().trim().isEmpty()) {
            editorialLibro.error = "Required"
        }
        Toast.makeText(this, "¡Necesitas llenar todos los campos!", Toast.LENGTH_LONG).show()
    }

    // Función para listar datos desde Firebase
    private fun listarDatos() {
        databaseReference.child("Libro").addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                listLibros.clear()
                for (objSnapshot in dataSnapshot.children) {
                    val libro = objSnapshot.getValue(Libro::class.java)
                    listLibros.add(libro!!)
                }
                arrayAdapterLibro.notifyDataSetChanged()
            }

            override fun onCancelled(databaseError: DatabaseError) {
            }
        })
    }

    // Función para inicializar Firebase
    private fun inicializarFirebase() {
        FirebaseApp.initializeApp(this)
        val firebaseDatabase = FirebaseDatabase.getInstance()
        databaseReference = firebaseDatabase.reference
    }
}
