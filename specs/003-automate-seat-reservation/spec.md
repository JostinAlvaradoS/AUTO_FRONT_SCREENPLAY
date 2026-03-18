# Feature Specification: Automatización de Reserva de Asientos (HU-01)

**Feature Branch**: `003-automate-seat-reservation`  
**Created**: 2026-03-17  
**Status**: Draft  
**Input**: User description: "Genera la especificación técnica para la automatización de la HU-01. El SSOT principal es la HU en el archivo hu01-reserva-asiento.feature. Valida que los nombres de los estados de los asientos en la automatización coincidan con los enums del contrato catalog.yaml (available, reserved, sold). Define que la navegación del test debe ser dinámica: baseUrl + /events/ + createdEventId."

## Source of Truth & Contracts

- La única fuente autorizada de escenarios, criterios y requisitos es el archivo [shared-specs/specs/001-ticketing-mvp/hu01-reserva-asiento.feature](shared-specs/specs/001-ticketing-mvp/hu01-reserva-asiento.feature).
- El comportamiento de UI y los pasos automatizados deben mapearse contra [shared-specs/contracts/openapi/catalog.yaml](shared-specs/contracts/openapi/catalog.yaml) para mantener la fidelidad al servicio `catalog`.
- Los estados de los asientos en la automatización DEBEN coincidir exactamente con los enums del contrato: `available`, `reserved`, `sold`.

## User Scenarios & Testing *(mandatory)*

### User Story 1 - Reserva Exitosa de Asiento Disponible (Priority: P1)

Como Cliente, quiero seleccionar un asiento disponible en el mapa para agregarlo al carrito y reservarlo temporalmente.

**Why this priority**: Es el flujo principal de valor del MVP (Happy Path) definido en la HU-01.

**Independent Test**: Puede probarse navegando a la ruta dinámica del evento y verificando que el asiento seleccionado se agrega al carrito y el sistema inicia el TTL.

**Acceptance Scenarios**:

1. **Given** que el Hook de inicialización ha creado un evento con asientos en estado `available` vía API.
2. **When** el cliente navega a la URL dinámica: `baseUrl + /events/ + createdEventId`.
3. **And** selecciona un asiento con estado `available`.
4. **Then** el sistema debe confirmar la reserva y la adición al carrito exitosamente.
5. **And** el asiento seleccionado debe quedar bloqueado para otros usuarios.

---

### User Story 2 - Intento de Reserva de Asiento Ocupado (Priority: P2)

Como Cliente, no debo poder seleccionar asientos que ya están reservados o vendidos.

**Why this priority**: Garantiza la integridad del negocio y evita colisiones entre usuarios concurrentes.

**Independent Test**: Navegar a un evento donde el setup previo configuró ciertos asientos como `reserved` o `sold`.

**Acceptance Scenarios**:

1. **Given** un evento con asientos configurados en estado `reserved` o `sold`.
2. **When** el cliente intenta seleccionar un asiento con estado `reserved`.
3. **Then** el sistema debe rechazar la selección de forma inmediata.
4. **And** debe mostrar un mensaje de error indicando que el asiento no está disponible.

---

### User Story 3 - Visualización del Tiempo Restante (TTL) (Priority: P3)

Como Cliente, quiero ver un temporizador con el tiempo restante para completar mi compra.

**Why this priority**: Mejora la experiencia de usuario informando sobre el tiempo de expiración de la reserva.

**Independent Test**: Verificar la presencia y funcionamiento del componente visual del temporizador tras una reserva exitosa.

**Acceptance Scenarios**:

1. **Given** que el cliente tiene una reserva activa.
2. **When** consulta el estado de su proceso o ve su carrito.
3. **Then** el sistema debe mostrar un temporizador con el tiempo restante.

### Edge Cases

- **Expiración de TTL**: El asiento debe volver automáticamente al estado `available` en el mapa si el tiempo expira sin completar la compra.
- **Navegación a Evento Inexistente**: Manejo de error 404 cuando el ID de evento en la ruta dinámica no existe en el catálogo.

## Requirements *(mandatory)*

### Functional Requirements

- **FR-001**: La navegación del test DEBE ser dinámica, construyendo la URL como: `${baseUrl}/events/${createdEventId}`.
- **FR-002**: Los selectores de automatización DEBEN basarse en los componentes de Next.js localizados en `app/events/[eventId]/page.tsx` y sus subcomponentes (`Seatmap`, `SeatButton`).
- **FR-003**: Los estados de los asientos en la lógica de automatización DEBEN coincidir con los definidos en `catalog.yaml`: `available`, `reserved`, `sold`.
- **FR-010 (Idempotencia)**: El proyecto DEBE incluir un Hook de inicialización (`@Before`) que use la API de `Catalog.Api` para crear un evento y asientos antes de cada prueba, y un Hook de limpieza (`@After`) para eliminarlos al finalizar.

### Key Entities

- **Evento (Event)**: Identificado por un `eventId` dinámico (UUID).
- **Asiento (Seat)**: Posee un `status` que determina su interactividad en el frontend.

## Automation Discipline

- Toda ejecución de prueba debe ser independiente y auto-contenida (autocreación de su propia data).
- Se prohíbe el uso de IDs de eventos estáticos o "hardcoded" en los archivos de features o tasks.
- Los pasos de la automatización deben validar los contratos contra el esquema de `catalog`.

## Success Criteria *(mandatory)*

### Measurable Outcomes

- **SC-001**: Ejecución exitosa del 100% de los escenarios de la HU-01 en el pipeline de CI.
- **SC-002**: Los hooks de `@Before` y `@After` deben ejecutarse correctamente garantizando que no haya basura de datos entre pruebas.
- **SC-003**: La navegación dinámica debe ser capaz de resolver correctamente la ruta del frontend usando el ID devuelto por el backend.
- **SC-004**: Los estados visuales del frontend deben mapearse 1:1 con los estados del contrato API.

