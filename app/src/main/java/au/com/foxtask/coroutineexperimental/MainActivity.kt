package au.com.foxtask.coroutineexperimental

import android.os.Bundle
import android.util.Log
import android.view.View
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
        cancel_job.setOnClickListener { cancelJob() }
        cancel_subjobs_before_finishing.setOnClickListener { cancelAllSubjobsBeforeFinishing() }
        load_data_from_backend.setOnClickListener { loadingDataFromBackend() }
        same_load_data_from_backend_with_context_swap.setOnClickListener { sameLoadingDataFromBackendWithContextSwap() }
        load_data_from_two_apis.setOnClickListener { loadDataFromTwoApis() }
        load_data_timeout.setOnClickListener { loadDataTimeout() }
        catalogue_example.setOnClickListener { launchCatalogueExample() }
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

    fun cancelJob() {
        uiScope.launch {
            val job = defaultScope.launch {
                // massive computation
                delay(3000)
                log("finished job")
            }
            delay(1000)
            log("cancel job so we won't see the finished job log")
            job.cancel()
        }
    }

    fun cancelAllSubjobsBeforeFinishing() {
        val parentJob = SupervisorJob()
        uiScope.launch {
            val job1 = (defaultScope + parentJob).launch {
                repeat(10) {
                    log("hello from job1")
                    delay(500)
                }
            }
            val job2 = (defaultScope + parentJob).launch {
                repeat(1) {
                    log("hello from job2")
                    delay(500)
                }
            }
            delay(1000)

            // here we should take care of using cancel() as the cancel will also cancel the parent job as well
            // which means next time when we want to create a new sub-job, we need to create a new parent job
            parentJob.cancelChildren()
        }
    }

    fun loadingDataFromBackend() {
        uiScope.launch {
            load_data_from_backend.text = "Start loading"
            load_data_from_backend_progress_bar.visibility = View.VISIBLE
            val deferred = ioScope.async {
                // assuming we are loading http api result and it comes back after 5 secs
                delay(5000)
                "api response"
            }
            val res = deferred.await()
            load_data_from_backend_progress_bar.visibility = View.GONE
            load_data_from_backend.text = "Got result $res"
        }
    }

    fun sameLoadingDataFromBackendWithContextSwap() {
        uiScope.launch {
            same_load_data_from_backend_with_context_swap.text = "Start loading"
            same_load_data_from_backend_with_context_swap_progress_bar.visibility = View.VISIBLE
            val res = withContext(Dispatchers.IO) {
                // assuming we are loading http api result and it comes back after 5 secs
                delay(5000)
                "api response"
            }
            same_load_data_from_backend_with_context_swap_progress_bar.visibility = View.GONE
            same_load_data_from_backend_with_context_swap.text = "Got result $res"
        }
    }

    fun loadDataFromTwoApis() {
        uiScope.launch {
            load_data_from_two_apis.text = "Start loading"
            load_data_from_two_apis_progress_bar.visibility = View.VISIBLE
            val deferred1 = ioScope.async {
                // assuming we are loading http api result and it comes back after 5 secs
                delay(5000)
                "hello"
            }

            val deferred2 = ioScope.async {
                // assuming we are loading http api result and it comes back after 5 secs
                delay(3000)
                "world"
            }

            // similar to reactive zip operator
            val res = "${deferred1.await()}, ${deferred2.await()}"
//            val res = awaitAll(deferred1, deferred2).joinToString { it }
            load_data_from_two_apis_progress_bar.visibility = View.GONE
            load_data_from_two_apis.text = "Got result $res"
        }
    }

    fun loadDataTimeout() {
        uiScope.launch {
            load_data_timeout.text = "Start loading"
            val res = withTimeoutOrNull(2000) {
                ioScope.async {
                    // assuming we are loading http api result and it comes back after 5 secs
                    delay(5000)
                    "hello"
                }.await()
            }

            load_data_timeout.text = "Got result $res"
        }
    }

    fun launchCatalogueExample() {
        CatalogueActivity.launch(this)
    }
}
