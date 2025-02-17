# Noise

A collection of noise algorithms

## Random Noise

![random noise](./img/random_noise.jpg)

```bash
./mill graphics.runMain --mainClass org.bbstilson.graphics.noise.RandomNoise
```

## Perlin Noise

![perline noise 1](./img/perlin_noise_1.jpg)

|Width|Height|Num Cells X|Num Cells Y|
|:---|:---|:---|:---|
|300|300|8|8|

![perlin noise 2](./img/perlin_noise_2.jpg)

|Width|Height|Num Cells X|Num Cells Y|
|:---|:---|:---|:---|
|300|300|10|1|

## 3-dimentional Perlin Noise (youtube video)

<a href="http://www.youtube.com/watch?feature=player_embedded&v=In6MKsEksfQ
" target="_blank"><img src="http://img.youtube.com/vi/In6MKsEksfQ/0.jpg"
alt="3d Perlin Noise as water video" width="240" height="180" border="10" /></a>

`$DEPTH` images are rendered in parallel and then compiled into a video using QuickTime.

### Sources

<https://flafla2.github.io/2014/08/09/perlinnoise.html>

<https://weber.itn.liu.se/~stegu/simplexnoise/simplexnoise.pdf>

<https://en.wikipedia.org/wiki/Perlin_noise#Algorithm_detail>

<https://www.youtube.com/watch?v=MJ3bvCkHJtE>

## Linear Perlin

![linear perlin 1](./img/linear_perlin_1.jpg)

This was a simple extension of the 2d Perlin Noise generator. First, we generate a 2D Perlin Noise field. Then, rather than calculating what the color is at every pixel in the image, we only calculate pixels that fall on a circle. Next, we convert the color value (0-255) to a y-offset position where 127 is `height / 2` and 255 is 100% of some "window" (so that pixels aren't drawn to the edge of the image). Finally, progress around the circle is converted to progress through the image rectangle, which gives us our X value.

![moving linear perlin](https://i.imgur.com/bM1xPfq.gif)

The moving version is similar to the static, but rather than using the center of the frame as the center of the circle, each frame's circle-center is determined as a point on a circle in the center of the frame.

```bash
# uncomment `moving()` in the `main` method
./mill graphics.runMain org.bbstilson.graphics.noise.LinearPerlinNoise

# This generates a series of jpgs in `~/Desktop/perlin`. `cd` there, then, to convert
# to an mp4, run:
ffmpeg -framerate 30 -pattern_type glob -i '*.jpg' -c:v libx264 out.mp4 
```
