package net.dhleong.slingshot

import android.annotation.SuppressLint
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.View.GONE
import android.view.View.VISIBLE
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    private val subs = CompositeDisposable()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        sign_out.visibility = GONE
        get_display_name.setOnClickListener {
            requestDisplayName()
        }
        sign_out.setOnClickListener {
            Slingshot.signOut(this)
                .subscribe {
                    text.setText("Hello world!")
                    get_display_name.visibility = VISIBLE
                    sign_out.visibility = GONE
                }
        }

        subs.add(
            Slingshot.checkSignIn(this)
                .subscribe(this::onGotAccount)
        )
    }

    override fun onDestroy() {
        super.onDestroy()

        subs.clear()
    }

    private fun requestDisplayName() {
        Slingshot.signIn(this, signInOptions())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(this::onGotAccount, this::onError)
    }

    @SuppressLint("SetTextI18n") // Don't ignore in a real app!
    private fun onGotAccount(acct: GoogleSignInAccount) {
        text.text = "Welcome, ${acct.displayName}!"

        get_display_name.visibility = GONE
        sign_out.visibility = VISIBLE
    }

    @SuppressLint("SetTextI18n") // Don't ignore in a real app!
    private fun onError(e: Throwable) {
        text.text = "ERROR: ${e.message}"
        e.printStackTrace()
    }

    private fun signInOptions(): GoogleSignInOptions =
        GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestEmail()
            .build()
}
