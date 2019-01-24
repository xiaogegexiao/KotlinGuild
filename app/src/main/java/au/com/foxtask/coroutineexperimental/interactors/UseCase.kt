package au.com.foxtask.coroutineexperimental.interactors

import au.com.foxtask.coroutineexperimental.base.Either
import au.com.foxtask.coroutineexperimental.base.Failure
import kotlinx.coroutines.*

abstract class UseCase<out Type, in Params> {
    abstract suspend fun run(params: Params): Either<Failure, Type>

    operator fun invoke(
            params: Params,
            bgScope: CoroutineScope = CoroutineScope(Dispatchers.Default), // background scope
            fgScope: CoroutineScope = CoroutineScope(Dispatchers.Main), // foreground scope
            onPreExecute: () -> Unit = {},
            onPostExecute: () -> Unit = {},
            onResult: (Either<Failure, Type>) -> Unit = {}
    ): Job {
        val job = bgScope.async { run(params) }
        return fgScope.launch {
            onPreExecute()
            onResult(job.await())
            onPostExecute()
        }
    }

    object None
}