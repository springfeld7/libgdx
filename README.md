![CI](https://github.com/springfeld7/libgdx/actions/workflows/dt214g-project-ci.yml/badge.svg)

## DT214G - Group 5 - libGDX Project
[https://github.com/springfeld7/libgdx](https://github.com/springfeld7/libgdx)

Final submission: [v1.0](https://github.com/springfeld7/libgdx/tree/v1.0)

### Release notes
- Added test and static analysis tools, including configurations for JaCoCo, PIT, SpotBugs, and PMD
- Added full CI workflow running build, tests, JaCoCo coverage, PIT mutation testing, and static analysis (SpotBugs/PMD)
- Extended and implemented custom unit tests (See the "Extended Test Suite" section below)
- Added one-page visual summary (`docs/visual-summary.pdf`)

### Build and Run Instructions

To build the project (skipping Spotless formatting checks, which otherwise fail due to inherited formatting inconsistencies):

`gradlew.bat build -x spotlessApply -x spotlessCheck`

### Tests & Analysis Reports

| Task | Command | Report Location |
|------|---------|----------------|
| **Run Unit Tests** | `gradlew.bat :gdx:test` | `gdx/build/reports/tests/test/index.html` |
| **Code Coverage (JaCoCo)** | `gradlew.bat :gdx:jacocoTestReport` | `gdx/build/reports/jacoco/test/html/index.html` |
| **Mutation Testing (PIT)** | `gradlew.bat :gdx:pitest` | `gdx/build/reports/pitest/index.html` |
| **Static Analysis (SpotBugs)** | `gradlew.bat :gdx:spotbugsMain :gdx:spotbugsTest` | `gdx/build/reports/spotbugs/main.html`<br>`gdx/build/reports/spotbugs/test.html` |
| **Static Analysis (PMD)** | `gradlew.bat :gdx:pmdMain :gdx:pmdTest` | `gdx/build/reports/pmd/main.html`<br>`gdx/build/reports/pmd/test.html` |

### Continuous Integration Workflow

The project uses GitHub Actions for continuous integration. The workflow is defined in the `.github/workflows/dt214g-project-ci.yml` file.
Automates build, test, coverage (JaCoCo), mutation testing (PIT), and static analysis checks (SpotBugs/PMD) on every push and pull request.

### Extended Test Suite
- `/gdx/test/com/badlogic/gdx/graphics/glutils/ECT1Test.java`
- `/gdx/test/com/badlogic/gdx/graphics/glutils/IndexArrayTest.java`
- `/gdx/test/com/badlogic/gdx/utils/CharArrayTest.java`
- `/gdx/test/com/badlogic/gdx/utils/IntArrayTest.java`
- `/gdx/test/com/badlogic/gdx/utils/QueueTest.java`

### Visual Summary
[docs/visual_summary.pdf](docs/visual_summary.pdf)
