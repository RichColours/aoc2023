package days

import assertk.assertThat
import assertk.assertions.isEqualTo
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.CsvSource
import util.*
import kotlin.math.exp
import kotlin.streams.asStream

class Day06 {

    @ParameterizedTest
    @CsvSource(
        value = [
            "src/test/resources/days/06/samp1.txt, 4 8 9, 288",
            "src/test/resources/days/06/samp2.txt, 4, 4",
            "src/test/resources/days/06/prod1.txt, 36 58 57 37, 4403592"
        ]
    )
    fun day06Question1(inputFile: String, expectedPerRace: String, expected: Int) {

        val lines = filePathToLines(inputFile)
        val expectedPerRace = expectedPerRace.split(' ').map { it.toInt() }

        val raceTimes = lines[0].split(':')[1].trim().replace("\\s+".toRegex(), " ").split(' ').map { it.toInt() }
        val raceBests = lines[1].split(':')[1].trim().replace("\\s+".toRegex(), " ").split(' ').map { it.toInt() }

        val raceWinPossibs = raceTimes.mapIndexed {i, raceTime ->

            val usableRange = 1..< (raceTime)
            val rangeWidth = usableRange.width()

            val holdTimes = usableRange

            val distancesTravelled = holdTimes.map { holdTime ->

                val travelTime = raceTime - holdTime
                val distance = holdTime * travelTime

                distance
            }

            val betterDistances = distancesTravelled.filter { it > raceBests[i] }

            betterDistances
        }

        val raceWinPossibsCounts = raceWinPossibs.map { it.size }

        assertThat(raceWinPossibsCounts).isEqualTo(expectedPerRace)

        val margin = raceWinPossibsCounts.fold(1) { acc: Int, v: Int -> acc * v}

        assertThat(margin).isEqualTo(expected)
    }

    @ParameterizedTest
    @CsvSource(
        value = [
            //"src/test/resources/days/06/samp2-1.txt, 71503, 71503",
            "src/test/resources/days/06/prod2.txt, 38017587, 38017587",
        ]
    )
    fun day06Question2(inputFile: String, expectedPerRace: String, expected: Int) {

        val lines = filePathToLines(inputFile)
        val expectedPerRace = expectedPerRace.split(' ').map { it.toInt() }

        val raceTimes = lines[0].split(':')[1].trim().replace("\\s+".toRegex(), " ").split(' ').map { it.toLong() }
        val raceBests = lines[1].split(':')[1].trim().replace("\\s+".toRegex(), " ").split(' ').map { it.toLong() }

        val raceWinPossibCounts = raceTimes.mapIndexed {i, raceTime ->

            val usableRange = 1..< (raceTime)
            val rangeWidth = usableRange.width()

            val holdTimes = usableRange

            val bestDistance = raceBests[i]

            val numDistancesTravelled = holdTimes.count { holdTime ->

                val travelTime = raceTime - holdTime
                val distance = holdTime * travelTime

                distance > bestDistance
            }

            numDistancesTravelled
        }

        assertThat(raceWinPossibCounts).isEqualTo(expectedPerRace)

        val margin = raceWinPossibCounts.fold(1) { acc: Int, v: Int -> acc * v}

        assertThat(margin).isEqualTo(expected)
    }
}
