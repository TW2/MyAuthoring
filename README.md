# MyAuthoring
Like a DVD or BD for all your videos.

MyAuth permits the use of a JSON script containing reference to source folders and other data to make menus and triggers to show videos (using FFmpeg). It can be used for archive.

### Example
Folders structure:
```
+ res
  + libs
  + media
      01.mp4
      01.mkv
      02.mp4
      ...
  + menu
      0001.jpg
      0001-001.jpg
      0001-002.png
      ...
  + playlist
      root.txt
      ...
  root.json
  ...
```
JSON for the menus:
```
[
    {
        "ref": "root-0001",
        "img src": "menu/0001.jpg",
        "img x": 0,
        "img y": 0,
        "img w": 1280,
        "img h": 720,
        "dest": "none",
        "playlist": "none"
    },
    {
        "ref": "sub-0001",
        "img src": "menu/0001-001.jpg",
        "img x": 100,
        "img y": 100,
        "img w": 200,
        "img h": 113,
        "dest": "media/01.mp4",
        "playlist": "none"
    },
    {
        "ref": "sub-0001",
        "img src": "menu/0001-002.jpg",
        "img x": 400,
        "img y": 300,
        "img w": 200,
        "img h": 113,
        "dest": "media/01.mkv",
        "playlist": "none"
    },
    {
        "ref": "all",
        "img src": "none",
        "img x": -1,
        "img y": -1,
        "img w": -1,
        "img h": -1,
        "dest": "none",
        "playlist": "playlist/root.txt"
    }
]
```

You can join me on Discord to speak or idle, in English or French (cause I'm a half white black Frenchy).

[![Discord](https://github.com/user-attachments/assets/99ec6536-7624-41c1-afd1-7993fc4a1e25)](https://discord.gg/ef8xvA9wsF)
