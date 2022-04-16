package br.com.alura.aluraesporte.ui.activity

import android.os.Bundle
import android.util.Log
import android.view.View.GONE
import android.view.View.VISIBLE
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.navigation.findNavController
import androidx.navigation.ui.setupWithNavController
import br.com.alura.aluraesporte.R
import br.com.alura.aluraesporte.ui.viewmodel.EstadoAppViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import kotlinx.android.synthetic.main.main_activity.*
import org.koin.android.viewmodel.ext.android.viewModel

class MainActivity : AppCompatActivity() {

    private val controlador by lazy {
        findNavController(R.id.main_activity_nav_host)
    }
    private val viewModel: EstadoAppViewModel by viewModel()

    private lateinit var firebaseAuth: FirebaseAuth


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.main_activity)

        firebaseAuth = Firebase.auth

//        cadastraUsuario()
//        autenticaUsuario()

        val usuario = firebaseAuth.currentUser
        if(usuario != null )
            Toast.makeText(this, "usuario logado doido", Toast.LENGTH_LONG).show()
        else
            Toast.makeText(this,"nao deu bom tio", Toast.LENGTH_LONG).show()

        firebaseAuth.signOut()

        controlador.addOnDestinationChangedListener { _,
                                                      destination,
                                                      _ ->
            title = destination.label
            viewModel.componentes.observe(this, Observer {
                it?.let { temComponentes ->
                    if(temComponentes.appBar){
                        supportActionBar?.show()
                    } else {
                        supportActionBar?.hide()
                    }
                    if(temComponentes.bottomNavigation) {
                        main_activity_bottom_navigation.visibility = VISIBLE
                    } else {
                        main_activity_bottom_navigation.visibility = GONE
                    }
                }
            })
        }
        main_activity_bottom_navigation
            .setupWithNavController(controlador)
    }

    private fun autenticaUsuario() {
        firebaseAuth.signInWithEmailAndPassword(
            "pablo@aluraesporte.com", "teste123"
        ).addOnSuccessListener {
            Toast.makeText(this, "login deu bom d+ ${firebaseAuth.currentUser?.email}", Toast.LENGTH_LONG).show()
        }.addOnFailureListener {
            Toast.makeText(this, "erro doido em n foi nao: $it", Toast.LENGTH_LONG).show()
        }
    }

    private fun cadastraUsuario() {
        firebaseAuth.createUserWithEmailAndPassword(
            "pablo@aluraesporte.com", "teste123"
        ).addOnSuccessListener { task ->
            Toast.makeText(this, "usuario foi cadastrado com sucesso ${firebaseAuth.currentUser?.email} ", Toast.LENGTH_LONG).show()
        }.addOnFailureListener {
            Log.i("exception cadastro", "Erro: ${it.message}")
            Toast.makeText(this, "deu erro $it", Toast.LENGTH_LONG).show()
        }
    }

}
