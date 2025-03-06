DreamBeam
=========

todo full test eng, rus with scan, save

todo liberica

- Settings: Выбор языка

- Настройки - автоматическое обновление. пока только оповещать.

- PrimaryPane: GDI, CHD

- ViewPaneController: читать блоками, чтобы отображать прогресс отдельных файлов.
- ViewPaneController: в случае битого образа попытаться найти ближайший (вывести список).

- ComparePaneController: в перспективе пользовательскую базу надо загружать фоном после старта и работать с ней.
- ComparePaneController: для списка различий просится tableView

- StatsPaneController: на перспективу надо помнить левые образы и иметь возможность убирать их из статистики.
- StatsPaneController: выгрузка статистики или какие-то операции (правый клик по списку)

- BaseStageController: сейчас текстовые описания в кодировке windows-1251. Необходимо преобразовать в юникод.

- Build: паковать в архив с названием версии.

Level2 - насканировать время создания файлов и сравнивать его тоже.
Отличный способ понять какой образ был нарезан раньше.
Время записи, создания и изменения должно быть идентичное везде (CD-ROM). Подумать что FS - какое там время сохраняется при копировании.

Вообще, было бы круто читать всю структуру ISO 9660. Может быть, если конвертировать BIN в ISO, то такая возможность появится.
Либо автоматически монтировать в привод хотя бы.


Итак, что же я хочу.
1. Портировать код Delphi
2. Внести улучшения, например чтение GDI (с помощью сторонних скриптов и утилит).
3. Режим админа, когда образы попадают и в базу тоже, чтобы не вручную копировать.
4. Переименовывание из утилиты напрямую.
5. Показывать самые близкие по содержанию.
6. База данных - уметь хранить картинки тоже.
7. Связь с https://rgdb.info/base/rus-04753 (картинки можно брать оттуда)
8. Добавлять (GDI) автоматически 




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