package net.dhleong.slingshot

import com.google.android.gms.tasks.Task
import io.reactivex.Completable
import io.reactivex.Single

/**
 * Convert a Task<T> into a Single<T>. If your result
 *  type is Void in Java, you will get Unit from this;
 *  you should probably prefer [toCompletable] in that
 *  case, however.
 */
fun <TResult> Task<TResult>.toSingle(): Single<TResult> = Single.create { emitter ->
    addOnCompleteListener { task ->
        if (task.isSuccessful) {
            if (task.result == null) {
                // should be a Void request
                @Suppress("UNCHECKED_CAST")
                emitter.onSuccess(Unit as TResult)
            } else {
                emitter.onSuccess(task.result)
            }
        } else {
            emitter.onError(
                task.exception
                    ?: Exception("Unknown error")
            )
        }
    }
}

/**
 * Convert a <Void> task into a Completable.
 */
fun Task<Void>.toCompletable(): Completable =
    // NOTE this relies on the trick above where Void type
    // seems to be perfectly happy with a Unit value
    toSingle().toCompletable()
