package br.com.alura.aluraesporte.repository

import android.util.Log
import android.widget.Toast
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.google.firebase.auth.FirebaseAuth

private const val TAG = "FirebaseAuthRepository"

class FirebaseAuthRepository(private val firebaseAuth: FirebaseAuth) {

    private fun desloga(firebaseAuth: FirebaseAuth) {
        firebaseAuth.signOut()
    }

    private fun verificaUsuario(firebaseAuth: FirebaseAuth) {
        val usuario = firebaseAuth.currentUser
        if(usuario != null){

        }else{

        }
    }

    private fun autenticaUsuario(firebaseAuth: FirebaseAuth) {
        firebaseAuth.signInWithEmailAndPassword(
            "pablo@aluraesporte.com", "teste123"
        ).addOnSuccessListener {
        }.addOnFailureListener {
        }
    }

    fun cadastra(email: String, senha: String): LiveData<Boolean>{
        val liveData = MutableLiveData<Boolean>()

        firebaseAuth.createUserWithEmailAndPassword(email, senha)
            .addOnSuccessListener { task ->
                Log.i(TAG, "Cadastro foi feito filhote")
                liveData.value = true
            }
            .addOnFailureListener { exception ->
                Log.i(TAG, "Erro feio $exception")
                liveData.value = false
            }

        return liveData
    }
}