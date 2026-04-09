# Feature Specification: Sistema de Lista de Espera Inteligente — UI Automation (Screenplay)

**Feature Branch**: `004-waitlist-screenplay`
**Created**: 2026-04-03
**Status**: Draft
**Input**: Automatizar los 6 escenarios del Sistema de Lista de Espera Inteligente mediante la UI del frontend Next.js usando el patrón Screenplay con Serenity BDD.
**Technical Base**: Frontend Next.js (`http://localhost:3000`) + WaitlistModal component + Waitlist.Api (`http://localhost:5006`).

---

## User Scenarios & Testing *(mandatory)*

### User Story 1 — Registro via UI en Lista de Espera (Priority: P1) 🎯 MVP

Como usuario que visita la página de un evento agotado, quiero ver el modal de lista de espera, ingresar mi email y recibir confirmación de mi posición en la cola.

**Why this priority**: Es el flujo de usuario principal que valida la integración completa: UI → API → Persistencia.

**Independent Test**: Actor "El Usuario" navega a `/events/{eventId}` (agotado), realiza `UnirseAWaitlist.conEmail("jostin@example.com")`, verifica `ElResultadoDeWaitlist.mostroExito()`.

**Acceptance Scenarios**:

1. **Given** el evento "Concierto Rock 2026" está agotado y el usuario navega a su página, **When** el usuario se une a la waitlist con email "jostin@example.com", **Then** la UI muestra "You're on the list!" con posición en la cola.
2. **Given** el evento tiene tickets disponibles, **When** el usuario navega a la página del evento, **Then** el botón "Join the Waitlist" no es visible en la UI.
3. **Given** "jostin@example.com" ya está registrado en la lista del evento, **When** intenta registrarse nuevamente, **Then** la UI muestra el mensaje de conflicto retornado por la API.

---

### User Story 2 — Verificación de Estado Post-Asignación (Priority: P2)

Como sistema de automatización, quiero verificar mediante Tasks y Questions del Screenplay que los estados de waitlist son correctos tras los eventos del backend.

**Acceptance Scenarios**:

4. **Given** "jostin@example.com" es el primero en la lista de espera, **When** el tiempo de reserva caduca, **Then** el Question `HasPendingInWaitlist` retorna `false` indicando que la entrada fue asignada.
5. **Given** "jostin@example.com" fue asignado y no pagó, y "segundo@example.com" está en cola, **When** el worker detecta expiración, **Then** la API confirma que "jostin@example.com" expiró y "segundo@example.com" fue asignado.
6. **Given** "jostin@example.com" fue asignado y no pagó, sin más usuarios en cola, **When** el worker detecta expiración, **Then** la API confirma que el asiento fue liberado al pool general.

---

### Edge Cases

- Actor intenta `UnirseAWaitlist` con email vacío → el campo `required` del HTML previene el envío.
- API Waitlist no disponible → Question retorna estado de error, el Actor falla con mensaje descriptivo.

---

## Requirements *(mandatory)*

### Architectural & Framework Requirements

- **FR-001**: El Actor "El Usuario" DEBE usar la ability `BrowseTheWeb` para los escenarios UI.
- **FR-002**: DEBE existir un Task `NavegarAlEventoAgotado` que encapsula la navegación a `/events/{eventId}`.
- **FR-003**: DEBE existir un Task `UnirseAWaitlist` que encapsula: abrir modal → ingresar email → submit.
- **FR-004**: DEBE existir un Question `ElResultadoDeWaitlist` que verifica el mensaje de éxito/error en la UI.
- **FR-005**: Para escenarios 4-6, DEBE existir un Question `HasPendingInWaitlist` que llama a `GET /api/v1/waitlist/has-pending` via `SerenityRest`.
- **FR-006**: El `HookIdempotencia` DEBE crear un evento agotado (todos los asientos bloqueados) antes de los escenarios UI.
- **FR-007**: `ApiConstants` DEBE incluir la URL base del Waitlist.Api (`http://localhost:5006`) y los endpoints de waitlist.

### Screenplay Components

| Componente | Tipo | Propósito |
|---|---|---|
| `NavegarAlEventoAgotado` | Task | Navegar a `/events/{eventId}` donde el evento está agotado |
| `UnirseAWaitlist` | Task | Abrir modal → ingresar email → submit → esperar respuesta |
| `ElResultadoDeWaitlist` | Question | Verificar mensaje de éxito ("You're on the list!") o error en UI |
| `HasPendingInWaitlist` | Question | Llamar a `has-pending` API y retornar `hasPending` boolean |

---

## Success Criteria *(mandatory)*

- **SC-001**: Task `UnirseAWaitlist` ejecuta sin errores y la Question confirma mensaje de éxito.
- **SC-002**: Escenario 2 verifica la ausencia del botón "Join the Waitlist" cuando hay stock.
- **SC-003**: Escenario 3 muestra el mensaje de error 409 en la UI.
- **SC-004**: Question `HasPendingInWaitlist` retorna booleano correcto para escenarios 4-6.
- **SC-005**: Todos los Tasks y Questions siguen el principio de Single Responsibility.

---

## Clarifications

- **Q**: ¿El Actor usa UI o API? → **A**: Ambos. UI para escenarios 1-3. API (Question via RestAssured) para 4-6.
- **Q**: ¿El texto del modal está en inglés? → **A**: Sí — "Join the Waitlist", "You're on the list!", "Join Waitlist".
- **Q**: ¿Cómo se crea un evento agotado para el test? → **A**: `HookIdempotencia` usa `EventSetupService` + `SeatBlockingService` para bloquear TODOS los asientos.
- **Q**: ¿El Runner filtra por algún tag? → **A**: Sí, filtrará por `@ListaEspera` o los tags de escenario específicos.

---

## Assumptions

- **A-001**: El frontend corre en `http://localhost:3000/events/{eventId}`.
- **A-002**: El `HookIdempotencia` existente se extiende para soportar el nuevo escenario de "todos los asientos bloqueados".
- **A-003**: Los Tasks heredan el patrón de `NavegarAlEvento` y `SeleccionarAsiento` ya existentes.
- **A-004**: Los escenarios 4-6 son de integración completa (Kafka + workers). Se marcan con tag `@IntegracionCompleta`.
