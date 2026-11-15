package com.example.prueba_iot_semaforo

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.activity.addCallback
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class Menu : AppCompatActivity() {

    private lateinit var tvusumenu: TextView
    private lateinit var btn1: Button
    private lateinit var btn2: Button
    private lateinit var btn3: Button
    private lateinit var btn4: Button
    private lateinit var btn5: Button

    private var usu: Usuario? = null
    private var nombreUsuarioExtra: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_menu)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        tvusumenu = findViewById(R.id.tvusumenu)
        btn1 = findViewById(R.id.btn1)
        btn2 = findViewById(R.id.btn2)
        btn3 = findViewById(R.id.btn3)
        btn4 = findViewById(R.id.btn4)
        btn5 = findViewById(R.id.btn5)

        @Suppress("DEPRECATION")
        usu = intent.getSerializableExtra("usu") as? Usuario
        nombreUsuarioExtra = intent.getStringExtra("NOMBRE_USUARIO") ?: ""

        val textoBienvenida = if (usu != null && usu?.nombre?.isNotEmpty() == true) {
            val ape = usu?.apellido ?: ""
            "Bienvenido Usuario : ${usu?.nombre} $ape".trim()
        } else if (nombreUsuarioExtra.isNotEmpty()) {
            "Bienvenido Usuario : $nombreUsuarioExtra"
        } else {
            "Bienvenido Usuario"
        }
        tvusumenu.text = textoBienvenida

        boton1()
        boton2()
        boton3()
        boton4()
        boton5()
        desactivarFlecha()
    }

    private fun obtenerNombreParaIntent(): String {
        return if (usu != null && usu?.nombre?.isNotEmpty() == true) {
            "${usu?.nombre} ${usu?.apellido}".trim()
        } else if (nombreUsuarioExtra.isNotEmpty()) {
            nombreUsuarioExtra
        } else {
            "Usuario"
        }
    }

    fun boton1() {
        btn1.setOnClickListener {
            val intent = Intent(this@Menu, ManipularEsp32::class.java)
            intent.putExtra("usu", usu)
            intent.putExtra("NOMBRE_USUARIO", obtenerNombreParaIntent())
            startActivity(intent)
        }
    }

    fun boton2() {
        btn2.setOnClickListener {
            val intent = Intent(this@Menu, Ver_acciones::class.java)
            intent.putExtra("usu", usu)
            intent.putExtra("NOMBRE_USUARIO", obtenerNombreParaIntent())
            startActivity(intent)
        }
    }

    fun boton3() {
        btn3.setOnClickListener {
            val intent = Intent(this@Menu, Estadisticas::class.java)
            intent.putExtra("usu", usu)
            intent.putExtra("NOMBRE_USUARIO", obtenerNombreParaIntent())
            startActivity(intent)
        }
    }

    fun boton4() {
        btn4.setOnClickListener {
            val intent = Intent(this@Menu, Ver_perfil::class.java)
            intent.putExtra("usu", usu)
            intent.putExtra("NOMBRE_USUARIO", obtenerNombreParaIntent())
            startActivity(intent)
        }
    }

    fun boton5() {
        btn5.setOnClickListener {
            val a = AlertDialog.Builder(this@Menu)
            a.setTitle("Pregunta")
            a.setMessage("¿Desea Cerrar La Sesión?")

            a.setNegativeButton("Cancelar") { dialog, _ ->
                dialog.dismiss()
            }

            a.setPositiveButton("Aceptar") { _, _ ->
                val intent = Intent(this@Menu, Login::class.java)
                startActivity(intent)
                finish()
            }

            a.setIcon(R.drawable.pregunta)
            a.setCancelable(false)
            a.show()
        }
    }

    fun desactivarFlecha() {
        onBackPressedDispatcher.addCallback(this) { }
    }
}
