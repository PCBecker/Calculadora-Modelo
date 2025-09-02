package com.example.calculadoramodelo

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.example.calculadoramodelo.fragments.LambertFragment
import com.example.calculadoramodelo.fragments.EqGrau2Fragment
import com.example.calculadoramodelo.fragments.Eqgrau3
import com.example.calculadoramodelo.fragments.TecladoBasico
import com.example.calculadoramodelo.interfaces.FragmentInteractionListener

class MainActivity : AppCompatActivity(), FragmentInteractionListener {
    private var currentFragmentTag: String? = null
    companion object {
        private const val CURRENT_FRAGMENT_TAG_KEY = "current_fragment_tag_key"
        private const val TAG_BASICO = "BASICO"

    }
    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)

        currentFragmentTag = savedInstanceState?.getString(CURRENT_FRAGMENT_TAG_KEY)

        if (savedInstanceState == null) {
            supportFragmentManager.beginTransaction()
                .replace(R.id.fragment_container, TecladoBasico(), TAG_BASICO)
                // Substitua 'fragment_container' pelo ID do seu FrameLayout/Container
                .commit()
            currentFragmentTag = TAG_BASICO // Define a tag do fragmento inicial
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        // Salva o currentFragmentTag para que ele possa ser restaurado após a rotação
        outState.putString(CURRENT_FRAGMENT_TAG_KEY, currentFragmentTag)
    }

    override fun onLoadFragment(fragment: Fragment, tag: String) {
        Log.d("MainActivity", "onLoadFragment chamado. Fragment: ${fragment::class.simpleName}, Tag: $tag")

        // Procura por um fragmento existente com a mesma tag
        val existingFragment = supportFragmentManager.findFragmentByTag(tag)

        if (existingFragment != null) {
            Log.d("MainActivity", "Fragmento $tag já existe. Atualizando argumentos e exibindo-o.")

            // Se o fragmento já existe, anexa os novos argumentos a ele e o exibe.
            existingFragment.arguments = fragment.arguments

            supportFragmentManager.beginTransaction()
                .replace(R.id.fragment_container, existingFragment, tag)
                .addToBackStack(tag)
                .commit()

        } else {
            Log.d("MainActivity", "Trocando de fragmento. currentFragmentTag: $currentFragmentTag -> $tag")

            // Se o fragmento não existe, cria-o do zero.
            supportFragmentManager.beginTransaction()
                .replace(R.id.fragment_container, fragment, tag)
                .addToBackStack(tag)
                .commit()
        }
        currentFragmentTag = tag
        Log.d("MainActivity", "Transação de fragmento confirmada.")
    }

    fun clearDisplays() {
        when (currentFragmentTag) {
            "BASICO" -> {
                val visor1 = supportFragmentManager.findFragmentByTag("BASICO")?.view?.findViewById<TextView>(R.id.visor1)
                val visor2 = supportFragmentManager.findFragmentByTag("BASICO")?.view?.findViewById<TextView>(R.id.visor2)
                visor1?.text = ""
                visor2?.text = ""
            }
            "EQGRAU2" -> {
                val visor1grau2 = supportFragmentManager.findFragmentByTag("EQGRAU2")?.view?.findViewById<TextView>(R.id.visor1grau2)
                val visor2grau2 = supportFragmentManager.findFragmentByTag("EQGRAU2")?.view?.findViewById<TextView>(R.id.visor2grau2)
                visor1grau2?.text = ""
                visor2grau2?.text = ""
            }
            "EQGRAU3" -> {
                val visor1grau3 = supportFragmentManager.findFragmentByTag("EQGRAU3")?.view?.findViewById<TextView>(R.id.visor1grau3)
                val visor2grau3 = supportFragmentManager.findFragmentByTag("EQGRAU3")?.view?.findViewById<TextView>(R.id.visor2grau3)
                visor1grau3?.text = ""
                visor2grau3?.text = ""
            }
            "Lambert" -> {
                val visor1LambertFragment = supportFragmentManager.findFragmentByTag("Lambert")?.view?.findViewById<TextView>(R.id.visor1Lambert)
                val visor2LambertFragment = supportFragmentManager.findFragmentByTag("Lambert")?.view?.findViewById<TextView>(R.id.visor2Lambert)
                visor1LambertFragment?.text = ""
                visor2LambertFragment?.text = ""
            }
        }
    }

    @SuppressLint("SetTextI18n")
    override fun onNumberClicked(number: String) {
        when (currentFragmentTag) {
            "BASICO" -> {
                val textView =
                    supportFragmentManager.findFragmentByTag("BASICO")?.view?.findViewById<TextView>(
                        R.id.visor1
                    )
                textView?.let {
                    if (it.text == "0") it.text = number else it.text = "${it.text}$number"
                }
            }
            "EQGRAU2" -> {
                val textView =
                    supportFragmentManager.findFragmentByTag("EQGRAU2")?.view?.findViewById<TextView>(
                        R.id.visor1grau2
                    )
                textView?.let {
                    if (it.text == "0") it.text = number else it.text = "${it.text}$number"
                }
            }
            "EQGRAU3" -> {
                val textView =
                    supportFragmentManager.findFragmentByTag("EQGRAU3")?.view?.findViewById<TextView>(
                        R.id.visor1grau3
                    )
                textView?.let {
                    if (it.text == "0") it.text = number else it.text = "${it.text}$number"
                }
            }
            "Lambert" -> {
                val textView =
                    supportFragmentManager.findFragmentByTag("Lambert")?.view?.findViewById<TextView>(
                        R.id.visor1Lambert
                    )
                textView?.let {
                    if (it.text == "0") it.text = number else it.text = "${it.text}$number"
                }
            }
        }
    }

    override fun onOperatorClicked(operator: String) {
        // Lógica para lidar com operadores (+, -, *, /)
        Log.d("MainActivity", "Operador clicado no fragmento: $operator")
        // Exemplo: processar a operação
    }

    override fun onClearClicked() {
        // Lógica para limpar o visor ou reiniciar a calculadora
        Log.d("MainActivity", "Botão AC clicado no fragmento. Visor limpo.")
        // Exemplo:
        // val mainDisplay: TextView = findViewById(R.id.main_calculator_display)
        // mainDisplay.text = ""
    }

    override fun onCleanFragmentVisors(fragmentTag: String) {
        // Implementação vazia, pois a lógica de limpeza agora está nos ViewModels dos Fragments
    }
}

