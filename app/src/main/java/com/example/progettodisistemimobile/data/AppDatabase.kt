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
            // --- POPOLAMENTO CANTANTI (Classifiche originali corrette) ---
            val cantanti = listOf(
                Cantante("Marco Mengoni", "Due Vite", 20, 15, "Lisa", "Money", 1, 13, null, 12, 29),
                Cantante("Sal Da Vinci", "Per Sempre Sì", 18, 9, "Bobby Solo", "Una Lacrima sul Viso", 2, 5, null, 29, 20),
                Cantante("Mr. Rain", "Supereroi", 15, 12, "Zucchero", "Funky Gallo", 3, 15, null, 11, 22),
                Cantante("Lady Gaga", "Judas", 25, 20, "Queen", "Radio Gaga", 4, 6, null, 1, 7),
                Cantante("Taylor Swift", "The Fate of Ophelia", 18, 10, "The Jonas Brothers", "Cool", 5, 12, null, 30, 21),
                Cantante("Mina", "Grande, grande, grande", 20, 6, "Luciano Pavarotti", "Nessun Dorma", 6, 14, null, 28, 6),
                Cantante("Michael Jackson", "Will You Be There", 25, 17, "Albano", "I Cigni di Balaka", 7, 11, null, 13, 2),
                Cantante("Ricchi e Poveri", "Mamma Maria", 16, 13, "RM", "Hooligan", 8, 4, null, 26, 23),
                Cantante("Fausto Leali", "Mi Manchi", 10, 9, "Arisa", "Sincerità", 9, 7, null, 27, 24),
                Cantante("Elvis Presley", "Love Me Tender", 25, 20, "Jin", "Super Toona", 10, 10, null, 10, 13),
                Cantante("Anna Oxa", "Ti Lascerò", 15, 10, "Pupo", "Gelato al Chioccolato", 11, 9, null, 2, 25),
                Cantante("Cristiano Malgiolio", "Fernando", 18, 12, "Jungkook", "Seven", 12, 3, null, 9, 1),
                Cantante("Simon Le Bon", "Wild Boys", 10, 5, "Orietta Berti", "Finché la Barca Va", 13, 2, null, 3, 3),
                Cantante("I Cugini di Campagna", "Anima Mia", 18, 13, "Riccardo Cocciante", "Cervo a Primavera", 14, 8, null, 25, 14),
                Cantante("Psy", "Gangnam Style", 17, 10, "SUGA", "That, That", 15, 1, null, 14, 16),
                Cantante("Sabrina Carpenter", "Espresso", 20, 16, "Romina Power", "Il Ballo del Qua Qua", 16, null, 11, 8, 11),
                Cantante("Cristina D'Avena", "Occhi di Gatto", 14, 17, "Gem Boy", "Batman è Figo", 17, null, 14, 24, 15),
                Cantante("Pooh", "Dammi Solo un Minuto", 14, 14, "Ariana Grande", "Popular", 18, null, 13, 15, 26),
                Cantante("Righeira", "Vamos a la Playa", 10, 9, "Mungo Jerry", "In the Summertime", 19, null, 12, 23, 27),
                Cantante("Madonna", "Like a Virgin", 13, 15, "Jimin e Shakira", "Waka Waka", 20, null, 1, 4, 10),
                Cantante("Laura Pausini", "La Solitudine", 16, 13, "J-HOPE", "Cicken Noodle Soup", 21, null, 3, 16, 4),
                Cantante("Louis Armstron", "What a Wonderfull World", 18, 20, "V", "Into the Sun", 22, null, 8, 17, 9),
                Cantante("Vasco Rossi", "Una Vita Spericolata", 15, 11, "Jenni", "Boombayah", 23, null, 4, 20, 30),
                Cantante("Matia Bazar", "Vacanze Romane", 18, 15, "Coldplay", "Fix You", 24, null, 2, 5, 5),
                Cantante("Alicia", "A Natale Puoi", 15, 9, "George Michael", "Last Christmas", 25, null, 5, 18, 17),
                Cantante("Patty Pravo", "Opera", 12, 7, "John Lennon", "Imagine", 26, null, 7, 22, 28),
                Cantante("Edoardo Vianello", "I Watussi", 10, 4, "James Brown", "I Got You", 27, null, 15, 6, 12),
                Cantante("Il Volo", "Grande Amore", 15, 14, "Chuu", "Kiss a Kitty", 28, null, 9, 19, 8),
                Cantante("Gabri Ponte", "Blue (Da Ba Dee)", 12, 6, "Alan Walker", "Darkside", 29, null, 6, 7, 18),
                Cantante("Mahmood", "Tuta Gold", 14, 16, "Raffaella Carrà", "Tuca Tuca", 30, null, 10, 21, 19)
            )
            cantanti.forEach { dao.insertCantante(it) }

            // --- POPOLAMENTO BUNDLE ---
            val bundles = listOf(
                Bundle(1, 10, 1), Bundle(2, 25, 2), Bundle(3, 50, 4),
                Bundle(4, 100, 8), Bundle(5, 200, 15), Bundle(6, 500, 35)
            )
            bundles.forEach { dao.insertBundle(it) }

            // --- DATI PROVVISORI PER TEST ---
            val testUser = Utente("MarioRossi", "mario@example.com", "password123", null, 150, null, null)
            dao.insertUtente(testUser)

            // Creazione di più leghe per testare la schermata "Le Mie Leghe"
            val idL1 = dao.insertLega(Lega(0, "Lega degli Esperti", null, "Una lega per veri appassionati", true, 41.89, 12.49))
            val idL2 = dao.insertLega(Lega(0, "Amici di Sanremo", null, "Solo per divertimento", true, 45.44, 9.14))
            val idL3 = dao.insertLega(Lega(0, "Lega Mondiale", null, "Sfida globale", true, null, null))
            
            // Iscrizione di MarioRossi alle leghe
            dao.joinLega(UtenteInLega(0, "MarioRossi", idL1.toInt(), true, 45))
            dao.joinLega(UtenteInLega(0, "MarioRossi", idL2.toInt(), false, 120))
            dao.joinLega(UtenteInLega(0, "MarioRossi", idL3.toInt(), true, 10))
            
            // Popoliamo una squadra
            val idSquadra1 = 1 // Sappiamo che è il primo inserito
            val composizione = listOf(
                ComposizioneSquadra(idSquadra1, "Marco Mengoni", 0),
                ComposizioneSquadra(idSquadra1, "Cristiano Malgiolio", 1),
                ComposizioneSquadra(idSquadra1, "Sabrina Carpenter", 2),
                ComposizioneSquadra(idSquadra1, "Mahmood", 3),
                ComposizioneSquadra(idSquadra1, "Vasco Rossi", 4),
                ComposizioneSquadra(idSquadra1, "Mina", 5),
                ComposizioneSquadra(idSquadra1, "Anna Oxa", 6)
            )
            composizione.forEach { dao.insertComposizione(it) }
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
