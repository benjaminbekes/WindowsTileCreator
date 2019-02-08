# Windows Tile Creator

An open source <b>Java</b> program for creating custom designed Windows 10 tiles.<br>
Good for Windows users who want to improve their StartMenu looks.

<h3>Program restrictions</h3>
<ul>
  <li>The program should start with admin privileges. (Because it needs a write access to your selected application folder)
  <li>The program is not able to refresh your StartMenu tile. You need to do this manually by renaming StartMenu application shortcut or by resizing the tile.
</ul>

<h3>Program features:</h3>
<ul>
    <li>Creates the 'VisualManifestElements.xml' file inside of an exe file directory with user requirements. Such as
        background tile color, custom user image (70x70 or 150x150) or enables the title of an application on a large
        tile.<br><br>
    <li>Creates the 'Icons' folder inside of the exe file directory with occasional images of the application. This
        folder is being created only if a user requires an own application image.<br><br>
      <li>The program saves its local settings into the <i>%AppData%\WindowsTileCreator</i> folder as save.properties.
        This feature is smart for quick loading your previous settings.<br>
</ul>
<h3>The program preview</h3>

<p align="center">
  <img src="https://github.com/BenSvK/WindowsTileCreator/blob/master/src/source/icon/program_photo.png" alt="The program photo"><br>
  <i>The application photo preview</i>
</p>

 <h3>The result can be visible in the Windows Start Menu</h3>
 
<p align="center">
  <img src="https://github.com/BenSvK/WindowsTileCreator/blob/master/src/source/icon/windows_start_menu_preview.png" alt="Windows StartMenu photo"><br>
  <i>Improved Windows StartMenu photo preview</i>
</p>

# Apache License 2.0
```
  Copyright (C) 2019 Benjamín Bekeš
 
  Licensed under the Apache License, Version 2.0 (the "License");
  you may not use this file except in compliance with the License.
  You may obtain a copy of the License at
 
  http://www.apache.org/licenses/LICENSE-2.0
 
  Unless required by applicable law or agreed to in writing, software
  distributed under the License is distributed on an "AS IS" BASIS,
  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
  See the License for the specific language governing permissions and
  limitations under the License.
```
