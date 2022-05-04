package br.com.alura.aluraesporte.ui.fragment

import android.app.Activity.RESULT_OK
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import br.com.alura.aluraesporte.R
import br.com.alura.aluraesporte.extensions.googleSignClient
import br.com.alura.aluraesporte.extensions.snackBar
import br.com.alura.aluraesporte.model.Usuario
import br.com.alura.aluraesporte.ui.viewmodel.ComponentesVisuais
import br.com.alura.aluraesporte.ui.viewmodel.EstadoAppViewModel
import br.com.alura.aluraesporte.ui.viewmodel.LoginViewModel
import com.firebase.ui.auth.AuthUI
import com.firebase.ui.auth.IdpResponse
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.firebase.auth.GoogleAuthProvider
import kotlinx.android.synthetic.main.login.*
import org.koin.android.viewmodel.ext.android.sharedViewModel
import org.koin.android.viewmodel.ext.android.viewModel

class LoginFragment : Fragment() {

    private val controlador by lazy {
        findNavController()
    }
    private val viewModel: LoginViewModel by viewModel()
    private val estadoAppViewModel: EstadoAppViewModel by sharedViewModel()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(
            R.layout.login,
            container,
            false
        )
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        estadoAppViewModel.temComponentes = ComponentesVisuais()
        configuraBotaoLogin(view)
        configuraBotaoCadastro()
        configuraBotaoGoogle()
    }

    private fun configuraBotaoGoogle() {
        login_botao_signin_google.setOnClickListener {
            //migrado codigo para a extension
//            val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
//                .requestIdToken(getString(R.string.default_web_client_id))
//                .requestEmail()
//                .build()
//            val client = GoogleSignIn.getClient(requireContext(), gso)
            val cliente = requireContext().googleSignClient()
            startActivityForResult(cliente.signInIntent, RC_SIGN_IN_GOOGLE)
        }
    }

    private fun configuraBotaoCadastro() {
        login_botao_cadastrar_usuario.setOnClickListener {
            val direcao = LoginFragmentDirections
                .acaoLoginParaCadastroUsuario()
            controlador.navigate(direcao)
        }
    }

    private fun configuraBotaoLogin(view: View) {
        login_botao_logar.setOnClickListener {

            limpaCampos()

            val email = login_email.editText?.text.toString()
            val senha = login_senha.editText?.text.toString()

            if (validaCampos(email, senha)) {
                autentica(email, senha)
            }

        }
    }

    private fun autentica(email: String, senha: String) {
        viewModel.autentica(Usuario(email, senha)).observe(viewLifecycleOwner, Observer {
            it?.let { recurso ->
                if (recurso.dado) {
                    vaiParaListaProdutos()
                } else {
                    val mensagemErro = recurso.erro ?: "Erro durante a autenticacao"
                    view?.snackBar(mensagemErro)
                }
            }
        })
    }

    private fun validaCampos(email: String, senha: String): Boolean {
        var valido = true
        if (email.isBlank()) {
            login_email.error = "E-mail é obrigatorio"
            valido = false
        }

        if (senha.isBlank()) {
            login_senha.error = "Senha é obrigatorio"
            valido = false
        }
        return valido
    }

    private fun limpaCampos() {
        login_email.error = null
        login_senha.error = null
    }

    private fun vaiParaListaProdutos() {
        val direcao = LoginFragmentDirections.acaoLoginParaListaProdutos()
        controlador.navigate(direcao)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if(resultCode == RESULT_OK && requestCode == RC_SIGN_IN_GOOGLE){
            val contaGoogle = GoogleSignIn.getSignedInAccountFromIntent(data).result
            Log.i(TAG, "Conta google autenticada ${contaGoogle}")

            contaGoogle.let { conta ->
                val credential = GoogleAuthProvider.getCredential(conta.idToken, null)

                viewModel.vinculaContaGoogle(credential)
                    .observe(viewLifecycleOwner, Observer {
                        it?.let { recurso ->
                            if(recurso.dado){
                                vaiParaListaProdutos()
                            }else{
                                val mensagem = recurso.erro ?: "Falha ao vincular conta google"
                                view?.snackBar(mensagem)
                            }
                        }
                    })
            }
        }
    }

    companion object{
        const val TAG = "LoginFragment"
        const val RC_SIGN_IN_GOOGLE = 1
    }

}