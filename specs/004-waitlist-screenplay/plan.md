# Implementation Plan: Sistema de Lista de Espera — UI Automation (Screenplay)

**Branch**: `004-waitlist-screenplay` | **Date**: 2026-04-03 | **Spec**: [spec.md](./spec.md)

## Summary
Automatización UI del WaitlistModal usando Screenplay Pattern. Actor "El Usuario" ejecuta Tasks (`NavegarAlEventoAgotado`, `UnirseAWaitlist`) y Questions (`ElResultadoDeWaitlist`, `HasPendingInWaitlist`) para los 6 escenarios.

## Technical Context
| Ítem | Valor |
|---|---|
| **Frontend URL** | `http://localhost:3000` |
| **Waitlist API** | `http://localhost:5006` |
| **Actor** | "El Usuario" con `BrowseTheWeb` |
| **Patron** | Screenplay (Tasks + Questions) |
| **Extensiones** | `SeatBlockingService.blockAllSeats()` + `ApiConstants` waitlist keys |

## Constitution Check
- [x] **Screenplay Pattern**: Tasks + Questions + Actor. Mismo patrón que `NavegarAlEvento` + `SeleccionarAsiento`.
- [x] **Dependency Inversion**: `HookIdempotencia` usa servicios inyectados.
- [x] **Single Responsibility**: Cada Task tiene una sola responsabilidad.
- [x] **Hybrid Testing**: Questions 4-6 usan API directa (RestAssured) para estado backend.

## New Files
```
specs/004-waitlist-screenplay/
├── spec.md | plan.md | tasks.md

src/test/resources/features/
└── hu_waitlist.feature

src/test/java/com/sofka/automation/
├── tasks/NavegarAlEventoAgotado.java
├── tasks/UnirseAWaitlist.java
├── questions/ElResultadoDeWaitlist.java
├── questions/HasPendingInWaitlist.java
├── stepdefinitions/WaitlistStepDefinitions.java
└── runners/WaitlistRunner.java
```

## Edited Files
```
utils/ApiConstants.java              ← waitlist URL + endpoints + session keys
services/SeatBlockingService.java    ← blockAllSeats() method
hooks/HookIdempotencia.java          ← nuevo @Before para waitlist scenarios
```
