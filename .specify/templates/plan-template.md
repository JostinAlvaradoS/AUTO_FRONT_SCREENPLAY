# Implementation Plan: [FEATURE]

**Branch**: `[###-feature-name]` | **Date**: [DATE] | **Spec**: [link]
**Input**: Feature specification from `/specs/[###-feature-name]/spec.md`

**Note**: This template is filled in by the `/speckit.plan` command. See `.specify/templates/plan-template.md` for the execution workflow.

## Summary

[Extract from feature spec: primary requirement + technical approach from research]

## Technical Context

<!--
  ACTION REQUIRED: Replace the content in this section with the technical details
  for the project. The structure here is presented in advisory capacity to guide
  the iteration process.
-->

**Language/Version**: [e.g., Python 3.11, Swift 5.9, Rust 1.75 or NEEDS CLARIFICATION]  
**Primary Dependencies**: [e.g., FastAPI, UIKit, LLVM or NEEDS CLARIFICATION]  
**Storage**: [if applicable, e.g., PostgreSQL, CoreData, files or N/A]  
**Testing**: [e.g., pytest, XCTest, cargo test or NEEDS CLARIFICATION]  
**Target Platform**: [e.g., Linux server, iOS 15+, WASM or NEEDS CLARIFICATION]
**Project Type**: [e.g., library/cli/web-service/mobile-app/compiler/desktop-app or NEEDS CLARIFICATION]  
**Performance Goals**: [domain-specific, e.g., 1000 req/s, 10k lines/sec, 60 fps or NEEDS CLARIFICATION]  
**Constraints**: [domain-specific, e.g., <200ms p95, <100MB memory, offline-capable or NEEDS CLARIFICATION]  
**Scale/Scope**: [domain-specific, e.g., 10k users, 1M LOC, 50 screens or NEEDS CLARIFICATION]

## Constitution Check

*GATE: Must pass before Phase 0 research. Re-check after Phase 1 design.*

The gates below enforce the AUTO_FRONT_SCREENPLAY constitution:

1. Screenplay compliance: confirm the plan only references the root directories hooks, models, runners, stepdefinitions, tasks, userinterfaces and questions.
2. Shared-specs truth: cite the exact files under shared-specs that provide the acceptance criteria and business requirements for this feature.
3. Catalog fidelity: map each UI interaction to the operations defined in shared-specs/contracts/openapi/catalog.yaml and explain how the runners/stepdefinitions use the catalog service.
4. Scenario idempotency: describe how the hooks package implements an @Before that POSTs to catalog to create an event with seats and an @After that DELETEs the event so no scenario carries over state.
5. Serenity Starter baseline: verify the branch builds from the Serenity Starter Gradle seed (wrappers, build files, serenity.properties) before introducing additional modules.
## Project Structure

### Documentation (this feature)

```text
specs/[###-feature]/
├── plan.md              # This file (/speckit.plan command output)
├── research.md          # Phase 0 output (/speckit.plan command)
├── data-model.md        # Phase 1 output (/speckit.plan command)
├── quickstart.md        # Phase 1 output (/speckit.plan command)
├── contracts/           # Phase 1 output (/speckit.plan command)
└── tasks.md             # Phase 2 output (/speckit.tasks command - NOT created by /speckit.plan)
```

### Source Code (repository root)

```text
AUTO_FRONT_SCREENPLAY/
├── hooks/                # Screenplay hook implementations (setup/cleanup)
├── models/               # Screenplay models / domain helpers
├── runners/              # Serenity runners that wire actors & stories
├── stepdefinitions/      # Step definitions that map Gherkin to actors
├── tasks/                # Screenplay tasks for actions and interactions
├── userinterfaces/       # UI interactions (Wrappers, selectors)
├── questions/            # Questions that inspect the UI or services
├── specs/                # Local feature specs (copies of shared-specs entries)
├── shared-specs/         # Git submodule (source of truth for stories & contracts)
├── gradle/               # Serenity Starter Gradle tooling
├── build.gradle
├── gradlew
├── gradlew.bat
├── serenity.properties
└── settings.gradle
```

**Structure Decision**: The implementation stays faithful to the Serenity Starter Gradle baseline while layering the Screenplay packages (hooks, models, runners, stepdefinitions, tasks, userinterfaces, questions). The shared-specs submodule remains the single source of truth for features and contracts so the plan outlines which files under that directory drive the current work.
directories captured above]

## Complexity Tracking

> **Fill ONLY if Constitution Check has violations that must be justified**

| Violation | Why Needed | Simpler Alternative Rejected Because |
|-----------|------------|-------------------------------------|
| [e.g., 4th project] | [current need] | [why 3 projects insufficient] |
| [e.g., Repository pattern] | [specific problem] | [why direct DB access insufficient] |
