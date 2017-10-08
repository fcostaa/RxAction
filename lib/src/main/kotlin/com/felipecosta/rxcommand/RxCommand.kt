package com.felipecosta.rxcommand

import io.reactivex.Completable

interface RxCommand<in Input : Any> {
    fun execute(input: Input? = null): Completable
}