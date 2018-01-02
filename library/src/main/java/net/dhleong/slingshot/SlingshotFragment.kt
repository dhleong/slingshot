package net.dhleong.slingshot

import android.app.Fragment
import android.content.Intent
import android.os.Bundle
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import io.reactivex.Observable
import io.reactivex.subjects.PublishSubject

/**
 * @author dhleong
 */
internal class SlingshotFragment : Fragment() {

    private var subject: PublishSubject<GoogleSignInAccount> =
        PublishSubject.create<GoogleSignInAccount>()
    private val pendingRequestOptions = HashSet<GoogleSignInOptions>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        retainInstance = true
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode != SIGNIN_REQUEST_CODE) return

        GoogleSignIn.getSignedInAccountFromIntent(data)
            .toSingle()
            .subscribe { result, e ->
                pendingRequestOptions.clear()

                if (result != null) {
                    subject.onNext(result)
                } else {
                    subject.onError(
                        e ?: Exception("Unexpected error")
                    )

                    // the old subject is dead, so create a new one
                    subject = PublishSubject.create<GoogleSignInAccount>()
                }
            }
    }

    fun requestSignin(gso: GoogleSignInOptions): Observable<GoogleSignInAccount> {
        if (gso !in pendingRequestOptions) {
            pendingRequestOptions.add(gso)
            startActivityForResult(
                GoogleSignIn.getClient(activity, gso).signInIntent,
                SIGNIN_REQUEST_CODE
            )
        }

        return subject
    }

    companion object {
        private const val SIGNIN_REQUEST_CODE = 9001
    }
}