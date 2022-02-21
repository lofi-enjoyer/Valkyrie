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

<img src="https://i.imgur.com/sEH1oHx.png" width="640" height="336">

> Screenshot showing a render distance of 16 chunks (32 Minecraft chunks / ~512 meters) around the camera

<div id="features"></div>

## Features ⚙

- [Greedy Meshing](https://0fps.net/2012/07/07/meshing-minecraft-part-2/)
- Chunk-based world loading
- Procedurally generated environment using [Perlin Noise](https://en.wikipedia.org/wiki/Perlin_noise)
- Transparent textures support using depth sorting
- Allows scripting using JavaScript
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
git clone https://github.com/aurgiyalgo/Nublada
cd Nublada/
```

2. Build the project with Maven

```sh
mvn package
```

<div id="license"></div>

## License 📜 

This project is under a MIT License. Check [`LICENSE`](https://github.com/aurgiyalgo/TownyElections/blob/master/LICENSE) for more information.
