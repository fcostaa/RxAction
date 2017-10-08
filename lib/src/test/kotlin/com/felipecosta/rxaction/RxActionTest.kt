package com.felipecosta.rxaction

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

        val rxAction = RxAction<Any, Any> { just(expectedResult) }

        rxAction.elements.subscribe(executionObserver)

        rxAction.execute().subscribe()

        executionObserver.assertValue(expectedResult)
    }

    @Test
    fun givenSubscribedToErrorsWhenExecuteThenAssertException() {

        val expectedException = Exception()

        val rxAction = RxAction<Any, Any> { error<Any>(expectedException) }

        rxAction.errors.subscribe(errorObserver)

        rxAction.execute().subscribe()

        errorObserver.assertValue(expectedException)
    }

    @Test
    fun givenSubscribedToExecutionWhenExecuteWithValueThenAssertValue() {

        val expectedResult = Any()
        val stubbedInput = Any()

        val rxAction = RxAction<Any, Any>(stubAction(stubbedInput, just(expectedResult)))

        rxAction.elements.subscribe(executionObserver)

        rxAction.execute(stubbedInput).subscribe()

        executionObserver.assertValue(expectedResult)
    }

    @Test
    fun givenSubscribedToExecutingWhenExecuteWithValueThenAssertStartAndFinishExecutingValues() {

        val expectedResult = Any()
        val stubbedInput = Any()

        val rxAction = RxAction<Any, Any>(stubAction(stubbedInput, just(expectedResult)))

        rxAction.executing.subscribe(executingObserver)

        val disposable = rxAction.execute().subscribe()

        executingObserver.assertValues(true, false)

        disposable.dispose()
    }

    @Test
    fun givenSubscribedToErrorsWhenExecuteWithValueThenAssertException() {

        val expectedException = Exception()
        val stubbedInput = Any()

        val rxAction = RxAction<Any, Any> { error<Any>(expectedException) }

        rxAction.errors.subscribe(errorObserver)

        rxAction.execute(stubbedInput).subscribe()

        errorObserver.assertValue(expectedException)
    }

    private fun <Input : Any, Result : Any> stubAction(stubbedInput: Input, action: Observable<out Result>)
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