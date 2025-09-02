// com.example.calculadoramodelo.fragments/TecladoBasico.kt
package com.example.calculadoramodelo.fragments

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.calculadoramodelo.MainActivity
import com.example.calculadoramodelo.databinding.FragmentTecladoBasicoBinding
import com.example.calculadoramodelo.viewmodels.TecladoBasicoViewModel
import kotlin.math.cos
import kotlin.math.ln
import kotlin.math.log10
import kotlin.math.pow
import kotlin.math.sin
import kotlin.math.sqrt
import kotlin.math.tan

@Suppress("NAME_SHADOWING")
class TecladoBasico : Fragment() {

    // Use View Binding para acessar as Views
    private var _binding: FragmentTecladoBasicoBinding? = null
    private val binding get() = _binding!!

    // Inicialize o ViewModel
    private lateinit var tecladoBasicoViewModel: TecladoBasicoViewModel

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        // Inicializa o binding aqui
        _binding = FragmentTecladoBasicoBinding.inflate(inflater, container, false)
        return binding.root
    }

    @SuppressLint("SetTextI18n", "DefaultLocale")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        // Inicializa o ViewModel (isso garante que o mesmo ViewModel sobreviva às rotações)
        tecladoBasicoViewModel = ViewModelProvider(this).get(TecladoBasicoViewModel::class.java)

        // Configura os observadores para LiveData
        setupObservers()

        // Configura os listeners dos botões
        setupButtonListeners()

        // Remove esta chamada, pois o setupObservers() já cuida disso
        // updateDisplayTextView()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null // Limpa a referência ao binding para evitar memory leaks
    }

    // --- Nova função para configurar todos os listeners dos botões ---
    private fun setupButtonListeners() {
        // Botões numéricos
        binding.zero.setOnClickListener { tecladoBasicoViewModel.handleNumberClick("0") }
        binding.one.setOnClickListener { tecladoBasicoViewModel.handleNumberClick("1") }
        binding.two.setOnClickListener { tecladoBasicoViewModel.handleNumberClick("2") }
        binding.three.setOnClickListener { tecladoBasicoViewModel.handleNumberClick("3") }
        binding.four.setOnClickListener { tecladoBasicoViewModel.handleNumberClick("4") }
        binding.five.setOnClickListener { tecladoBasicoViewModel.handleNumberClick("5") }
        binding.six.setOnClickListener { tecladoBasicoViewModel.handleNumberClick("6") }
        binding.seven.setOnClickListener { tecladoBasicoViewModel.handleNumberClick("7") }
        binding.eight.setOnClickListener { tecladoBasicoViewModel.handleNumberClick("8") }
        binding.nine.setOnClickListener { tecladoBasicoViewModel.handleNumberClick("9") }

        // Operadores
        binding.soma.setOnClickListener { tecladoBasicoViewModel.appendOperator("+") }
        binding.subtrac.setOnClickListener { tecladoBasicoViewModel.appendOperator("-") }
        binding.multiplic.setOnClickListener { tecladoBasicoViewModel.appendOperator("*") }
        binding.divide.setOnClickListener { tecladoBasicoViewModel.appendOperator("/") }

        // Parênteses
        binding.botaoAbPar.setOnClickListener { tecladoBasicoViewModel.appendParenthesis("(") }
        binding.botaoFechPar.setOnClickListener { tecladoBasicoViewModel.appendParenthesis(")") }

        // Funções trigonométricas/matemáticas
        binding.sin.setOnClickListener { tecladoBasicoViewModel.appendFunction("sin") }
        binding.cos.setOnClickListener { tecladoBasicoViewModel.appendFunction("cos") }
        binding.tan.setOnClickListener { tecladoBasicoViewModel.appendFunction("tan") }
        binding.ln.setOnClickListener { tecladoBasicoViewModel.appendFunction("ln") }
        binding.sqrt.setOnClickListener { tecladoBasicoViewModel.appendFunction("sqrt") }

        // Ponto decimal
        binding.ponto.setOnClickListener { tecladoBasicoViewModel.appendDot() }

        // Botão de apagar último caractere (Clear - Backspace)
        binding.clear.setOnClickListener { tecladoBasicoViewModel.clearLastCharacter() }

        // Botão de limpar tudo (Clear All - AC)
        // O ID R.id.clearText no seu código é geralmente para "limpar tudo"
        binding.clearText.setOnClickListener {
            tecladoBasicoViewModel.clearAllDisplays()
        }

        // Botão de igual (onde a lógica de avaliação será executada)
        binding.igual.setOnClickListener {
            // Pegue a expressão do ViewModel, calcule e defina o resultado no ViewModel
            val expression = tecladoBasicoViewModel.visor1Text.value ?: ""
            try {
                val result = eval(expression)
                tecladoBasicoViewModel.setResult(result.toString(), expression) // Atualiza o ViewModel com o resultado
            } catch (e: Exception) {
                tecladoBasicoViewModel.setResult("Invalid operation")
            }
        }
        // Navegação para outros fragments (mantendo a lógica com MainActivity)
        // Você precisará de uma referência ao listener aqui.
        // Se `activity as? MainActivity` te dá acesso ao `onLoadFragment`, mantenha assim.
        binding.eqgrau2.setOnClickListener {
            (activity as? MainActivity)?.onLoadFragment(EqGrau2Fragment(), "EQGRAU2")
        }
        binding.eqgrau3.setOnClickListener {
            (activity as? MainActivity)?.onLoadFragment(Eqgrau3(), "EQGRAU3")
        }
        binding.funcW.setOnClickListener {
            (activity as? MainActivity)?.onLoadFragment(LambertFragment(), "Lambert")
        }
    }
    // --- Implementação dos Observadores ---
    private fun setupObservers() {
        tecladoBasicoViewModel.visor1Text.observe(viewLifecycleOwner) { text ->
            binding.visor1.text = text
        }
        tecladoBasicoViewModel.visor2Text.observe(viewLifecycleOwner) { text ->
            binding.visor2.text = text
        }
    }

    // A função eval (e o objeto anônimo) pode permanecer como está,
    // já que ela faz o cálculo e não manipula as Views diretamente.
    private fun eval(str: String): Double {
        // ... (seu código da função eval permanece inalterado) ...
        return object : Any() {
            var pos = -1
            var ch = 0

            fun nextChar() {
                ch = if (++pos < str.length) str[pos].code else -1
            }
            fun eat(charToEat: Int): Boolean {
                while (ch == ' '.code) nextChar()
                if (ch == charToEat) {
                    nextChar()
                    return true
                }
                return false
            }
            fun parse(): Double {
                nextChar()
                val x = parseExpression()
                if (pos < str.length) throw RuntimeException("Unexpected: " + ch.toChar())
                return x
            }
            fun parseExpression(): Double {
                var x = parseTerm()
                while (true) {
                    if (eat('+'.code)) x += parseTerm()
                    else if (eat('-'.code)) x -= parseTerm()
                    else return x
                }
            }
            fun parseTerm(): Double {
                var x = parseFactor()
                while (true) {
                    if (eat('*'.code)) x *= parseFactor()
                    else if (eat('/'.code)) x /= parseFactor()
                    else return x
                }
            }
            fun parseFactor(): Double {
                if (eat('+'.code)) return parseFactor()
                if (eat('-'.code)) return -parseFactor()

                var x: Double
                val startPos = pos
                if (eat('('.code)) {
                    x = parseExpression()
                    eat(')'.code)
                } else if (ch >= '0'.code && ch <= '9'.code || ch == '.'.code) {
                    while (ch >= '0'.code && ch <= '9'.code || ch == '.'.code) nextChar()
                    x = str.substring(startPos, pos).toDouble()
                } else if (ch >= 'a'.code && ch <= 'z'.code) {
                    while (ch >= 'a'.code && ch <= 'z'.code) nextChar()
                    val func = str.substring(startPos, pos)
                    x = parseFactor()
                    x =
                        when (func) {
                            "sqrt" -> sqrt(x)
                            "sin" -> sin(Math.toRadians(x))
                            "cos" -> cos(Math.toRadians(x))
                            "tan" -> tan(Math.toRadians(x))
                            "log" -> log10(x)
                            "ln" -> ln(x)
                            else -> throw RuntimeException("Unknown function: $func")
                        }
                } else {
                    throw RuntimeException("Unexpected: " + ch.toChar())
                }
                if (eat('^'.code)) x = x.pow(parseFactor())
                return x
            }
        }.parse()
    }
}