package br.com.alura.aluraesporte.repository

import android.util.Log
import android.widget.Toast
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import br.com.alura.aluraesporte.model.Usuario
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseAuthUserCollisionException
import com.google.firebase.auth.FirebaseAuthWeakPasswordException
import java.lang.IllegalArgumentException

private const val TAG = "FirebaseAuthRepository"

class FirebaseAuthRepository(private val firebaseAuth: FirebaseAuth) {

    fun desloga() {
        firebaseAuth.signOut()
    }

    private fun autenticaUsuario(firebaseAuth: FirebaseAuth) {
        firebaseAuth.signInWithEmailAndPassword(
            "pablo@aluraesporte.com", "teste123"
        ).addOnSuccessListener {
        }.addOnFailureListener {
        }
    }

    fun cadastra(usuario: Usuario): LiveData<Resource<Boolean>>{
        val liveData = MutableLiveData<Resource<Boolean>>()
        try {
            firebaseAuth.createUserWithEmailAndPassword(usuario.email, usuario.senha)
                .addOnSuccessListener { task ->
                    Log.i(TAG, "Cadastro foi feito filhote")
                    liveData.value = Resource(true)
                }
                .addOnFailureListener { exception ->
                    Log.i(TAG, "Erro feio $exception")
                    val mensagemErro: String = devolveErroDeCadastro(exception)
                    liveData.value = Resource(false, mensagemErro)
                }
        } catch (e: Exception) {
            liveData.value = Resource(false, "Erro desconhecido")
        }
        return liveData
    }

    private fun devolveErroDeCadastro(exception: java.lang.Exception): String {
        return when (exception) {
            is FirebaseAuthWeakPasswordException -> "Senha precisa de pelo menos 6 digitos"
            is FirebaseAuthInvalidCredentialsException -> "Email invalido"
            is FirebaseAuthUserCollisionException -> "Email já cadastrado"
            else -> "Erro desconhecido ${exception.message}"
        }
    }

    fun estaLogado(): Boolean {
        val usuario = firebaseAuth.currentUser
        return usuario != null
    }
}