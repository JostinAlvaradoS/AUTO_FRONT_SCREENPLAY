# AUTO_FRONT_SCREENPLAY - Frontend Automation (Screenplay)

Este proyecto contiene las pruebas de reserva de asientos utilizando el patrón **Screenplay** con Serenity BDD para el frontend.

## Requisitos Previos

### Entorno de Aplicación (SUT)

1.  **Backend**: Debe estar corriendo vía Docker Compose en `ticketing_project`.
2.  **Frontend**: Debe estar corriendo en [http://localhost:5173](http://localhost:5173) (o el puerto configurado) tras ejecutar `pnpm dev`.

### Herramientas
*   Java JDK 17 o superior.
*   Google Chrome y ChromeDriver.

## Ejecución de Pruebas

```bash
./gradlew clean test aggregate
```

## Reporte
El reporte detallado se genera en:
`target/site/serenity/index.html`
