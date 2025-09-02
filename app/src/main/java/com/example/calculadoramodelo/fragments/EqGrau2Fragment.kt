package com.example.calculadoramodelo.fragments

import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.example.calculadoramodelo.MainActivity
import com.example.calculadoramodelo.databinding.FragmentEqGrau2Binding
import com.example.calculadoramodelo.viewmodels.CoeficientesViewModel
import com.jjoe64.graphview.GraphView
import com.jjoe64.graphview.GridLabelRenderer
import com.jjoe64.graphview.series.DataPoint
import com.jjoe64.graphview.series.LineGraphSeries
import kotlin.math.pow
import androidx.appcompat.app.AppCompatActivity

class EqGrau2Fragment : Fragment() {

    private var _binding: FragmentEqGrau2Binding? = null
    private val binding get() = _binding!!
    private val coeficientesViewModel: CoeficientesViewModel by viewModels()
    private lateinit var visor1grau2: TextView
    private lateinit var visor2grau2: TextView
    private lateinit var graphView: GraphView
    private lateinit var calcular: Button
    private lateinit var indiceA: Button
    private lateinit var indiceB: Button
    private lateinit var indiceC: Button
    private lateinit var grafico2G: Button
    private lateinit var help: Button
    private lateinit var sinal: Button
    private lateinit var clear: Button

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentEqGrau2Binding.inflate(inflater, container, false)
        return binding.root
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        indiceA = binding.indiceA
        indiceB = binding.indiceB
        indiceC = binding.indiceC
        calcular = binding.calcular
        visor1grau2 = binding.visor1grau2
        visor2grau2 = binding.visor2grau2
        help = binding.help
        sinal = binding.sinal
        clear = binding.clear
        // Certifique-se de que o ID no XML é 'graph_eqgrau2' ou 'graph_eq2'
        // Se for 'graph_eq2', mude para 'binding.graphEq2'
        graphView = binding.graphEqgrau2
        grafico2G = binding.grafico2G

        sinal.setOnClickListener { coeficientesViewModel.invertSignVisor1() }
        clear.setOnClickListener { coeficientesViewModel.clearLastCharacter() }

        calcular.setOnClickListener {
            // Chama uma função no ViewModel para garantir que a UI se atualize.
            // Poderia ser apenas uma função de "re-calcular"
            coeficientesViewModel.setIndiceA(coeficientesViewModel.indiceA.value ?: 0.0)
        }
        grafico2G.setOnClickListener {
            val a = coeficientesViewModel.indiceA.value ?: 0.0
            val b = coeficientesViewModel.indiceB.value ?: 0.0
            val c = coeficientesViewModel.indiceC.value ?: 0.0

            Log.d("EqGrau2Fragment", "Botão grafico2G clicado. Gerando gráfico com a=$a, b=$b, c=$c")
            generateQuadraticGraph(a, b, c, graphView)
        }
        binding.indiceA.setOnClickListener { indiceA() }
        binding.indiceB.setOnClickListener { indiceB() }
        binding.indiceC.setOnClickListener { indiceC() }
        binding.clearText.setOnClickListener { handleClearInput() }


        help.setOnClickListener{
            val builder = AlertDialog.Builder(requireContext())
            builder.setTitle("Instructions!").setMessage("Digitar: o valor a, após clicar index_a, o valor b, "+ "após clicar em index_b, " +
                    "o valor de c, após clicar em index_c,  exemplo: 1*X^2 + 2*X +1 = 0" +
                    " digitar o valor de a que é 1 clicar em indice_a, após digitar o valor de b que é 2 e clicar em" +
                    " indice_b, e finalmente digitar o valor de c e clicar em indice_c" +
                    " clicar em Calcular (X1=-1 e X2=-1), ou em Graphic ")
                .setCancelable(true)
                .setPositiveButton("Ok"){ _, _ ->

                }
                .show()
        }

        coeficientesViewModel.result.observe(viewLifecycleOwner) { result ->
            // Certifique-se de que o ID no XML é 'textViewResultEq2'
            visor1grau2.text = result.resultText
            // NOTA: A chamada generateQuadraticGraph que estava aqui foi movida para o listener do grafico2G.
            // Se você quiser que o gráfico se atualize automaticamente ao mudar os coeficientes,
            // e também ao clicar no botão grafico2G, você pode manter a chamada aqui E no listener.
            // Por enquanto, ela está apenas no listener do botão grafico2G para atender ao seu pedido.
            Log.d("EqGrau2Fragment", "Resultado do ViewModel observado. Texto: ${result.resultText}")
        }
        coeficientesViewModel.visor1Text.observe(viewLifecycleOwner) { text ->
            visor1grau2.text = text
            Log.d("EqGrau2Fragment", "Visor 1 atualizado para: $text")
        }
        coeficientesViewModel.visor2Text.observe(viewLifecycleOwner) { text ->
            visor2grau2.text = text
            Log.d("EqGrau2Fragment", "Visor 2 atualizado para: $text")
        }
        setupNumberButtons() // Crie esta nova função para organizar
        setupNavigationButtons() // Crie esta nova função para organizar

        drawInitialGraph()
    }
    private fun setupNavigationButtons() {
        binding.voltar.setOnClickListener {
            (activity as? MainActivity)?.onLoadFragment(TecladoBasico(), "BASICO")
        }
        binding.eqgrau3.setOnClickListener {
            (activity as? MainActivity)?.onLoadFragment(Eqgrau3(), "EQGRAU3")
        }

        binding.funcW.setOnClickListener {
             (activity as? MainActivity)?.onLoadFragment(LambertFragment(), "Lambert")
        }
    }
    private fun setupNumberButtons() {
        // Obtenha os botões usando binding
        val buttons = listOf(
            binding.zero2g, binding.one, binding.two, binding.three, binding.four,
            binding.five, binding.six, binding.seven, binding.eight, binding.nine, binding.ponto // Inclua o ponto decimal
        )
        buttons.forEach { button ->
            button.setOnClickListener {
                val digit = (it as Button).text.toString() // Pega o texto do botão clicado
                handleNumberInput(digit)
            }
        }
    }
    private fun EqGrau2Fragment.handleNumberInput(digit: String) {
        val currentVisor1Text = coeficientesViewModel.visor1Text.value ?: ""
        val currentVisor2Text = coeficientesViewModel.visor2Text.value ?: ""

        if (currentVisor2Text.contains("X1") || currentVisor2Text.contains("Não é equação")) {
            coeficientesViewModel.clearAll() // Limpa tudo se um resultado anterior está sendo exibido
        }
// Se o visor de entrada é "0" e o dígito é "0", não faça nada (evita "000")
        if (currentVisor1Text == "0" && digit == "0") return

// Se o visor é "0" e o dígito não é um ponto, substitui "0"
        if (currentVisor1Text == "0" && digit != ".") {
            coeficientesViewModel.setVisor1Text(digit)
        } else {
            // Lógica para ponto decimal: só adiciona se já não houver um
            if (digit == ".") {
                if (!currentVisor1Text.contains(".")) {
                    coeficientesViewModel.setVisor1Text(currentVisor1Text + digit)
                }
            } else {
                coeficientesViewModel.setVisor1Text(currentVisor1Text + digit)
            }
        }// Limpa visores de resultados/equação se o usuário começar a digitar um novo número

    }
    private fun setupObservers() {
        coeficientesViewModel.visor1Text.observe(viewLifecycleOwner) { text ->
            binding.visor1grau2.text = text // Certifique-se que você tem um TextView com ID @+id/visor1 no seu layout
        }
        coeficientesViewModel.visor2Text.observe(viewLifecycleOwner) { text ->
            binding.visor2grau2.text = text // Certifique-se que você tem um TextView com ID @+id/visor2 no seu layout
        }
        // Observe outros LiveData como indiceA, B, C, D se precisar refletir na UI
    }
    private fun handleClearInput() {
        coeficientesViewModel.clearVisorOnly()
    }
    private fun setupClearButton() {
        binding.clearText.setOnClickListener {
            handleClearInput() // ou eqgrau3ViewModel.clearAll()
        }
    }
    private fun indiceA() {
        val value = visor1grau2.text.toString().toDoubleOrNull() ?: 0.0
        Log.d("EqGrau2Fragment", "Função indiceA() chamada. Valor lido do visor: ${visor1grau2.text}, Convertido: $value")

        visor2grau2.text = ""
        visor1grau2.text = ""
        visor2grau2.append("a =   $value ; ")

        coeficientesViewModel.setIndiceA(value)
        coeficientesViewModel.setVisor1Text("0")
        Log.d("EqGrau2Fragment", "Indice A definido no ViewModel para: ${coeficientesViewModel.indiceA.value}")
    }
    private fun indiceB() {
        val value = visor1grau2.text.toString().toDoubleOrNull() ?: 0.0
        Log.d("EqGrau2Fragment", "Função indiceB() chamada. Valor lido do visor: ${visor1grau2.text}, Convertido: $value")
        visor2grau2.append("b =   $value ; ")
        coeficientesViewModel.setIndiceB(value)
        coeficientesViewModel.setVisor1Text("0")
        Log.d("EqGrau2Fragment", "Indice B definido no ViewModel para: ${coeficientesViewModel.indiceB.value}")
    }
    private fun indiceC() {
        val value = visor1grau2.text.toString().toDoubleOrNull() ?: 0.0
        Log.d("EqGrau2Fragment", "Função indiceC() chamada. Valor lido do visor: ${visor1grau2.text}, Convertido: $value")
        visor2grau2.append("c =   $value ; ")
        coeficientesViewModel.setIndiceC(value)
        coeficientesViewModel.setVisor1Text("0")
        Log.d("EqGrau2Fragment", "Indice C definido no ViewModel para: ${coeficientesViewModel.indiceC.value}")
    }
    private fun drawInitialGraph() {
        val a = coeficientesViewModel.indiceA.value ?: 0.0
        val b = coeficientesViewModel.indiceB.value ?: 0.0
        val c = coeficientesViewModel.indiceC.value ?: 0.0

        graphView.removeAllSeries()
        graphView.title = "${a}*x² + (${b})*x + $c = 0"
        graphView.titleColor = Color.WHITE
        graphView.titleTextSize = 40f

        val gridLabel = graphView.gridLabelRenderer
        gridLabel.horizontalAxisTitle = "X"
        gridLabel.verticalAxisTitle = "Y"
        gridLabel.horizontalAxisTitleColor = Color.WHITE
        gridLabel.verticalAxisTitleColor = Color.WHITE
        gridLabel.labelsSpace = 8
        gridLabel.horizontalLabelsColor = Color.WHITE
        gridLabel.verticalLabelsColor = Color.WHITE
        gridLabel.textSize = 30f
        gridLabel.gridColor = Color.DKGRAY
        gridLabel.gridStyle = GridLabelRenderer.GridStyle.BOTH
        gridLabel.setHumanRounding(false)

        graphView.viewport.isXAxisBoundsManual = true
        graphView.viewport.setMinX(-5.0)
        graphView.viewport.setMaxX(5.0)
        graphView.viewport.isYAxisBoundsManual = true
        graphView.viewport.setMinY(-10.0)
        graphView.viewport.setMaxY(10.0)

        graphView.viewport.isScalable = true
        graphView.viewport.isScrollable = true
    }
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
    private fun generateQuadraticGraph(a: Double,
                                       b: Double,
                                       c: Double,
                                       graph: GraphView){
        // Limpa as séries existentes antes de desenhar a nova
        graph.removeAllSeries()

        // Cria uma nova série para a parábola.
        val series = LineGraphSeries<DataPoint>()

        // Define o intervalo X para a plotagem
        val minXPlot = -10.0
        val maxXPlot = 10.0
        val numPoints = 500

        // Itera e gera os pontos da parábola
        for (i in 0..numPoints) {
            val x = minXPlot + (i / numPoints.toDouble()) * (maxXPlot - minXPlot)
            val y = a * x.pow(2) + b * x + c
            series.appendData(DataPoint(x, y), true, numPoints + 1)
        }

        // Adiciona a série ao gráfico
        graph.addSeries(series)

        // Adiciona logs para verificar os pontos e limites
        //Log.d("GraphDebug", "generateQuadraticGraph: Número de pontos = ${series.getValues().count()}")
        Log.d("GraphDebug", "generateQuadraticGraph: Viewport X: [$minXPlot, $maxXPlot]")

        // Configurações visuais da série
        series.color = Color.RED
        series.thickness = 6
        series.isDrawBackground = true
        series.backgroundColor = Color.argb(50, 0, 255, 255)

        // Ajusta o viewport para focar na parábola gerada
        graph.viewport.isXAxisBoundsManual = true
        graph.viewport.setMinX(minXPlot)
        graph.viewport.setMaxX(maxXPlot)

        // O viewport Y é ajustado dinamicamente para melhor visualização
        val calculatedMinY = series.lowestValueY
        val calculatedMaxY = series.highestValueY
        val marginY = (calculatedMaxY - calculatedMinY) * 0.1
        graph.viewport.isYAxisBoundsManual = true
        graph.viewport.setMinY(calculatedMinY - marginY)
        graph.viewport.setMaxY(calculatedMaxY + marginY)
    }
}
