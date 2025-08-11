# Kindergarten Math App (Android)

A university assignment project by building kid-friendly Android app featuring mini-games to build early numeracy skills. The current implementation includes a complete Counting game, with stubs/hooks for Number Recognition and Missing Number.

# ✨ Features

Three game modes from a home screen: Counting, Number Recognition, Missing Number

Difficulty toggle (Easy / Hard) on the home screen, passed via Intent

Audio feedback for correct/incorrect answers (res/raw/correct.mp3, res/raw/wrong.mp3)

Progress bar per 10-question round, with end-of-round summary dialog

Ten-frame grid layout that auto-sizes and wraps for any count

Default difficulty is EASY. HARD expands the number range.

# 📱 Screenshots

Add images to docs/images and link them here.

Home
<img width="314" height="697" alt="image" src="https://github.com/user-attachments/assets/d1dcca3f-9b2e-47ed-a341-5d611dd35a3e" />


Counting
<img width="291" height="650" alt="image" src="https://github.com/user-attachments/assets/09278cf9-01d1-496e-bd36-6b7a46dc6b7d" />


Number Recognition
<img width="292" height="652" alt="image" src="https://github.com/user-attachments/assets/156232d3-f38a-43ba-94be-7fa2c9c0983f" />


Missing Number
<img width="295" height="659" alt="image" src="https://github.com/user-attachments/assets/f41fe771-a5a9-4a2d-b80d-d7343f3d0969" />


# 🚀 Getting Started

Prerequisites

Android Studio (Giraffe or newer recommended)

Gradle (use the project wrapper)

Android SDK / build tools matching your project build.gradle

Run

Clone the repo and open in Android Studio.

Sync Gradle.

Run on an emulator or physical device (USB debugging enabled).

No special permissions are required based on current code.


# 🧩 Game Modes

1) Counting (implemented)

Goal: Count the number of stars displayed in a responsive 10-column grid and pick the correct number.

Flow:

10 questions per round (roundSize = 10)

For each question:

Random count n is generated in the range [lo, hi] based on difficulty

A ten-frame grid renders n stars; empty cells fill the row/column to maintain alignment

Four answer options are generated (correct + 3 unique distractors), shuffled

Tap to answer → immediate color feedback + toast + sound

Progress advances; after 10 questions, show a round summary dialog with score and replay option

Difficulty ranges in Counting:

EASY: lo = 0, hi = 10

HARD: lo = 0, hi = 99



2) Number Recognition (planned)

Goal: Show a numeral; child chooses the matching option.

10 questions per round (roundSize = 10)

For each question:

A random number is chosen based on the difficulty

It is then converted to a word. 

Four answer options are generated (correct + 3 unique distractors), shuffled

Tap to answer → immediate color feedback + toast + sound

Progress advances; after 10 questions, show a round summary dialog with score and replay option

Difficulty ranges in Counting:

EASY: lo = 0, hi = 10

HARD: lo = 0, hi = 99


3) Missing Number (planned)

Goal: Show a short sequence with a gap (e.g., 3, 4, __, 6); child selects the missing number.

10 questions per round (roundSize = 10)

For each question:

Random 5 numbers in a sequence are generated based on difficulty

One number out of five from the sequence is chosen and hidden. 

Four answer options are generated (correct + 3 unique distractors), shuffled

Tap to answer → immediate color feedback + toast + sound

Progress advances; after 10 questions, show a round summary dialog with score and replay option

Difficulty ranges in Counting:

EASY: lo = 0, hi = 10

HARD: lo = 0, hi = 99
📄 License

Choose a license (MIT/Apache-2.0) and place the file at /LICENSE. Update this section to reference it.

🙌 Credits

Icons: https://x.com/InanisVirus/status/1457860829060157443

Built with AndroidX + Material Components
