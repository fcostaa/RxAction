package com.felipecosta.rxcommand

import io.reactivex.Completable

interface Command {
    fun execute(): Completable
}