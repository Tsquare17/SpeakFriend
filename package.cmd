:<<"::CMDLITERAL"
@ECHO OFF
GOTO :CMDSCRIPT
::CMDLITERAL

$JAVA_HOME/bin/jlink \
--no-header-files \
--no-man-pages \
--compress=2 \
--strip-debug \
--module-path /home/$USER/.m2/repository/org/openjfx/javafx-base/17.0.2-ea+2:\
/home/$USER/.m2/repository/org/openjfx/javafx-graphics/17.0.2-ea+2:\
/home/$USER/.m2/repository/org/openjfx/javafx-controls/17.0.2-ea+2:\
/home/$USER/.m2/repository/org/openjfx/javafx-fxml/17.0.2-ea+2:\
/home/$USER/.m2/repository/org/openjfx/javafx-web/17.0.2-ea+2:\
/home/$USER/.m2/repository/org/openjfx/javafx-media/17.0.2-ea+2:\
/home/$USER/.m2/repository/org/xerial/sqlite-jdbc/3.28.0 \
--add-modules java.base,javafx.base,javafx.controls,javafx.fxml,javafx.web,javafx.media,java.sql \
--output target/java-runtime

mvn package

jpackage \
--type "deb" \
--dest dist/installer \
--input target/ \
--name SpeakFriend \
--main-class com.tsquare.speakfriend.controller.main.Loader \
--main-jar SpeakFriend-0.7.0-jar-with-dependencies.jar \
--java-options -Xmx2048m \
--runtime-image target/java-runtime \
--icon src/main/resources/img/icon.png \
--app-version "0.7.0" \
--vendor "Trevor Thompson"

exit $?

:CMDSCRIPT
C:\Users\%USERNAME%\.jdks\temurin-17.0.1\bin\jlink.exe ^
--no-header-files --no-man-pages --compress=2 --strip-debug --module-path ^
'C:\Users\%USERNAME%\.m2\repository/org/openjfx/javafx-base/17.0.2-ea+2;^
C:\Users\%USERNAME%\.m2\repository/org/openjfx/javafx-graphics/17.0.2-ea+2;^
C:\Users\%USERNAME%\.m2\repository/org/openjfx/javafx-controls/17.0.2-ea+2;^
C:\Users\%USERNAME%\.m2\repository/org/openjfx/javafx-fxml/17.0.2-ea+2;^
C:\Users\%USERNAME%\.m2\repository/org/openjfx/javafx-web/17.0.2-ea+2;^
C:\Users\%USERNAME%\.m2\repository/org/openjfx/javafx-media/17.0.2-ea+2;^
C:\Users\%USERNAME%\.m2\repository/org/xerial/sqlite-jdbc/3.28.0'^
 --add-modules java.base,javafx.base,javafx.controls,javafx.fxml,javafx.web,javafx.media,java.sql ^
 --output target/java-runtime

C:\Users\%USERNAME%\.jdks\temurin-17.0.1\bin\jpackage ^
--type "msi" ^
--dest dist/installer ^
--input target/ ^
--name SpeakFriend ^
--main-class com.tsquare.speakfriend.controller.main.Loader ^
--main-jar SpeakFriend-0.7.0.jar ^
--java-options -Xmx2048m ^
--runtime-image target/java-runtime ^
--icon src/main/resources/img/icon.ico ^
--app-version "0.7.0" ^
--vendor "Trevor Thompson" ^
--win-dir-chooser ^
--win-menu ^
--win-shortcut
