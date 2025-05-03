# SnakeKana

SnakeKana is an educational twist on the classic Snake game designed to help players learn **Hiragana** and **Katakana**. Players control a snake that eats kana symbols based on a given romaji prompt. The game features a built-in quiz mode, audio customization, and adjustable difficulty.

---

## How to Run the Program

### Prerequisites

To run this program, you need:

- An IDE like **[VSCode](https://code.visualstudio.com/)** or **IntelliJ**
- **Java Development Kit (JDK 17 or higher)**:  
  [Download here](https://www.oracle.com/java/technologies/javase-downloads.html)

### Run Instructions

1. Clone or download this repository.
2. Open a terminal in the project directory.
3. Compile all Java files:
   ```bash
   javac -encoding UTF-8 src/*.java
4. Run the game:
   ```bash
   java -cp .\src SnakeGame

---

## Design Overview

SnakeKana combines gameplay with language learning by turning the snake’s food into Japanese kana characters. The player must eat the correct symbol shown in the romaji prompt while avoiding incorrect ones.

### High-Level Description

- SnakeKana uses the core mechanics of a typical Snake game, and is modified to help people learn both Hiragana and Katakana.
  - This is done by substituting the normal "apple" that the snake would eat in place of Japanese characters (kanas).
  - There is always **one correct** kana shown, and the player must "eat" it to gain a point.
  - The player also has the option to have **1 - 10 incorrect** kana shown. These serve as distractions and will cause the accuracy to go down if "eaten."
- There is also a quiz to practice one character at a time.
  - This quiz can either be done with Hiragana or Katakana characters.
  - It involves the character popping up and the player having to type in the correct English equivalent.
  - There are also two hint buttons and a skip button to help the player if they are confused or forget.

### Purpose

- Teach Japanese syllabaries (Hiragana and Katakana) through repetition and gameplay.
- Make language learning engaging with visual feedback, sound effects, and real-time input.
- Offer customizations to support various learning styles and skill levels.

### Design Concepts

- Initial Design:
  - I initially was going to just add modifications to the generic snake game such as difficulty levels, speed adjustments, and a color picker.
  - However, after completing this, it did not feel like enough work to justify the project's 6-week length. Because of this, I decided to think outside the box and find a separate topic that I could use the base mechanics of the snake game for. That is when I had the idea for SnakeKana.
- Final Design:
  - Using the snake game that I had built up to this point, I added many modifications to allow the 6-week length to be justified. This included:
    - Overhauling the core logic of the snake game to spawn a correct character and up to 10 incorrect characters.
    - Tracking correct and incorrect choices.
    - Adding a quiz screen for all characters.
    - Creating a modification menu that includes:
      - Incorrect kana slider.
      - Snake speed slider.
      - SFX volume slider.
      - Music volume slider.
      - "No-Death" Mode checkbox.
      - "Infinite" Mode checkbox.
  - All this was done cleanly in a 900px x 900px UI that is user-friendly and easy to navigate.

### Images from the Game

- Home Screen
![image](https://github.com/user-attachments/assets/58528e37-bf43-4dbb-860f-ecd26576e87d)

- Menu/Settings Screen
![image](https://github.com/user-attachments/assets/f7a61b80-4efc-41cd-9d3b-b2c0b5b8c6a9)

- Gameplay Screen
![image](https://github.com/user-attachments/assets/d2692739-95ef-4f1d-842f-cfea103b07bf)

- Game Over Screen
![image](https://github.com/user-attachments/assets/602f4aa5-bb84-48c2-950f-0d8307275f3f)

- Victory Screen
![image](https://github.com/user-attachments/assets/3789ae55-ea61-46d8-84f3-02ef251aca48)

- Pause Screen
![image](https://github.com/user-attachments/assets/b3c9345c-7ff8-4541-bbf8-075c8f5c4ceb)

- Quiz Home Page
![image](https://github.com/user-attachments/assets/9fdce4d7-05d8-410a-a813-871183de4fcc)

- Quiz Screen
![image](https://github.com/user-attachments/assets/f58152bb-d7ff-4b9c-ab93-9337904df13a)

---

## Preliminary Design Verification

### Testing Steps

- My initial design was a very basic snake game that I created following a YouTube tutorial. Once I got that fully working, I moved on to the modifications that I had initially intended.
  - Initial Modifications:
    - Difficulty Levels:
      - Designed 5 different levels.
      - These levels would trigger after eating 5 apples, and each level would increase the speed of the snake.
    - Color Picker:
      - Gave a variety of colors that the player could choose for their snake.
  - These were verified through tests of playing the game to see how the logic was handled in real time.
- Using this preliminary design, I used it as the foundation to create the final design you see now.

---

## Design Implementation

### Overall Structure

- Java GUI built with `JFrame`, `JPanel`, and custom painting.
- Multiple screens: Home, Game, Quiz, Settings, Help, Pause.
- Real-time input via custom `GlobalKeyDispatcher`.

### Main Components

| Class | Purpose |
|-------|---------|
| `SnakeGame.java` | Entry point and screen manager |
| `GameFrame.java` | Main window setup |
| `GamePanel.java` | Snake logic, rendering, collisions |
| `KanaManager.java` | Kana selection and state tracking |
| `QuizScreen.java` | Flashcard-like quiz feature |
| `MusicManager.java`, `SoundManager.java` | Audio playback and control |
| `GameSettings.java` | Central configuration object |

### Customization Options

- **No-Death Mode** via JCheckBox
  - Allows the player to never die.
  - The snake can pass through walls and itself.
- **Infinite Mode** via JCheckBox
  - Kanas that the player gets correct are not added to the `gottenCorrect` list.
  - Allows the user to play until they wish to stop.
- **Snake speed** via JSlider
  - Slider from 1 - 10 (1 being slowest, 10 being fastest).
  - Player adjusts the snake's speed freely.
- **SFX and music volume** via JSlider
- **Amount of "wrong" kana** via JSlider
- Music hotkeys:  
  - `J`: Previous song  
  - `L`: Next song

---

## Design Testing

### Final Testing Plan

- After implementing any change, I would run the program to test it thoroughly. I made sure to do both predictable and unpredictable actions to ensure the game handled all edge cases.

### Notable Tests and Fixes 

- Multiple Characters (Kanas) Showing at Once
  - This was probably the most difficult part of the project.
  - The goal here is to have one correct kana show up that the player must "eat" with the snake. Then there also will be 0 - 10 incorrect kanas that show up. These are meant to serve as a distraction and help the player learn the kanas more effectively.
  - A lot of logic and testing went into this. Below, I will explain each element of it.
  - Placing the Kanas:
    - In order to correctly place a Kana, a few checks were necessary:
      - The coordinates could not be the same as any part of the snake or a Kana that was already placed. Also, I made it so it would spawn at least 5 units away from the snake's head so the player would not accidentally choose another kana right after picking one.
      - If the kana that was currently being processed was set to the correct kana, then it could not be a kana already in the `gottenCorrect` list (when Infinite Mode is off).
      - If the kana was meant to be an incorrect kana, there could not be any duplicates.
      - Finally, if "Both" was chosen at the Home Screen, then an incorrect kana could not be chosen if it represents the same English equivalent as the correct kana, but within the other set of characters.
  - Handling the User's Choice:
    - The `PlacedKana` class was the key to solving this.
    - It is a very simple helper class, but it allows me to be able to reference the coordinates of all the Kanas on the board, which Kana it represents, and whether the Kana is "correct" or not. Here is the class:
    - ![image](https://github.com/user-attachments/assets/dc3c81e3-a6bf-4cae-b554-1dca3bd1360d)
    - Using this, I was simply able to loop through the `PlacedKana` objects to see if the snake head was equal to the coordinates of any of them.
    - When this was the case, I would then check whether this given `PlacedKana` object was assigned `correct` to be `true` or `false`.
    - Given this information, I would update all other values appropriately. Below is a function that shows this in action:
    - ![image](https://github.com/user-attachments/assets/eb2a08c5-0172-4900-bd09-da1dc76803b9)
    - `newKanas()` is then called to repeat this whole process.
- Opening New Screens
  - To do this, I had to close the current screen (`frame`) and open a new screen. This was difficult at first, but after understanding the logic of it, it became a simple process to add a new screen. Below is a screenshot of code that opens the gameplay screen:
  - ![image](https://github.com/user-attachments/assets/2360445a-37cd-4276-aa4a-dcc0f4dad2d3)
  - After understanding this logic, I had to figure out how to get something on the screen to allow the player to switch screens. After testing different built in features such as JButton, I decided to actually draw my own "Button."
    - This button is simply just text, but I liked the appearance of it.
    - The issue was, since it was just text, I was not sure how to treat it as a button.
    - The solution I found to this was to use the MouseListener to save the coordinates of where I click on the screen:
      - ![image](https://github.com/user-attachments/assets/748c8b97-8e21-4d82-b1d1-a17c4b94d98d)
    - Using this, I was able to check if the coordinates matched up with the area that I wrote my text in:
      - ![image](https://github.com/user-attachments/assets/1998929e-c86f-42c1-91bd-ddcde0b0ed52)
  - After solving this, I was able to create these text buttons and open new screens very easily.
- No-Death Mode
  - This mode allows players to never die, and focus more on playing the game and learning the characters. In order to implement this, I had to understand how to reposition the snake on the screen when it hit one of the edges.
  - Initially, when doing this, I did not take into account the `GameConstants.UNIT_SIZE` variable. Because of this, the snake would be one unit off when going through the walls.
  - After accounting for this, the snake smoothly went from one edge to the other edge. Below is a code snippet of that:
    - ![image](https://github.com/user-attachments/assets/f0874987-dce9-4c7d-a7b9-3d6fcd1f95e7)
  - Below is a picture of the mode working correctly:
    - ![image](https://github.com/user-attachments/assets/4fafe739-9a88-4b81-becf-80fc3ac6aa66)

## Conclusions and Future Work

### Summary

Overall, I really enjoyed this project. I was able to combine two things I am passionate about and create a functioning product with them.

### Future Improvements

- Currently I have the screen locked at 900px x 900px. This is okay for the type of application it is, however, it would be nice if the resolution was modifiable. Being able to change it to fullscreen would add a great touch to it and make the game more immersive.
- Another addition I would like to add to the game is a summary screen that shows all the kana that you got incorrect, both in the game itself and the quiz. Currently, it just tells you how many you got wrong, but seeing the specific characters that you got incorrect would help players focus on learning those.

## References

- https://docs.oracle.com/javase/8/docs/api/javax/swing/JFrame.html
- https://www.youtube.com/watch?v=bI6e6qjJ8JQ&t=606s&ab_channel=BroCode
- https://stackoverflow.com/questions
