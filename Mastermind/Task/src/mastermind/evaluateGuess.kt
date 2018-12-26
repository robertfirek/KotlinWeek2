package mastermind

import kotlin.math.min

data class Evaluation(val rightPosition: Int, val wrongPosition: Int)

fun evaluateGuess(secret: String, guess: String): Evaluation {
    return when {
        secret.length != 4 || guess.length != 4 -> Evaluation(rightPosition = 0, wrongPosition = 0)
        secret == guess -> Evaluation(rightPosition = 4, wrongPosition = 0)
        else -> calculateEvaluation(secret, guess)
    }
}

fun calculateEvaluation(secret: String, guess: String): Evaluation {
    val secretAndGuess = secret zip guess


    val evaluationWithRightPositions = secretAndGuess.fold(Evaluation(rightPosition = 0, wrongPosition = 0))
    { evalution, characters ->
        Evaluation(rightPosition = evalution.rightPosition + (if (isTheSameCharacter(characters)) 1 else 0), wrongPosition = 0)

    }

    val numberOfLettersOnWrongPosition = secretAndGuess
            .filter { characters -> !isTheSameCharacter(characters) }
            .fold(mapOf<Char, Pair<Int, Int>>()) { characterCounter, characters ->
                val secretLeterCounter = characterCounter.getOrDefault(characters.first, Pair(0, 0))
                val guessLeterCounter = characterCounter.getOrDefault(characters.second, Pair(0, 0))
                characterCounter
                        .plus(
                                Pair(characters.first, Pair(secretLeterCounter.first + 1, secretLeterCounter.second))
                        )
                        .plus(

                                Pair(characters.second, Pair(guessLeterCounter.first, guessLeterCounter.second + 1))
                        )

            }
            .filterValues { counters -> notInSecret(counters) && notInGuess(counters) }
            .map { characterAndCounters ->
                numberOfCharactersInWrongPosition(characterAndCounters.value)
            }.sum()



    return evaluationWithRightPositions.copy(wrongPosition = numberOfLettersOnWrongPosition)
}

private fun notInGuess(counters: Pair<Int, Int>) = counters.second != 0

private fun notInSecret(counters: Pair<Int, Int>) = counters.first != 0

private fun numberOfCharactersInWrongPosition(value: Pair<Int, Int>) =
        min(value.first, value.second)

private fun isTheSameCharacter(characters: Pair<Char, Char>) = characters.first == characters.second
