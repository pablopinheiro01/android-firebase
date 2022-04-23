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
import br.com.alura.aluraesporte.extensions.snackBar
import br.com.alura.aluraesporte.model.Usuario
import br.com.alura.aluraesporte.ui.viewmodel.ComponentesVisuais
import br.com.alura.aluraesporte.ui.viewmodel.EstadoAppViewModel
import br.com.alura.aluraesporte.ui.viewmodel.LoginViewModel
import com.firebase.ui.auth.AuthUI
import com.firebase.ui.auth.IdpResponse
import kotlinx.android.synthetic.main.login.*
import org.koin.android.viewmodel.ext.android.sharedViewModel
import org.koin.android.viewmodel.ext.android.viewModel

class LoginFragment : BaseFragment() {

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

        var authUI = AuthUI.getInstance()
        val intent = authUI.createSignInIntentBuilder().setAvailableProviders(
            listOf(AuthUI.IdpConfig.EmailBuilder().build())
        ).build()

        startActivityForResult(intent, RC_SIGN_IN)

//        configuraBotaoLogin(view)
//        configuraBotaoCadastro()
    }

//    private fun configuraBotaoCadastro() {
//        login_botao_cadastrar_usuario.setOnClickListener {
//            val direcao = LoginFragmentDirections
//                .acaoLoginParaCadastroUsuario()
//            controlador.navigate(direcao)
//        }
//    }

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
//        val direcao = LoginFragmentDirections.acaoLoginParaListaProdutos()
//        controlador.navigate(direcao)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if(requestCode == RC_SIGN_IN){
            if(resultCode == RESULT_OK){
                vaiParaListaProdutos()
            }else{
                val response = IdpResponse.fromResultIntent(data)
                Log.e(TAG, "onactivityResult: Falha ao autenticar", response?.error)
                view?.snackBar("falha ao autenticar")
            }
        }
    }

    companion object{
        const val RC_SIGN_IN = 1
        const val TAG = "LoginFragment"
    }

}