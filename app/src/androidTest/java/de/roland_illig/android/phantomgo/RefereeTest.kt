package de.roland_illig.android.phantomgo

import android.support.test.InstrumentationRegistry
import android.support.test.runner.AndroidJUnit4
import de.roland_illig.phantomgo.Player
import de.roland_illig.phantomgo.Referee
import de.roland_illig.phantomgo.RefereeResult
import org.junit.ComparisonFailure
import org.junit.Test
import org.junit.runner.RunWith
import java.util.Locale

@RunWith(AndroidJUnit4::class)
class RefereeTest {

    @Test
    fun testRefereeEnglish() {
        assertReferee(
                Locale.ENGLISH,

                "White to move.",
                "Black to move.",

                "White to move.",
                "Black captures one stone.",
                "Black captures 3 stones.",
                "Black puts himself in atari.",
                "Black captures one stone and puts himself in atari.",
                "Black captures 3 stones and puts himself in atari.",
                "Black puts White in atari.",
                "Black captures one stone and puts White in atari.",
                "Black captures 3 stones and puts White in atari.",
                "Black puts White and himself in atari.",
                "Black captures one stone and puts White and himself in atari.",
                "Black captures 3 stones and puts White and himself in atari.",

                "There is already an own stone.",
                "There is already an opponent’s stone.",
                "Suicide.",
                "The ko cannot be captured back immediately.",
                "Black passes.")
    }

    @Test
    fun testRefereeFrench() {
        assertReferee(
                Locale.FRENCH,

                "Blanc à jouer.",
                "Noir à jouer.",

                "Blanc à jouer.",
                "Noir capture une pierre.",
                "Noir capture 3 pierres.",
                "Noir fait atari à lui-même.",
                "Noir capture une pierre et fait atari à lui-même.",
                "Noir capture 3 pierres et fait atari à lui-même.",
                "Noir fait atari à Blanc.",
                "Noir capture une pierre et fait atari à Blanc.",
                "Noir capture 3 pierres et fait atari à Blanc.",
                "Noir fait atari à Blanc et lui-même.",
                "Noir capture une pierre et fait atari à Blanc et lui-même.",
                "Noir capture 3 pierres et fait atari à Blanc et lui-même.",

                "Ici, il y’a déjà une pierre de toi.",
                "Ici, il y’a déjà une pierre adversaire.",
                "Suicide.",
                "Tu ne peux pas reprendre le ko tout de suite.",
                "Noir passe.")
    }

    @Test
    fun testRefereeGerman() {
        assertReferee(
                Locale.GERMAN,

                "Weiß ist dran.",
                "Schwarz ist dran.",

                "Weiß ist dran.",
                "Schwarz fängt einen Stein.",
                "Schwarz fängt 3 Steine.",
                "Schwarz setzt sich selbst ins Atari.",
                "Schwarz fängt einen Stein und setzt sich selbst ins Atari.",
                "Schwarz fängt 3 Steine und setzt sich selbst ins Atari.",
                "Schwarz setzt Weiß ins Atari.",
                "Schwarz fängt einen Stein und setzt Weiß ins Atari.",
                "Schwarz fängt 3 Steine und setzt Weiß ins Atari.",
                "Schwarz setzt Weiß und sich selbst ins Atari.",
                "Schwarz fängt einen Stein und setzt Weiß und sich selbst ins Atari.",
                "Schwarz fängt 3 Steine und setzt Weiß und sich selbst ins Atari.",

                "Da steht schon ein eigener Stein.",
                "Da steht schon ein gegnerischer Stein.",
                "Selbstmord.",
                "Das Ko darf nicht sofort zurückgeschlagen werden.",
                "Schwarz passt.")
    }

    @Test
    fun testRefereeSpanish() {
        assertReferee(
                Locale("es", "es"),

                "Blanco juega.",
                "Negro juega.",

                "Blanco juega.",
                "Negro captura una piedra.",
                "Negro captura 3 piedras.",
                "Negro pone en atari a sí mismo.",
                "Negro captura una piedra y pone en atari a sí mismo.",
                "Negro captura 3 piedras y pone en atari a sí mismo.",
                "Negro pone en atari a Blanco.",
                "Negro captura una piedra y pone en atari a Blanco.",
                "Negro captura 3 piedras y pone en atari a Blanco.",
                "Negro pone en atari a Blanco y a sí mismo.",
                "Negro captura una piedra y pone en atari a Blanco y a sí mismo.",
                "Negro captura 3 piedras y pone en atari a Blanco y a sí mismo.",

                "Aquí ya encuentra una piedra tuya.",
                "Aquí ya encuentra una piedra puesta.",
                "Suicidio.",
                "No puedes recapturar el ko inmediatamente.",
                "Negro pasa.")
    }

    private fun assertReferee(locale: Locale, vararg results: String) {
        val res = InstrumentationRegistry.getTargetContext().resources
        res.updateConfiguration(res.configuration.also { it.locale = locale }, res.displayMetrics)

        val actuals = listOf(
                Referee.comment(RefereeResult.Ok(false, false, 0), Player.BLACK, res),
                Referee.comment(RefereeResult.Ok(false, false, 0), Player.WHITE, res),

                Referee.comment(RefereeResult.Ok(false, false, 0), Player.BLACK, res),
                Referee.comment(RefereeResult.Ok(false, false, 1), Player.BLACK, res),
                Referee.comment(RefereeResult.Ok(false, false, 3), Player.BLACK, res),
                Referee.comment(RefereeResult.Ok(false, true, 0), Player.BLACK, res),
                Referee.comment(RefereeResult.Ok(false, true, 1), Player.BLACK, res),
                Referee.comment(RefereeResult.Ok(false, true, 3), Player.BLACK, res),
                Referee.comment(RefereeResult.Ok(true, false, 0), Player.BLACK, res),
                Referee.comment(RefereeResult.Ok(true, false, 1), Player.BLACK, res),
                Referee.comment(RefereeResult.Ok(true, false, 3), Player.BLACK, res),
                Referee.comment(RefereeResult.Ok(true, true, 0), Player.BLACK, res),
                Referee.comment(RefereeResult.Ok(true, true, 1), Player.BLACK, res),
                Referee.comment(RefereeResult.Ok(true, true, 3), Player.BLACK, res),

                Referee.comment(RefereeResult.OwnStone, Player.BLACK, res),
                Referee.comment(RefereeResult.OtherStone, Player.BLACK, res),
                Referee.comment(RefereeResult.Suicide, Player.BLACK, res),
                Referee.comment(RefereeResult.Ko, Player.BLACK, res),
                Referee.comment(RefereeResult.Pass, Player.BLACK, res))

        val actual = actuals.joinToString("\n")
        val expected = listOf(*results).joinToString("\n")
        if (actual != expected) {
            throw ComparisonFailure(null, expected, actual)
        }
    }
}
