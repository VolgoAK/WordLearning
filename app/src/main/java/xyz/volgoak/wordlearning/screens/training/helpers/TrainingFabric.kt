package xyz.volgoak.wordlearning.screens.training.helpers


import com.annimon.stream.Collectors
import com.annimon.stream.Stream
import com.attiladroid.data.DataProvider
import com.attiladroid.data.entities.Word

import java.util.ArrayList
import java.util.Collections
import java.util.Comparator
import java.util.LinkedList
import java.util.Queue

import io.reactivex.Single
import xyz.volgoak.wordlearning.screens.training.helpers.TrainingFabric.BOOL_TRAINING
import xyz.volgoak.wordlearning.screens.training.helpers.TrainingFabric.TRANSLATION_WORD
import xyz.volgoak.wordlearning.screens.training.helpers.TrainingFabric.WORD_TRANSLATION
import xyz.volgoak.wordlearning.utils.Optional


/**
 * Created by Alexander Karachev on 07.05.2017.
 */
object TrainingFabric {

    const val WORD_TRANSLATION = 0
    const val TRANSLATION_WORD = 1
    const val BOOL_TRAINING = 3


    fun getSimpleTraining(trainingType: Int, setId: Long,
                          wordsLimit: Int, provider: DataProvider): Optional<Training> {
        return createSimpleTraining(trainingType, setId, wordsLimit, provider)
    }

    fun getBoolTrainingRx(setId: Long, provider: DataProvider): Single<TrainingBool> {
        return Single.create { subscriber -> subscriber.onSuccess(createBoolTraining(setId, provider)) }
    }

    private fun createSimpleTraining(trainingType: Int, setId: Long,
                                     wordsLimit: Int, provider: DataProvider): Optional<Training> {
        val wordList = provider.getTrainingWords(setId)

        val wordGetter: (Word) -> String
        val variantsGetter: (Word) -> String
        val trainedComparator: Comparator<Word>

        if (trainingType == WORD_TRANSLATION) {
            wordGetter =  { it.word }
            variantsGetter = { it.translation }
            trainedComparator = Comparator { w1, w2 -> w1.trainedWt.compareTo(w2.trainedWt) }

        } else if (trainingType == TRANSLATION_WORD) {
            wordGetter =  { it.translation }
            variantsGetter =  { it.word }
            trainedComparator = Comparator { w1, w2 -> w1.trainedTw.compareTo(w2.trainedTw) }
        } else
            throw IllegalArgumentException("incorrect training type")

        if (wordList.size == 0) {
            //no untrained words in a dictionary
            return Optional(null)
        }

        Collections.shuffle(wordList)
        Collections.sort(wordList, trainedComparator)

        val playWords = ArrayList<PlayWord>()

        for (w in wordList) {
            val word = wordGetter(w)
            val translation = variantsGetter(w)

            val varList = provider.getVariants(w.id, 3, setId)
            val vars = arrayOfNulls<String>(3)
            for (a in 0..2) {
                vars[a] = variantsGetter(varList[a])
            }
            val playWord = PlayWord(word, translation, vars, w.id)
            playWords.add(playWord)
            if (playWords.size == wordsLimit) break
        }

        return if (playWords.size == 0) Optional(null) else Optional(Training(playWords, trainingType))
    }

    private fun createBoolTraining(setId: Long, provider: DataProvider): TrainingBool {
       /* val words = provider.getTrainingWords(setId)

        val variants = Stream.of(words).map<String>(Function<Word, String> { it.getTranslation() })
                .collect<LinkedList<String>, Any>(Collectors.toCollection(Supplier<LinkedList<String>> { LinkedList() }))
        Collections.shuffle(words)

        val playWords = Stream.of(words).map<PlayWord>(Function<Word, PlayWord> { PlayWord(it) })
                .map { p ->
                    p.vars = arrayOf(variants.poll())
                    p
                }
                .toList()

        Collections.shuffle(playWords)*/

        return TrainingBool(listOf(), BOOL_TRAINING)
    }

    internal interface GetWord {
        fun getString(word: Word): String
    }
}
