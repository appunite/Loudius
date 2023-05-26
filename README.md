# Loudius - Android experimental playground

## 📢 Project overview

Our app is a sample Android application designed to showcase some solutions to the architecture,
networking layer, and Jetpack Compose. It provides a basic user interface and functionality that can
be expanded upon to suit specific needs.

The app is open-source and intended for use by developers who are interested in expanding their
knowledge about Android development. The main functionalities are:

- login through GitHub OAuth,
- list user’s pull requests,
- ping collaborators for a faster code review.

The small range of functionalities leads to the ease of understanding the code and conducting
experiments with different development libraries and tools.

<https://user-images.githubusercontent.com/72873966/229077972-0e22227f-e90c-43e2-a604-b23410da7da2.mov>

## ⚙️ Tech/framework used

- Jetpack Compose
- Hilt
- Kotlin Coroutines
- Retrofit
- OkHttp3
- Gson

## 🔬 Experiments, the purpose of the project

Our project is designed for those who want to experiment with different solutions, architectures,
and libraries in the Android development world. We believe that experimenting is the key to
improving your skills and finding the best solutions for your needs.

We encourage everyone to join us and create their own experiments. You can experiment with anything
related to Android development - UI, performance, architecture, libraries, and more.

To create your own experiment, simply download our repository, create a new branch, and start
exploring. Once you're done, create a pull request, and our community will review it. We believe
that by sharing our ideas with each other, we can all learn and improve.

We welcome all levels of experience in our community, whether you're a beginner or an expert. We're
all here to learn and grow together.

So come, join us in Loudius - Android experimental playground, and let's experiment together!

### Rules for Experiments

We have a few rules for experiments to keep everything organized and consistent:

- Every experiment should be on a separate branch in the experiment folder.
- The branch should be named after the experiment.
- The experiment should not be merged into the develop branch.
- The experiment should have a clear purpose and goal.
- The experiment should not interfere with the stability of the existing codebase.

### Example Experiment

Here's an example of an experiment that meets our rules:

**Branch Name:** experiment/navigation-by-voyager-library\
**Purpose:** Check compose navigation with voyager library. Compare that with standard way.\
**Goal:** To resolve which navigation is better for compose. What are the pros and cons of each way.\
**Method:** Implement navigation with voyager library.

## 🚀 Project setup

In order to properly start the application and use it, the CLIENT_SECRET environment variable must be set on your computer. CLIENT_SECRET is a GitHub client secret key provided from ``Settings -> Developer Settings -> OAuth Apps -> my application``.

If you're AppUniter, you can find this secrets [here](https://www.notion.so/appunite/Github-Secrets-0c2c6c1b56e2472c8a4752241f1e20d3?pvs=4).

If you're not, don't worry, here's a video to help you create a new one:

https://github.com/appunite/Loudius/assets/72873966/4820b6df-81ca-48ed-9f3c-425011b758dd

### How to set environmental variable on mac?

1. Launch zsh (command `zsh`)
2. `$ echo 'export CLIENT_SECRET=you know what' >> ~/.zshenv`
3. Restart Android studio and Terminal.
4. `$ echo $CLIENT_SECRET`

## 🧑🏻‍🎓 Contributing

We believe that there is no ideal code and that every code can be improved. Therefore, we welcome
every issue and new idea. We encourage you to open a new issue or pull request, as we can all learn
from each other.

## ⭐️ License

     Copyright (C) 2023 AppUnite

     Licensed under the Apache License, Version 2.0 (the "License");
     you may not use this file except in compliance with the License.
     You may obtain a copy of the License at

         http://www.apache.org/licenses/LICENSE-2.0

     Unless required by applicable law or agreed to in writing, software
     distributed under the License is distributed on an "AS IS" BASIS,
     WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
     See the License for the specific language governing permissions and
     limitations under the License.
