package com.syntecxhub.calculator

import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {

    private lateinit var display: TextView
    private var input = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        display = findViewById(R.id.tvExpression)

        val numberIds = listOf(
            R.id.btn0,
            R.id.btn1,
            R.id.btn2,
            R.id.btn3,
            R.id.btn4,
            R.id.btn5,
            R.id.btn6,
            R.id.btn7,
            R.id.btn8,
            R.id.btn9
        )

        numberIds.forEach { id ->
            findViewById<Button>(id).setOnClickListener {
                input += (it as Button).text
                display.text = input
            }
        }

        val operators = mapOf(
            R.id.btnPlus to "+",
            R.id.btnMinus to "-",
            R.id.btnMultiply to "*",
            R.id.btnDivide to "/"
        )

        operators.forEach { (id, operator) ->
            findViewById<Button>(id).setOnClickListener {

                if (
                    input.isNotEmpty() &&
                    !input.last().toString().matches(
                        Regex("[+*/.-]")
                    )
                ) {
                    input += operator
                    display.text = input
                }
            }
        }

        findViewById<Button>(R.id.btnDot).setOnClickListener {

            input += if (
                input.isEmpty() ||
                input.last() in "+-*/"
            ) {
                "0."
            } else {
                "."
            }

            display.text = input
        }

        findViewById<Button>(R.id.btnClear).setOnClickListener {
            clearCalculator()
        }

        findViewById<Button>(R.id.btnDelete).setOnClickListener {

            if (input.isNotEmpty()) {
                input = input.dropLast(1)
            }

            display.text =
                if (input.isEmpty()) "0"
                else input
        }

        findViewById<Button>(R.id.btnEquals).setOnClickListener {
            calculate()
        }
    }

    private fun calculate() {

        try {

            if (input.isBlank()) return

            if (input.last() in "+-*/.") {
                throw IllegalArgumentException()
            }

            val result = evaluateExpression(input)

            input =
                if (result % 1.0 == 0.0) {
                    result.toLong().toString()
                } else {
                    result.toString()
                }

            display.text = input

        } catch (_: ArithmeticException) {

            Toast.makeText(
                this,
                "Cannot divide by zero",
                Toast.LENGTH_SHORT
            ).show()

        } catch (_: Exception) {

            Toast.makeText(
                this,
                "Invalid expression",
                Toast.LENGTH_SHORT
            ).show()
        }
    }

    private fun evaluateExpression(
        expression: String
    ): Double {

        val values = mutableListOf<Double>()
        val operators = mutableListOf<Char>()

        var number = ""

        fun applyOperation() {

            val b = values.removeLast()
            val a = values.removeLast()
            val operator = operators.removeLast()

            val result = when (operator) {

                '+' -> a + b

                '-' -> a - b

                '*' -> a * b

                '/' -> {
                    if (b == 0.0) {
                        throw ArithmeticException()
                    }

                    a / b
                }

                else -> {
                    throw IllegalArgumentException()
                }
            }

            values.add(result)
        }

        fun precedence(operator: Char): Int {
            return if (
                operator == '+' ||
                operator == '-'
            ) {
                1
            } else {
                2
            }
        }

        expression.forEach { character ->

            if (
                character.isDigit() ||
                character == '.'
            ) {

                number += character

            } else {

                if (number.isEmpty()) {
                    throw IllegalArgumentException()
                }

                values.add(number.toDouble())
                number = ""

                while (
                    operators.isNotEmpty() &&
                    precedence(operators.last()) >=
                    precedence(character)
                ) {
                    applyOperation()
                }

                operators.add(character)
            }
        }

        if (number.isEmpty()) {
            throw IllegalArgumentException()
        }

        values.add(number.toDouble())

        while (operators.isNotEmpty()) {
            applyOperation()
        }

        return values.single()
    }

    private fun clearCalculator() {

        input = ""
        display.text = "0"
    }
}
