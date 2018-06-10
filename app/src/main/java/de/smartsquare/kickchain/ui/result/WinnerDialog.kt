package de.smartsquare.kickchain.ui.result

import android.app.Dialog
import android.arch.lifecycle.Lifecycle
import android.graphics.Typeface
import android.os.Bundle
import android.support.v4.app.DialogFragment
import android.support.v4.app.FragmentActivity
import android.support.v4.content.ContextCompat
import android.text.SpannableString
import android.text.SpannableStringBuilder
import android.text.style.StyleSpan
import android.view.ViewGroup
import android.widget.TextView
import com.afollestad.materialdialogs.MaterialDialog
import com.github.jinatonic.confetti.CommonConfetti
import com.github.jinatonic.confetti.ConfettiManager
import com.jakewharton.rxbinding2.view.clicks
import com.uber.autodispose.android.lifecycle.scope
import com.uber.autodispose.kotlin.autoDisposable
import de.smartsquare.kickchain.R
import io.reactivex.Single
import kotterknife.bindView
import java.util.concurrent.TimeUnit

/**
 * @author Ruben Gees
 */
class WinnerDialog : DialogFragment() {

    companion object {
        private const val WINNER_PLAYERS_ARGUMENT = "winnerPlayers"
        private const val WINNER_SCORE_ARGUMENT = "winnerScore"
        private const val LOSER_SCORE_ARGUMENT = "loserScore"

        fun show(context: FragmentActivity, winnerPlayers: List<String>, winnerScore: Int, loserScore: Int) {
            WinnerDialog()
                .apply {
                    arguments = Bundle().apply {
                        putStringArrayList(WINNER_PLAYERS_ARGUMENT, ArrayList(winnerPlayers))
                        putInt(WINNER_SCORE_ARGUMENT, winnerScore)
                        putInt(LOSER_SCORE_ARGUMENT, loserScore)
                    }
                }
                .showNow(context.supportFragmentManager, "WinnerDialog")
        }
    }

    private val root by bindView<ViewGroup>(R.id.root)
    private val status by bindView<TextView>(R.id.status)

    private val winnerPlayers
        get() = arguments?.getStringArrayList(WINNER_PLAYERS_ARGUMENT) ?: emptyList<String>()

    private val winnerScore
        get() = arguments?.getInt(WINNER_SCORE_ARGUMENT) ?: 0

    private val loserScore
        get() = arguments?.getInt(LOSER_SCORE_ARGUMENT) ?: 0

    private val confettiColors by lazy {
        intArrayOf(
            ContextCompat.getColor(requireContext(), R.color.red),
            ContextCompat.getColor(requireContext(), R.color.green),
            ContextCompat.getColor(requireContext(), R.color.blue),
            ContextCompat.getColor(requireContext(), R.color.yellow),
            ContextCompat.getColor(requireContext(), R.color.purple),
            ContextCompat.getColor(requireContext(), R.color.orange)
        )
    }

    private var confettiManager: ConfettiManager? = null

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return MaterialDialog.Builder(requireContext())
            .customView(R.layout.winner_dialog, false)
            .build()
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        status.text = buildMessage()

        root.clicks()
            .autoDisposable(this.scope(Lifecycle.Event.ON_DESTROY))
            .subscribe { _ -> dismiss() }

        Single.timer(10, TimeUnit.SECONDS)
            .autoDisposable(this.scope(Lifecycle.Event.ON_DESTROY))
            .subscribe { _ -> dismiss() }

        root.post {
            val previousHeight = root.height

            confettiManager = CommonConfetti.rainingConfetti(root, confettiColors)
                .infinite()
                .setEmissionRate(25f)
                .setVelocityY(300f, 100f)
                .animate()

            dialog.window.setLayout(root.width, previousHeight)
        }
    }

    override fun onDestroy() {
        confettiManager?.terminate()
        confettiManager = null

        super.onDestroy()
    }

    private fun buildMessage(): CharSequence {
        val scoreMessage = requireContext().getString(R.string.winner_message, winnerScore, loserScore)
        val playersMessage = winnerPlayers.drop(1).fold(buildBoldString(winnerPlayers.first())) { acc, next ->
            acc.append(requireContext().getString(R.string.winner_message_and)).append(buildBoldString(next))
        }

        return playersMessage.insert(0, scoreMessage)
    }

    private fun buildBoldString(raw: String): SpannableStringBuilder {
        return SpannableStringBuilder(raw).apply {
            setSpan(StyleSpan(Typeface.BOLD), 0, raw.length, SpannableString.SPAN_EXCLUSIVE_EXCLUSIVE)
        }
    }
}
