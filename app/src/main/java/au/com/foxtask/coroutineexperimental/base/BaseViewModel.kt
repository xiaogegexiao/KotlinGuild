package au.com.foxtask.coroutineexperimental.base

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import au.com.foxtask.coroutineexperimental.Failure
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job

open class BaseViewModel : ViewModel() {

    var failureLiveData: MutableLiveData<Failure> = MutableLiveData()

    protected fun handleFailure(failure: Failure) {
        this.failureLiveData.value = failure
    }

    private val mJob = Job()

    // foreground coroutine scope which take mJob as parent mJob and Main thread. It is view model lifecycle awareness
    protected val mFgScope: CoroutineScope = CoroutineScope(mJob + Dispatchers.Main)

    // background coroutine scope which take mJob as parent mJob and Main thread. It is view model lifecycle awareness
    protected val mBgScope: CoroutineScope = CoroutineScope(mJob + Dispatchers.Default)

    override fun onCleared() {
        super.onCleared()
        mJob.cancel()
    }
}