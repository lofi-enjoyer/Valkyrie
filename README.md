# Nublada ⛅

#### OpenGL &amp; Java 11 Voxel Engine

<details open="open">
  <summary>Table of contents</summary>
  <ol>
    <li>
      <a href="#about-the-project">About the project</a>
    </li>
    <li>
      <a href="#features">Features</a>
    </li>
    <li><a href="#building">Building</a></li>
    <li><a href="#license">License</a></li>
  </ol>
</details>

<div id="about-the-project"></div>

## About the project 📝

Nublada is a personal project I'm working on with the objective of learning more about Java, OpenGL, graphics rendering and the technologies needed to put together a basic Minecraft-like game engine.

![2022-02-21 15-52-14 (5)](https://user-images.githubusercontent.com/25750692/155811531-ac8be712-774b-420f-b6e2-2bbdf0e50922.gif)

> Render distance of 8 chunks (16 Minecraft chunks / ~256 meters) using around 500MB of memory, though the engine is capable of view distances up to 1 kilometer with a memory usage of about 4GB

<div id="features"></div>

## Features ⚙

- [Greedy Meshing](https://0fps.net/2012/07/07/meshing-minecraft-part-2/)
- Chunk-based world loading
- Procedurally generated environment using [Perlin Noise](https://en.wikipedia.org/wiki/Perlin_noise)
- Transparent textures support using depth sorting
- [Scripting using JavaScript](https://docs.oracle.com/en/java/javase/12/nashorn/introduction.html)
- Dynamic data loading with YAML, allowing to add new blocks and textures easily
- Multithreaded mesh building and world generation
- Frustum culling
- Skybox
- Fog effect

<div id="building"></div>

## Building 🚀 

_Prerequisites: Java Development Kit 11 or newer and Maven_

1. Clone the repository and access the directory
```sh
git clone https://github.com/lofi-enjoyer/Nublada
cd Nublada/
```

2. Build the project with Maven

```sh
mvn package
```

<div id="license"></div>

## License 📜 

This project is under a MIT License. Check [`LICENSE`](https://github.com/aurgiyalgo/TownyElections/blob/master/LICENSE) for more information.
