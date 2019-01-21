package au.com.foxtask.coroutineexperimental

/**
 * Base Class for handling errors/failures/exceptions.
 * Every feature specific failureLiveData should extend [FeatureFailure] class.
 */
sealed class Failure {
    class NetworkConnection: Failure()
    class ServerError: Failure()

    /** * Extend this class for feature specific failures.*/
    abstract class FeatureFailure: Failure()
}
