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
