# Copilot Instructions – Vocabulary Trainer

Swing-Desktopanwendung, die eine **eingebettete Kopie** von OOPDI (Dependency-Injection-Mini-Framework) als Quellcode mitbringt (kein externes Jar!). Diese Instruktionen sind auf den tatsächlichen Code-Stand kalibriert (Stand: 2026-09-01) — nicht auf die generische OOPDI-Doku eines anderen Repos, da diese Kopie älter/abweichend ist.

## Build & Test

- Build: `mvn -q -DskipTests package` (JDK 21 erforderlich, `<release>21</release>` in [pom.xml](pom.xml)).
- Tests: `mvn test` — **es existiert aktuell kein `src/test/java`**. Bevor ein Agent Tests schreibt: Verzeichnis `src/test/java` anlegen, JUnit 5 (`junit-jupiter:5.10.2`, bereits als Dependency vorhanden) nutzen.
- Surefire hat bereits `--add-opens java.base/java.lang` und `--add-opens java.base/java.util` gesetzt (für Reflection/cglib-Proxies nötig).
- **Swing-Headless-Hinweis**: Für automatisierte Tests von Nicht-UI-Logik (Model-Schicht) ist kein Display nötig. Falls jemals UI-Komponenten getestet werden, `-Djava.awt.headless=true` setzen — aber bevorzugt: UI-Klassen (`ui`-Package) NICHT direkt testen, sondern die Model-Klassen isoliert.
- Kein Web-Server/keine Runtime zum "Hochfahren" — die App ist ein Swing-`JFrame`. Einstieg: `de.oopexpert.vocabulary.ui.VocabularyTrainerUI#main` (siehe [VocabularyTrainerUI.java](src/main/java/de/oopexpert/vocabulary/ui/VocabularyTrainerUI.java)). Agent soll App nicht interaktiv starten, um zu "testen" — stattdessen Model-Klassen direkt aufrufen/unit-testen.
- Packaging: `maven-assembly-plugin` erzeugt ein `jar-with-dependencies` mit `VocabularyTrainerUI` als Main-Class.

## Architektur

- `de.oopexpert.vocabulary.model` — Domänenlogik (Vokabeln, Sets, Statistik, Fragen, Belohnungen). Hier steckt die eigentliche Business-Logik und die OOPDI-verwalteten Beans.
- `de.oopexpert.vocabulary.ui` — reine Swing-Views (`JFrame`/`JPanel`), **nicht** OOPDI-verwaltet, werden klassisch per `new` gebaut und erhalten die `OOPDI<VocabularyTrainer>`-Instanz im Konstruktor (siehe `MainPanel`, `RewardsPanel`, `QuestionnairePanel`, `ImageTransitionPanel`).
- `de.oopexpert.oopdi` — eingebettetes DI-Framework, siehe unten. **Nicht als externe Library behandeln** — Änderungen hier wirken sich nur auf dieses Repo aus.
- `JSONFileModifier` (Top-Level, `de.oopexpert.vocabulary`) ist ein Standalone-Migrationsskript (eigene `main`-Methode) für Statistik-JSON-Dateien, kein Teil der App-Laufzeit.

## Persistenz

- **Vokabelsets**: CSV-Dateien, geladen über `VocabularyLibrary#loadCSVFiles` (Apache Commons CSV). Nicht das CSV-Spaltenformat ändern, ohne den Loader/Writer synchron anzupassen.
- **Statistik**: JSON-Dateien im Verzeichnis `statistics/` (Gson, siehe [JSONFileModifier.java](src/main/java/de/oopexpert/vocabulary/JSONFileModifier.java) und `Statistic`-Modell). Felder wie `lastQuestionnaire1`/`lastQuestionnaire2` sind Unix-Timestamps — bei Modellwechseln Rückwärtskompatibilität beachten, da vorhandene Nutzerdaten sonst brechen.

## OOPDI-Nutzung in dieser App (verifiziert am Code, Stand heute)

Dieses Repo nutzt eine **eigene, ältere Kopie** von OOPDI mit reduziertem Funktionsumfang. Folgende Punkte aus generischen OOPDI-Hinweisen gelten **hier nicht** und sollten nicht angenommen werden:

- ❌ **Kein Byte Buddy** — Proxies werden mit **cglib** (`net.sf.cglib.proxy.Enhancer`) erzeugt, siehe [ProxyManager.java](src/main/java/de/oopexpert/oopdi/ProxyManager.java). Konsequenz: Zielklassen dürfen nicht `final` sein, Konstruktor-Handling ist reflection-basiert (0/1-Konstruktor-Fälle gesondert behandelt).
- ❌ **Kein `@InjectVariable`**, **kein `@PreDestroy`**, **kein `OOPDI.shutdown()`** in diesem Framework-Stand (Annotation-Package enthält nur `Injectable`, `InjectInstance`, `InjectSet`, `PostConstruct`). Konfigurationswerte (z. B. Speicherpfade) werden aktuell hart im Code oder per Konstruktor gesetzt, nicht per DI-Konfiguration.
- ✅ **Profile existieren**: `@Injectable(profiles = {...})` + `new OOPDI<>(RootClass.class, "profileA")`. Falls mehrere Implementierungen (z. B. Datei- vs. DB-Repository) nötig werden, über `profiles` lösen — OOPDI bindet nicht Interface→Implementierung.
- ✅ **Scopes**: `GLOBAL`, `THREAD`, `LOCAL`, `REQUEST` (siehe [Scope.java](src/main/java/de/oopexpert/oopdi/Scope.java)). Aktuell nutzen alle App-Beans (`VocabularyTrainer`, `VocabularyLibrary`, …) `Scope.GLOBAL`.
- ✅ **`@InjectSet`** ist die einzige Multi-Injection-Form — für "mehrere Übungsmodi als Plugins" hier ansetzen, nicht `List<T>` injizieren.

### Proxy-Semantik (gilt weiterhin)

- Jede `@Injectable`-Klasse wird als cglib-Proxy zurückgegeben. `this.method()`-Aufrufe **innerhalb** einer Bean umgehen den Proxy — kein Scope-Switch, kein Re-Resolve. Wichtig bei `ActionListener`/Swing-Callbacks, die intern private Methoden derselben Bean aufrufen.
- Swing läuft auf dem EDT. Bisher wird ausschließlich `Scope.GLOBAL` verwendet — es gibt **keinen** `SwingWorker`-Hintergrundthread-Code in diesem Repo. Falls das eingeführt wird: `THREAD`-Scope-Beans sind pro Thread, ein `SwingWorker`-Thread bekäme eine andere Instanz als der EDT. Vor Einführung von Hintergrundarbeit explizit klären, ob `GLOBAL` beibehalten werden soll.
- `REQUEST`-Scope ist an ThreadLocal + Call-Tiefe gebunden — passend für "eine Aktion = ein Request" (Button-Klick), nicht für langlebige UI-Panels. Wird aktuell nicht genutzt.
- `LOCAL`-Scope erzeugt bei jedem Proxy-Call ein neues Realobjekt — ungeeignet für teure Services (Repository/CSV-Zugriff), okay für zustandslose Helfer.
- UI-Komponenten (`JFrame`/`JPanel`-Subklassen) **nicht** über OOPDI verwalten — cglib-Subclassing von Swing-Komponenten kann unerwartete Nebeneffekte haben. Bestehendes Muster (Views erhalten `OOPDI<VocabularyTrainer>` im Konstruktor und rufen `oopdi.getInstance(...)` bei Bedarf) beibehalten.

## Release-Prozess

Ein einziger, manuell auslösbarer Workflow `.github/workflows/release-version.yml` (`workflow_dispatch`, liegt am Repo-Root `C:\Users\arnel\git\vocabularytrainer\.github\`, **nicht** im verschachtelten `vocabularytrainer/vocabularytrainer/.github`, da der Git-Root eine Ebene höher liegt) erledigt in einem Lauf:

1. Scannt `git log` seit dem letzten `vX.Y.Z`-Tag nach Conventional Commits (`fix:`=Patch, `feat:`=Minor, `!:`/`BREAKING CHANGE:`=Major, sonst kein Release).
2. Bumpt `<version>` in [pom.xml](pom.xml) per `sed`.
3. Schreibt/erweitert `CHANGELOG.md` (existiert aktuell noch nicht — wird beim ersten Lauf neu angelegt).
4. Committet + taggt direkt auf `main` (kein PR — Repo-Regeln analog zu OOPDI erlauben keine Actions-erstellten PRs/Workflow-Pushes ohne zusätzliche Token-Scopes).
5. Baut mit `mvn package` (nutzt das bestehende `maven-assembly-plugin` → `jar-with-dependencies`).
6. Erstellt das GitHub Release und hängt das Jar an (`gh release create`).

Anders als beim OOPDI-Repo (das nach GitHub Packages published): **kein** `mvn deploy`/`distributionManagement`, da der Vokabeltrainer keine Bibliothek ist, sondern nur als ausführbares Jar am Release hängt. Vorlage/Rationale: siehe OOPDI-Repo `C:\Users\arnel\git\oopdi\.github\copilot-instructions.md` (Abschnitt "Release Process").

## Konventionen

- Ein Commit pro Task.
- Vor Annahmen über OOPDI-Funktionsumfang: **immer den eingebetteten Code in `de.oopexpert.oopdi` prüfen**, nicht auf Wissen aus anderen OOPDI-Versionen/Repos verlassen.

## Self-Maintenance

Wenn du beim Arbeiten in diesem Repo neue, verifizierte Erkenntnisse gewinnst (z. B. weitere OOPDI-Eigenheiten, Persistenzformat-Details, Build-Stolperfallen), ergänze sie hier oder in `/memories/repo/` — nicht nur im Chat-Verlauf beantworten.
