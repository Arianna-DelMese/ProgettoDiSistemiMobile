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
                Cantante("Marco Mengoni", "Due Vite", 20, 15, "Lisa", "Money"),
                Cantante("Sal Da Vinci", "Per Sempre Sì", 18, 9, "Bobby Solo", "Una Lacrima sul Viso"),
                Cantante("Mr. Rain", "Supereroi", 15, 12, "Zucchero", "Funky Gallo"),
                Cantante("Lady Gaga", "Judas", 25, 20, "Queen", "Radio Gaga"),
                Cantante("Taylor Swift", "The Fate of Ophelia", 18, 10, "The Jonas Brothers", "Cool"),
                Cantante("Mina", "Grande, grande, grande", 20, 6, "Luciano Pavarotti", "Nessun Dorma"),
                Cantante("Michael Jackson", "Will You Be There", 25, 17, "Albano", "I Cigni di Balaka"),
                Cantante("Ricchi e Poveri", "Mamma Maria", 16, 13, "RM", "Hooligan"),
                Cantante("Fausto Leali", "Mi Manchi", 10, 9, "Arisa", "Sincerità"),
                Cantante("Elvis Presley", "Love Me Tender", 25, 20, "Jin", "Super Toona"),

                Cantante("Anna Oxa", "Ti Lascerò", 15, 10, "Pupo", "Gelato al Cioccolato"),
                Cantante("Cristiano Malgiolio", "Fernando", 18, 12, "Jungkook", "Seven"),
                Cantante("Simon Le Bon", "Wild Boys", 10, 5, "Orietta Berti", "Finché la Barca Va"),
                Cantante("I Cugini di Campagna", "Anima Mia", 18, 13, "Riccardo Cocciante", "Cervo a Primavera"),
                Cantante("Psy", "Gangnam Style", 17, 10, "SUGA", "That, That"),
                Cantante("Sabrina Carpenter", "Espresso", 20, 16, "Romina Power", "Il Ballo del Qua Qua"),
                Cantante("Cristina D'Avena", "Occhi di Gatto", 14, 17, "Gem Boy", "Batman è Figo"),
                Cantante("Pooh", "Dammi Solo un Minuto", 14, 14, "Ariana Grande", "Popular"),
                Cantante("Righeira", "Vamos a la Playa", 10, 9, "Mungo Jerry", "In the Summertime"),
                Cantante("Madonna", "Like a Virgin", 13, 15, "Jimin e Shakira", "Waka Waka"),

                Cantante("Laura Pausini", "La Solitudine", 16, 13, "J-HOPE", "Cicken Noodle Soup"),
                Cantante("Louis Armstron", "What a Wonderfull World", 18, 20, "V", "Into the Sun"),
                Cantante("Vasco Rossi", "Una Vita Spericolata", 15, 11, "Jenni", "Boombayah"),
                Cantante("Matia Bazar", "Vacanze Romane", 18, 15, "Coldplay", "Fix You"),
                Cantante("Alicia", "A Natale Puoi", 15, 9, "George Michael", "Last Christmas"),
                Cantante("Patty Pravo", "Opera", 12, 7, "John Lennon", "Imagine"),
                Cantante("Edoardo Vianello", "I Watussi", 10, 4, "James Brown", "I Got You"),
                Cantante("Il Volo", "Grande Amore", 15, 14, "Chuu", "Kiss a Kitty"),
                Cantante("Gabri Ponte", "Blue (Da Ba Dee)", 12, 6, "Alan Walker", "Darkside"),
                Cantante("Mahmood", "Tuta Gold", 14, 16, "Raffaella Carrà", "Tuca Tuca")
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
