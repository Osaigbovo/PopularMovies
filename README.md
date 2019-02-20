
# <h1 align="center"> <img src="https://github.com/Osaigbovo/PopularMovies/blob/master/app/src/main/res/mipmap-xxxhdpi/ic_launcher.png" width="40">  PopularMovies</h1>
This is an App made for Udacity Android Nanodegree Project 2.
Discover Top Rated and Popular Movies with your friends and family using this app, also find out more infomation about each movie.
This app uses an API provided by https://www.themoviedb.org/.
It is made with latest Android Architecture Components like LiveData, ROOM, ViewModel and Paging library. It uses Retrofit for network calls, uses OkHTTP for logging, uses
Dagger2 for Dependency Injection, Glide for image processing, airBnB's Lottie for animation, and RxJava for reactive programming.
Also the layouts are designed using Constraint Layout.


## Features:
* Browse through the Top Rated, Popular and your Favorite Movies.
* Saves favorite movies into Room database for offline access.
* Search for movies.
* Swipe to delete favorite movies.
* Watch and send movie trailers.
* Read movie reviews.
* MVVM with Android Architecture Components
* Endless scrolling using Android Paging library.
* Handles network status and network failures
* Material Design.
* UI optimized for phone and tablet


## Screenshots
<img src="../master/art/home.png" width="240"> <img src="../master/art/options.png" width="240"> <img src="../master/art/favorite.png" width="240">

<img src="../master/art/swipe to delete.png" width="240"> <img src="../master/art/search.png" width="240">

<img src="../master/art/movie deets.png" width="240"> <img src="../master/art/movie deetss.png" width="240"> <img src="../master/art/movie deetsss.png" width="240">


<h2>Steps To Run The App</h2>
<p>The app uses themoviedb.org API to get movie information and posters. You must provide your own API key in order to build the app.</p>
<ol>
<li>In your Android directory</li>
<li>Open build.gradle (Module)</li>
<li>Paste your API key under buildTypes node -> debug -> buildConfigField </li>
</ol>
<p>Build the project and Run</p>


## Libraries

* [Android Architecture Components](https://developer.android.com/topic/libraries/architecture/)
* [Android Paging](https://developer.android.com/topic/libraries/architecture/paging/)
* [Lottie](https://github.com/airbnb/lottie-android)
* [Dagger 2](https://github.com/google/dagger)
* [Retrofit 2](https://github.com/square/retrofit)
* [RxJava 2 & RxAndroid 2](https://github.com/ReactiveX/RxAndroid)
* [Glide](https://github.com/bumptech/glide)
* [ButterKnife](https://github.com/JakeWharton/butterknife)
* [Youtube](https://github.com/youtube/yt-android-player)
* [Timber](https://github.com/JakeWharton/timber)
* [Leak Canary](https://github.com/square/leakcanary)
* [Constraint Layout](https://developer.android.com/reference/android/support/constraint/ConstraintLayout)
* [FlowLayoutManager](https://github.com/xiaofeng-han/AndroidLibs/tree/master/flowlayoutmanager)

# License

	MIT License
	
	Copyright 2018 Osaigbovo Odiase

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