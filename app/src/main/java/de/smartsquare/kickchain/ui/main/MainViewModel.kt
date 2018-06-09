package de.smartsquare.kickchain.ui.main

import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.ViewModel
import de.smartsquare.kickchain.MainApplication
import de.smartsquare.kickchain.domain.Game
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers

class MainViewModel : ViewModel() {

    val result = MutableLiveData<Unit?>()
    val error = MutableLiveData<Throwable?>()
    val isLoading = MutableLiveData<Boolean?>()

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
                    .doOnSubscribe {
                        isLoading.value = true
                        result.value = null
                        error.value = null
                    }
                    .doAfterTerminate {
                        gameDisposable = null
                        isLoading.value = false
                    }
                    .subscribe({
                        result.value = Unit
                    }, {
                        error.value = it
                    })
        }
    }
}
