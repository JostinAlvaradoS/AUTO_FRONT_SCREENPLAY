# Tasks: Sistema de Lista de Espera — UI Automation (Screenplay)

**Feature**: `004-waitlist-screenplay` | **Date**: 2026-04-03
**Plan**: [plan.md](./plan.md) | **Spec**: [spec.md](./spec.md)
**Total Tasks**: 16

---

## Phase 1: Feature File

- [x] **T001** Crear `src/test/resources/features/hu_waitlist.feature` con los 6 escenarios en español, tags: `@HU-Waitlist`, `@ListaEspera`, `@RegistroExitoso`, `@TicketsDisponibles`, `@RegistroDuplicado`, `@AsignacionAutomatica`, `@LiberacionConSiguiente`, `@LiberacionSinCola`

---

## Phase 2: ApiConstants

- [x] **T002** Editar `utils/ApiConstants.java` — agregar:
  - `DEFAULT_WAITLIST_API_URL = "http://localhost:5006"`
  - `WAITLIST_API_URL_KEY = "waitlist.api.url"`
  - `WAITLIST_JOIN_ENDPOINT = "/api/v1/waitlist/join"`
  - `WAITLIST_HAS_PENDING_ENDPOINT = "/api/v1/waitlist/has-pending"`
  - `SESSION_WAITLIST_EVENT_ID = "WAITLIST_EVENT_ID"`
  - `SESSION_WAITLIST_EMAIL = "WAITLIST_EMAIL"`
  - `TAG_ALL_SEATS_BLOCKED = "@TodosAsientosBlockeados"`

---

## Phase 3: Tasks (Screenplay)

- [x] **T003** Crear `tasks/NavegarAlEventoAgotado.java`
  - Patrón: igual a `NavegarAlEvento.java` existente
  - Navega a `http://localhost:3000/events/{eventId}`
  - Factory: `NavegarAlEventoAgotado.conId(String eventId)`
  - `performAs`: `Open.browserOn().thePageNamed("events/" + eventId)` o `NavigateTo.thePageAt(...)`

- [x] **T004** Crear `tasks/UnirseAWaitlist.java`
  - Factory: `UnirseAWaitlist.conEmail(String email)`
  - `performAs` secuencia:
    1. Click en "Join the Waitlist" button → `Click.on(WAITLIST_JOIN_BUTTON)`
    2. Ingresar email → `Enter.theValue(email).into(WAITLIST_EMAIL_INPUT)`
    3. Submit → `Click.on(WAITLIST_SUBMIT_BUTTON)`
  - Usa `Target` de Serenity para los elementos

---

## Phase 4: Questions (Screenplay)

- [x] **T005** Crear `questions/ElResultadoDeWaitlist.java`
  - `mostroExito()` → Question que verifica visibilidad de "You're on the list!" en la UI
  - `mostroError()` → Question que verifica visibilidad del mensaje de error
  - `obtuvoMensajeDeError()` → Question que retorna el texto del mensaje de error
  - `botonWaitlistVisible()` → Question que verifica visibilidad del botón "Join the Waitlist"

- [x] **T006** Crear `questions/HasPendingInWaitlist.java`
  - Llama a `GET /api/v1/waitlist/has-pending?eventId={eventId}` via `SerenityRest` o `RestAssured`
  - Retorna `boolean` del campo `hasPending`
  - Factory: `HasPendingInWaitlist.paraEvento(String eventId)`

---

## Phase 5: Hooks

- [x] **T007** Extender `hooks/HookIdempotencia.java` o crear `hooks/WaitlistHook.java`
  - `@Before(order=1, "@RegistroExitoso or @RegistroDuplicado or @AsignacionAutomatica or @LiberacionConSiguiente or @LiberacionSinCola")`:
    - Crear evento con 1 asiento
    - Bloquear TODOS los asientos → `SeatBlockingService.blockAllSeats(eventId)` (extensión del servicio existente)
    - Guardar `eventId` en `Serenity.setSessionVariable(SESSION_WAITLIST_EVENT_ID)`
  - `@Before(order=1, "@TicketsDisponibles")`:
    - Crear evento con asientos disponibles (sin bloquear)
    - Guardar `eventId`
  - `@After`: desactivar evento de test

- [x] **T008** Extender `services/SeatBlockingService.java` — agregar:
  - `blockAllSeats(String eventId)`: bloquea TODOS los asientos del seatmap (no solo el primero)

---

## Phase 6: Step Definitions (RED → GREEN)

- [x] **T009** 🔴 Crear `stepdefinitions/WaitlistStepDefinitions.java`

  **Escenario 1 (RegistroExitoso)**:
  - `Dado que el evento ... está agotado y el usuario navega a su página` → `OnStage.theActorCalled("El Usuario").attemptsTo(NavegarAlEventoAgotado.conId(eventId))`
  - `Cuando el usuario se une a la waitlist con email ...` → `actor.attemptsTo(UnirseAWaitlist.conEmail(email))`
  - `Entonces la UI muestra "You're on the list!"` → `actor.should(seeThat(ElResultadoDeWaitlist.mostroExito(), is(true)))`
  - `Y el usuario recibe su posición en la cola` → verificar texto de posición no nulo

  **Escenario 2 (TicketsDisponibles)**:
  - Navegar al evento con stock
  - `Entonces el botón "Join the Waitlist" no es visible` → `seeThat(ElResultadoDeWaitlist.botonWaitlistVisible(), is(false))`

  **Escenario 3 (RegistroDuplicado)**:
  - Primera `UnirseAWaitlist` → éxito
  - Segunda `UnirseAWaitlist` → `seeThat(ElResultadoDeWaitlist.mostroError(), is(true))`

  **Escenarios 4-6**:
  - `Dado que ... es el primero en la lista` → join via API directo (no UI)
  - `Cuando el tiempo caduca` → no-op (worker async)
  - `Entonces ...` → `actor.should(seeThat(HasPendingInWaitlist.paraEvento(eventId), is(false)))`

---

## Phase 7: Runner

- [x] **T010** Crear `runners/WaitlistRunner.java`
  - Suite annotation style JUnit 5 (igual al `EventRunner` existente)
  - `@ConfigurationParameter(key = FILTER_TAGS_PROPERTY_NAME, value = "@RegistroExitoso or @TicketsDisponibles or @RegistroDuplicado")`
  - Glue: `com.sofka.automation`

---

## Phase 8: UI Targets (Serenity)

- [ ] **T011** Definir `Target` constants para elementos del WaitlistModal (puede ir en `ui/WaitlistTargets.java` o inline en Task):
  - `WAITLIST_JOIN_BUTTON = Target.the("join waitlist button").locatedBy("//button[contains(text(), 'Join the Waitlist')]")`
  - `WAITLIST_EMAIL_INPUT = Target.the("waitlist email input").locatedBy("#waitlist-email")`
  - `WAITLIST_SUBMIT_BUTTON = Target.the("submit waitlist button").locatedBy("//button[normalize-space()='Join Waitlist']")`
  - `WAITLIST_SUCCESS = Target.the("success message").locatedBy("//*[contains(text(), \"You're on the list!\")]")`
  - `WAITLIST_ERROR = Target.the("error message").locatedBy("p.text-sm.text-destructive")`

---

## Phase 9: Polish

- [ ] **T012** [P] Verificar que `NavegarAlEventoAgotado` espera correctamente el render del modal de "sold out" (puede necesitar `WaitUntil` para el banner)
- [ ] **T013** [P] Agregar logs descriptivos en cada Task via `@Step` annotation de Serenity
- [ ] **T014** Ejecutar escenarios 1-3 y verificar que pasan con el Actor Screenplay
- [ ] **T015** Marcar escenarios 4-6 con `@IntegracionCompleta` en el Runner hasta tener infra completa
- [ ] **T016** Verificar que el `WaitlistHook` no interfiere con el `HookIdempotencia` existente (orden correcto)

---

## TDD Cycle Summary

| Ciclo | Escenario | Task / Question | Estado |
|---|---|---|---|
| 1 | Registro exitoso UI | `UnirseAWaitlist` + `ElResultadoDeWaitlist.mostroExito()` | 🔴 → 🟢 |
| 2 | Botón no visible | `ElResultadoDeWaitlist.botonWaitlistVisible()` = false | 🔴 → 🟢 |
| 3 | Duplicado UI | `ElResultadoDeWaitlist.mostroError()` = true | 🔴 → 🟢 |
| 4 | Asignación automática | `HasPendingInWaitlist` = false | 🔴 (Kafka) |
| 5 | Rotación | `HasPendingInWaitlist` = false | 🔴 (Kafka) |
| 6 | Cola vacía | `HasPendingInWaitlist` = false | 🔴 (Kafka) |
