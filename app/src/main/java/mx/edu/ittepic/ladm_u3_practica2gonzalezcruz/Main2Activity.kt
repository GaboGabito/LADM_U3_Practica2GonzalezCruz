package mx.edu.ittepic.ladm_u3_practica2gonzalezcruz

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.activity_main2.*

class Main2Activity : AppCompatActivity() {
    var id = ""
    var basedatos = FirebaseFirestore.getInstance()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main2)
        var extras = intent.extras

        id = extras?.getString("id")!!
        nombre2.setText(extras.getString("nombre"))
        domicilio2.setText(extras.getString("domicilio"))
        celular2.setText(extras.getString("celular"))
        cantidad2.setText(extras.getDouble("cantidad").toString())
        producto2.setText(extras.getString("producto"))
        precio2.setText(extras.getDouble("precio").toString())

        checkBox.isChecked = false
        if(extras.getBoolean("entregado")==true){
            checkBox.isChecked = true
        }


        button.setOnClickListener {
            basedatos.collection("restaurante").document(id)
                .update("nombre",nombre2.text.toString(),
                    "domicilio",domicilio2.text.toString(),
                    "celular",celular2.text.toString(),"pedido.cantidad",cantidad2.text.toString().toDouble(),
                "pedido.producto",producto2.text.toString(),"pedido.precio",precio2.text.toString().toDouble(),
                "pedido.entregado",checkBox.isChecked)
                .addOnSuccessListener {
                    Toast.makeText(this,"ACTUALIZACION REALIZADA", Toast.LENGTH_LONG).show()
                    finish()
                }
                .addOnFailureListener {
                    Toast.makeText(this,"ERROR NO SE PUEDE ACTUALIZAR, NO HAY CONEXION", Toast.LENGTH_LONG).show()
                }
        }

        button2.setOnClickListener {
            finish()
        }
    }

}
