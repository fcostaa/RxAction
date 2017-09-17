package com.felipecosta.rxcommand

import com.jakewharton.rxrelay2.PublishRelay
import io.reactivex.Completable
import io.reactivex.Observable


class AsyncCommand<Input : Any, Result : Any>(private val action: (input: Input?) -> Observable<out Result>) : Command<Input> {

    private val inputRelay: PublishRelay<Any>

    private val executingRelay: PublishRelay<Boolean>

    private val throwablePublishRelay: PublishRelay<Throwable>

    private val elementsPublishRelay: PublishRelay<Result>

    init {
        this.inputRelay = PublishRelay.create()
        this.executingRelay = PublishRelay.create()

        this.throwablePublishRelay = PublishRelay.create()
        this.elementsPublishRelay = PublishRelay.create()
    }

    override fun execute(input: Input?): Completable {
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

    val execution: Observable<Result>
        get() = elementsPublishRelay
}