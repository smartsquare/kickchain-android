package de.smartsquare.kickchain.ui.main

import android.arch.lifecycle.Lifecycle
import android.arch.lifecycle.ViewModelProviders
import android.content.Context
import android.os.Bundle
import android.support.design.widget.TextInputEditText
import android.support.design.widget.TextInputLayout
import android.support.v4.app.Fragment
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.Button
import android.widget.TextView
import com.jakewharton.rxbinding2.view.clicks
import com.jakewharton.rxbinding2.widget.editorActionEvents
import com.jakewharton.rxbinding2.widget.textChangeEvents
import com.jakewharton.rxbinding2.widget.textChanges
import com.uber.autodispose.android.lifecycle.scope
import com.uber.autodispose.kotlin.autoDisposable
import de.smartsquare.kickchain.R
import de.smartsquare.kickchain.domain.Game
import de.smartsquare.kickchain.util.NameValidator
import de.smartsquare.kickchain.util.ScoreValidator
import de.smartsquare.kickchain.util.simpleError
import de.smartsquare.kickchain.util.trimmedText
import io.reactivex.Observable
import kotterknife.bindView


class MainFragment : Fragment() {

    companion object {
        fun newInstance() = MainFragment()
    }

    private val root by bindView<ViewGroup>(R.id.root)

    private val team1Player1NameInputContainer by bindView<TextInputLayout>(R.id.team1Player1NameInputContainer)
    private val team1Player1NameInput by bindView<TextInputEditText>(R.id.team1Player1NameInput)
    private val team1ScoreInputContainer by bindView<TextInputLayout>(R.id.team1ScoreInputContainer)
    private val team1ScoreInput by bindView<TextInputEditText>(R.id.team1ScoreInput)

    private val team2Player1NameInputContainer by bindView<TextInputLayout>(R.id.team2Player1NameInputContainer)
    private val team2Player1NameInput by bindView<TextInputEditText>(R.id.team2Player1NameInput)
    private val team2ScoreInputContainer by bindView<TextInputLayout>(R.id.team2ScoreInputContainer)
    private val team2ScoreInput by bindView<TextInputEditText>(R.id.team2ScoreInput)

    private val errorTextViewContainer by bindView<ViewGroup>(R.id.errorTextViewContainer)
    private val errorTextView by bindView<TextView>(R.id.errorTextView)
    private val saveButton by bindView<Button>(R.id.saveButton)

    private val viewModel by lazy { ViewModelProviders.of(this).get(MainViewModel::class.java) }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        return inflater.inflate(R.layout.main_fragment, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        Observable.merge(team1ScoreInput.textChanges(), team2ScoreInput.textChanges())
                .autoDisposable(this.scope(Lifecycle.Event.ON_DESTROY))
                .subscribe { validateScoresAndSetErrorIfPresent() }

        Observable.merge(team1Player1NameInput.textChangeEvents(), team2Player1NameInput.textChangeEvents())
                .autoDisposable(this.scope(Lifecycle.Event.ON_DESTROY))
                .subscribe {
                    (it.view().parent.parent as? TextInputLayout)?.simpleError = null
                    errorTextViewContainer.visibility = View.GONE
                }

        team2ScoreInput.editorActionEvents()
                .filter { it.actionId() == EditorInfo.IME_ACTION_DONE }
                .autoDisposable(this.scope(Lifecycle.Event.ON_DESTROY))
                .subscribe { createGame() }

        saveButton.clicks()
                .autoDisposable(this.scope(Lifecycle.Event.ON_DESTROY))
                .subscribe { createGame() }
    }

    private fun validateScoresAndSetErrorIfPresent(): Boolean {
        var errorPresent = false

        arrayOf(team1ScoreInputContainer, team2ScoreInputContainer).forEach { scoreContainer ->
            val errorMessage = ScoreValidator.validateFormat(scoreContainer.trimmedText)

            scoreContainer.simpleError = errorMessage

            if (errorMessage != null) {
                errorPresent = true
            }
        }

        ScoreValidator.validateOneIsTen(team1ScoreInput.trimmedText, team2ScoreInput.trimmedText)?.let { errorMessage ->
            team1ScoreInputContainer.simpleError = errorMessage
            team2ScoreInputContainer.simpleError = errorMessage
            errorPresent = true
        }

        return errorPresent
    }

    private fun validateNamesAndSetErrorIfPresent(): Boolean {
        val nameContainers = listOf(team1Player1NameInputContainer, team2Player1NameInputContainer)
        var errorPresent = false

        nameContainers.forEach { nameContainer ->
            NameValidator.validateNotBlank(nameContainer.trimmedText)?.let { errorMessage ->
                nameContainer.simpleError = errorMessage
                errorPresent = true
            }
        }

        NameValidator.validateDistinct(nameContainers.map { it.trimmedText })?.let {
            errorTextViewContainer.visibility = View.VISIBLE
            errorTextView.text = it

            errorPresent = true
        }

        return errorPresent
    }

    private fun validateScoresIsNotBlankAndSetErrorIfPresent(): Boolean {
        var errorPresent = false

        arrayOf(team1ScoreInputContainer, team2ScoreInputContainer).forEach { scoreContainer ->
            ScoreValidator.validateNotBlank(scoreContainer.trimmedText)?.let { errorMessage ->
                scoreContainer.simpleError = errorMessage
                errorPresent = true
            }
        }

        return errorPresent
    }

    private fun createGame() {
        root.requestFocus()

        (requireActivity().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager).also {
            it.hideSoftInputFromWindow(root.windowToken, 0)
        }

        val scoreError = validateScoresAndSetErrorIfPresent()
        val nameError = validateNamesAndSetErrorIfPresent()
        val scoreBlankError = validateScoresIsNotBlankAndSetErrorIfPresent()

        if (!scoreError && !nameError && !scoreBlankError) {
            val team1Players = listOf(team1Player1NameInput.trimmedText.toString())
            val team2Players = listOf(team2Player1NameInput.trimmedText.toString())
            val team1Score = team1ScoreInput.trimmedText.toString().toInt()
            val team2Score = team2ScoreInput.trimmedText.toString().toInt()
            val game = Game(team1Players, team2Players, team1Score, team2Score)

            viewModel.createGame(game)

            Log.i("My Kicker", "Creating game $game")
        }
    }
}
