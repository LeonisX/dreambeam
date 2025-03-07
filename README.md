DreamBeam
=========

Java edition.

Утилита для идентификации образов игр Dreamcast.

DreamBeam - утилита, упрощающая сбор и систематизацию дисков легендарной приставки Sega Dreamcast. После чтения диска (или образа диска), вы получаете его полное название и детальное описание.

Программа сканирует игровой диск DC, подсчитывая контрольные суммы CRC32 для каждого файла.
DreamBeam в работе опирается на базу данных, поэтому, желательно всегда иметь последнюю версию.

[Что такое DreamBeam](http://tv-games.ru/pages/Projects/DreamBeam.html)
[База данных DreamBeam](https://github.com/LeonisX/dreambeam-base)


Как внести вклад
----------------

Сообщайте о найденных ошибках, присылайте свои версии образов игр или отсканированную полиграфию, фотографии.

- Почта: tv-games@mail.ru
- Telegram: https://t.me/P1nkie_Pie

База данных строится открытой, все обновления постепенно становятся доступны в облачном хранилище.

http://tv-games.ru/news/read/dreamcast_collection.html


Run
---

### Editor:

    mvn clean javafx:run

### Resources extractor:

    mvn clean javafx:run@extractor

### The right way

https://stackoverflow.com/questions/53668630/how-to-run-javafx-applications-in-intellij-idea-ide


Build executable file
---------------------

1. Install `[GraalVM](https://github.com/gluonhq/graal/releases/latest)`
2. Install `[Microsoft Visual Studio](https://visualstudio.microsoft.com/downloads/)`
3. In terminal run `cmd.exe /k "C:\Program Files\Microsoft Visual Studio\2022\Community\VC\Auxiliary\Build\vcvars64.bat"` (don't forget last double quote :)
4. Next `mvn clean gluonfx:build gluonfx:nativerun`

[Full instruction](https://docs.gluonhq.com/#platforms_windows)

Compile vol.exe

1. cl src\main\c\vol.c & del vol.obj

[VirusTotal report](https://www.virustotal.com/gui/file-analysis/ZjMwOTdkNGEyOTU0NmM0OGI1OWNmMjBhMTUyNmFjM2U6MTc0MTI2MjA2OA==)

4 false-positive reports from 71. You can verify the harmlessness of the file code `vol.c` source code or recompile it yourself.


Technical problems
------------------

### AWT

- https://github.com/oracle/graal/issues/5372
- https://github.com/gluonhq/gluonfx-maven-plugin/issues/505
- https://github.com/gluonhq/substrate/pull/1103

### ICONS

- https://stackoverflow.com/questions/70530643/display-of-an-icon-in-a-program-compiled-with-gluonfx
- https://github.com/gluonhq/substrate/issues/1309
