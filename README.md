# 🩸 DivaTerm

### *"The terminal's power awakens."*

[//]: # (<p align="center">)

[//]: # (  <img src="https://i.imgur.com/diva-term-banner.png" alt="DivaTerm in action" width="600"/>)

[//]: # (</p>)

*Java terminal components with a Diva elegance*

---

## ✨ **Features**

- **Sleek, Rich Styling** – ANSI colors, bold prompts, and smooth animations.
- **JLine3-Powered** – Sharp and precise.
- **Ready to use** – Jump in straight to terminal components.

```java
try (var presenter = ConsoleTerminalPresenter.standard()) {
    presenter.message("Welcome to app");
    
    var singleResult = presenter.stringChoice("What action you want to perform?",
        List.of("first", "second", "third", "fourth", "fifth"));
    
    var confirmation = presenter.promptBoolean("Are you sure?");
}
```

<p align="center">

  <img src="https://i.imgur.com/cBQFvIi.gif" alt="DivaTerm in action" width="600"/>

</p>

---

## 🏰 **Installation**

Add the **essence of DivaTerm** to your project:


### Gradle (Kotlin)
Add GitHub repository
```groovy
repositories {
        maven {
            name = "GitHubPackages"
            url = uri("https://maven.pkg.github.com/klakpin/DivaTerm")
            credentials {
                username = project.findProperty("gpr.user") ?: System.getenv("GITHUB_USERNAME")
                password = project.findProperty("gpr.key") ?: System.getenv("GITHUB_PERSONAL_TOKEN")
            }
        }
    }
```
Add dependency
```kotlin
dependencies {
    implementation("io.github.klakpin:divaterm:0.0.1-SHAPSHOT")
}  
```

---

## 🌙 **Quickstart**

Awaken your terminal’s power:

```java

```

---

## 🎶 **Diva’s Hymn (Contributing)**

This project thrives on **elegant code and sharp fangs** (bug reports).

- Fork → `git clone` → Put your magic (code).
- Submit a PR

---

## ⚔️ **Chevalier (Sister Project)**

For **Micronaut/GraalVM** warriors:  
👉 [Chevalier: The Elite CLI Runtime](https://github.com/klakpin/chevalier)

---

## 📜 **License**

[MIT](LICENSE) – *"Free as the night wind."*

### Attributions

This project uses:

- [JLine3](https://github.com/jline/jline3) (BSD 3-Clause).
- [Apache Commons](https://commons.apache.org/) (Apache 2.0)

<p align="right">
  <i>"Sing, my Diva."</i>
</p>