package com.example.calculadoramodelo.fragments

import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.calculadoramodelo.databinding.FragmentGrafico2Binding // <<<< VERIFIQUE ESTE IMPORT
// Assumindo que seu XML do gráfico é 'fragment_grafico2.xml'
// Se for 'fragment_grafico_graphview.xml', o import seria:
// import com.example.calculadoramodelo.databinding.FragmentGraficoGraphviewBinding

import com.jjoe64.graphview.GraphView
import com.jjoe64.graphview.GridLabelRenderer
import com.jjoe64.graphview.helper.StaticLabelsFormatter
import com.jjoe64.graphview.series.DataPoint
import com.jjoe64.graphview.series.LineGraphSeries

class Grafico2Fragment : Fragment() {

    // Use View Binding para acessar a GraphView (e outras views)
    private var _binding: FragmentGrafico2Binding? = null // Certifique-se do nome do binding
    private val binding get() = _binding!!

    // Variáveis para armazenar os valores recebidos (podem ser inicializadas aqui)
    private var a: Double = 0.0
    private var b: Double = 0.0
    private var c: Double = 0.0

    // Recupera os argumentos no onCreate (antes da view ser criada)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            a = it.getDouble("gab1", 0.0)
            b = it.getDouble("gab2", 0.0)
            c = it.getDouble("gab3", 0.0)
        }
    }
    // ONDE O BINDING É INFLADO
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentGrafico2Binding.inflate(inflater, container, false) // <<<< Inicializa o binding
        return binding.root // Retorna a View raiz do binding
    }
    // ONDE AS VIEWS SÃO ACESSADAS E CONFIGURADAS
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        // Agora você pode acessar o GraphView através do binding
        // Não use 'findViewById(R.id.graph)' se estiver usando binding para a mesma view!
        val graph = binding.graph // <<<< ACESSE O GRAPHDVIEW AQUI
        graph.removeAllSeries()

        generateQuadraticGraph(a, b, c, graph) // Passe o graph como parâmetro
        // Personalizações do gráfico
        // Personalizações do gráfico
        graph.title = "Equação: ${a}x² + ${b}x + $c"
        graph.titleTextSize = 50f
        graph.titleColor = Color.WHITE

        val gridLabel = graph.gridLabelRenderer
        // Eixo X
        gridLabel.horizontalAxisTitle = "Valor de X"
        gridLabel.horizontalAxisTitleTextSize = 32f
        gridLabel.horizontalAxisTitleColor = Color.WHITE
        gridLabel.labelsSpace = 8

        // Eixo Y
        gridLabel.verticalAxisTitle = "Valor de Y (Resultado)"
        gridLabel.verticalAxisTitleTextSize = 30f
        gridLabel.verticalAxisTitleColor = Color.WHITE

        // Cor e tamanho dos rótulos (números) nos eixos
        gridLabel.labelsSpace = 6
        gridLabel.horizontalLabelsColor = Color.WHITE
        gridLabel.verticalLabelsColor = Color.WHITE
        gridLabel.textSize = 40f

        // Cor e espessura das linhas de grade
        gridLabel.gridColor = Color.DKGRAY
        gridLabel.verticalLabelsColor = Color.WHITE

        // --- MELHORIAS PARA O EIXO X E RAÍZES ---
        // Removido o 'setHumanRounding' e 'numHorizontalLabels'
        // para evitar conflito com os rótulos manuais.

        // CORREÇÃO: Força a exibição de rótulos específicos, incluindo o 0.0
        val labels = arrayOf("-5.0", "-2.5", "0.0", "2.5", "5.0")
        val labelsFormatter = StaticLabelsFormatter(graph)
        labelsFormatter.setHorizontalLabels(labels)
        gridLabel.labelFormatter = labelsFormatter

        // As linhas para destacar as linhas de zero foram removidas para resolver o erro de compilação.
        // Se precisar dessa funcionalidade, verifique a versão da sua biblioteca GraphView.

        gridLabel.gridStyle = GridLabelRenderer.GridStyle.BOTH

        // --- CONFIGURAÇÃO DO VIEWPORT INICIAL PARA FOCO NAS RAÍZES ---
        // Certifique-se que a sua dependência da GraphView está na versão 4.2.2 ou superior
        // para que estas propriedades funcionem.
        graph.viewport.isXAxisBoundsManual = true


        graph.viewport.isYAxisBoundsManual = true


        // Configurações de viewport (já existentes)
        graph.viewport.isScalable = true
        graph.viewport.isScrollable = true

        // 3. Aparência da Linha do Gráfico (Linha Cúbica)
        val series = graph.series.firstOrNull() as? LineGraphSeries<*>
        series?.let {
            it.color = Color.RED
            it.thickness = 6
            it.isDrawBackground = true
            it.backgroundColor = Color.argb(50, 0, 255, 255)
            it.isDrawDataPoints = true
            it.dataPointsRadius = 8f
            it.setCustomPaint(null)
        }
    }
    private fun generateQuadraticGraph(a: Double, b: Double, c: Double, graph: GraphView) {
        val series = LineGraphSeries<DataPoint>()
        for (i in 0..100) {
            val x = (i / 100.0) * 20.0 - 10.0
            val y = a * x * x + b * x + c
            series.appendData(DataPoint(x, y), true, 101)
        }
        graph.addSeries(series)
    }

    // IMPORTANTE: Limpe o binding em onDestroyView para evitar vazamentos de memória
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}