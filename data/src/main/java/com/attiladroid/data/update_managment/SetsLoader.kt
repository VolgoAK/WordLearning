package com.attiladroid.data.update_managment

import android.content.Context
import android.os.Environment
import android.widget.Toast

import com.attiladroid.data.DataProvider
import com.attiladroid.data.DataContract
import com.attiladroid.data.entities.Dictionary
import com.attiladroid.data.entities.Link
import com.attiladroid.data.entities.Theme
import com.attiladroid.data.entities.Word
import com.attiladroid.data.entities.Set
import com.google.gson.Gson
import com.google.gson.GsonBuilder

import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.io.IOException
import java.io.InputStream
import java.nio.channels.FileChannel


/**
 * Created by Alexander Karachev on 07.05.2017.
 */

/**
 * This class is using only for creating sqlite database on debug devices.
 * It isn' well optimized  and shouldn't be.
 * !!!Run this shit only on debug devices and never use it in production!!!
 */
object SetsLoader{

    const val TAG = "SetsLoader"
    const val DATA_ID_ATTR = "data_id"
    const val DATA_SOURCE_ATTR = "source"

    const val LOADED_SETS_PREF = "loaded_sets"

    fun insertTestBase(dataProvider: DataProvider, context: Context) {

        dataProvider.insertWord(Word(word = "Hello", translation =  "Привет"))
        dataProvider.insertWord(Word(word = "Name",translation = "Имя"))
        dataProvider.insertWord(Word(word = "Human",translation = "Человек"))
        dataProvider.insertWord(Word(word = "He",translation = "Он"))
        dataProvider.insertWord(Word(word = "She",translation = "Она"))
        dataProvider.insertWord(Word(word = "Where",translation = "Где"))
        dataProvider.insertWord(Word(word = "When", translation ="Когда"))
        dataProvider.insertWord(Word(word = "Why", translation ="Почему"))
        dataProvider.insertWord(Word(word = "Who",translation = "Кто"))
        dataProvider.insertWord(Word(word = "What",translation = "Что"))
        dataProvider.insertWord(Word(word = "Time",translation = "Время"))
        dataProvider.insertWord(Word(word = "Country",translation = "Страна"))

        try {
            val inputStream = context.assets.open("my.json")
            val size = inputStream.available()
            val buffer = ByteArray(size)
            inputStream.read(buffer)

            val json = String(buffer)
            createAndInsertDictionary(dataProvider, json)
        } catch (ex: IOException) {
            ex.printStackTrace()
        }

    }

    internal fun createAndInsertDictionary(provider: DataProvider, dictionaryString: String): SetsUpdatingInfo {
        val gsonBuilder = GsonBuilder()
        gsonBuilder.excludeFieldsWithoutExposeAnnotation()
        gsonBuilder.disableHtmlEscaping()
        gsonBuilder.setPrettyPrinting()
        val gson = gsonBuilder.create()

        val dictionary = gson.fromJson(dictionaryString, Dictionary::class.java)

        return insertSetsIntoDb(provider, dictionary)
    }

    fun insertSetsIntoDb(provider: DataProvider, dictionary: Dictionary): SetsUpdatingInfo {
        val info = SetsUpdatingInfo()
        val themes = dictionary.themes

        if (themes != null && themes.isNotEmpty()) {
            provider.insertThemes(*themes.toTypedArray())
        }

        val sets = dictionary.sets
        for (set in sets!!) {
            set.visibitity = DataContract.Sets.VISIBLE

            val setId = provider.insertSet(set)
            info.onSetAdded(setId)
            val words = set.words

            for (word in words) {
                info.incrementWordsAdded()
                val dictionaryWord = provider.getWord(word.word, word.translation)
                val wordId: Long
                if (dictionaryWord != null) {
                    wordId = dictionaryWord.id
                } else {
                    word.translation = word.translation.capitalize()
                    word.word = word.word.capitalize()
                    wordId = provider.insertWord(word)
                }
                val link = Link()
                link.wordId = wordId
                link.idOfSet = setId
                provider.insertLink(link)
            }
        }

        return info
    }

    fun exportDbToFile(context: Context, dbName: String) {
        val sd = Environment.getExternalStorageDirectory()
        val data = Environment.getDataDirectory()
        var source: FileChannel? = null
        var destination: FileChannel? = null
        val currentDBPath = context.getDatabasePath(dbName).toString()
        val currentDB = File(currentDBPath)
        val backupDB = File(sd, dbName)
        try {
            source = FileInputStream(currentDB).channel
            destination = FileOutputStream(backupDB).channel
            destination!!.transferFrom(source, 0, source!!.size())
            source.close()
            destination.close()
            Toast.makeText(context, "DB Exported!", Toast.LENGTH_LONG).show()
        } catch (e: IOException) {
            e.printStackTrace()
        }

    }

    fun importDbFromAsset(context: Context, dbName: String): Boolean {
        var dbIs: InputStream? = null
        var dbOus: FileOutputStream? = null
        val dbFileTarget = context.getDatabasePath(dbName)
        val data = Environment.getDataDirectory()
        try {
            dbFileTarget.parentFile.mkdirs()
            dbOus = FileOutputStream(dbFileTarget)
            dbIs = context.assets.open(dbName)

            val buffer = ByteArray(1024)
            var read = dbIs!!.read(buffer)

            while (read != -1) {
                dbOus.write(buffer)
                read = dbIs.read(buffer)
            }

            dbIs.close()
            dbOus.flush()
            dbOus.close()

        } catch (ex: IOException) {
            ex.printStackTrace()
        }

        return dbFileTarget.exists()
    }
}
