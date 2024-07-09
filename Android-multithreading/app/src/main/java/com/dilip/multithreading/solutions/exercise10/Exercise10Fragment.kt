package com.dilip.multithreading.solutions.exercise10

import android.app.Activity
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.Button
import android.widget.EditText
import android.widget.TextView

import com.dilip.multithreading.R
import com.dilip.multithreading.common.BaseFragment

import java.math.BigInteger
import androidx.fragment.app.Fragment
import com.dilip.multithreading.DefaultConfiguration
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

class SolutionExercise10Fragment : BaseFragment() {

    private lateinit var edtArgument: EditText
    private lateinit var edtTimeout: EditText
    private lateinit var btnStartWork: Button
    private lateinit var txtResult: TextView

    private lateinit var computeFactorialUseCase: ComputeFactorialUseCase

    private var job : Job? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        computeFactorialUseCase = ComputeFactorialUseCase()
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_exercise_10, container, false)

        view.apply {
            edtArgument = findViewById(R.id.edt_argument)
            edtTimeout = findViewById(R.id.edt_timeout)
            btnStartWork = findViewById(R.id.btn_compute)
            txtResult = findViewById(R.id.txt_result)
        }

        btnStartWork.setOnClickListener { _ ->
            if (edtArgument.text.toString().isEmpty()) {
                return@setOnClickListener
            }

            txtResult.text = ""
            btnStartWork.isEnabled = false


            val imm = requireContext().getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.hideSoftInputFromWindow(btnStartWork.windowToken, 0)

            val argument = Integer.valueOf(edtArgument.text.toString())

            job = CoroutineScope(Dispatchers.Main).launch {
                when (val result = computeFactorialUseCase.computeFactorial(argument, getTimeout())) {
                    is ComputeFactorialUseCase.Result.Success -> onFactorialComputed(result.result)
                    is ComputeFactorialUseCase.Result.Timeout -> onFactorialComputationTimedOut()
                }
            }
        }

        return view
    }

    override fun onStart() {
        super.onStart()
    }

    override fun onStop() {
        super.onStop()
        job?.apply { cancel() }
    }

    override fun getScreenTitle(): String {
        return "Exercise 10"
    }

    fun onFactorialComputed(result: BigInteger) {
        txtResult.text = result.toString()
        btnStartWork.isEnabled = true
    }

    fun onFactorialComputationTimedOut() {
        txtResult.text = "Computation timed out"
        btnStartWork.isEnabled = true
    }

    private fun getTimeout() : Int {
        var timeout: Int
        if (edtTimeout.text.toString().isEmpty()) {
            timeout = MAX_TIMEOUT_MS
        } else {
            timeout = Integer.valueOf(edtTimeout.text.toString())
            if (timeout > MAX_TIMEOUT_MS) {
                timeout = MAX_TIMEOUT_MS
            }
        }
        return timeout
    }
    
    companion object {
        fun newInstance(): Fragment {
            return SolutionExercise10Fragment()
        }
        private const val MAX_TIMEOUT_MS = DefaultConfiguration.DEFAULT_FACTORIAL_TIMEOUT_MS
    }
}