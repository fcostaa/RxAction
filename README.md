# RxAction

[![Build Status](https://travis-ci.org/felipehjcosta/kotlin-rxjava-android.svg?branch=master)](https://travis-ci.org/felipehjcosta/kotlin-rxjava-android)
[![codecov](https://codecov.io/gh/felipehjcosta/RxAction/branch/master/graph/badge.svg)](https://codecov.io/gh/felipehjcosta/RxAction)
[![codebeat badge](https://codebeat.co/badges/a4d4b1a5-cce8-4f2b-b4f8-bae6614d3aa2)](https://codebeat.co/projects/github-com-fcostaa-rxcommand-master)

This library is used with [RxJava2](https://github.com/ReactiveX/RxJava) to provide an abstraction on top of observables: actions, based on [Action](https://github.com/RxSwiftCommunity/Action).

How to use
--------

RxActions accept an `action`: a lambda that takes some input and produces an observable. When the method `execute()` is called, it passes its parameter to this lambda and subscribes to the action managing the states, such as elements, executing, error as observables using [RxJava2](https://github.com/ReactiveX/RxJava). Often the execution is triggered by some interaction in the UI like when the user clicks a button.

#### As Action

```kotlin
val action: RxAction<Any, List<Character>> { input -> characterRepository.fetchCharacters()}

...

action.elements.subscribe { /* item state */ }
    
action.executing.subscribe { /* executing state */ }

action.errors.subscribe { /* error state */ }
                
```

#### As Command in ViewModel

```kotlin
class CharacterListViewModel(private val characterRepository: CharacterRepository) {

    private val usersAction: RxAction<Any, List<Character>>;

    init {
        usersAction = RxAction { input -> characterRepository.fetchCharacters() }
    }
    
    val loadItemsCommand: RxCommand
        get() = usersAction
    
    val items: Observable<List<Character>>
        get() = usersAction.elements
        
    val showLoading: Observable<Boolean>
        get() = usersAction.executing
        
    val showLoadItemsError: Observable<Boolean>
        get() = usersAction.errors.map { true }
}

...

viewModel.items.subscribe { /* setup items on the screen */ }
    
viewModel.showLoading.subscribe { /* show loading or content to the user */ }

viewModel.showLoadItemsError.subscribe { /* show failure to the user */ }
                
viewModel.loadItemsCommand.execute().subscribe()
                
```

Download
--------

```groovy
    compile 'com.fcosta:rxaction:1.1.0'
    compile 'io.reactivex.rxjava2:rxjava:2.1.3'
    compile 'com.jakewharton.rxrelay2:rxrelay:2.0.0'
```

Bugs and Feedback
--------

For bugs, feature requests, and discussion please use [GitHub Issues][issues].

Download
--------

  MIT License
  
  Copyright (c) 2016 Felipe Costa
  
  Permission is hereby granted, free of charge, to any person obtaining a copy
  of this software and associated documentation files (the "Software"), to deal
  in the Software without restriction, including without limitation the rights
  to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
  copies of the Software, and to permit persons to whom the Software is
  furnished to do so, subject to the following conditions:
  
  The above copyright notice and this permission notice shall be included in all
  copies or substantial portions of the Software.
  
  THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
  IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
  FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
  AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
  LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
  OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
  SOFTWARE.
