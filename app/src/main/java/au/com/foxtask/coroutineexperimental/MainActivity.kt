package au.com.foxtask.coroutineexperimental

import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.coroutines.*

class MainActivity : AppCompatActivity() {
    val log: (String) -> Unit = { text ->
        Log.d("COROUTINE_TEST", "$text is running on thread:${Thread.currentThread().name}")
    }

    val toast: (String?) -> Unit = { text ->
        runOnUiThread {
            Toast.makeText(this, "$text", Toast.LENGTH_SHORT).show()
        }
    }

    val uiScope = CoroutineScope(Dispatchers.Main)
    val defaultScope = CoroutineScope(Dispatchers.Default)
    val ioScope = CoroutineScope(Dispatchers.IO)
    val unconfinedScope = CoroutineScope(Dispatchers.Unconfined)

    val uiScopeWithExceptionHandler = uiScope + CoroutineExceptionHandler { coroutineContext, throwable ->
        toast("catch exception ${throwable.message}")
    }
    val defaultScopeWithExceptionHandler = defaultScope + CoroutineExceptionHandler { coroutineContext, throwable ->
        toast("catch exception ${throwable.message}")
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        default_launch_button.setOnClickListener { defaultLaunch() }
        default_async_button.setOnClickListener { defaultAsync() }
        launch_exception_button.setOnClickListener { launchWithException() }
        async_exception_button.setOnClickListener { asyncWithException() }
        unconfined_launch_button.setOnClickListener { unconfinedLaunch() }
    }

    fun defaultLaunch() {
        uiScope.launch {
            log("before default launch")
            val job = defaultScope.launch {
                log("start default launch by using default dispatcher")
            }
            job.join()
        }
    }

    fun defaultAsync() {
        uiScope.launch {
            log("before default async")
            val deferred = defaultScope.async {
                log("start default async by using default dispatcher")
                1
            }
            log("waiting for async result ${deferred.await()}")
        }
    }

    fun launchWithException() {
        uiScope.launch {
            log("before default launch")
            val job = defaultScopeWithExceptionHandler.launch {
                log("start default launch by using default dispatcher")
                throw RuntimeException("fake runtime exception")
            }
            job.join()
        }
    }

    @ExperimentalCoroutinesApi
    fun asyncWithException() {
        uiScope.launch {
            log("before default launch")
            val deferred = defaultScope.async {
                log("start default launch by using default dispatcher")
                throw RuntimeException("fake runtime exception")
            }
            deferred.join()
            toast("Doesn't get exception in async because the exception ${deferred.getCompletionExceptionOrNull()?.message} is wrapped inside the returned Deferred")
        }
    }

    fun unconfinedLaunch() {
        unconfinedScope.launch {
            log("before delay")
            defaultScope.launch {
                delay(5000)
            }.join()
            log("after delay")
        }
    }
}
