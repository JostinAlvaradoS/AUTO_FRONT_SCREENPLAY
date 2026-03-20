# AUTO_FRONT_SCREENPLAY - Frontend Automation (Screenplay Pattern)

Pruebas de automatización del frontend para la reserva de asientos utilizando el patrón **Screenplay** con **Serenity BDD** y **Cucumber**.

## 📋 Requisitos Previos

### Software Requerido

- **Java JDK 17 o superior**
- **Gradle 7.0 o superior**
- **Docker y Docker Compose**
- **Node.js 18+ y pnpm** (para el frontend)
- **Google Chrome** (para pruebas)

### Verificar Instalaciones

```bash
java -version
gradle --version
docker --version
docker-compose --version
node --version
pnpm --version
```

---

## 🚀 Guía de Ejecución Paso a Paso

### 1. Clonar el Repositorio y Submódulos

El proyecto utiliza submódulos para las especificaciones compartidas. Clona el repositorio e inicializa los submódulos:

```bash
git clone https://github.com/JostinAlvaradoS/AUTO_API_SCREENPLAY.git
cd AUTO_API_SCREENPLAY
git submodule update --init --recursive
```

### 2. Levantar la Infraestructura (Docker)

Entra en la carpeta de infraestructura y levanta los servicios necesarios (Bases de datos, APIs, etc.):

```bash
cd shared-specs/infra
docker-compose up -d
cd ../..
```

**Servicios levantados:**
- Base de datos (PostgreSQL/MySQL)
- API de Catálogo (puerto 50001)
- API de Inventario (puerto 50002)
- Kafka (si aplica)

**Verificar estado:**
```bash
docker-compose ps
```

### 3. Levantar el Frontend

Navega a la carpeta del frontend, instala las dependencias y ejecútalo:

```bash
cd shared-specs/frontend
pnpm install      # o npm install
pnpm dev          # o npm run dev
cd ../..
```

El frontend estará disponible en: **http://localhost:5173** (o el puerto configurado)

---

## 🧪 Construir y Ejecutar las Pruebas

### Compilar el Proyecto

```bash
# Limpiar build anterior y compilar
gradle clean build
```

### Ejecutar Todas las Pruebas

```bash
# Ejecutar tests y generar reporte
gradle test
```

O de forma más explícita:

```bash
gradle clean test aggregate
```

**Nota:** El `defaultTasks` definido en `build.gradle` ejecuta automáticamente: `clean`, `test`, `aggregate`.

### Ejecutar Pruebas por Tags (Filtrado)

```bash
# Ejecutar solo pruebas de reserva válida
gradle test --filter="@ReservaValida"

# Ejecutar solo pruebas de reserva inválida
gradle test --filter="@ReservaInvalida"
```

### Ejecutar Pruebas Específicas

```bash
# Ejecutar un scenario específico
gradle test -Dcucumber.filter.tags="@ReservaValida"
```

---

## 📊 Reportes

### Ubicación de Reportes Serenity

Después de ejecutar las pruebas, los reportes se encuentran en:

- **Reporte de una página:** `target/site/serenity/index.html`
- **Reportes estándar:** `target/site/serenity/`
- **Reporte Cucumber:** `target/cucumber-report/cucumber.html`

### Características del Reporte

✅ **Screenshots automáticos** en cada acción del test  
✅ **HTML interactivo** con detalles paso a paso  
✅ **Historial de ejecución** con resultados  
✅ **Tabla de resumen** con estadísticas  
✅ **Logs detallados** de cada operación  

**Abrir reporte:**
```bash
# Linux/Mac
open target/site/serenity/index.html

# Windows
start target/site/serenity/index.html

# Desde terminal (Linux/Mac)
xdg-open target/site/serenity/index.html
```

---

## 📁 Estructura del Proyecto

```
AUTO_FRONT_SCREENPLAY/
├── src/test/
│   ├── java/com/sofka/automation/
│   │   ├── tasks/              # Acciones que los actores realizan
│   │   │   ├── NavegarAlEvento.java
│   │   │   └── SeleccionarAsiento.java
│   │   ├── questions/          # Preguntas/consultas (assertions)
│   │   ├── ui/                 # Localizadores de elementos
│   │   │   └── EventPage.java
│   │   ├── stepdefinitions/    # Definiciones de pasos Gherkin
│   │   ├── runners/            # Ejecutores de Cucumber
│   │   ├── services/           # Servicios auxiliares
│   │   ├── api/                # Clientes API
│   │   ├── models/             # DTOs y modelos
│   │   ├── utils/              # Utilidades
│   │   ├── exceptions/         # Excepciones personalizadas
│   │   └── hooks/              # Hooks de Cucumber
│   └── resources/
│       ├── serenity.conf       # Configuración de Serenity
│       └── features/           # Archivos .feature (BDD)
├── build.gradle                # Configuración de Gradle
├── build/                      # Artefactos compilados
├── target/                     # Reportes y resultados
│   ├── site/serenity/          # Reportes Serenity
│   ├── cucumber-report/        # Reportes Cucumber
│   └── test-results/           # Resultados de tests
├── shared-specs/               # Submódulo con especificaciones compartidas
│   ├── infra/                  # Docker Compose para infraestructura
│   └── frontend/               # Aplicación frontend
└── README.md                   # Este archivo
```

---

## 🔧 Configuración

### serenity.conf

Archivo de configuración ubicado en `src/test/resources/serenity.conf`:

```hocon
serenity {
    take.screenshots = FOR_EACH_ACTION          # Screenshot en cada acción
    screenshot.height = 1024
    screenshot.width = 1024
    verbose.screenshots = true
    reports.outdir = "target/site/serenity"
}

webdriver {
    driver = chrome
    autodriver = true                           # Auto descargar ChromeDriver
    capabilities {
        browserName = "chrome"
        acceptInsecureCerts = true
        "goog:chromeOptions" {
            args = ["remote-allow-origins=*", "test-type", "no-sandbox", 
                    "ignore-certificate-errors", "--window-size=1000,800"]
        }
    }
}

environments {
    default {
        webdriver.base.url = "http://localhost:3000"
        catalog.api.url = "http://localhost:50001"
        inventory.api.url = "http://localhost:50002"
    }
    dev {
        webdriver.base.url = "http://localhost:3000"
        catalog.api.url = "http://localhost:50001"
        inventory.api.url = "http://localhost:50002"
    }
    ci {
        webdriver.base.url = "http://web-app:3000"
        catalog.api.url = "http://catalog-api:50001"
        inventory.api.url = "http://inventory-api:50002"
    }
}
```

---

## 🎯 Patrón Screenplay

El proyecto utiliza el **patrón Screenplay**, que organiza los tests en términos de:

- **Tasks (Tareas):** Acciones que el usuario realiza
- **Questions (Preguntas):** Consultas sobre el estado de la aplicación
- **Actors (Actores):** Usuarios o sistemas que interactúan con la aplicación

**Ejemplo:**
```java
actor.attemptsTo(
    NavegarAlEvento.conId(eventId),           // Task
    SeleccionarAsiento.disponible(),          // Task
    seeThat(ElAsientoEstaReservado.estado())  // Question
);
```

---

## 🐛 Solución de Problemas

### ChromeDriver no se descarga automáticamente

**Solución:**
```bash
# Actualizar webdriverManager
gradle clean build --refresh-dependencies
```

### Docker containers no inician

```bash
# Verificar logs
docker-compose logs

# Reiniciar servicios
docker-compose down
docker-compose up -d
```

### Frontend no accesible en localhost:5173

```bash
# Verificar si el puerto está en uso
lsof -i :5173

# Si está en uso, cambiar puerto en package.json o:
pnpm dev -- --port 5174
```

### Tests fallan por timeout

Aumentar timeouts en `serenity.conf`:
```hocon
serenity {
    webdriver.wait.for.timeout = 10000  # 10 segundos
}
```

---

## 📚 Dependencias Principales

| Dependencia | Versión | Propósito |
|---|---|---|
| Serenity BDD | 5.3.2 | Framework de automatización |
| Serenity Screenplay | 5.3.2 | Patrón Screenplay |
| Cucumber | 7.34.2 | BDD y Gherkin |
| JUnit Platform | 1.13.0 | Test runner |
| Selenium | (via Serenity) | Automatización web |
| REST Assured | 5.3.0 | Pruebas API |
| AssertJ | 3.23.1 | Assertions fluidas |
| Lombok | 1.18.28 | Boilerplate reduction |

---

## 🔄 Flujo de Trabajo Recomendado

1. **Clonar repositorio con submódulos**
   ```bash
   git clone --recursive https://github.com/JostinAlvaradoS/AUTO_API_SCREENPLAY.git
   ```

2. **Levantar infraestructura**
   ```bash
   cd shared-specs/infra && docker-compose up -d && cd ../..
   ```

3. **Levantar frontend**
   ```bash
   cd shared-specs/frontend && pnpm install && pnpm dev && cd ../..
   ```

4. **Ejecutar tests**
   ```bash
   gradle clean test aggregate
   ```

5. **Revisar reportes**
   - Abrir `target/site/serenity/index.html`

---

## 📝 Escribir Nuevos Tests

### 1. Crear archivo .feature

`src/test/resources/features/mi_feature.feature`:
```gherkin
#language: es

Funcionalidad: Descripción de la funcionalidad
  Como usuario
  Quiero realizar una acción
  Para lograr un objetivo

  @MiTag
  Escenario: Descripción del escenario
    Dado que existe un evento con asientos configurados
    Cuando el cliente realiza una acción
    Entonces el sistema debería responder correctamente
```

### 2. Crear StepDefinitions

`src/test/java/com/sofka/automation/stepdefinitions/MiStepDefinitions.java`:
```java
@Dado("que existe un evento con asientos configurados")
public void queExisteUnEvento() {
    // Implementación
}
```

### 3. Crear Tasks si es necesario

`src/test/java/com/sofka/automation/tasks/MiTarea.java`:
```java
public class MiTarea implements Task {
    @Override
    public <T extends Actor> void performAs(T actor) {
        actor.attemptsTo(
            // acciones
        );
    }
}
```

---

## 🤝 Contribuir

1. Crea una rama para tu feature: `git checkout -b feature/mi-feature`
2. Commit tus cambios: `git commit -am 'Add mi-feature'`
3. Push a la rama: `git push origin feature/mi-feature`
4. Abre un Pull Request

---

## 📄 Licencia

MIT

---

## 📞 Contacto

Para preguntas o soporte, contacta al equipo de QA Automation.

---

**Última actualización:** Marzo 2026
