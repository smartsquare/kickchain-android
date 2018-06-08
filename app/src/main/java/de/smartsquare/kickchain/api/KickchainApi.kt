package de.smartsquare.kickchain.api

import de.smartsquare.kickchain.domain.Game
import io.reactivex.Single
import retrofit2.http.Body
import retrofit2.http.POST

interface KickchainApi {

    @POST("/game/new")
    fun createGame(@Body game: Game): Single<Void>
}
