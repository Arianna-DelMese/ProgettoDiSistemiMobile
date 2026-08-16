package com.example.progettodisistemimobile.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Database(
    entities = [
        Utente::class,
        Lega::class,
        UtenteInLega::class,
        Cantante::class,
        ComposizioneSquadra::class,
        Bundle::class,
        OffertaUtente::class
    ],
    version = 1,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun appDao(): AppDao

    private class AppDatabaseCallback(
        private val scope: CoroutineScope
    ) : Callback() {

        override fun onCreate(db: SupportSQLiteDatabase) {
            super.onCreate(db)
            INSTANCE?.let { database ->
                scope.launch(Dispatchers.IO) {
                    populateDatabase(database.appDao())
                }
            }
        }

        suspend fun populateDatabase(dao: AppDao) {
            // --- POPOLAMENTO CANTANTI ---
            val cantanti = listOf(
                Cantante("Marco Mengoni", "Due Vite", 20, 0, null, null),
                Cantante("Lazza", "Cenere", 18, 0, null, null),
                Cantante("Mr. Rain", "Supereroi", 15, 0, null, null),
                // Qui puoi aggiungere tutti i 30 cantanti di Sanremo
            )
            cantanti.forEach { dao.insertCantante(it) }

            // --- POPOLAMENTO BUNDLE ---
            val bundles = listOf(
                Bundle(1, 10, 1),   // 10 token per 1€
                Bundle(2, 25, 2),
                Bundle(3, 50, 4),
                Bundle(4, 100, 8),
                Bundle(5, 200, 15),
                Bundle(6, 500, 35)
            )
            bundles.forEach { dao.insertBundle(it) }
        }
    }

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "fanta_sanremo_db"
                )
                .addCallback(AppDatabaseCallback(CoroutineScope(Dispatchers.IO)))
                .fallbackToDestructiveMigration()
                .build()
                INSTANCE = instance
                instance
            }
        }
    }
}
