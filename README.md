<p align="center">
<img src="https://github.com/xsahil03x/MovieMania/blob/master/app/src/main/res/mipmap-xxxhdpi/ic_launcher.png">
</p>

<h1 align="center">Popular Movies</h1>
<p align="center">This is an App made for Udacity Android Nanodegree Project 1.
It's an Android app to browse the Top Rated and Popular Movies.
This app uses an API provided by https://www.themoviedb.org/.
It is made with latest Android Architecture Components like LiveData, ViewModel and Paging library.
It uses Retrofit for network calls, uses Dagger2 for Dependency Injection, Glide for image processing, and RxJava for reactive programming.
Also the layouts are designed using Constraint Layout.</p>


<h2>Features</h2>
- Browse through the Top Rated and Popular Movies
- Offline access to movies list
- Caches data from Room Database (Refreshes every 60 minutes from Network)
- UI optimized for potrait and landscape
- Add movies to favorite and access them offline
- Design inspired from Pinterest


<h2>Steps To Run The App</h2>
<p>The app uses themoviedb.org API to get movie information and posters. You must provide your own API key in order to build the app.</p>
<p>If you do not have a gradle.properties file, create one</p>
<ol>
<li>Change you Android view to Project file in the directory</li>
<li>Right click > New > File</li>
<li>Put the name as gradle.properties</li>
</ol>
<p>Now paste THE_MOVIE_DB_API_KEY="your-api-key-here" in the gradle.properties file</p>
<p>Build the project and Run</p>



## Libraries

* [Android Architecture Components](https://developer.android.com/topic/libraries/architecture/)
* [Android Paging](https://developer.android.com/topic/libraries/architecture/paging/)
* [Retrofit2](https://github.com/square/retrofit)
* [RxJava & RxAndroid](https://github.com/ReactiveX/RxAndroid)
* [Glide](https://github.com/bumptech/glide)
* [ButterKnife](https://github.com/JakeWharton/butterknife)
* [Dagger2](https://github.com/google/dagger)
* [Timber](https://github.com/JakeWharton/timber)
* [Leak Canary](https://github.com/square/leakcanary)
* [Constraint Layout](https://developer.android.com/reference/android/support/constraint/ConstraintLayout)
* [FlowLayoutManager](https://github.com/xiaofeng-han/AndroidLibs/tree/master/flowlayoutmanager)

## License
Copyright 2018 Osaigbovo Odiasse
Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License. You may obtain a copy of the License at

http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR
CONDITIONS OF ANY KIND, either express or implied. See the License for the specific language governing permissions and limitations under the License.
