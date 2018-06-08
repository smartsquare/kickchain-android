package de.smartsquare.kickchain.util

object ScoreValidator {

    fun validateFormat(rawScore: CharSequence): String? {
        if (rawScore.isBlank()) {
            return null
        }

        val scoreTeam1 = rawScore.toString().toIntOrNull()

        if (scoreTeam1 != null) {
            if (scoreTeam1 in 0..10) {
                return null
            } else {
                return "The score must be between 0 and 10"
            }
        } else {
            return "The score must be an integer"
        }
    }

    fun validateOneIsTen(rawScoreTeam1: CharSequence, rawScoreTeam2: CharSequence): String? {
        if (rawScoreTeam1.isBlank() || rawScoreTeam2.isBlank()) {
            return null
        }

        val scoreTeam1 = rawScoreTeam1.toString().toIntOrNull()
        val scoreTeam2 = rawScoreTeam2.toString().toIntOrNull()

        if (scoreTeam1 == 10 || scoreTeam2 == 10) {
            if (scoreTeam1 == 10 && scoreTeam2 == 10) {
                return "Only one score can be ten"
            }
        } else {
            return "At least one score must be ten"
        }

        return null
    }

    fun validateNotBlank(score: CharSequence): String? {
        if (score.isBlank()) {
            return "A score is required"
        } else {
            return null
        }
    }
}