
import java.awt.Color;
import java.util.Random;
import javalib.funworld.*;
import javalib.worldimages.Posn;
import javalib.worldimages.RectangleImage;
import tester.Tester;

/*
 * TEMPLATE 
 * -------- 
 * Fields 
 * ... this.blue ...                                 -- Button
 * ... this.red ...                                  -- Button
 * ... this.green ...                                -- Button
 * ... this.yellow ...                               -- Button
 * ... this.sequence ...                             -- ILoInt
 * ... this.temp ...                                 -- ILoInt
 * ... this.clickMode ...                            -- boolean
 * ... this.generator ...                            -- Random

 * Methods
 * ... this.run() ...                                -- boolean
 * ... this.makeScene() ...                          -- WorldScene
 * ... this.onTick() ...                             -- World
 * ... this.onMousePressed(Posn posn) ...            -- World
 * ... this.onMouseReleased(Posn posn) ...           -- World
 * ... this.buttonPressed(Posn posn) ...             -- int
 * ... this.darken(int color) ...                    -- SimonSays
 * ... this.resetButtons() ...                       -- SimonSays
 * ... this.resetTemp() ...                          -- SimonSays
 * ... this.pause() ...                              -- SimonSays
 * ... this.next() ...                               -- SimonSays
 * ... this.switchMode() ...                         -- SimonSays
 * ... this.addToSequence() ...                      -- SimonSays 
 * ... this.nextColor() ...                          -- int
 */


class SimonSays extends World {

  Button blue;
  Button red;
  Button green;
  Button yellow;
  ILoInt sequence;
  ILoInt temp;
  boolean clickMode;
  Random generator;

  //constructor with all fields
  SimonSays(Button blue, Button red, Button green, Button yellow, 
      ILoInt sequence, ILoInt temp, boolean clickMode, Random generator) {
    this.blue = blue;
    this.red = red;
    this.green = green;
    this.yellow = yellow;
    this.sequence = sequence;
    this.temp = temp;
    this.clickMode = clickMode;
    this.generator = generator;
  }

  //default constructor
  SimonSays() {
    this.blue = Utils.B1;
    this.red = Utils.B2;
    this.green = Utils.B3;
    this.yellow = Utils.B4;
    this.generator = new Random();
    this.sequence = new ConsLoInt(this.nextColor(), new MtLoInt());
    this.temp = sequence;
    this.clickMode = false;
  }

  //starts the game
  public boolean run() {
    return this.bigBang(Utils.GAME_SIZE, Utils.GAME_SIZE, Utils.GAME_SPEED);
  }

  // draws the game state
  public WorldScene makeScene() {
    WorldScene s = new WorldScene(Utils.GAME_SIZE, Utils.GAME_SIZE);
    return blue.draw(red.draw(green.draw(yellow.draw(s))));
  }

  //if not in clickMode plays, through the current sequence
  public World onTick() {
    if (clickMode) {
      return this;
    } else {
      //playing through the sequence in temp
      if (this.temp.rightButton(1)) {
        return this.darken(1).pause();
      } else if (this.temp.rightButton(2)) {
        return this.darken(2).pause();
      } else if (this.temp.rightButton(3)) {
        return this.darken(3).pause();
      } else if (this.temp.rightButton(4)) {
        return this.darken(4).pause();
      } else if (this.temp.rightButton(0)) {
        return this.resetButtons().next();
      } else {
        //temp is empty, base case, resets and changes mode
        return this.resetButtons().resetTemp().switchMode();
      }
    }  
  }

  //does nothing if playing back the sequence
  //if the wrong button is pressed ends the world
  //if the all the correct buttons are pressed,
  //wait until the user clicks again to show the next sequence
  public World onMousePressed(Posn pos) {
    if (this.clickMode) {
      if (this.temp instanceof MtLoInt) {
        return this.addToSequence().resetButtons().resetTemp().switchMode();
      } else {
        if (this.temp.rightButton(this.buttonPressed(pos))) {
          return this.darken(this.buttonPressed(pos)).next();
        } else {
          this.endOfWorld("You Done Son!");
        }
      } 
    }
    return this;
  }

  //resets buttons to defaults if in click mode
  public World onMouseReleased(Posn pos) {
    if (this.clickMode) {
      return this.resetButtons();
    } else {
      return this;
    }
  }

  // given a posn returns which button was pressed
  public int buttonPressed(Posn pos) {
    if (pos.x < Utils.BUTTON_SIZE && pos.y < Utils.BUTTON_SIZE ) {
      return 1;
    } else if (pos.x >= Utils.BUTTON_SIZE  && pos.y <= Utils.BUTTON_SIZE ) {
      return 2;
    } else if (pos.x >= Utils.BUTTON_SIZE  && pos.y > Utils.BUTTON_SIZE) {
      return 4;
    } else {
      return 3;
    }
  }

  //darkens the corresponding button
  public SimonSays darken(int color) {
    if (color == 1) {
      return new SimonSays(this.blue.clicked(),this.red,this.green,this.yellow,
          this.sequence, this.temp, this.clickMode, this.generator);
    } else if (color == 2) {
      return new SimonSays(this.blue,this.red.clicked(),this.green,this.yellow,
          this.sequence, this.temp, this.clickMode, this.generator);
    } else if (color == 3) {
      return new SimonSays(this.blue, this.red, this.green.clicked(), this.yellow,
          this.sequence, this.temp, this.clickMode, this.generator);
    } else if (color == 4) {
      return new SimonSays(this.blue, this.red, this.green, this.yellow.clicked(),
          this.sequence, this.temp, this.clickMode, this.generator);
    } else {
      return this;
    }
  }

  //resets all buttons to default values
  public SimonSays resetButtons() {
    return new SimonSays(Utils.B1, Utils.B2,Utils.B3,Utils.B4,
        this.sequence, this.temp, this.clickMode, this.generator);
  }

  //sets temp equal to sequence
  public SimonSays resetTemp() {
    return new SimonSays(this.blue, this.red, this.green, this.yellow,
        this.sequence, this.sequence, this.clickMode, this.generator);
  }

  //changes the first this in temp to a zero so onTick can draw the animation correctly
  public SimonSays pause() {
    return new SimonSays(this.blue, this.red, this.green, this.yellow,
        this.sequence, this.temp.changeFirst(0), this.clickMode, this.generator);
  }

  //removes the first int from temp
  public SimonSays next() {
    return new SimonSays(this.blue, this.red, this.green, this.yellow,
        this.sequence, this.temp.next(), this.clickMode, this.generator);
  }

  //negates clickMode
  public SimonSays switchMode() {
    return new SimonSays(Utils.B1, Utils.B2,Utils.B3,Utils.B4,
        this.sequence, this.temp, !(this.clickMode), this.generator);
  }

  //adds a random number to sequence
  public SimonSays addToSequence() {
    return new SimonSays(Utils.B1, Utils.B2,Utils.B3,Utils.B4,
        this.sequence.addToEnd(this.nextColor()), this.temp, this.clickMode, this.generator);
  }

  //generates the next random color
  public int nextColor() {
    return generator.nextInt(4) + 1;
  }
}

//holds constants
class Utils {
  static int GAME_SIZE = 400;
  static double GAME_SPEED = .5;
  static int BUTTON_SIZE = (Utils.GAME_SIZE / 2) ;
  static Button B1 = new Button(BUTTON_SIZE / 2, BUTTON_SIZE / 2, 
      Color.BLUE, BUTTON_SIZE);
  static Button B2 = new Button((int) (BUTTON_SIZE * 1.5), BUTTON_SIZE / 2,
      Color.RED, BUTTON_SIZE);
  static Button B3 = new Button(BUTTON_SIZE / 2, (int) (BUTTON_SIZE * 1.5), 
      Color.GREEN, BUTTON_SIZE);
  static Button B4 = new Button((int) (BUTTON_SIZE * 1.5), (int) (BUTTON_SIZE * 1.5),
      Color.YELLOW, BUTTON_SIZE);
  static Random testR = new Random(0);
}

/*
 * TEMPLATE 
 * -------- 
 * Fields 
 * ... this.x ...                                    -- int
 * ... this.y ...                                    -- int
 * ... this.color ...                                -- color
 * ... this.size ...                                 -- int

 * Methods
 * ... this.draw(WorldScene img) ...                 -- WorldScene
 * ... this.clicked() ...                            -- Button
 */

class Button {
  int x;
  int y;
  Color color;
  int size;

  Button(int x, int y, Color color, int size) {
    this.x = x;
    this.y = y;
    this.color = color;
    this.size = size;
  }

  //draws a button
  WorldScene draw(WorldScene img) {
    return img.placeImageXY(new RectangleImage(this.size, this.size, "solid", this.color),
        this.x, this.y);
  }

  //returns a new button with a darker color
  Button clicked() {
    return new Button(x,y, color.darker(), size);
  }

}

//This class represents a List of Colors
//A ILoInt is one of:
//- ConsLoInt(int, ILoInt)
//- MtLoInt()

/*
 * TEMPLATE 
 * -------- 
 * Methods
 * ... this.rightButton(int button) ...              -- boolean
 * ... this.changeFirst(int i) ...                   -- ILoInt
 * ... this.addToEnd(int n) ...                      -- ILoInt
 * ... this.next() ...                               -- ILoInt
 * ... this.mthuh() ...                              -- boolean
 */

interface ILoInt {

  //returns true if the given int equals first
  boolean rightButton(int button);

  //swaps the first int in the list with i
  ILoInt changeFirst(int i);

  //adds n to the end
  ILoInt addToEnd(int n);

  //moves on to the next sequence of the pattern
  ILoInt next();

  //returns true if the list is empty
  boolean mthuh();
}

/*
 * TEMPLATE 
 * -------- 
 * Fields 
 * ... this.first ...                                -- int
 * ... this.rest ...                                 -- ILoInt

 * Methods
 * ... this.rightButton(int button) ...              -- boolean
 * ... this.changeFirst(int i) ...                   -- ILoInt
 * ... this.addToEnd(int n) ...                      -- ILoInt
 * ... this.next() ...                               -- ILoInt
 * ... this.mthuh() ...                              -- boolean
 */

class ConsLoInt implements ILoInt {
  int first;
  ILoInt rest;

  ConsLoInt(int first, ILoInt rest) {
    this.first = first;
    this.rest = rest;
  }

  //returns true if the given int equals first
  public boolean rightButton(int button) {
    return this.first == button;
  }

  //moves on to the next sequence of the pattern
  public ILoInt next() {
    return rest;
  }

  //adds n to the end
  public ILoInt addToEnd(int n) {
    return new ConsLoInt(first, rest.addToEnd(n));
  }

  //swaps the first int in the list with i
  public ILoInt changeFirst(int i) {
    return new ConsLoInt(i, rest);
  }

  //returns true if the list is empty
  public boolean mthuh() {
    return false;
  }
}

class MtLoInt implements ILoInt {

  //returns false, always
  public boolean rightButton(int button) {
    return false;
  }

  //returns an empty
  public ILoInt next() {
    return this;
  }

  //adds n to the end
  public ILoInt addToEnd(int n) {
    return new ConsLoInt(n, this);
  }

  //returns an empty
  public ILoInt changeFirst(int i) {
    return this;
  }

  //returns true
  public boolean mthuh() {
    return true;
  }
}

class Examples {
  Button b1 = new Button(125, 125, Color.BLUE, 100);
  Button b2 = new Button(350, 125, Color.RED, 100);
  Button b3 = new Button(125, 350, Color.GREEN, 100);
  Button b4 = new Button(350, 350, Color.YELLOW, 100);

  Posn p1 = new Posn(0, 0);
  Posn p2 = new Posn(Utils.BUTTON_SIZE, 0);
  Posn p3 = new Posn(0, Utils.BUTTON_SIZE);
  Posn p4 = new Posn(Utils.BUTTON_SIZE, Utils.BUTTON_SIZE);
  Posn p5 = new Posn(Utils.BUTTON_SIZE, Utils.BUTTON_SIZE + 1);
  Posn p6 = new Posn(Utils.BUTTON_SIZE + 1, Utils.BUTTON_SIZE);
  Posn p7 = new Posn(Utils.BUTTON_SIZE + 1, Utils.BUTTON_SIZE + 1);


  ILoInt l1 = new ConsLoInt(1, new ConsLoInt(2, new ConsLoInt(3, 
      new ConsLoInt(4, new MtLoInt()))));
  ILoInt l2 = new ConsLoInt(2, l1);
  ILoInt l3 = new ConsLoInt(3, l2);
  ILoInt l4 = new ConsLoInt(4, l3);
  ILoInt l0 = new ConsLoInt(0, l4);

  SimonSays w = new SimonSays(Utils.B1, Utils.B2, Utils.B3, Utils.B4, 
      l1, l1, false, new Random(0));
  SimonSays w0 = new SimonSays(Utils.B1, Utils.B2, Utils.B3, Utils.B4, 
      l0, l0, false, new Random(0));
  SimonSays w2 = new SimonSays(Utils.B1, Utils.B2, Utils.B3, Utils.B4, 
      l2, l2, false, new Random(0));
  SimonSays w3 = new SimonSays(Utils.B1, Utils.B2, Utils.B3, Utils.B4, 
      l1, l3, false, new Random(0));
  SimonSays w4 = new SimonSays(Utils.B1, Utils.B2, Utils.B3, Utils.B4, 
      l4, l4, false, new Random(0));
  SimonSays s = new SimonSays(Utils.B1, Utils.B2, Utils.B3, Utils.B4, 
      l1, l1, true, new Random(5));
  SimonSays n = new SimonSays(Utils.B1, Utils.B2, Utils.B3, Utils.B4, 
      l1, new MtLoInt(), true, new Random(6));
  SimonSays n2 = new SimonSays(Utils.B1, Utils.B2, Utils.B3, Utils.B4, 
      l1, new MtLoInt(), true, new Random(6));



  //  boolean testBigBang(Tester t) {
  //    return new SimonSays().run();
  //  }
  //  

  boolean testAddToSequence(Tester t) {
    return t.checkExpect(w.addToSequence(),
        new SimonSays(Utils.B1, Utils.B2, Utils.B3, Utils.B4, 
            l1.addToEnd(3), l1, false, new Random(0)))
        &&  t.checkExpect(w.addToSequence(),
            new SimonSays(Utils.B1, Utils.B2, Utils.B3, Utils.B4, 
                l1.addToEnd(4), l1, false, new Random(1)));
  }

  boolean testButtonPressed(Tester t) {
    return t.checkExpect(w.buttonPressed(p1), 1)
        && t.checkExpect(w.buttonPressed(p2), 2)
        && t.checkExpect(w.buttonPressed(p3), 3)
        && t.checkExpect(w.buttonPressed(p4), 2)
        && t.checkExpect(w.buttonPressed(p5), 4)
        && t.checkExpect(w.buttonPressed(p6), 2)
        && t.checkExpect(w.buttonPressed(p7), 4);
  }

  boolean testDarken(Tester t) {
    return t.checkExpect(w.darken(0), w)
        && t.checkExpect(w.darken(1), 
            new SimonSays(Utils.B1.clicked(), Utils.B2, Utils.B3, Utils.B4,
                l1, l1, false, new Random(0)))
        && t.checkExpect(w.darken(2), 
            new SimonSays(Utils.B1, Utils.B2.clicked(), Utils.B3, Utils.B4,
                l1, l1, false, new Random(0)))
        && t.checkExpect(w.darken(3), 
            new SimonSays(Utils.B1, Utils.B2, Utils.B3.clicked(), Utils.B4,
                l1, l1, false, new Random(0)))
        && t.checkExpect(w.darken(4), 
            new SimonSays(Utils.B1, Utils.B2, Utils.B3, Utils.B4.clicked(),
                l1, l1, false, new Random(0)));
  }

  boolean testMakeScene(Tester t) {
    return t.checkExpect(w.makeScene(),
        Utils.B1.draw(Utils.B2.draw(Utils.B3.draw(Utils.B4.draw(
            new WorldScene(Utils.GAME_SIZE, Utils.GAME_SIZE))))));
  }

  boolean testNext(Tester t) {
    return t.checkExpect(w.next(), new SimonSays(Utils.B1, Utils.B2, Utils.B3, Utils.B4,
        l1, l1.next(), false, new Random(0)));
  }

  boolean testNextColor(Tester t) {
    return t.checkExpect(new SimonSays(Utils.B1, Utils.B2, Utils.B3, Utils.B4,
        l1, l1, false, new Random(3)).nextColor(),
        new Random(3).nextInt(4) + 1);
  }

  boolean testOnMousePressed(Tester t) {
    return t.checkExpect(w.onMousePressed(p1), w)
        && t.checkExpect(s.onMousePressed(p1), 
            new SimonSays(Utils.B1.clicked(), Utils.B2, Utils.B3, Utils.B4, 
                l1, l1.next(), true, new Random(5)))
        && t.checkExpect(n.onMousePressed(p1), 
            n2.addToSequence().resetButtons().resetTemp().switchMode())
        && t.checkExpect(s.onMousePressed(p6), s.endOfWorld("You Done Son!"));
  }

  boolean testOnMouseReleased(Tester t) {
    return t.checkExpect(w.onMouseReleased(p1), w)
        && t.checkExpect(s.onMouseReleased(p1), s.resetButtons());
  }

  boolean testOnTick(Tester t) {
    return t.checkExpect(s.onTick(),s)
        && t.checkExpect(w.onTick(), w.darken(1).pause())
        && t.checkExpect(w2.onTick(), w2.darken(2).pause())
        && t.checkExpect(w3.onTick(), w3.darken(3).pause())
        && t.checkExpect(w4.onTick(), w4.darken(4).pause())
        && t.checkExpect(w0.onTick(), w0.resetButtons().next())
        && t.checkExpect(n2.switchMode().onTick(), 
            n2.switchMode().resetButtons().resetTemp().switchMode());
  }

  boolean testPause(Tester t) {
    return t.checkExpect(s.pause(), new SimonSays(Utils.B1, Utils.B2, Utils.B3, Utils.B4, 
        l1, l1.changeFirst(0), true, new Random(5)));
  }

  boolean testResetButtons(Tester t) {
    return t.checkExpect(s.resetButtons(), new SimonSays(Utils.B1, Utils.B2, Utils.B3, Utils.B4,
        l1, l1, true, new Random(5)));
  }

  boolean testResetTemp(Tester t) {
    return t.checkExpect(s.resetButtons(), new SimonSays(Utils.B1, Utils.B2, Utils.B3, Utils.B4,
        l1, l1, true, new Random(5)));
  }

  boolean testRun(Tester t) {
    return t.checkExpect(w.run(), w.bigBang(Utils.GAME_SIZE, Utils.GAME_SIZE, Utils.GAME_SPEED));
  }

  boolean testSwitchMode(Tester t) {
    return t.checkExpect(w.switchMode(), new SimonSays(Utils.B1, Utils.B2, Utils.B3, Utils.B4,
        l1, l1, true, new Random(5)));
  }

  boolean testDraw(Tester t) {
    return t.checkExpect(Utils.B1.draw(new WorldScene(500,500)), 
        new WorldScene(500,500).placeImageXY(
            new RectangleImage(Utils.B1.size, Utils.B1.size, "solid", Utils.B1.color), 
            Utils.B1.x, Utils.B1.y));
  }

  boolean testClicked(Tester t) {
    return t.checkExpect(Utils.B1.clicked(), 
        new Button(Utils.B1.x, Utils.B1.y, Utils.B1.color.darker(), Utils.B1.size));
  }

  boolean testAddToEnd(Tester t) {
    return t.checkExpect(l1.addToEnd(3), new ConsLoInt(1, new ConsLoInt(2, 
        new ConsLoInt(3, new ConsLoInt(4, new ConsLoInt(3, new MtLoInt()))))))
        && t.checkExpect(new MtLoInt().addToEnd(2), new ConsLoInt(2, new MtLoInt()));
  }

  boolean testChangeFirst(Tester t) {
    return t.checkExpect(l1.changeFirst(0), new ConsLoInt(0, new ConsLoInt(2, 
        new ConsLoInt(3, new ConsLoInt(4, new MtLoInt())))))
        && t.checkExpect(new MtLoInt().changeFirst(0), new MtLoInt());
  }

  boolean testMthuh(Tester t) {
    return t.checkExpect(l1.mthuh(), false)
        && t.checkExpect(new MtLoInt().mthuh(), true);
  }

  boolean testNexts(Tester t) {
    return t.checkExpect(l1.next(), new ConsLoInt(2, 
        new ConsLoInt(3, new ConsLoInt(4, new MtLoInt()))))
        && t.checkExpect(new MtLoInt().next(), new MtLoInt());
  }

  boolean testRightButton(Tester t) {
    return t.checkExpect(l1.rightButton(1), true)
        && t.checkExpect(l1.rightButton(2), false)
        && t.checkExpect(new MtLoInt().rightButton(1), false);
  }

}