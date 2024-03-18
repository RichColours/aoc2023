package days

import assertk.assertThat
import assertk.assertions.isEqualTo
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.CsvSource
import util.filePathToLines
import util.timed
import kotlin.math.pow

class Day04 {

    @ParameterizedTest
    @CsvSource(
        value = [
            "src/test/resources/days/04/samp1.txt, 13",
            "src/test/resources/days/04/prod1.txt, 21568"
        ]
    )
    fun day04Question1(inputFile: String, expected: Int) {

        data class Card(
            val cardNumber: Int,
            val myNumbers: List<Int>,
            val winningNumbers: List<Int>
        )

        val lines = filePathToLines(inputFile)

        val cards = lines.map {
            val match = "Card\\s+(\\d+): (.+) \\| (.+)".toRegex().matchEntire(it)!!

            val cardNum = match.groupValues[1].toInt()
            val myNums = match.groupValues[2].trim().split("\\s+".toRegex()).map { it.toInt() }
            val winNums = match.groupValues[3].trim().split("\\s+".toRegex()).map { it.toInt() }

            Card(cardNum, myNums, winNums)
        }

        val cardPoints = cards.map {

            val matchedNumbers = it.myNumbers.toSet().intersect(it.winningNumbers.toSet())

            if (matchedNumbers.isNotEmpty())
                (1 * 2.0.pow(matchedNumbers.size.toDouble() - 1)).toInt()
            else
                0
        }

        val sum = cardPoints.sum()
        assertThat(sum).isEqualTo(expected)
    }

    @ParameterizedTest
    @CsvSource(
        value = [
            "src/test/resources/days/04/samp1.txt, 30",
            //"src/test/resources/days/04/samp2.txt, 5",
            "src/test/resources/days/04/prod1.txt, 11827296"
        ]
    )
    fun day04Question2(inputFile: String, expected: Int) {

        data class Card(
            val cardNumber: Int,
            val myNumbers: List<Int>,
            val winningNumbers: List<Int>,
            val wins: Int
        ) {
            override fun equals(other: Any?): Boolean {
                return cardNumber.equals(other)
            }

            override fun hashCode(): Int {
                return cardNumber.hashCode()
            }
        }

        val lines = filePathToLines(inputFile)

        val cards = timed {
            lines.map {
                val match = "Card\\s+(\\d+): (.+) \\| (.+)".toRegex().matchEntire(it)!!

                val cardNum = match.groupValues[1].toInt()
                val myNums = match.groupValues[2].trim().split("\\s+".toRegex()).map { it.toInt() }
                val winNums = match.groupValues[3].trim().split("\\s+".toRegex()).map { it.toInt() }
                val wins = myNums.toSet().intersect(winNums.toSet()).size

                Card(cardNum, myNums, winNums, wins)
            }
        }

        val cardProcessingsRemaining = cards.associateWith { 1 }.toMutableMap()

        val cardHits = cards.associateWith { 0 }.toMutableMap()

        cardProcessingsRemaining.forEach { (card, n) ->

            val cardIndex = card.cardNumber - 1
            val copyIndexes = cardIndex + 1..cardIndex + card.wins

            cardHits[card] = cardHits[card]!! + n

            (0..<n).forEach {

                copyIndexes.forEach {
                    cardProcessingsRemaining[cards[it]] = cardProcessingsRemaining[cards[it]]!! + 1
                }
            }
        }

        //println(cardHits.map { it.key.cardNumber to it.value })

        val sum = cardHits.values.sum()
        assertThat(sum).isEqualTo(expected)
    }
}