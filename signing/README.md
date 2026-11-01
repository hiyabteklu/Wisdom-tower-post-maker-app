# Signing key

`wtp.keystore.b64` is the permanent signing key of Wisdom Tower Post, stored as text. When Gradle runs, `app/build.gradle.kts` turns it back into `signing/wtp.keystore` (if that file is not there yet) and signs the release APK with it. No workflow change and no extra step is needed, on GitHub or in Android Studio.

Because every build is signed with the same key, a new APK installs over the old one and keeps your data. Do not delete or replace this file: a different key brings back the 'uninstall first' problem, and your saved posts would be lost again.

This repo is private and the app is personal, so the key and its password live in the repo. If the repo is ever made public, make a new key and move the password into GitHub secrets.
