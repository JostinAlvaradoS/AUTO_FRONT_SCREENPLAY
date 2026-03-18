# Tasks: Automatización de Reserva de Asientos (HU-01)

**Input**: Design documents from `specs/003-automate-seat-reservation/`
**Feature Name**: 003-automate-seat-reservation
**Tech Stack**: Java 17, Serenity BDD, Screenplay, RestAssured, Gradle.

## Phase 1: Setup (Shared Infrastructure)

**Purpose**: Project initialization and environment configuration.

- [ ] T001 Configure environment variables for API and Web in `src/test/resources/serenity.conf`
- [ ] T002 [P] Define API Base Path and JSON Schemas for Catalog in `src/test/resources/serenity.conf`
- [ ] T003 Create project package structure under `src/test/java/com/sofka/automation/` (models, questions, runners, stepdefinitions, tasks, ui, utils)

---

## Phase 2: Foundational (Blocking Prerequisites)

**Purpose**: Core Screenplay infrastructure for Idempotencia and REST interaction.

- [ ] T004 Create `CatalogDTO` and `SeatDTO` models for API interaction in `src/test/java/com/sofka/automation/models/`
- [ ] T005 Implement `HookIdempotencia` with `@Before` using RestAssured (POST admin/events & POST admin/events/{id}/seats) in `src/test/java/com/sofka/automation/hooks/`
- [ ] T006 Implement `@After` logic in `HookIdempotencia` to deactivate/cleanup events in `src/test/java/com/sofka/automation/hooks/`
- [ ] T007 [P] Implement `NavegarAlEvento` Task using dynamic URL logic in `src/test/java/com/sofka/automation/tasks/`

---

## Phase 3: User Story 1 - Reserva Exitosa (Priority: P1) 🎯 MVP

**Goal**: Permitir la reserva de un asiento disponible.

**Independent Test**: Navegar a un evento nuevo y reservar el asiento 1.

### Implementation for User Story 1

- [ ] T008 [P] [US1] Define UI Targets for `SeatButton` and `ReserveButton` in `src/test/java/com/sofka/automation/ui/EventPage.java`
- [ ] T009 [US1] Implement `SeleccionarAsiento` Task in `src/test/java/com/sofka/automation/tasks/`
- [ ] T010 [US1] Implement `ElEstadoDelAsiento` Question in `src/test/java/com/sofka/automation/questions/`
- [ ] T011 [US1] Create Gherkin scenarios for US1 in `src/test/resources/features/hu01_reserva_asiento.feature`
- [ ] T012 [US1] implement Step Definitions for US1 in `src/test/java/com/sofka/automation/stepdefinitions/EventStepDefinitions.java`

---

## Phase 4: User Story 2 - Escenario Negativo (Priority: P2)

**Goal**: Verificar que no se pueden reservar asientos ocupados.

**Independent Test**: Setup de un asiento como 'reserved' vía API antes de interactuar.

### Implementation for User Story 2

- [ ] T013 [US2] Extend `HookIdempotencia` or Create Task to force seat status to `reserved` vía API in `src/test/java/com/sofka/automation/hooks/`
- [ ] T014 [US2] Create Gherkin scenario for "Intento de reserva de asiento ocupado" in `src/test/resources/features/hu01_reserva_asiento.feature`
- [ ] T015 [US2] Implement validation for error message (Question) in `src/test/java/com/sofka/automation/questions/ErrorMessage.java`

---

## Phase 5: User Story 3 - Visualización TTL (Priority: P3)

**Goal**: Validar la presencia del temporizador.

### Implementation for User Story 3

- [ ] T016 [US3] Define UI Target for `CountdownTimer` in `src/test/java/com/sofka/automation/ui/CartSidebar.java`
- [ ] T017 [US3] Implement Question `ElTiempoRestanteEsVisible` in `src/test/java/com/sofka/automation/questions/`
- [ ] T018 [US3] Create Gherkin scenario for TTL visibility in `src/test/resources/features/hu01_reserva_asiento.feature`

---

## Final Phase: Polish & Execution

- [ ] T019 Create `EventRunner` using `@RunWith(CucumberWithSerenity.class)` in `src/test/java/com/sofka/automation/runners/`
- [ ] T020 [P] Final validation of all tasks following SSOT and Idempotencia requirements.

---

## Summary
- **Total tasks**: 20
- **US1 (MVP)**: 5 tasks
- **US2 (Negative)**: 3 tasks
- **Parallel Opportunities**: T002, T003, T007, T008 (independent UI/Config setup)
- **Implementation Strategy**: Implementar US1 completo (Happy Path) antes de abordar US2 y US3.


## Phase 4: User Story 2 - [Title] (Priority: P2)

**Goal**: [Brief description of what this story delivers]

**Independent Test**: [How to verify this story works on its own]

### Tests for User Story 2 (OPTIONAL - only if tests requested) ⚠️

- [ ] T018 [P] [US2] Contract test for [endpoint] in tests/contract/test_[name].py
- [ ] T019 [P] [US2] Integration test for [user journey] in tests/integration/test_[name].py

### Implementation for User Story 2

- [ ] T020 [P] [US2] Create [Entity] model in src/models/[entity].py
- [ ] T021 [US2] Implement [Service] in src/services/[service].py
- [ ] T022 [US2] Implement [endpoint/feature] in src/[location]/[file].py
- [ ] T023 [US2] Integrate with User Story 1 components (if needed)

**Checkpoint**: At this point, User Stories 1 AND 2 should both work independently

---

## Phase 5: User Story 3 - [Title] (Priority: P3)

**Goal**: [Brief description of what this story delivers]

**Independent Test**: [How to verify this story works on its own]

### Tests for User Story 3 (OPTIONAL - only if tests requested) ⚠️

- [ ] T024 [P] [US3] Contract test for [endpoint] in tests/contract/test_[name].py
- [ ] T025 [P] [US3] Integration test for [user journey] in tests/integration/test_[name].py

### Implementation for User Story 3

- [ ] T026 [P] [US3] Create [Entity] model in src/models/[entity].py
- [ ] T027 [US3] Implement [Service] in src/services/[service].py
- [ ] T028 [US3] Implement [endpoint/feature] in src/[location]/[file].py

**Checkpoint**: All user stories should now be independently functional

---

[Add more user story phases as needed, following the same pattern]

---

## Phase N: Polish & Cross-Cutting Concerns

**Purpose**: Improvements that affect multiple user stories

- [ ] TXXX [P] Documentation updates in docs/
- [ ] TXXX Code cleanup and refactoring
- [ ] TXXX Performance optimization across all stories
- [ ] TXXX [P] Additional unit tests (if requested) in tests/unit/
- [ ] TXXX Security hardening
- [ ] TXXX Run quickstart.md validation

---

## Dependencies & Execution Order

### Phase Dependencies

- **Setup (Phase 1)**: No dependencies - can start immediately
- **Foundational (Phase 2)**: Depends on Setup completion - BLOCKS all user stories
- **User Stories (Phase 3+)**: All depend on Foundational phase completion
  - User stories can then proceed in parallel (if staffed)
  - Or sequentially in priority order (P1 → P2 → P3)
- **Polish (Final Phase)**: Depends on all desired user stories being complete

### User Story Dependencies

- **User Story 1 (P1)**: Can start after Foundational (Phase 2) - No dependencies on other stories
- **User Story 2 (P2)**: Can start after Foundational (Phase 2) - May integrate with US1 but should be independently testable
- **User Story 3 (P3)**: Can start after Foundational (Phase 2) - May integrate with US1/US2 but should be independently testable

### Within Each User Story

- Tests (if included) MUST be written and FAIL before implementation
- Models before services
- Services before endpoints
- Core implementation before integration
- Story complete before moving to next priority

### Parallel Opportunities

- All Setup tasks marked [P] can run in parallel
- All Foundational tasks marked [P] can run in parallel (within Phase 2)
- Once Foundational phase completes, all user stories can start in parallel (if team capacity allows)
- All tests for a user story marked [P] can run in parallel
- Models within a story marked [P] can run in parallel
- Different user stories can be worked on in parallel by different team members

---

## Parallel Example: User Story 1

```bash
# Launch all tests for User Story 1 together (if tests requested):
Task: "Contract test for [endpoint] in tests/contract/test_[name].py"
Task: "Integration test for [user journey] in tests/integration/test_[name].py"

# Launch all models for User Story 1 together:
Task: "Create [Entity1] model in src/models/[entity1].py"
Task: "Create [Entity2] model in src/models/[entity2].py"
```

---

## Implementation Strategy

### MVP First (User Story 1 Only)

1. Complete Phase 1: Setup
2. Complete Phase 2: Foundational (CRITICAL - blocks all stories)
3. Complete Phase 3: User Story 1
4. **STOP and VALIDATE**: Test User Story 1 independently
5. Deploy/demo if ready

### Incremental Delivery

1. Complete Setup + Foundational → Foundation ready
2. Add User Story 1 → Test independently → Deploy/Demo (MVP!)
3. Add User Story 2 → Test independently → Deploy/Demo
4. Add User Story 3 → Test independently → Deploy/Demo
5. Each story adds value without breaking previous stories

### Parallel Team Strategy

With multiple developers:

1. Team completes Setup + Foundational together
2. Once Foundational is done:
   - Developer A: User Story 1
   - Developer B: User Story 2
   - Developer C: User Story 3
3. Stories complete and integrate independently

---

## Notes

- [P] tasks = different files, no dependencies
- [Story] label maps task to specific user story for traceability
- Each user story should be independently completable and testable
- Verify tests fail before implementing
- Commit after each task or logical group
- Stop at any checkpoint to validate story independently
- Avoid: vague tasks, same file conflicts, cross-story dependencies that break independence
