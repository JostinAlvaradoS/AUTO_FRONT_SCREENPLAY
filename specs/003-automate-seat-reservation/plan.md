# Implementation Plan: Screenplay para Reserva de Asientos (HU-01)

**Branch**: `003-automate-seat-reservation` | **Date**: 2026-03-17 | **Spec**: [specs/003-automate-seat-reservation/spec.md](specs/003-automate-seat-reservation/spec.md)
**Input**: Feature specification for building a Screenplay automation for seat reservation.

## Summary

Este plan detalla el diseño de los componentes Screenplay para automatizar la reserva de asientos (HU-01) en el proyecto `AUTO_FRONT_SCREENPLAY`. Se prioriza la **idempotencia** mediante un `IdempotenceHook` que interactúa con la `Catalog.Api` y la **navegación dinámica** basada en los eventos creados en tiempo de ejecución.

## Technical Context

**Language/Version**: Java 17+ (Gradle 8.x)  
**Primary Dependencies**: Serenity BDD, Screenplay Pattern, RestAssured (para Hooks), Selenium/Playwright (vía Serenity).  
**Storage**: N/A (Consumo de microservicios en `shared-specs`).  
**Testing**: JUnit 5 + Serenity BDD.  
**Target Platform**: Web Frontend (Next.js).  
**Project Type**: Automation Framework (Screenplay).  
**Performance Goals**: Ejecución de hooks < 5s, interacción UI estable.  
**Constraints**: Dependencia de disponibilidad de `Catalog.Api` en entorno local/dev.  
**Scale/Scope**: 4 escenarios principales de la HU-01.

## Constitution Check

1. **Screenplay compliance**: Se utilizarán los paquetes raíz: `hooks` (Idempotence), `tasks` (SeleccionarAsiento), `userinterfaces` (EventPage), `questions` (ElEstadoDelAsiento), `stepdefinitions` y `runners`.
2. **Shared-specs truth**: Basado en `shared-specs/specs/001-ticketing-mvp/hu01-reserva-asiento.feature` y `shared-specs/contracts/openapi/catalog.yaml`.
3. **Catalog fidelity**: Los estados `available`, `reserved` y `sold` se mapean 1:1 con el enum de `catalog.yaml`. La tarea `SeleccionarAsiento` valida el estado visual contra el contrato.
4. **Scenario idempotency**: `hooks/IdempotenceHook.java` implementará `@Before` (POST a `admin/events` y `admin/events/{id}/seats`) y `@After` (POST a `admin/events/{id}/deactivate`).
5. **Serenity Starter baseline**: Confirmado, el proyecto usa el wrapper de Gradle y archivos de propiedades estándar de Serenity.

## Screenplay Architecture Detail

### 1. Hook de Idempotencia (`hooks/IdempotenceHook.java`)
- **Setup (@Before)**:
  - Crea un evento llamando al endpoint `POST /admin/events`.
  - Genera 5 asientos disponibles llamando a `POST /admin/events/{eventId}/seats`.
  - Almacena el `eventId` en la sesión de Serenity (`Serenity.setSessionVariable("CREATED_EVENT_ID")`).
- **Cleanup (@After)**:
  - Desactiva el evento para mantener el entorno limpio.

### 2. Task: Seleccionar Asiento (`tasks/SeleccionarAsiento.java`)
- **Navegación**: Usa el `eventId` de la sesión para construir la URL `${baseUrl}/events/${eventId}`.
- **Acción**: Localiza el `SeatButton` que tenga el estado `available` (validando via `aria-label` o CSS).
- **Interacción**: Hace clic y luego confirma en el botón "Reserve & Add to Cart".

### 3. User Interface (`userinterfaces/EventPage.java`)
- **SEAT_MAP**: Contenedor principal del mapa.
- **SEAT_BUTTON**: Botón dinámico identificado por `aria-label` que incluye el estado.
- **RESERVE_BUTTON**: `Button` con texto "Reserve & Add to Cart".

### 4. Question (`questions/ElEstadoDelAsiento.java`)
- Extrae el estado visual del asiento para asegurar que cambió de `available` a `reserved` (o que coincide con el esperado).

