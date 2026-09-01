## 0.3.5 (2026-09-01)

- fix: notify ImageTransition on empty or single background image so panel actually paints it

## 0.3.4 (2026-09-01)

- fix: tolerate missing vocabularysets directory instead of throwing NPE on startup

## 0.3.3 (2026-09-01)

- fix: tolerate missing rewards directory instead of throwing NPE on startup

## 0.3.2 (2026-09-01)

- fix: package no_background.jpg fallback resource into jar

## 0.3.1 (2026-09-01)

- fix: pass required --add-opens java options to packaged JVM launcher

## 0.3.0 (2026-09-01)

- feat: build and release Linux and Windows distribution zips in parallel

## 0.2.1 (2026-09-01)

- fix: build and publish distribution zip instead of plain jar in release workflow

## 0.2.0 (2026-09-01)

- feat: add distribution profile bundling jlink runtime and jpackage app-image into a zip
- fix: tolerate missing backgrounds directory instead of throwing on startup

## 0.1.0 (2026-09-01)

- fix: push release commit/tag to actual branch instead of hardcoded main
- docs: calibrate copilot-instructions for embedded OOPDI copy and release process
- feat: initial vocabulary trainer implementation

