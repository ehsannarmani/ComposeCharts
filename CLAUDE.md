# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Overview

Compose Charts is a Kotlin Multiplatform charting library for Compose Multiplatform, published to Maven Central as `io.github.ehsannarmani:compose-charts`. It targets Android, Desktop (JVM), iOS, JS (browser), and Wasm/JS. The library exposes four chart composables — `LineChart`, `ColumnChart`, `RowChart`, `PieChart` — all drawn manually on a Compose `Canvas`/`DrawScope`.

Note: the Kotlin package/Android namespace is `ir.ehsannarmani.compose_charts`, but the published Maven coordinates use the `io.github.ehsannarmani` group.

## Modules

- `compose-charts/` — the published library. Only `commonMain` contains chart logic; the platform source sets (`androidMain`, `desktopMain`, `iosMain`/`nativeMain`, `jsMain`, `wasmJsMain`) exist almost entirely to provide the `expect`/`actual` for `extensions/PointerIntropFilter.kt` (pointer interop differs per platform).
- `app/` — a Compose Multiplatform sample/demo app that consumes `:compose-charts`. Entry points: `desktopMain/kotlin/main.kt`, `androidMain/.../MainActivity.kt`, `wasmJsMain`/`jsMain` `main`, `iosMain/MainViewController.kt`. Shared UI starts at `app/src/commonMain/.../ui/App.kt` with per-chart samples (`LineSample`, `ColumnSample`, `RowSample`, `PieSample`).

## Build & run

Build everything: `./gradlew build`
Build only the library: `./gradlew :compose-charts:build`

Run the demo app:
- Desktop: `./gradlew :app:run`
- Android: `./gradlew :app:installDebug` (or run from Android Studio)
- Wasm/JS in browser (dev server): `./gradlew :app:wasmJsBrowserDevelopmentRun`
- JS in browser: `./gradlew :app:jsBrowserDevelopmentRun`

Publishing (maintainer): `./gradlew :compose-charts:publishToMavenCentral`. Bump the `version` in `compose-charts/build.gradle.kts` (the `mavenPublishing { coordinates(...) }` block) before publishing.

There is no test suite in this repo, and no lint configuration beyond Kotlin's `official` code style (`kotlin.code.style=official` in `gradle.properties`).

## Library architecture

Each chart is a single large `@Composable` in `compose-charts/src/commonMain/.../<Chart>.kt`. The shared pattern across all four:

1. **Inputs are data + "*Properties*" config objects.** Charts take a `data` list (`List<Line>`, `List<Bars>`, `List<Pie>`) plus many optional config objects from the `models/` package (`GridProperties`, `LabelProperties`, `IndicatorProperties`, `PopupProperties`, `DotProperties`, `DividerProperties`, `LabelHelperProperties`, `BarProperties`, `LineProperties`, etc.). These are the public API — changing their constructors is a source-breaking change for consumers.
2. **Animation.** Each datum carries its own Compose `Animatable` for height/value, and visibility is driven via `LaunchedEffect`. `AnimationMode` (`models/AnimationMode.kt`) selects `Together` vs `OneByOne` (sequential, staggered) reveal. Row/Column animation helpers live in `utils/RCAnimation.kt`.
3. **Manual layout + draw.** Axis labels, indicators, and the chart body are laid out with `Column`/`Row`, and the plot itself is a `Canvas` whose `DrawScope` lambda draws bars/lines/slices, grid lines, dividers, dots, and the zero line. Value→pixel mapping uses helpers in `utils/` (`calculateOffset`, `rememberComputedChartMaxValue`, `Height.kt`, `IndicatorValues.kt`).
4. **Popups & selection.** Touch handling uses `pointerInput` + the platform `pointerInteropFilter` (`extensions/PointerIntropFilter.kt`, `expect`/`actual` per platform). Hit-testing produces `SelectedBar`/`BarPopupData` and popups are rendered via `extensions/line_chart/PopupHelper.kt` and `components/LabelHelper.kt`.

### Where things live
- `models/` — all public config/data classes (`Line`, `Bars`, `Pie`, and every `*Properties`). Start here when adding or changing a chart option.
- `extensions/` — `DrawScope`/geometry/number helpers (`Size.kt`, `Degree.kt`, `Circle.kt`, `CornerRadius.kt`, `Format.kt`, `GridLines.kt`). `extensions/line_chart/` holds line-specific path building (`getLinePath`, `drawLineGradient`, `PathData`).
- `utils/` — value computation: max-value, offsets, labels, animation, and `DataCheck.kt` (`require`-based validation of caller-supplied min/max vs actual data; "RC" = Row & Column).
- `components/` — shared composables like `LabelHelper` (the legend).

## Conventions

- New chart options should be added as a field on the relevant `*Properties` model (usually with a default) rather than as a new chart-function parameter, to keep call sites stable.
- Keep chart logic in `commonMain`; only touch platform source sets when something genuinely cannot be expressed commonly (the pointer interop `actual`s are the existing example).
- The `app/` samples double as the manual test harness — when changing a chart, update or use the corresponding `*Sample.kt` to verify visually via `./gradlew :app:run`.

## Documentation

User-facing docs are MkDocs-style markdown under `document/docs/` (per-chart guides under `charts/`, per-option guides under `chart-properties/`). The published site is https://ehsannarmani.github.io/ComposeCharts/. Update these when changing public API.
