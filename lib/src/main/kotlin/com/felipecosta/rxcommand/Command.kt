package com.felipecosta.rxcommand

import io.reactivex.Completable

interface Command<in Input : Any> {
    fun execute(input: Input? = null): Completable
}