package com.felipecosta.rxcommand

import com.nhaarman.mockito_kotlin.mock
import io.reactivex.Observable
import io.reactivex.observers.TestObserver
import org.junit.Test

class AsyncCommandTest {

    @Test
    fun givenSubscribedToExecutionWhenExecuteThenAssertValue() {

        val mockResult = mock<Any>()

        val observable = Observable.just(mockResult)

        val executionObserver: TestObserver<Any> = TestObserver.create()
        val asyncCommand = AsyncCommand<Any, Any>({ observable })

        asyncCommand.execution.subscribe(executionObserver)

        val disposable = asyncCommand.execute().subscribe()

        executionObserver.assertValue(mockResult)

        disposable.dispose()
    }

    @Test
    fun givenSubscribedToErrorsWhenExecuteThenAssertException() {

        val exception = Exception()

        val actionObservable = Observable.error<Any>(exception)

        val action = stubAction(null, actionObservable)

        val errorObserver: TestObserver<Throwable> = TestObserver.create()
        val asyncCommand = AsyncCommand<Nothing, Any>(action)

        asyncCommand.errors.subscribe(errorObserver)

        val disposable = asyncCommand.execute().subscribe()

        errorObserver.assertValue(exception)

        disposable.dispose()
    }

    @Test
    fun givenSubscribedToExecutionWhenExecuteWithValueThenAssertValue() {

        val expectedResult = Any()
        val actionObservable = Observable.just(expectedResult)
        val stubbedInput = Any()

        val action = stubAction(stubbedInput, actionObservable)

        val executionObserver: TestObserver<Any> = TestObserver.create()
        val asyncCommand = AsyncCommand<Any, Any>(action)

        asyncCommand.execution.subscribe(executionObserver)

        val disposable = asyncCommand.execute(stubbedInput).subscribe()

        executionObserver.assertValue(expectedResult)

        disposable.dispose()
    }

    @Test
    fun givenSubscribedToExecutingWhenExecuteWithValueThenAssertStartAndFinishExecutingValues() {

        val expectedResult = Any()
        val actionObservable = Observable.just(expectedResult)
        val stubbedInput = Any()

        val action = stubAction(stubbedInput, actionObservable)

        val executingObserver: TestObserver<Boolean> = TestObserver.create()
        val asyncCommand = AsyncCommand<Any, Any>(action)

        asyncCommand.executing.subscribe(executingObserver)

        val disposable = asyncCommand.execute().subscribe()

        executingObserver.assertValues(true, false)

        disposable.dispose()
    }

    @Test
    fun givenSubscribedToErrorsWhenExecuteWithValueThenAssertException() {

        val exception = Exception()
        val actionObservable = Observable.error<Any>(exception)
        val stubbedInput = Any()

        val action = stubAction(stubbedInput, actionObservable)

        val errorObserver: TestObserver<Throwable> = TestObserver.create()
        val asyncCommand = AsyncCommand<Any, Any>(action)

        asyncCommand.errors.subscribe(errorObserver)

        val disposable = asyncCommand.execute(stubbedInput).subscribe()

        errorObserver.assertValue(exception)

        disposable.dispose()
    }

    private fun <Input : Any, Result : Any> stubAction(stubbedInput: Input? = null, action: Observable<out Result>)
            : (input: Input?) -> Observable<out Result> {
        return { input: Any? ->
            if (input == stubbedInput) {
                action
            } else {
                throw IllegalArgumentException()
            }
        }
    }
}