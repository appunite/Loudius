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
- Koin
- Kotlin Coroutines
- Ktor
- OkHttp3
- Kotlin Serialization

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
**Goal:** To resolve which navigation is better for compose. What are the pros and cons of each
way.\
**Method:** Implement navigation with voyager library.

## 🚀 Project setup

In order to properly start the application and use it, the `LOUDIUS_CLIENT_SECRET` and
`LOUDIUS_CLIENT_ID` environment variables must be set on your computer.

* `LOUDIUS_CLIENT_SECRET` is a GitHub client secret key
* `LOUDIUS_CLIENT_ID` is a GitHub client id

Both are provided from ``Settings -> Developer Settings -> OAuth Apps -> my application``.

If you're not AppUniter, here's a video to help you create such appliation:

<https://github.com/appunite/Loudius/assets/72873966/4820b6df-81ca-48ed-9f3c-425011b758dd>.

If you'd like to run end-to-end tests you'd also need `LOUDIUS_GITHUB_USER_NAME` and
`LOUDIUS_GITHUB_USER_PASSWORD` which are credentials to GitHub test account.
This is just a standard GitHub account that you can create by yourself.

If you're AppUniter, you can find those
secrets [here](https://www.notion.so/appunite/Github-Secrets-0c2c6c1b56e2472c8a4752241f1e20d3?pvs=4).

### How to set environmental variable on mac?

1. Launch zsh (command `zsh`)
2. `$ echo 'export LOUDIUS_CLIENT_SECRET="you know what"' >> ~/.zshenv`
3. `$ echo 'export LOUDIUS_CLIENT_ID="you know what"' >> ~/.zshenv`
4. optionally: `$ echo 'export LOUDIUS_GITHUB_USER_NAME="you know what"' >> ~/.zshenv`
5. optionally: `$ echo 'export LOUDIUS_GITHUB_USER_PASSWORD="you know what"' >> ~/.zshenv`
6. Restart Android studio and Terminal.
7. `$ echo $LOUDIUS_CLIENT_ID/$LOUDIUS_CLIENT_SECRET $LOUDIUS_GITHUB_USER_NAME/$LOUDIUS_GITHUB_USER_PASSWORD`

### Screenshots tests

We are using screenshot tests to check if the UI is not broken. We are recording screenshots on CI.
To do that - add `[New snapshots]` to the pull request title. Otherwise the snapshots are tested.

### Design system documentation

We also are having [design system documentation](components/README.md).

## 🧐 Firebase Analytics

Google Analytics serves as an application measurement solution, offering valuable insights into app
utilization and user engagement. Check out our [Analytics Documentation](docs/analytics.md)!

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
