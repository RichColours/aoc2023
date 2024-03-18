package days

import assertk.assertThat
import assertk.assertions.hasSize
import assertk.assertions.isEqualTo
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.CsvSource
import util.filePathToLines

class Day09 {

    @ParameterizedTest
    @CsvSource(
        value = [
            "src/test/resources/days/09/samp1.txt, 18|28|68, 114",
            "src/test/resources/days/09/prod1.txt, 143|119197|1251655|155966|5614|32533964|992346|302179|21551436|148253|14478936|16738922|-108934|39955558|26040058|7443|313|-53|6352803|25162744|21176515|123|10510694|9364876|880049|170605|-77188|-104|2112|23928028|14043640|815741|16994|446889|1958146|25280200|35665365|1302|17876256|5413829|13862848|21437461|98111|1896|-8123|2265951|19159425|9331163|-59567|18868376|420522|444500|64396|19335982|39|2847|10199505|279|14117|2875517|2065|-8203|27219380|-125899|15261054|24115510|22336887|23795756|11336795|-75526|14261801|423176|20424366|18647054|2164|17263663|517272|21305842|24638109|28509812|16383820|23492045|27087196|10070066|-103|12619583|493584|3389|28025747|-58914|11773434|40983983|15868119|433567|17136982|12841956|13291028|30743445|1443|18165598|27129930|64607|196|9762368|20184681|24095478|7047142|345330|3394500|22630903|-747201|13180954|998920|-4829|-1644526|1267|21951890|-424801|50792|23020|888716|22021714|30134919|10064478|32712984|2662216|18448313|370339|2597419|33845751|898|6489942|1471345|205321|954144|5921778|95008|-310862|5812102|2669|189|21748|2109138|3510|14427|13277713|27325634|109676|15036208|38638|3345718|1437402|29283381|17220198|37204194|12932557|14424|225614|22403742|-564591|5826|22177101|33726505|228|254100|155980|7257045|47765|463678|11290646|3975422|1012|296|139904|13810978|121624|-8857|5819456|15584089|8487242|-191715|21928985|13604878|-7232|3658264|6481232|431784|35932753|20526047|19601498|19350764|6229671|9169535|1256870|30196744|1045585|1294932|27674093|21276358|654870, 1887980197"
        ]
    )
    fun day09Question1(inputFile: String, expectedIntermed: String, expected: Int) {

        val historyLines = filePathToLines(inputFile)

        val expectedIntermed = expectedIntermed.split('|').map { it.toInt() }

        val historiesValues = historyLines.map {
            it.split(' ').map { it.toInt() }
        }

        tailrec fun deriveDiffs(values: List<Int>, priorSets: List<List<Int>>): List<List<Int>> {

            val diffs = values.flatMapIndexed { index, t ->
                if (index == 0) {
                    emptyList()
                } else {
                    listOf(
                        t - values[index - 1]
                    )
                }
            }

            val newDiffsTree = priorSets.plus<List<Int>>(diffs)

            return if (diffs.all { it == 0 })
                newDiffsTree
            else
                deriveDiffs(diffs, newDiffsTree)
        }

        val historyDiffTrees = historiesValues
            .map {
                deriveDiffs(it, listOf(it))
            }

        val historyFinals = historyDiffTrees.map {

            val finals = it.map { it.last() }
            finals.sum()
        }

        val answer = historyFinals.sum()

        assertThat(expectedIntermed).hasSize(historyDiffTrees.size)
        //println(historyFinals.joinToString("|"))
        assertThat(historyFinals).isEqualTo(expectedIntermed)
        assertThat(answer).isEqualTo(expected)
    }

    @ParameterizedTest
    @CsvSource(
        value = [
            "src/test/resources/days/09/samp2.txt, 5, 5",
            "src/test/resources/days/09/prod1.txt, 11|12|-2|8|15|6|3|9|-5|-5|1|-1|10|8|7|-4|5|13|6|-1|-4|13|-4|10|5|6|10|6|11|3|10|14|-1|3|3|-3|3|4|5|-4|4|0|2|15|6|6|15|-5|9|10|14|1|2|-3|6|-2|-3|15|15|7|8|14|4|7|4|-1|12|10|-3|11|-5|-5|4|5|8|7|8|15|15|-3|8|3|13|4|7|-2|14|1|5|12|-3|1|6|2|15|12|14|0|2|8|15|4|-2|-1|4|5|3|-4|-1|15|-4|13|10|0|-4|2|4|8|5|8|15|13|12|15|9|-4|-1|2|0|11|-4|-4|-4|-5|15|5|1|-2|10|7|13|1|9|1|6|4|13|6|7|6|4|10|-2|2|11|11|14|15|6|-5|-4|-4|10|8|0|0|-3|14|6|7|11|11|10|6|-3|8|-2|5|9|5|0|14|-1|-5|5|-1|1|8|-4|13|-5|1|1|-1|7|2|1|-1|14|7, 990"
        ]
    )
    fun day09Question2(inputFile: String, expectedIntermed: String, expected: Int) {

        val historyLines = filePathToLines(inputFile)

        val expectedIntermed = expectedIntermed.split('|').map { it.toInt() }

        val historiesValues = historyLines.map {
            it.split(' ').map { it.toInt() }
        }

        tailrec fun deriveDiffs(values: List<Int>, priorSets: List<List<Int>>): List<List<Int>> {

            val diffs = values.flatMapIndexed { index, t ->
                if (index == 0) {
                    emptyList()
                } else {
                    listOf(
                        t - values[index - 1]
                    )
                }
            }

            val newDiffsTree = priorSets.plus<List<Int>>(diffs)

            return if (diffs.all { it == 0 })
                newDiffsTree
            else
                deriveDiffs(diffs, newDiffsTree)
        }

        val historyDiffTrees = historiesValues
            .map {
                deriveDiffs(it, listOf(it))
            }

        val historyFinals = historyDiffTrees.map {

            val finals = it.map { it.first() }

            val final = finals.foldRight(0) { v, acc -> v - acc }

            final
        }

        val answer = historyFinals.sum()

        assertThat(expectedIntermed).hasSize(historyDiffTrees.size)
        //println(historyFinals.joinToString("|"))
        assertThat(historyFinals).isEqualTo(expectedIntermed)
        assertThat(answer).isEqualTo(expected)
    }
}
