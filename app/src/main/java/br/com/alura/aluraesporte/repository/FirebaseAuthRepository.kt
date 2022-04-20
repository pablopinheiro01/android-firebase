package br.com.alura.aluraesporte.repository

import android.util.Log
import android.widget.Toast
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import br.com.alura.aluraesporte.model.Usuario
import com.google.firebase.auth.*
import java.lang.IllegalArgumentException

private const val TAG = "FirebaseAuthRepository"

class FirebaseAuthRepository(private val firebaseAuth: FirebaseAuth) {

    fun desloga() {
        firebaseAuth.signOut()
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

    fun autentica(usuario: Usuario): LiveData<Resource<Boolean>> {
        val liveData = MutableLiveData<Resource<Boolean>>()
        firebaseAuth.signInWithEmailAndPassword(usuario.email, usuario.senha)
            .addOnCompleteListener{ task ->
                if(task.isSuccessful){
                    liveData.value = Resource(true)
                }else{
                    Log.e(TAG, "autentica: ", task.exception)
                    val mensagemErro:String = when(task.exception){
                        is FirebaseAuthInvalidUserException -> "E-mail invalido"
                        is FirebaseAuthInvalidCredentialsException -> "Credenciais Invalidas"
                        else -> "Erro desconhecido"
                    }
                    liveData.value = Resource(false, mensagemErro)
                }
            }
        return liveData
    }
}