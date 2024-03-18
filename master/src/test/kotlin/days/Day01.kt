package days

import assertk.assertThat
import assertk.assertions.isEqualTo
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.CsvSource
import util.filePathToLines

class Day01 {

    @ParameterizedTest
    @CsvSource(
        value = [
            "src/test/resources/days/01/samp1.txt, 142",
            "src/test/resources/days/01/prod1.txt, 54927"
        ]
    )
    fun day01Question1(inputFile: String, expected: Int) {

        val lines = filePathToLines(inputFile)

        val numbersFromLines = lines.map { line ->

            val firstNum = line.first { it.isDigit() }
            val lastNum = line.last { it.isDigit() }

            val s = "$firstNum$lastNum"
            val theNumber = s.toInt()

            theNumber
        }

        val sum = numbersFromLines.sum()

        assertThat(sum).isEqualTo(expected)
    }

    @ParameterizedTest
    @CsvSource(
        value = [
            "src/test/resources/days/01/samp2.txt, 281",
            "src/test/resources/days/01/prod1.txt, 54581"
        ]
    )
    fun day01Question2(inputFile: String, expected: Int) {

        val lines = filePathToLines(inputFile)

        val myNumWords = mapOf(
            "1" to 1,
            "2" to 2,
            "3" to 3,
            "4" to 4,
            "5" to 5,
            "6" to 6,
            "7" to 7,
            "8" to 8,
            "9" to 9,
            "one" to 1,
            "two" to 2,
            "three" to 3,
            "four" to 4,
            "five" to 5,
            "six" to 6,
            "seven" to 7,
            "eight" to 8,
            "nine" to 9
        )

        val myBackwardsWords = myNumWords.map { e ->
            e.key.reversed() to e.value
        }.toMap()

        fun String.findFirst(options: List<String>): String {

            val values = this.flatMapIndexed { index, _ ->

                options.flatMap {

                    if (index + it.length > this.length) {
                        emptyList()
                    } else {

                        val tryString = this.substring(index, index + it.length)

                        if (tryString == it) {
                            listOf(it)
                        } else {
                            emptyList()
                        }
                    }
                }
            }

            return values.first()
        }

        val numbersFromLines = lines.map { line ->

            val firstNum = line.findFirst(myNumWords.keys.toList())

            val lastNum = line.reversed().findFirst(
                myBackwardsWords.keys.toList()
            )
                .reversed()

            val firstNumber = myNumWords[firstNum]
            val lastNumber = myNumWords[lastNum]

            val s = "$firstNumber$lastNumber"
            val theNumber = s.toInt()

            theNumber
        }

        val sum = numbersFromLines.sum()

        assertThat(sum).isEqualTo(expected)
    }
}