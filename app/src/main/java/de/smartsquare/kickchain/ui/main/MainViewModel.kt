package de.smartsquare.kickchain.ui.main

import android.arch.lifecycle.ViewModel
import de.smartsquare.kickchain.MainApplication
import de.smartsquare.kickchain.domain.Game
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers

class MainViewModel : ViewModel() {

    private var gameDisposable: Disposable? = null

    override fun onCleared() {
        gameDisposable?.dispose()
        gameDisposable = null

        super.onCleared()
    }

    fun createGame(game: Game) {
        if (gameDisposable == null) {
            gameDisposable = MainApplication.kickchainApi.createGame(game)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .doAfterTerminate { gameDisposable = null }
                    .subscribe({

                    }, {

                    })
        }
    }
}
