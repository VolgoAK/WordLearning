package xyz.volgoak.wordlearning.screens.training.helpers


import com.attiladroid.data.DataProvider
import com.attiladroid.data.entities.Word
import io.reactivex.Single
import java.lang.RuntimeException
import java.util.*


/**
 * Created by Alexander Karachev on 07.05.2017.
 */
object TrainingFabric {

    const val WORD_TRANSLATION = 0
    const val TRANSLATION_WORD = 1
    const val BOOL_TRAINING = 3


    fun getSimpleTraining(trainingType: Int, setId: Long,
                          wordsLimit: Int, provider: DataProvider): Training? {
        return createSimpleTraining(trainingType, setId, wordsLimit, provider)
    }

    fun getBoolTrainingRx(setId: Long, provider: DataProvider): Single<TrainingBool> {
        return Single.create { subscriber -> subscriber.onSuccess(createBoolTraining(setId, provider)) }
    }

    fun getBoolTraining(setId: Long, provider: DataProvider): TrainingBool {
        return createBoolTraining(setId, provider)
    }

    private fun createSimpleTraining(trainingType: Int, setId: Long,
                                     wordsLimit: Int, provider: DataProvider): Training? {
        if(trainingType != WORD_TRANSLATION && trainingType != TRANSLATION_WORD)
            throw RuntimeException("Unexpected training type $trainingType")

        val wordGetter = if (trainingType == WORD_TRANSLATION) Word::word else Word::translation
        val variantsGetter = if (trainingType == WORD_TRANSLATION) Word::translation else Word::word
        val compareField = if(trainingType == WORD_TRANSLATION) Word::trainedWt else Word::trainedTw

        val playWords = provider.getTrainingWords(setId)
                .shuffled()
                .sortedBy(compareField)
                .take(wordsLimit)
                .map { tw ->
                    val word = wordGetter(tw)
                    val translation = variantsGetter(tw)

                    val vars = provider.getVariants(tw.id, 3, setId)
                            .map(variantsGetter)
                            .toTypedArray()

                    PlayWord(word, translation, vars, tw.id)
                }

        return if (playWords.isEmpty()) null else Training(playWords, trainingType)
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

        val words = provider.getTrainingWords(setId)

        val variants = words.map { it.translation }
                .shuffled()
                .zip(words)
                .map { PlayWord(it.second.word, it.second.translation, arrayOf(it.first), it.second.id) }

        return TrainingBool(variants)
    }
}
