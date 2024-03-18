package days

import assertk.assertThat
import assertk.assertions.isEqualTo
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.CsvSource
import util.filePathToStream
import java.util.stream.Stream
import kotlin.streams.toList

class Day02 {

    @ParameterizedTest
    @CsvSource(
        value = [
            "src/test/resources/days/02/samp1.txt, 8",
            "src/test/resources/days/02/prod1.txt, 3059"
        ]
    )
    fun day02Question1(inputFile: String, expected: Int) {

        val possibleWith = object {
            val red = 12
            val green = 13
            val blue = 14
        }

        val stream = filePathToStream(inputFile)

        val gameIds = stream.flatMap { line ->

            val parts = "^Game (\\d+):(.+)$".toRegex().matchEntire(line)
            val gameId = parts!!.groupValues[1].toInt()
            val roundsString = parts.groupValues[2]

            val rounds = roundsString.split(';').map { it.trim() }

            val allRoundsValid = rounds.all {

                fun String.findColourAmount(colourName: String): Int {
                    val reg = "(\\d+) $colourName".toRegex().find(this)

                    return reg?.groupValues?.get(1)?.toInt() ?: 0
                }

                val red = it.findColourAmount("red")
                val green = it.findColourAmount("green")
                val blue = it.findColourAmount("blue")

                red <= possibleWith.red && green <= possibleWith.green && blue <= possibleWith.blue
            }

            if (allRoundsValid)
                Stream.of(gameId)
            else
                Stream.empty()

        }.toList()

        println(gameIds)

        val sum = gameIds.sum()

        assertThat(sum).isEqualTo(expected)
    }

    @ParameterizedTest
    @CsvSource(
        value = [
            "src/test/resources/days/02/samp1.txt, 2286",
            "src/test/resources/days/02/prod1.txt, 65371"
        ]
    )
    fun day02Question2(inputFile: String, expected: Int) {

        data class Round(
            val red: Int,
            val green: Int,
            val blue: Int
        )

        val stream = filePathToStream(inputFile)

        val gamePowers = stream.map { line ->

            val parts = "^Game (\\d+):(.+)$".toRegex().matchEntire(line)
            val allRoundsString = parts!!.groupValues[2]

            val roundStrings = allRoundsString.split(';').map { it.trim() }

            val rounds = roundStrings.map {

                fun String.findColourAmount(colourName: String): Int {
                    val reg = "(\\d+) $colourName".toRegex().find(this)

                    return reg?.groupValues?.get(1)?.toInt() ?: 0
                }

                val red = it.findColourAmount("red")
                val green = it.findColourAmount("green")
                val blue = it.findColourAmount("blue")

                Round(red, green, blue)
            }

            val minRed = rounds.maxOf { it.red }
            val minGreen = rounds.maxOf { it.green }
            val minBlue = rounds.maxOf { it.blue }

            val power = minRed * minGreen * minBlue
            power

        }.toList()

        println(gamePowers)

        val sum = gamePowers.sum()

        assertThat(sum).isEqualTo(expected)
    }
}