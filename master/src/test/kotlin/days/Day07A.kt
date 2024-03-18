package days

import assertk.assertThat
import assertk.assertions.isEqualTo
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.CsvSource
import util.filePathToLines

class Day07A {

    enum class Type {
        HighCard,
        OnePair,
        TwoPair,
        ThreeKind,
        FullHouse,
        FourKind,
        FiveKind
    }

    enum class Card {
        `2`,
        `3`,
        `4`,
        `5`,
        `6`,
        `7`,
        `8`,
        `9`,
        T,
        J,
        Q,
        K,
        A
    }

    fun typeOf(hand: String): Type {

        val freqMap = hand.map {
            it to 1
        }
            .groupBy { it.first }
            .toMap()

        if (freqMap.size == 1 && freqMap.count { it.value.size == 5 } == 1)
            return Type.FiveKind

        if (freqMap.size == 2 && freqMap.count { it.value.size == 4 } == 1)
            return Type.FourKind

        if (freqMap.size == 2 && freqMap.count { it.value.size == 3 } == 1 && freqMap.count { it.value.size == 2 } == 1)
            return Type.FullHouse

        if (freqMap.size == 3 && freqMap.count { it.value.size == 3 } == 1 && freqMap.count { it.value.size == 1 } == 2)
            return Type.ThreeKind

        if (freqMap.size == 3 && freqMap.count { it.value.size == 2 } == 2 && freqMap.count { it.value.size == 1 } == 1)
            return Type.TwoPair

        if (freqMap.size == 4 && freqMap.count { it.value.size == 2 } == 1 && freqMap.count { it.value.size == 1 } == 3)
            return Type.OnePair

        if (freqMap.size == 5 && freqMap.count { it.value.size == 1 } == 5)
            return Type.HighCard

        throw Error("Uncategorisable hand $hand")
    }

    class HandDataComparator : Comparator<HandData> {
        override fun compare(o1: HandData, o2: HandData): Int {

            val handComp = o1.type.compareTo(o2.type)

            if (handComp != 0) {
                return handComp
            } else {

                val comparedCards = o1.hand.zip(o2.hand)
                val firstDiff = comparedCards.find { it.first != it.second }!!

                return Card.valueOf(firstDiff.first.toString()).compareTo(Card.valueOf(firstDiff.second.toString()))
            }
        }
    }

    data class HandData(val hand: String, val type: Type, val bid: Int, val first: Card)

    @ParameterizedTest
    @CsvSource(
        value = [
            "src/test/resources/days/07/samp1.txt, 6440",
            "src/test/resources/days/07/prod1.txt, 249483956"
        ]
    )
    fun day07AQuestion1(inputFile: String, expected: Int) {

        val lines = filePathToLines(inputFile)

        val handsInOrder = lines.map {

            val parts = it.split(' ')
            val hand = parts[0]
            val bid = parts[1].toInt()
            val type = typeOf(hand)

            HandData(hand, type, bid, Card.valueOf(hand.substring(0, 1)))
        }
            .sortedWith(HandDataComparator())

        val bidsInOrder = handsInOrder.map { it.bid }

        val winnings = handsInOrder.mapIndexed { index, v ->

            v.bid * (index + 1)
        }

        val totalWinnings = winnings.sum()

        assertThat(totalWinnings).isEqualTo(expected)
    }

}
