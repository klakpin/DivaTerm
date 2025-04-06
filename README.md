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
try (var presenter = ConsoleTerminalPresenter.standard()) {
   var actions = List.of("first", "second", "third", "fourth", "fifth");
   
   var choice = presenter.stringChoice("What action you want to perform?", actions);

   var confirmed = presenter.confirm(choice + " - are you sure?");

   if (confirmed) {
      presenter.successMessage("Action completed");
   }
}
```

[//]: # (<p> <img src="https://i.imgur.com/cBQFvIi.gif" alt="DivaTerm in action" width="600"/> </p>)

---

## 🏰 **Installation**

Add the **essence of DivaTerm** to your project:

### Gradle

```groovy
dependencies {
    implementation("io.github.klakpin:divaterm:0.0.6")
}
```

### Maven

```xml

<dependency>
    <groupId>io.github.klakpin</groupId>
    <artifactId>divaterm</artifactId>
    <version>0.0.6</version>
</dependency>
```

## 🩸 **Quickstart**

Awaken your terminal's power with the high-level presenter and use it directly:

```java
var presenter = ConsoleTerminalPresenter.standard();

presenter.successMessage("Action completed");
```

### Available components in presenter

#### Messages

- plain
- success
- error
- bracket
- error bracket

[//]: # (<p> <img src="https://i.imgur.com/El2AJ8B.gif" alt="DivaTerm in action" width="600"/> </p>)

#### Waiting

- simple waiting
- waiting with details

```java
var waitingFuture = CompletableFuture.runAsync(() -> {
   try {
      Thread.sleep(5000);
   } catch (InterruptedException e) {
      throw new RuntimeException(e);
   }
});

presenter.waitWhile("Waiting 5 seconds", waitingFuture);
```
<p> <img src="https://i.imgur.com/exDjVB5.gif" alt="DivaTerm in action" width="400"/> </p>

#### Prompt

- simple prompt
- prompt with default value

#### Choice

- string single choice
- string multichoice
- choice builder
```java
var dessertsChoices = /* init choices */
        
var selectedChoice = presenter.choice(cb ->
   cb.withQuestion("What dessert you want to have today?")
          .withOptions(new ArrayList<>(dessertsChoices))
          .withMaxDisplayResults(5)
          .withFilteringEnabled(true)
          .withFilterSimilarityCutoff(0.7)
);
```
<p> <img src="https://i.imgur.com/tQnsL0b.gif" alt="DivaTerm in action" width="400"/> </p>

```java
var dessertsChoices = /* init choices */
        
var selectedChoice = presenter.multiChoice(cb ->
        cb.withQuestion("What dessert you want to have today?")
            .withOptions(new ArrayList<>(dessertsChoices))
            .withMaxDisplayResults(5)
            .withMaxSelectResults(3)
            .withFilteringEnabled(true)
        );
```
<p> <img src="https://i.imgur.com/eHP6LH2.gif" alt="DivaTerm in action" width="400"/> </p>

#### Confirmation

```java
presenter.confirm();
```
<p> <img src="https://i.imgur.com/isbYbEm.gif" alt="DivaTerm in action" width="400"/> </p>

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
   For maximum flexibility, use components directly. Do not call the same component twice, most of them are single-use.
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