package com.felipecosta.rxaction

import com.jakewharton.rxrelay2.PublishRelay
import io.reactivex.Completable
import io.reactivex.Observable

class RxAction<Input : Any, Element : Any>(
    private val action: (input: Input?) -> Observable<out Element>
) : RxCommand<Input> {

    private val executingRelay: PublishRelay<Boolean> = PublishRelay.create()

    private val throwablePublishRelay: PublishRelay<Throwable> = PublishRelay.create()

    private val elementsPublishRelay: PublishRelay<Element> = PublishRelay.create()

    override fun execute(input: Input): Completable {
        return Observable.defer { action(input) }
            .doOnSubscribe { executingRelay.accept(true) }
            .doOnNext { elementsPublishRelay.accept(it) }
            .doOnNext { executingRelay.accept(false) }
            .ignoreElements()
            .doOnError { throwablePublishRelay.accept(it) }
            .doOnError { executingRelay.accept(false) }
            .onErrorResumeNext { Completable.complete() }
    }

    val executing: Observable<Boolean>
        get() = executingRelay

    val errors: Observable<Throwable>
        get() = throwablePublishRelay

    val elements: Observable<Element>
        get() = elementsPublishRelay
}
