SSOT (Single Source of Truth)
Requerimientos Comportamentales: shared-specs/specs/hu01-reserva-asiento.feature
Contratos de Datos: catalog.yaml
Rutas y Parámetros API: EventsController.cs
Selectores Frontend: page.tsx
Arquitectura Requerida: Screenplay
Debe usar el patrón Screenplay (Actor, Tasks, Actions, Questions).
Idempotencia: OBLIGATORIO usar @Before y @After hooks que invoquen la API de Catalog para crear/eliminar data de prueba.
Stack: Java, Serenity BDD, Gradle.
Reglas del Taller Semana 5
Nomenclatura semántica (Sin comentarios innecesarios).
Responsabilidad única en cada clase Task.