# 📱 Appium Mobile Test Automation Framework

> **Framework profesional de automatización móvil** con arquitectura POM (Page Object Model) + BDD usando tecnologías líderes de la industria: Appium, Cucumber, JUnit 5 y Allure Reports.

[![Java](https://img.shields.io/badge/Java-17-orange.svg)](https://www.oracle.com/java/)
[![Appium](https://img.shields.io/badge/Appium-9.3.0-blue.svg)](https://appium.io/)
[![Cucumber](https://img.shields.io/badge/Cucumber-7.20.1-green.svg)](https://cucumber.io/)
[![JUnit5](https://img.shields.io/badge/JUnit-5.10.3-red.svg)](https://junit.org/junit5/)
[![Allure](https://img.shields.io/badge/Allure-2.27.0-yellow.svg)](https://docs.qameta.io/allure/)
[![Maven](https://img.shields.io/badge/Maven-3.9+-purple.svg)](https://maven.apache.org/)

---

## 🎯 Características Principales

✅ **Arquitectura POM (Page Object Model)** - Separación clara de capas (Pages, Actions, Steps)  
✅ **BDD con Cucumber** - Escenarios legibles en Gherkin para stakeholders no técnicos  
✅ **Selectores Multiplataforma** - Soporte Android/iOS con `@AndroidFindBy` y `@iOSXCUITFindBy`  
✅ **Reportes Visuales con Allure** - Screenshots automáticos, gráficos y tendencias  
✅ **Captura de Evidencias** - Screenshots en fallos + logs detallados  
✅ **Configuración Flexible** - Archivos `.properties` para diferentes ambientes  
✅ **CI/CD Ready** - Integración lista con Jenkins, GitHub Actions, GitLab CI  

---

## 🏗️ Stack Tecnológico

| Tecnología | Versión | Propósito |
|------------|---------|-----------|
| **Java** | 17 | Lenguaje base |
| **Appium Java Client** | 9.3.0 | Interacción con dispositivos móviles |
| **Selenium** | 4.26.0 | WebDriver base |
| **Cucumber** | 7.20.1 | BDD Framework |
| **JUnit 5** | 5.10.3 | Test Runner |
| **Allure** | 2.27.0 | Reportes visuales |
| **Maven** | 3.9+ | Gestión de dependencias |

---

## 📁 Estructura del Proyecto

```text
appium-cucumber-base/
├── src/test/java/com/automatizacion/base/
│   ├── actions/              # Lógica de negocio (BDD layer)
│   │   ├── CounterActions.java
│   │   └── HomeActions.java
│   ├── config/               # Configuración del framework
│   │   └── FrameworkConfig.java
│   ├── driver/               # Factory y Manager de Appium Driver
│   │   ├── DriverFactory.java
│   │   └── DriverManager.java
│   ├── hooks/                # Cucumber Hooks (Before/After)
│   │   ├── Hooks.java
│   │   └── EvidenceHooks.java
│   ├── pages/                # Page Objects (POM)
│   │   ├── BasePage.java
│   │   ├── CounterPage.java
│   │   └── HomePage.java
│   ├── runner/               # JUnit Suite Runner
│   │   └── RunCucumberTest.java
│   ├── steps/                # Cucumber Step Definitions
│   │   ├── CounterDemoSteps.java
│   │   ├── FrameworkHealthSteps.java
│   │   └── MobileSmokeSteps.java
│   ├── ui/                   # Locators (Multiplataforma)
│   │   ├── CounterUI.java
│   │   └── HomePageUI.java
│   └── utils/                # Utilidades (Screenshots, logs)
│       └── EvidenceCapture.java
├── src/test/resources/
│   ├── config/
│   │   └── local.properties  # Configuración del entorno
│   ├── features/             # Escenarios Cucumber (Gherkin)
│   │   ├── counter_demo.feature
│   │   ├── framework_health.feature
│   │   └── mobile_smoke.feature
│   ├── allure.properties
│   └── junit-platform.properties
├── .gitignore
├── pom.xml
├── COMO_VER_REPORTES.md      # Guía detallada de reportes
└── README.md
```

---

## 🚀 Inicio Rápido

### Prerrequisitos

1. **Java 17+** instalado y configurado (`JAVA_HOME`)
2. **Maven 3.9+** instalado
3. **Appium Server 2.x** corriendo (solo para tests `@mobile`)
4. **Android SDK** / **Xcode** según plataforma
5. **Emulador/Dispositivo** configurado y visible con `adb devices` (Android) o Simulator (iOS)

### Instalación

```bash
# 1. Clonar el repositorio
git clone https://github.com/tu-usuario/appium-cucumber-base.git
cd appium-cucumber-base

# 2. Configurar el entorno
# Editar src/test/resources/config/local.properties con tus datos

# 3. Compilar el proyecto
mvn clean install -DskipTests
```

### Configuración

Edita `src/test/resources/config/local.properties`:

```properties
# Plataforma
platform.name=Android
platform.version=16
device.name=emulator-5554
automation.name=UiAutomator2

# Aplicación
app.package=com.expandtesting.practice
app.activity=com.expandtesting.practice.MainActivity
# O usa: app.path=/ruta/a/tu/app.apk

# Appium Server
appium.server.url=http://127.0.0.1:4723
```

---

## 🧪 Ejecución de Tests

### Opción 1: Tests SIN dispositivo móvil (Framework Health Check)

```bash
mvn clean test -Drun.mobile.tests=false
```

Ejecuta solo el test de sanidad del framework sin necesidad de Appium/dispositivo.

### Opción 2: Tests CON dispositivo móvil (Full Suite)

```bash
# Asegúrate de tener:
# - Appium Server corriendo (appium)
# - Emulador/dispositivo conectado

mvn clean test
```

### Opción 3: Ejecutar un Feature específico

```bash
mvn test -Dcucumber.filter.tags="@mobile"
```

---

## 📊 Reportes

### Ver Reporte de Allure (Recomendado)

```bash
# Generar y abrir automáticamente en el navegador
mvn allure:serve
```

**Incluye:**
- ✅ Screenshots de fallos
- ✅ Logs del driver
- ✅ Gráficos de tendencias
- ✅ Timeline de ejecución
- ✅ Historia de tests

### Ver Reporte de Cucumber

```bash
# Abrir HTML generado
Start-Process target/cucumber-reports/cucumber.html
```

📖 **Guía completa:** Ver [COMO_VER_REPORTES.md](COMO_VER_REPORTES.md)

---

## 🧩 Ejemplo de Uso

### Feature (Gherkin)

```gherkin
@mobile
Feature: Counter Demo
  As a QA Engineer
  I want to test the counter functionality
  To ensure it works correctly

  Scenario: Increment counter multiple times
    Given the user is on the application home page
    And the application title should be "The Practice App"
    When the user navigates to the counter demo screen
    And the user increments the counter 3 times
    Then the counter value should be "3"
```

### Step Definition

```java
@When("the user increments the counter {int} times")
public void incrementCounterTimes(int times) {
    OnStage.theActorInTheSpotlight().attemptsTo(
        CounterActions.incrementTimes(times)
    );
}
```

### Page Object (Multiplataforma)

```java
public class CounterUI {
    @AndroidFindBy(id = "com.expandtesting.practice:id/btnIncrement")
    @iOSXCUITFindBy(accessibility = "incrementButton")
    public static Target BUTTON_INCREMENT = Target.the("Increment Button");
}
```

---

## 🔧 Configuración Avanzada

### Cambiar entre Android e iOS

En `local.properties`:

```properties
# Android
platform.name=Android
app.package=com.example.app
app.activity=.MainActivity

# iOS
platform.name=iOS
bundle.id=com.example.app
```

### Configurar múltiples ambientes

Crea archivos como `qa.properties`, `staging.properties`:

```bash
./gradlew test -DTEST_ENV=qa
```

---

## 🤝 Contribuir

1. Fork el proyecto
2. Crea una rama para tu feature (`git checkout -b feature/AmazingFeature`)
3. Commit tus cambios (`git commit -m 'Add some AmazingFeature'`)
4. Push a la rama (`git push origin feature/AmazingFeature`)
5. Abre un Pull Request

---

## 📝 Licencia

Este proyecto está bajo la Licencia MIT - ver el archivo [LICENSE](LICENSE) para más detalles.

---

## 👤 Autor

**Tu Nombre**
- GitHub: [Harp-Andres](https://github.com/Harp-Andres/Harp-Andres)
- LinkedIn: [Hardware Andres Rodriguez P](https://www.linkedin.com/in/andresrodriguezpisa-calidaddesoftware/)

---

## 🙏 Agradecimientos

- [Appium](https://appium.io/) - Automatización móvil open source
- [Cucumber](https://cucumber.io/) - BDD Framework
- [Allure Framework](https://docs.qameta.io/allure/) - Reportes visuales
- Comunidad de testing de software

---

**⭐ Si este proyecto te fue útil, dale una estrella!**

### 2) Pruebas mobile reales con Appium

```powershell
.\gradlew.bat test -DTEST_ENV=local -Dcucumber.filter.tags=@mobile -Drun.mobile.tests=true
```

### 3) Ejecutar todo (smoke + mobile)

```powershell
.\gradlew.bat test -DTEST_ENV=local -Dcucumber.filter.tags="@mobile or not @mobile" -Drun.mobile.tests=true
```

## Allure report

```powershell
mvn allure:report
mvn allure:serve
```

> Los resultados quedan en `target/allure-results`.

## Notas de diseno

- `DriverManager` usa `ThreadLocal` para ejecucion paralela futura.
- `Hooks` inicializa y cierra driver para escenarios `@mobile`.
- El runner excluye `@mobile` por defecto; para incluirlos usa `-Dcucumber.filter.tags`.
- Si `run.mobile.tests=false` y fuerzas `@mobile`, los escenarios se omiten por seguridad.
- El escenario `framework_health.feature` sirve como verificacion de CI sin infraestructura mobile.
