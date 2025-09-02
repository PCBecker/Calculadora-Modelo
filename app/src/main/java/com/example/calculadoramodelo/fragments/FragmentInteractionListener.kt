package com.example.calculadoramodelo.fragments

// com.example.calculadoramodelo.MainActivity.kt (ou FragmentInteractionListener.kt, se for um arquivo separado)

import androidx.fragment.app.Fragment // Certifique-se de importar Fragment


interface FragmentInteractionListener {
    fun onNumberClicked(number: String)
    fun onOperatorClicked(operator: String)
    fun onClearClicked()
    fun onLoadFragment(fragment: Fragment, tag: String)
    fun onCleanFragmentVisors(fragmentTag: String)
}
