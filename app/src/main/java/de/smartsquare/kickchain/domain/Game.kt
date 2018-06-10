package de.smartsquare.kickchain.domain

data class Game(val team1: Team, val team2: Team, val score: Score) {

    constructor(
        team1Players: List<String>,
        team2Players: List<String>,
        team1Score: Int,
        team2Score: Int
    ) : this(Team(team1Players), Team(team2Players), Score(team1Score, team2Score))

    val winnerTeam
        get() = if (score.goals1 > score.goals2) team1 else team2

    val winnerScore
        get() = if (score.goals1 > score.goals2) score.goals1 else score.goals2

    val loserScore
        get() = if (score.goals1 > score.goals2) score.goals2 else score.goals1
}
