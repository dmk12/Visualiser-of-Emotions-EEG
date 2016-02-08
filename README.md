EmoApp
======
Brain-Computer Interface application made with the [Emotiv EPOC EEG headset](https://emotiv.com/epoc.php) and [Processing](https://processing.org/).

The headset measures electrical signals emitted from the brain, analyses them, and detects emotions and facial expressions. Examples of emotions that can be detected are excitement, engagement, meditation and frustration (these were determined by the producer of the headset). Multiple facial expressions such as smile, blink etc, can also be detected. 

The readings (values between 0 and 1) are saved in a CSV file and converted into visual effects similar to a visualizer in a music player. This is done using Java/Processing 2.x. The user can manipulate the visual effects by attempting to adjust their emotional responses and changing facial expressions. 

The visual effect is a sphere of scattered, glowing particles. It is blue/green when relaxed, red/pink when excited, larger when frustrated, round when facial expressions are idle, elliptical when smiling, scatters when blinking and tilted when winking left/right.

The app can be used as a relaxation and meditation tool, with the user goal being to keep the effect green/blue, small and round - indications of a relaxed state of mind an idle facial expressions.

Users can also record and save EEG datasets as CSV files on their local machine. They can also load saved CSV files and play them in the app, recreating the recorded visual effects, essentially recording their brain activity in a CSV format, and being able to play it back at a later stage.

#### Possible contributions:

Other visual effects could be added to the app, so the user can switch between effects. Colours indicating excitement and frustration could made user-adjustable. Cloud integration could be added, to save recordings on a cloud rather than locally.

Feel free to use the code as an example in your own Emotiv related projects. Any contributions are welcome.
