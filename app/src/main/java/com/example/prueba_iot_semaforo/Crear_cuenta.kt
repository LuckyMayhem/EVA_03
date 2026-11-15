package com.example.prueba_iot_semaforo

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Spinner
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.database.FirebaseDatabase
import java.security.MessageDigest

class Crear_cuenta : AppCompatActivity() {

    private lateinit var etRut: EditText
    private lateinit var etNombre: EditText
    private lateinit var etApellido: EditText
    private lateinit var etCorreo: EditText
    private lateinit var spnCargo: Spinner
    private lateinit var etPassword: EditText
    private lateinit var etPassword2: EditText
    private lateinit var btnRegistrar: Button
    private lateinit var btnVolver: Button

    private val db = FirebaseDatabase.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_crear_cuenta)

        etRut = findViewById(R.id.txtrut_reg)
        etNombre = findViewById(R.id.txtnom_reg)
        etApellido = findViewById(R.id.txtape_reg)
        etCorreo = findViewById(R.id.txtema_reg)
        spnCargo = findViewById(R.id.spncargo_reg)
        etPassword = findViewById(R.id.txtcon_reg)
        etPassword2 = findViewById(R.id.txtcon2_reg)
        btnRegistrar = findViewById(R.id.btcrear)
        btnVolver = findViewById(R.id.btvolver)

        btnRegistrar.setOnClickListener {
            val rutTexto = etRut.text.toString().trim()
            val nombre = etNombre.text.toString().trim()
            val apellido = etApellido.text.toString().trim()
            val correo = etCorreo.text.toString().trim()
            val cargo = spnCargo.selectedItem?.toString() ?: ""
            val passPlano = etPassword.text.toString().trim()
            val passPlano2 = etPassword2.text.toString().trim()

            if (rutTexto.isEmpty() || nombre.isEmpty() || correo.isEmpty() || passPlano.isEmpty()) {
                Toast.makeText(this, "Completa todos los campos obligatorios", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (passPlano != passPlano2) {
                Toast.makeText(this, "Las contraseñas no coinciden", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val passSeguro = hashSHA256(passPlano)

            val usuario = HashMap<String, Any>()
            usuario["rut"] = rutTexto.toIntOrNull() ?: rutTexto
            usuario["nombre"] = nombre
            usuario["apellido"] = apellido
            usuario["correo"] = correo
            usuario["ocupacion"] = cargo
            usuario["cargo"] = cargo
            usuario["password"] = passSeguro
            usuario["contraseña"] = passSeguro

            db.getReference("Usuario").push().setValue(usuario).addOnSuccessListener {
                Toast.makeText(this, "Cuenta registrada de forma segura", Toast.LENGTH_SHORT).show()
                finish()
            }.addOnFailureListener {
                Toast.makeText(this, "Error al registrar en Firebase", Toast.LENGTH_SHORT).show()
            }
        }

        btnVolver.setOnClickListener {
            finish()
        }
    }

    private fun hashSHA256(texto: String): String {
        val digest = MessageDigest.getInstance("SHA-256")
        val bytes = digest.digest(texto.toByteArray(Charsets.UTF_8))
        return bytes.joinToString("") { "%02x".format(it) }
    }
}
