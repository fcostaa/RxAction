package com.felipecosta.rxcommand

import io.reactivex.Observable
import io.reactivex.Observable.error
import io.reactivex.Observable.just
import io.reactivex.observers.TestObserver
import org.junit.Test

class RxActionTest {

    val executionObserver: TestObserver<Any> = TestObserver.create()

    val errorObserver: TestObserver<Throwable> = TestObserver.create<Throwable>()

    val executingObserver: TestObserver<Boolean> = TestObserver.create()

    @Test
    fun givenSubscribedToExecutionWhenExecuteThenAssertValue() {

        val expectedResult = Any()

        val asyncCommand = RxAction<Any, Any>({ just(expectedResult) })

        asyncCommand.execution.subscribe(executionObserver)

        asyncCommand.execute().subscribe()

        executionObserver.assertValue(expectedResult)
    }

    @Test
    fun givenSubscribedToErrorsWhenExecuteThenAssertException() {

        val expectedException = Exception()

        val asyncCommand = RxAction<Nothing, Any>(stubAction(null, error<Any>(expectedException)))

        asyncCommand.errors.subscribe(errorObserver)

        asyncCommand.execute().subscribe()

        errorObserver.assertValue(expectedException)
    }

    @Test
    fun givenSubscribedToExecutionWhenExecuteWithValueThenAssertValue() {

        val expectedResult = Any()
        val stubbedInput = Any()

        val asyncCommand = RxAction<Any, Any>(stubAction(stubbedInput, just(expectedResult)))

        asyncCommand.execution.subscribe(executionObserver)

        asyncCommand.execute(stubbedInput).subscribe()

        executionObserver.assertValue(expectedResult)
    }

    @Test
    fun givenSubscribedToExecutingWhenExecuteWithValueThenAssertStartAndFinishExecutingValues() {

        val expectedResult = Any()
        val stubbedInput = Any()

        val asyncCommand = RxAction<Any, Any>(stubAction(stubbedInput, just(expectedResult)))

        asyncCommand.executing.subscribe(executingObserver)

        val disposable = asyncCommand.execute().subscribe()

        executingObserver.assertValues(true, false)

        disposable.dispose()
    }

    @Test
    fun givenSubscribedToErrorsWhenExecuteWithValueThenAssertException() {

        val exception = Exception()
        val stubbedInput = Any()

        val asyncCommand = RxAction<Any, Any>(stubAction(stubbedInput, error<Any>(exception)))

        asyncCommand.errors.subscribe(errorObserver)

        asyncCommand.execute(stubbedInput).subscribe()

        errorObserver.assertValue(exception)
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