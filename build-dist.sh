#!/bin/bash
# Builds a shareable zip of the game that runs on Windows, Linux and macOS (Intel + Apple Silicon).
# Must be run on macOS (needs `lipo` to merge the two macOS JavaFX native builds).
# Output: target/dist/DungeonMaster.zip
set -e
cd "$(dirname "$0")"

FX_VERSION=21.0.6
FX_REPO="$HOME/.m2/repository/org/openjfx/javafx-graphics/$FX_VERSION"

./mvnw -q -Pdist -DskipTests clean package

WORK=target/dist-work
OUT=target/dist/DungeonMaster
rm -rf "$WORK" target/dist
mkdir -p "$WORK/intel" "$WORK/arm" "$WORK/universal" "$OUT"

# Intel and Apple Silicon natives share file names, so the shaded jar keeps only one of them.
# Merge both into universal binaries and put those back into the jar.
unzip -q -o "$FX_REPO/javafx-graphics-$FX_VERSION-mac.jar" '*.dylib' -d "$WORK/intel"
unzip -q -o "$FX_REPO/javafx-graphics-$FX_VERSION-mac-aarch64.jar" '*.dylib' -d "$WORK/arm"
for lib in "$WORK"/intel/*.dylib; do
    name=$(basename "$lib")
    lipo -create "$lib" "$WORK/arm/$name" -output "$WORK/universal/$name"
done

cp target/DungeonMaster-1.0-SNAPSHOT.jar "$OUT/DungeonMaster.jar"
(cd "$WORK/universal" && zip -q "$OLDPWD/$OUT/DungeonMaster.jar" *.dylib)

cat > "$OUT/Start (Mac).command" <<'EOF'
#!/bin/bash
cd "$(dirname "$0")"
java -jar DungeonMaster.jar
EOF
cat > "$OUT/Start (Linux).sh" <<'EOF'
#!/bin/bash
cd "$(dirname "$0")"
java -jar DungeonMaster.jar
EOF
printf '@echo off\r\ncd /d "%%~dp0"\r\njava -jar DungeonMaster.jar\r\nif errorlevel 1 pause\r\n' > "$OUT/Start (Windows).bat"
chmod +x "$OUT/Start (Mac).command" "$OUT/Start (Linux).sh"

cat > "$OUT/JAK SPUSTIT.txt" <<'EOF'
DUNGEON MASTER
==============

1) Nainstaluj Javu 24 nebo novější (zdarma): https://adoptium.net
   (stačí stáhnout a nainstalovat "Temurin", výchozí nastavení)

2) Spusť hru:
   - Windows: dvojklik na "Start (Windows).bat"
   - Mac:     pravým tlačítkem na "Start (Mac).command" -> Otevřít
              (napoprvé macOS varuje před neznámým vývojářem, potvrď Otevřít)
   - Linux:   ./"Start (Linux).sh"

3) Ve hře: Load -> Host -> Start

Hra ve více lidech
------------------
- Jeden hráč dá Host, ostatní vyplní jeho IP adresu a port 5000 a dají Connect.
- Hostitel pak dá Start.
- Ve stejné Wi-Fi stačí lokální IP hostitele (např. 192.168.x.x).
- Přes internet použijte Tailscale / ZeroTier / Hamachi (virtuální LAN),
  nebo musí mít hostitel na routeru přesměrovaný UDP port 5000.
- Všichni musí mít stejnou verzi hry.

Ovládání: WASD pohyb, levé tlačítko myši útok, E ultimátka,
F truhla, I inventář, C crafting.
EOF

(cd target/dist && zip -q -r DungeonMaster.zip DungeonMaster)
rm -rf "$WORK"
echo "Hotovo: $(pwd)/target/dist/DungeonMaster.zip"
