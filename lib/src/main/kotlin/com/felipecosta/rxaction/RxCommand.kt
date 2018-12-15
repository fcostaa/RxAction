package com.felipecosta.rxaction

import io.reactivex.Completable

interface RxCommand<Input : Any> {
    @Suppress("UNCHECKED_CAST")
    fun execute(input: Input = (Any() as Input)): Completable
}
