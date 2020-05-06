package mx.edu.ittepic.ladm_u3_practica2gonzalezcruz

import android.app.Dialog
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AlertDialog
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.consultar.*

class MainActivity : AppCompatActivity() {
    var baseRemota = FirebaseFirestore.getInstance()
    var dataLista = ArrayList<String>()
    var listaId = ArrayList<String>()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        insertar.setOnClickListener {
            insertarRegistro()
        }
        consultar.setOnClickListener {
            construirDialogo()
        }
        baseRemota.collection("restaurante")
            .addSnapshotListener { querySnapshot, firebaseFirestoreException ->
                if(firebaseFirestoreException!=null){
                    //SI ES DIFERENTE DE NULL ENTONCES SI HAY ERROR
                    Toast.makeText(this,"ERROR NO SE PUEDE ACCEDER A CONSULTA",Toast.LENGTH_LONG).show()
                    return@addSnapshotListener
                }
                dataLista.clear()
                listaId.clear()
                for(document in querySnapshot!!){
                    var cadena = document.getString("nombre")+"\n"+
                            document.getString("domicilio")+
                            "\n"+document.getString("celular")+"-----"+document.getString("pedido.producto")+"-------"+document.getBoolean("pedido.entregado")
                    dataLista.add(cadena)
                    listaId.add(document.id)
                }
                if(dataLista.size==0){
                    dataLista.add("NO HAY DATA")
                }
                var adaptador = ArrayAdapter<String>(this, android.R.layout.simple_list_item_1,dataLista)
                lista.adapter = adaptador
            }
        lista.setOnItemClickListener { parent, view, position, id ->
            if(listaId.size==0){
                return@setOnItemClickListener
            }
            AlertaEliminaActualizar(position)

        }
    }
    private fun AlertaEliminaActualizar(position: Int) {
        AlertDialog.Builder(this).setTitle("ATENCION").setMessage("¿Que desea hacer con \n${dataLista[position]}?")
            .setPositiveButton("Eliminar"){d, w->
                eliminar(listaId[position])
            }
            .setNegativeButton("Actualizar"){d,w->
                llamarVentanaActualizar(listaId[position])
            }
            .setNeutralButton("Cancelar"){dialog,which->}
            .show()
    }

    private fun llamarVentanaActualizar(idActualizar: String) {
        baseRemota.collection("restaurante").document(idActualizar).get()
            .addOnSuccessListener {
                var v = Intent(this, Main2Activity::class.java)

                v.putExtra("id",idActualizar)
                v.putExtra("nombre",it.getString("nombre"))
                v.putExtra("domicilio", it.getString("domicilio"))
                v.putExtra("celular",it.getString("celular"))
                v.putExtra("cantidad",it.getDouble("pedido.cantidad"))
                v.putExtra("producto",it.getString("pedido.producto"))
                v.putExtra("precio",it.getDouble("pedido.precio"))
                v.putExtra("entregado",it.getBoolean("pedido.entregado"))

                startActivity(v)
            }
            .addOnFailureListener {
                Toast.makeText(this,"ERROR NO HAY CONEXION DE RED",Toast.LENGTH_LONG).show()
            }
    }
    private fun construirDialogo() {
        var dialogo = Dialog(this)
        dialogo.setContentView(R.layout.consultar)
        //Declarar los objetos
        var valor = dialogo.findViewById<EditText>(R.id.valor)
        var buscar = dialogo.findViewById<Button>(R.id.buscar)
        var cerrar = dialogo.findViewById<Button>(R.id.salir)


        dialogo.show()
        cerrar.setOnClickListener {
            dialogo.dismiss()
        }
        buscar.setOnClickListener {
            if(valor.text.isEmpty()){
                Toast.makeText(this, "DEBES PONER VALOR PARA BUSCAR", Toast.LENGTH_LONG).show()
                return@setOnClickListener
            }
            consultaNombre(valor.text.toString())
            dialogo.dismiss()
        }
    }
    private fun consultaNombre(valor: String) {
        baseRemota.collection("restaurante")
            .whereEqualTo("nombre",valor)
            .addSnapshotListener { querySnapshot, firebaseFirestoreException ->
                if(firebaseFirestoreException != null){
                    resultado.setText("ERROR NO HAY CONEXION")
                    return@addSnapshotListener
                }
                var res= ""
                for(document in querySnapshot!!){
                    res += "ID: "+document.id+"\nNombre: "+document.getString("nombre")+
                            "\nCelular: "+document.getString("celular")+"\nDomicilio: "+document.getString("domicilio")+
                            "\nCantidad: "+document.getDouble("pedido.cantidad")+
                            "\nProducto/Descripcion: "+document.getString("pedido.producto")
                            "\nPrecio: "+document.getDouble("pedido.precio")+
                            "\nEstatus: "+document.getBoolean("pedido.entregado")
                }

                resultado.setText(res)

            }
    }
    private fun eliminar(idEliminar: String) {
        baseRemota.collection("restaurante").document(idEliminar).delete()
            .addOnSuccessListener {
                Toast.makeText(this,"SE ELIMINO CON EXITO",Toast.LENGTH_LONG).show()
            }
            .addOnFailureListener {
                Toast.makeText(this,"NO SE PUDO ELIMINAR",Toast.LENGTH_LONG).show()
            }
    }

    private fun insertarRegistro() {
        var data = hashMapOf(
            "nombre" to nombre.text.toString(),
            "celular" to celular.text.toString(),
            "domicilio" to domicilio.text.toString(),
            "pedido" to hashMapOf(
                "cantidad" to cantidad.text.toString().toDouble(),
                "producto" to producto.text.toString(),
                "precio" to precio.text.toString().toDouble(),
                "entregado" to entregado.isChecked()
            )
        )
        baseRemota.collection("restaurante")
            .add(data)
            .addOnSuccessListener {
                Toast.makeText(this,"SE CAPTURO", Toast.LENGTH_LONG).show()
                cantidad.setText("");producto.setText("");precio.setText("")
                nombre.setText("");celular.setText("");domicilio.setText("")
            }
            .addOnFailureListener {
                Toast.makeText(this,"ERROR NO SE CAPTURO", Toast.LENGTH_LONG).show()
            }

    }

}
