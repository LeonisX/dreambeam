DreamBeam (TODO List)
=====================

В процессе тестирования были найдены следующие ошибки:

**0. Навести порядок с кодировками в readFromFile(), readFromRussianFile, writeToFile, writeToRussianFile**

**1. Ошибка поиска файла: 2-in-1 Crazy Taxi 2 (Rus), Crazy Taxi 2 (Eng) (Kudos) (Alt)**

Проблема в файлах
- file1.cfg   [0 bytes] - Error!!!
- file2.cfg   [0 bytes] - Error!!!

При этом контрольная сумма полностью совпадает.

**2. Если нет файла настроек, то при запуске вылетает такая ошибка:**

- !New version verification error: NullPointerException: Cannot invoke "String.equals(Object)" because "md.leonis.dreambeam.statik.Config.notifiedVersion" is null

**3. Ошибка при попытке переименовать юзера:**

- Caused by: java.lang.NullPointerException
- at java.base/java.util.concurrent.ConcurrentHashMap.putVal(ConcurrentHashMap.java:1012)
- at java.base/java.util.concurrent.ConcurrentHashMap.put(ConcurrentHashMap.java:1007)
- at java.base/java.util.Properties.put(Properties.java:1346)
- at md.leonis.dreambeam.statik.Config.updateProperties(Config.java:105)
- at md.leonis.dreambeam.statik.Config.saveProperties(Config.java:97)
- at md.leonis.dreambeam.view.PrimaryPaneController.setAndSaveUser(PrimaryPaneController.java:453)
- at java.base/java.util.Optional.ifPresentOrElse(Optional.java:196)
- at md.leonis.dreambeam.view.PrimaryPaneController.inputUserName(PrimaryPaneController.java:442)
- at md.leonis.dreambeam.view.PrimaryPaneController.renameButtonClick(PrimaryPaneController.java:438)

**4. Метка диска пишется только большими буквами, когда она разными.**
- Пример: Метка диска (Volume Label): NORG
- На самом деле она NoRG

**5. L:\___dc_new\Russian\Platform\Spider-Man (Rus) (Vector+)**

**6. Сломался журнал (другая кодировка)**

**7. Если переименовывается файл юзера (меняется только регистр букв), то файл не переименовывается.** Нужно сначала в промежуточный файл переименовать.

**8. Ещё один вариант языка: Non-Rus. Как Rus полностью кроме названия.**



- Пакетное сканирование - искать начиная с определённого каталога, монтировать в Алкоголь, сканировать, выводить статистику в виде удобной таблицы (с данными диска)
- Остановился на том, что при сканировании сборников вывалилось много нераспознанных дисков.
- Проблема в file1.cfg, file2.cfg.
- Необходимо починить и пересканировать снова.


- PrimaryPane: GDI, CHD

- BaseStageController: сейчас текстовые описания в кодировке windows-1251. Необходимо преобразовать в юникод. Сделать после первой стабильной, проверенной версии, чтобы не было пути назад

- StatsPaneController: на перспективу надо помнить левые образы и иметь возможность убирать их из статистики (Level-2).
- StatsPaneController: выгрузка статистики или какие-то операции (правый клик по списку)

- Автоматическое обновление программы, скачивание базы.
- Build: паковать в архив с названием версии.

- у юзеров можно только новые образы хранить, по остальным только контрольные суммы.

remove C code if not need more

### Level2

Насканировать время создания файлов и сравнивать его тоже.
Отличный способ понять какой образ был нарезан раньше.
Время записи, создания и изменения должно быть идентичное везде (CD-ROM). Подумать что FS - какое там время сохраняется при копировании.

Так же можно хранить: все чанки названия файла (имя, диски, теги)
- метку
- серийный номер
- самую раннюю и позднюю даты (пока брать у файлов)
- важность (чтобы не искать леваки)
- номер с rgdb
- piperId
- локализованные названия
- вторая сторона
- размер, который возвращает система

Просится база данных.

### Level 3

Вообще, было бы круто читать всю структуру ISO 9660. Может быть, если конвертировать BIN в ISO, то такая возможность появится.
Либо автоматически монтировать в привод хотя бы.

- https://stackoverflow.com/questions/14068375/extracting-iso-file-using-java
- https://github.com/magnusja/java-fs
- https://github.com/tmyroadctfig/jnode
- https://github.com/jnode/jnode/tree/master/fs
- https://github.com/palantir/isofilereader
- https://github.com/stephenc/java-iso-tools
- https://sourceforge.net/projects/jiic/


### Глобальная цель проекта

1. Портировать код Delphi (вроде как готово)
2. Внести улучшения, например чтение GDI (с помощью сторонних скриптов и утилит).
3. Режим админа, когда образы попадают и в базу тоже, чтобы не вручную копировать.
4. Переименовывание из утилиты напрямую.
5. Показывать самые близкие по содержанию.
6. База данных - уметь хранить картинки тоже.
7. Связь с https://rgdb.info/base/rus-04753 (картинки можно брать оттуда), Piper.
8. Добавлять (GDI) автоматически 
