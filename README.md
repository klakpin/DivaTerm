# 🩸 DivaTerm

### *"The terminal's power awakens."*

[//]: # (<p align="center">)

[//]: # (  <img src="https://i.imgur.com/diva-term-banner.png" alt="DivaTerm in action" width="600"/>)

[//]: # (</p>)

*Java terminal components with a Diva elegance*

---

## ✨ **Features**

- **Simplicity** – This is not a framework for full-featured TUI rather than basic components for command-based CLI
  applications.
- **Sleek, Rich Styling** – ANSI colors, bold prompts, and smooth animations.
- **JLine3-Powered** – Sharp and precise.
- **Ready to use** – Jump in straight to terminal components.

```java
try(var presenter = ConsoleTerminalPresenter.standard()){
        presenter.

message("Welcome to app");

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

### Gradle

```groovy
dependencies {
    implementation("io.github.klakpin:divaterm:0.0.2")
}
```

### Maven

```xml

<dependency>
    <groupId>io.github.klakpin</groupId>
    <artifactId>divaterm</artifactId>
    <version>0.0.2</version>
</dependency>
```

## 🩸 **Quickstart**

Awaken your terminal's power with the high-level presenter and use it directly:

```java
var presenter = ConsoleTerminalPresenter.standard();

presenter.successMessage("Action completed");
```

### Available components in presenter are:

- Messages:
    - plain
    - success
    - error
    - bracket
    - error bracket
- Waiting:
    - simple waiting
    - waiting with details
- Prompt:
    - simple prompt
    - prompt with default value
- Choice:
    - string single choice
    - string multichoice
    - choice builder
- Confirmation pop-up

---

## 🌙 **Usage Options**

DivaTerm offers three elegant ways to bring terminal magic to your Java applications:

1. **High-Level Presenter** (Recommended)  
   The simplest way to create beautiful terminal interfaces with minimal code:
   ```java
   var presenter = ConsoleTerminalPresenter.standard();
   ```

Note: the high-level presenter assumes that application is run in interactive terminal with rich colors.

2. **Terminal Components Factory**  
   For more control, create individual components through our factory:
   ```java
    // JLine terminal instance
    var terminal = new JlineTerminalFactory().buildTerminal();        
    // Color palette for elements                                                      
    var colorPalette = new DefaultTerminalColorPalette(Collections.emptyMap(), true);
    // Executor for drawing async updates, like in a waiting component
    var drawingExecutor = Executors.newSingleThreadScheduledExecutor();
   
    // Build components factory
    var factory = new TerminalComponentFactory(terminal, drawingExecutor, colorPalette);
   
    // Use factory to show the element
    factory.buildConfirm().confirm(confirmationText);
   ```

3. **Direct Components Usage**  
   For maximum flexibility, use components directly:
   ```java
   // JLine terminal instance
   var terminal = new JlineTerminalFactory().buildTerminal();        
   // Color palette for elements                                                      
   var colorPalette = new DefaultTerminalColorPalette(Collections.emptyMap(), true);
   
   var messageComponent = new TerminalMessage(terminal, colorPalette);
   messageComponent.printMessage(message, ColorFeature.error);
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