package com.example.calculadoramodelo.fragments

import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.calculadoramodelo.databinding.FragmentCubicaBinding
import com.jjoe64.graphview.GraphView
import com.jjoe64.graphview.GridLabelRenderer
import com.jjoe64.graphview.helper.StaticLabelsFormatter
import com.jjoe64.graphview.series.DataPoint
import com.jjoe64.graphview.series.LineGraphSeries
import kotlin.math.pow

class CubicaFragment : Fragment() {
    private var _binding: FragmentCubicaBinding? = null
    private val binding get() = _binding!!

    private var a: Double = 0.0
    private var b: Double = 0.0
    private var c: Double = 0.0
    private var d: Double = 0.0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            a = it.getDouble("gab1", 0.0)
            b = it.getDouble("gab2", 0.0)
            c = it.getDouble("gab3", 0.0)
            d = it.getDouble("gab4", 0.0)
        }
        Log.d("CubicaFragment", "Coeficientes recebidos: a=$a, b=$b, c=$c, d=$d")
    }
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentCubicaBinding.inflate(inflater, container, false)
        return binding.root
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val graph = binding.cubicGraph
        //graph.removeAllSeries()
        generateCubicGraph(a, b, c, d, graph)

        // Personalizações do gráfico
        graph.title = "Equação: ${a}x³ + ${b}x² + ${c}x + $d"
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

        graph.viewport.isXAxisBoundsManual = true
        //graph.viewport.minX = -5.0 // Defina o limite mínimo do eixo X
        //graph.viewport.maxX = 5.0  // Defina o limite máximo do eixo X

        graph.viewport.isYAxisBoundsManual = true
        //graph.viewport.minY = -10.0 // Defina o limite mínimo do eixo Y
        //graph.viewport.maxY = 10.0  // Defina o limite máximo do eixo Y
        // Você pode ajustar esses valores com base nos valores esperados de Y para o seu gráfico.
        // Por exemplo, se a função for muito "alta" ou "baixa", ajuste minY/maxY.

        // Configurações de viewport (já existentes)
        graph.viewport.isScalable = true
        graph.viewport.isScrollable = true

        // 3. Aparência da Linha do Gráfico (Linha Cúbica)
        val series = graph.series.firstOrNull() as? LineGraphSeries<*>
        series?.let {
            it.color = Color.CYAN
            it.thickness = 10
            it.isDrawBackground = false
            it.backgroundColor = Color.argb(50, 0, 255, 255)
            it.isDrawDataPoints = true
            it.dataPointsRadius = 6f
            it.setCustomPaint(null)
        }
    }
    private fun generateCubicGraph(a: Double, b: Double, c: Double, d: Double, graph: GraphView) {
        val series = LineGraphSeries<DataPoint>()
        val minX = -15.0 // Intervalo para geração dos pontos (pode ser maior que o viewport inicial)
        val maxX = 15.0
        val numPoints = 500

        for (i in 0..numPoints) {
            val x = minX + (i / numPoints.toDouble()) * (maxX - minX)
            val y = a * x.pow(3) + b * x.pow(2) + c * x + d
            series.appendData(DataPoint(x, y), true, numPoints + 1)
        }
        graph.addSeries(series)
    }
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}