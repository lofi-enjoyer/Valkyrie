# Valkyrie ⛅

<div align="center">
  <a href="https://github.com/lofi-enjoyer/Valkyrie/releases/latest">
    <img src="https://img.shields.io/github/v/release/lofi-enjoyer/Valkyrie?include_prereleases" alt="Latest release" />
  </a>
  <a href="https://github.com/lofi-enjoyer/Valkyrie/blob/main/LICENSE">
    <img src="https://img.shields.io/badge/license-GPL--3.0-blue.svg" alt="License" />
  </a>
  <a href="https://github.com/lofi-enjoyer/Valkyrie/pulls">
    <img src="https://img.shields.io/github/issues-pr/lofi-enjoyer/Valkyrie" alt="Pull requests" />
  </a>
  <a href="https://github.com/lofi-enjoyer/Valkyrie/issues">
    <img src="https://img.shields.io/github/issues/lofi-enjoyer/Valkyrie" alt="Issues" />
  </a>
  <a href="https://discord.gg/wdq3RP6xxY">
    <img src="https://img.shields.io/badge/discord-Project_Valkyrie-blue?logo=discord">
  </a>
</div>

#### OpenGL &amp; Java 11 Voxel Engine

<details open="open">
  <summary>Table of contents</summary>
  <ol>
    <li><a href="#about-the-project">About the project</a></li>
    <li><a href="#features">Features</a></li>
    <li><a href="#requirements">Requirements</a></li>
    <li><a href="#building">Building</a></li>
    <li><a href="#license">License</a></li>
  </ol>
</details>

<div id="about-the-project"></div>

## About the project 📝

Valkyrie is a project with the objective of putting together a basic Hytale-like game engine, to be used as a foundation for a bigger game.

![2022-02-21 15-52-14 (5)](https://user-images.githubusercontent.com/25750692/155811531-ac8be712-774b-420f-b6e2-2bbdf0e50922.gif)

> Render distance of 8 chunks (16 Minecraft chunks / ~256 meters) using around 500MB of memory, though the engine is capable of view distances up to 1 kilometer with a memory usage of about 4GB

<div id="features"></div>

## Features ⚙

- [Greedy Meshing](https://0fps.net/2012/07/07/meshing-minecraft-part-2/)
- Chunk-based world loading
- Procedurally generated environment using [Perlin Noise](https://en.wikipedia.org/wiki/Perlin_noise)
- Dynamic data loading with YAML, allowing to add new blocks and textures easily
- Multithreaded mesh building and world generation
- Font rendering
- Frustum culling
- Skybox
- Fog effect

<div id="requirements"></div>

## Requirements 🎮

>[!IMPORTANT]
> Native Windows 10/11 ARM builds are not available yet.

|           | Minimum system requirements                   |
|-----------|-----------------------------------------------|
| CPU       | 64-bit (x86_64 or arm64-v8a) architecture     |
| GPU       | Any with OpenGL 4.6+ support                  |
| RAM       | 2 GB (at least 1 GB free)                     |
| Storage   | 1 GB free on HDD                              |
| OS        | Windows 8.1+, macOS or Linux                  |
| Java      | 11+ (JDK or JRE)                              |

<div id="building"></div>

## Building 🚀 

>[!WARNING]
>_Prerequisites: Java Development Kit 11 or newer and Maven_

1. Clone the repository and access the directory
```sh
git clone https://github.com/lofi-enjoyer/Valkyrie
cd Valkyrie/
```

2. Build the project with Maven

```sh
mvn package
```

<div id="license"></div>

## License 📜 

This project is under a GPL-3.0 license. Check [`LICENSE`](https://github.com/lofi-enjoyer/Valkyrie/blob/master/LICENSE) for more information.
