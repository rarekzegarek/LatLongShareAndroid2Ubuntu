#!/bin/bash

# Skrypt do odinstalowywania Gradle bez usuwania zmiennych systemowych

# 1. Usunięcie plików binarnych Gradle
echo "Usuwanie plików binarnych Gradle..."
sudo rm -f /usr/bin/gradle
sudo rm -f /usr/local/bin/gradle

# 2. Usunięcie katalogów Gradle
echo "Usuwanie katalogów Gradle..."
sudo rm -rf /usr/share/gradle
rm -rf ~/gradle-8.4
sudo rm -rf /opt/gradle

# 3. Usunięcie strony podręcznika systemowego (man page)
echo "Usuwanie strony podręcznika systemowego..."
sudo rm -f /usr/share/man/man1/gradle.1.gz

# 4. Usunięcie katalogów cache i konfiguracyjnych Gradle
echo "Usuwanie katalogów cache i konfiguracyjnych Gradle..."
rm -rf ~/.gradle

# 5. Usunięcie katalogu .gradle w projekcie (jeśli istnieje)
echo "Usuwanie katalogu .gradle w projekcie..."
cd ~/LatLongShareAndroid2Ubuntu
rm -rf .gradle

echo "Gradle zostało odinstalowane."
