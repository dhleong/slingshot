package net.dhleong.slingshot

import android.app.Activity
import android.content.Context
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import io.reactivex.Completable
import io.reactivex.Maybe
import io.reactivex.Single

/**
 * @author dhleong
 */
object Slingshot {
    private const val TAG = "net.dhleong.slingshot"

    fun checkSignIn(
        context: Context
    ): Maybe<GoogleSignInAccount> = Maybe.defer {
        GoogleSignIn.getLastSignedInAccount(context)?.let {
            Maybe.just(it)
        } ?: Maybe.empty()
    }

    fun signIn(
        context: Activity,
        gso: GoogleSignInOptions
    ): Single<GoogleSignInAccount> = Single.defer {
        GoogleSignIn.getLastSignedInAccount(context)?.let {
            Single.just(it)
        } ?: requestSignIn(context, gso)
    }

    fun signOut(
        context: Activity,
        gso: GoogleSignInOptions = GoogleSignInOptions.DEFAULT_SIGN_IN
    ): Completable = Completable.defer {
        GoogleSignIn.getClient(context, gso)
            .signOut()
            .toCompletable()
    }

    private fun requestSignIn(
        context: Activity,
        gso: GoogleSignInOptions
    ): Single<GoogleSignInAccount> =
        getSlingshotFragment(context)
            .requestSignin(gso)
            .firstOrError()

    private fun getSlingshotFragment(context: Activity): SlingshotFragment =
        context.fragmentManager.findFragmentByTag(TAG) as? SlingshotFragment
            ?: SlingshotFragment().also { fragment ->
                context.fragmentManager.apply {
                    beginTransaction()
                        .add(fragment, TAG)
                        .commitAllowingStateLoss()

                    executePendingTransactions()
                }
            }
}

