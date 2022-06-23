# Joko Report
Joko Report es una librería destinada a ayudar con la generación de reportes utilizando
[Apache Velocity](https://velocity.apache.org), además de tareas básicas como disparar trabajos de impresión y
generación de PDF. Ofrece además utilitarios para trabajar con comandos ESC/P2 de EPSON,
para impresoras matriciales. La misma está diseñada para integrarse a proyectos que utilizan **Spring Framework**.

>OBS: La librería aun se encuentra en etapas de prueba BETA  por tanto para utilizarla se debe construir y publicarla
> a un repositorio maven privado o agregar como librería dentro del proyecto.

### Características
* Provee clases que permiten diseñar reportes hml y convertirlos a PDF
* Provee clases que permiten diseñar reportes de texto con caracteres ESC/P2 para impresoras matriciales EPSON.
* Provee clases que permiten la conexion a servidores de impresión que utilicen cups utilizando la libreria cups4j.
* Provee un componente para Spring Boot, que provee métodos de acceso rápido a funcionalidades de impresión y generación de reportes.
* Provee una clase de auto configuración para Spring Boot.

### Compatibilidad con Spring Boot 
La librería soporta las siguientes versiones de Spring boot:

* Spring Boot 1.5.x o superior
* Spring Boot 2.x o superior

### Compatibilidad con Java
La librería soporta las siguientes versiones de JDK:

* JDK 8
* JDK 11

## Configuración
### Dependencias
Las funcionalidades de impresión utilizan la librería **cups4j** por tanto necesitamos agregar la dependencia:

```
<dependency>
    <groupId>org.cups4j</groupId>
    <artifactId>cups4j</artifactId>
    <version>0.7.9</version>
</dependency>
```

Luego podemos agregar la dependencia de joko-report si la tenemos publicada en un repositorio o agregarla como
librería(JAR) dentro del proyecto:

```
//JAVA 11
<joko-report.version>1.0.0-beta4</joko-report.version>
<dependency>
    <groupId>io.github.jokoframework</groupId>
    <artifactId>joko-report</artifactId>
    <version>${joko-report.version}</version>
</dependency>
```

```
//JAVA 8
<joko-report.version>1.0.0-beta4-jdk8</joko-report.version>
<dependency>
    <groupId>io.github.jokoframework</groupId>
    <artifactId>joko-report</artifactId>
    <version>${joko-report.version}</version>
</dependency>
```

### CLASE JokoReporter
La clase JokoReporter permite configurar un contexto de Apache Velocity de manera sencilla y ofrece varios métodos para la generación de los reportes.
A continuación ofrecemos un listado de los métodos principales con una descripción de su funcionamiento:

> Obs: Los métodos marcados con el tag **[ESC/P2]** se utilizan exclusivamente cuando se trabaja con la clase **EscPrinter**

| Método                                                          | Descripción                                                                       | Parámetros                                                                                                                                                                                                                                                    | Retorna                                         |
|-----------------------------------------------------------------|-----------------------------------------------------------------------------------|---------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------|-------------------------------------------------|
| **buildInstance**(String reportTemplatePath, Object params)     | Construye una instancia de **JokoReporter** e inicializa un contexto de Velocity. | **reportTemplatePath**: Url al archivo **.vm** de velocity que contiene el diseño del reporte<br/>Ej: '/reports/invoice.vm'<br/>**params**: Objeto con los parámetros que se pasará al conexto de Velociy con los valores a ser utilizados dentro del reporte | **io.github.jokoframework.report.JokoReporter** |
| **initializeContext**(String reportTemplatePath, Object params) | Inicializa un contexto de Velocity.                                               | **reportTemplatePath**: Url al archivo **.vm** de velocity que contiene el diseño del reporte<br/>Ej: '/reports/invoice.vm'<br/>**params**: Objeto con los parámetros que se pasará al conexto de Velociy con los valores a ser utilizados dentro del reporte | **void**                                        |
| **buildReport**()                                               | Procesa el archivo **.vm** generando el reporte correspondiene.                   | --                                                                                                                                                                                                                                                            | **java.io.StringWriter**                        |
| **getAsString**(boolean escEnabled)                             | Retorna el reporte como String.                                                   | **escEnabled**: Cuando se trabaja con caracteres de escape ESC/P2 el valor debe ser **true**                                                                                                                                                                  | **java.lang.String**                            |
| [ESC/P2] **getEscBytes**()                                      | Retorna el reporte como byte array.                                               | --                                                                                                                                                                                                                                                            | **byte[]**                                      |
| [ESC/P2] **getEncodedList**()                                   | Retorna el reporte como una lista con los caracteres codificados.                 | --                                                                                                                                                                                                                                                            | **java.util.List** of **java.lang.String**      |
| **getPDFAsByte**()                                              | Genera un pdf a partir de un String html y lo convierte a byte array.             | **html**: Código html como String                                                                                                                                                                                                                             | **byte[]**                                      |

#### CONFIGURACIÓN DE CONTEXTO VELOCITY
Parra inicializar un contexto velocity se puede utilizar el método esttático **buildInstance**.
Estet método realiza lo siguiente:
* Inicializa una instancia de la clase JokoReporter.
* Inicializa un objeto de contexto de Velocity (VelocityContext).
* Inicializa un objeto de template de Velocity (Template) en base a la ruta del template proporcionado.
* Configura uilitarios que se pueden utilizar dentro del reporte velocity.

Recibe como parámetros:
* La ruta donde se encuentra el reporte velocity(.vm) en el Classpath.
* El objeto que contiene los datos a ser utilizados dentro del reporte.
```
JokoReporter jr = JokoReporter.buildInstance("reports/people.vm", params);
```

También se puede invocar al método **initializeContext** de forma independiente si así se desea. Ej:

```
JokoReporter jokoReporter = new JokoReporter();
jokoReporter.initializeContext("reports/people.vm", params);
```

Los parámetros dentro del reporte se pueden acceder mediante la variable **$Params**.
Ej:

```
#set ($peopleList = $Params.peopleList)
```

#### Ejemplos de código
```
// ESCP/2
public class Reports {
    private static final String PEOPLE_REPORT_TEMPLATE = "/reports/people.vm";
    
    printPeopleReport(List people){
        JokoReporter jokoReporter = JokoReporter.buildInstance(PEOPLE_REPORT_TEMPLATE, people);
        String reportString = jokoReporter.getAsString(true);
        byte[] reportBytes = jokoReporter.getEscBytes();
        List<String> reportChars = jokoReporter.getEncodedList();
        ...
    }
}
```

```
// PDF
public class Reports {
    private static final String PEOPLE_REPORT_TEMPLATE = "/reports/people.vm";
    
    printPeopleReport(List people){
        JokoReporter jokoReporter = JokoReporter.buildInstance(PEOPLE_REPORT_TEMPLATE, people);
        String reportString = jokoReporter.getAsString(false);
        byte[] reportBytes = jokoReporter.getPDFAsBytes(reportString);
        ...
    }
}
```

#### UTILITARIOS

La librería provee una gama de utiliarios customizados que se pueden acceder mediante la variable **$Tools** en los reportes.
La misma contiene los siguientes atributos:

* **$Tools.number**. Permite realizar formateo de números.
* **$Tools.date**. Proporciona funciones para trabajar con fechas
* **$Tools.stringUtils**. Proporciona acceso a la clase **StringUtils** de Apache Commons.
* **$Tools.objectUtils**. Proporciona acceso a la clase **ObjectUtils** de Apache Commons.

Tambien se disponibilizan variables con acceso a las clases **java.lang.String** y **java.time.ZoneId** 
en las siguiente variables:

* **$String**
* **$ZoneId**

>Se disponibilizan ademas los utilitarios estandar de Velocity bajo las variables indicadas
>en la documentación oficial:
>[tools-summary](https://velocity.apache.org/tools/3.1/tools-summary.html)

#### Ejemplos
###### Formateando un número

```
 #set ($salary = $Tools.number.format('#,###.00', $people.salary))
```

###### Convirtiendo una fecha a letras

```
$Tools.date.formatAsWords($people.birthDate, '{1} {0}, {2}')
```

###### Manejando valores por defecto para nulos

```
$Tools.nullSafe($people.email, "--")
```

###### Ejemplo de Reporte completo
```
#set ($peopleList = $Params)
<html id="PeopleReport">
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8"/>
    <style>
        #include("report.css")
    </style>
</head>
<body id="PeopleReportBody">
<header class="table">
    <h3 class="centered">LIST OF PEOPLE</h3>
</header>
<div class="centered">
    <table id="PeopleReportTable" class="table">
        <thead>
            <tr>
                <th class="centered">Name</th>
                <th class="centered">Lastname</th>
                <th class="centered">Email</th>
                <th class="centered">Gender</th>
                <th class="centered">Salary</th>
                <th class="centered">Date of birth</th>
            </tr>
        </thead>
        <tbody>
            #foreach( $people in $peopleList )
                #set ($salary = $Tools.number.format('#,###.00', $people.salary))
            <tr>
                <td class="centered">
                    $people.name
                </td>
                <td class="centered">
                    $people.lastName
                </td>
                <td class="centered">
                    $Tools.nullSafe($people.email, "--")
                </td>
                <td class="align-right">
                    $people.gender
                </td>
                <td class="align-right">
                    $salary USD
                </td>
                <td class="align-right">
                    $Tools.date.formatAsWords($people.birthDate, '{1} {0}, {2}')
                </td>
            </tr>
            #end
        </tbody>
    </table>
</div>
</body>
</html>
```

#### Configurando Localización .
La clase JokoReporter ofrece un método para configurar la localización de los utilitarios denominado **configLocale**.
La localización interna se maneja con un atributo de tipo **java.util.Locale** y afecta directamente a los utilitarios
**$Tools.number** y **$Tools.date**.

Ej:
```
JokoReporter jokoReporter = jokoReport.newJokoReporter(Templates.PEOPLE_REPORT_TEMPLATE, this.people);
jokoReporter.configLocale("en", "US"); // Modifica la localización a inglés de Estados Unidos de los utilitarios $Tools.date y $Tools.number
String reportOutput = jokoReporter.getAsString(false);
```

Se puede asi mismo modificar la localización de un utilitario específico utilizando los getters y setters:

Ej:
```
JokoReporter jokoReporter = jokoReport.newJokoReporter(Templates.PEOPLE_REPORT_TEMPLATE, this.people);
jokoReporter.getReportTools().getDate().setLocale(new Locale("en", "US")); // Modifica la localización del utilitario $Tools.date
String reportOutput = jokoReporter.getAsString(false);
```

### CLASE EscPrinter.
La clase JokoReporter ofrece varios métodos que permiten diseñar reportes con caracteres ESC/P2.
A continuación ofrecemos un listado de los métodos principales con una descripción de su funcionamiento:


| Método                                                    | Descripción                                                                                                                                                                                 | Parámetros                                                                                                                                                     | Retorna                                               |
|-----------------------------------------------------------|---------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------|----------------------------------------------------------------------------------------------------------------------------------------------------------------|-------------------------------------------------------|
| **selectCharacterSet**(char charset, int arg)             | Setea el conjunto de caracteres internacional a ser utilizado por la impresora                                                                                                              | **charset**: El número decimal que representa el se de caracteres segun el manual de EPSON.<br/>**arg**: Argumento requerido indicado por el manual de epson.  | **io.github.jokoframework.report.printer.EscPrinter** |
| **select10CPI**()                                         | Setea el número de caracteres por pulgada a 10.                                                                                                                                             | --                                                                                                                                                             | **io.github.jokoframework.report.printer.EscPrinter** |
| **select12CPI**()                                         | Setea el número de caracteres por pulgada a 12.                                                                                                                                             | --                                                                                                                                                             | **io.github.jokoframework.report.printer.EscPrinter** |
| **select15CPI**()                                         | Setea el número de caracteres por pulgada a 15.                                                                                                                                             | --                                                                                                                                                             | **io.github.jokoframework.report.printer.EscPrinter** |
| **selectSixLinesPerInch**()                               | Setea el espaciado de línea a 1/6.                                                                                                                                                          | --                                                                                                                                                             | **io.github.jokoframework.report.printer.EscPrinter** |
| **selectEightLinesPerInch**()                             | Setea el espaciado de línea a 1/8.                                                                                                                                                          | --                                                                                                                                                             | **io.github.jokoframework.report.printer.EscPrinter** |
| **selectDraftPrinting**()                                 | Setea la calidad de impresión a Draft(Borrador).                                                                                                                                            | --                                                                                                                                                             | **io.github.jokoframework.report.printer.EscPrinter** |
| **selectLQPrinting**()                                    | Setea la calidad de impresión a LQ(Letter Quality).                                                                                                                                         | --                                                                                                                                                             | **io.github.jokoframework.report.printer.EscPrinter** |
| **selectRomanFont**()                                     | Setea la fuente a Roman.                                                                                                                                                                    | --                                                                                                                                                             | **io.github.jokoframework.report.printer.EscPrinter** |
| **selectSerifFont**()                                     | Setea la fuente a Serif.                                                                                                                                                                    | --                                                                                                                                                             | **io.github.jokoframework.report.printer.EscPrinter** |
| **setCharacterSize**()                                    | Setea el tamaño de caracteres.                                                                                                                                                              | --                                                                                                                                                             | **io.github.jokoframework.report.printer.EscPrinter** |
| **doubleWidth**(boolean on)                               | Inicia o finaliza el modo **doubleWidth** el cual duplica el ancho de los caracteres de una línea mientras esté activo.                                                                     | **on**: Si es true inicia el modo **dobleWitdh**, de lo contrario finaliza el mismo.                                                                           | **io.github.jokoframework.report.printer.EscPrinter** |
| **doubleHeight**(boolean on)                              | Inicia o finaliza el modo **doubleHeight** el cual duplica la altura de los caracteres de una línea  mientras esté activo.                                                                  | **on**: Si es true inicia el modo **doubleHeight**, de lo contrario finaliza el mismo.                                                                         | **io.github.jokoframework.report.printer.EscPrinter** |
| **doubleSize**(boolean on)                                | Inicia o finaliza el modo **doubleSize** el cual duplica el tamaño de los caracteres de una línea  mientras esté activo.                                                                    | **on**: Si es true inicia el modo **doubleSize**, de lo contrario finaliza el mismo.                                                                           | **io.github.jokoframework.report.printer.EscPrinter** |
| **condensed**(boolean on)                                 | Inicia o finaliza el modo **condensed** el cual condensa el tamaño de los caracteres de una línea  mientras esté activo.                                                                    | **on**: Si es true inicia el modo **condensed**, de lo contrario finaliza el mismo.                                                                            | **io.github.jokoframework.report.printer.EscPrinter** |
| **newLine**()                                             | Salta a la siguiente línea.                                                                                                                                                                 | --                                                                                                                                                             | **io.github.jokoframework.report.printer.EscPrinter** |
| **formFeed**()                                            | Indica si se debe saltar al inicio de la siguiente página.                                                                                                                                  |                                                                                                                                                                | **io.github.jokoframework.report.printer.EscPrinter** |
| **advanceVertical**(float centimeters)                    | Avanza la posición vertical de impresión a x centímetros desde el margen superior                                                                                                           | **centimeters**: longitud a avanzar en centímetros.                                                                                                            | **io.github.jokoframework.report.printer.EscPrinter** |
| **advanceHorizontal**(float centimeters)                  | Avanza la posición horizontal de impresión a x centímetros desde el margen izquierdo                                                                                                        | **centimeters**: longitud a avanzar en centímetros.                                                                                                            | **io.github.jokoframework.report.printer.EscPrinter** |
| **horizontalPositionCm**(float centimeters)               | Avanza la posición horizontal de impresión de forma absoluta a x centímetros desde el margen izquierdo. El espacio avanzado no empuja a los demas caracteres de la línea                    | **centimeters**: longitud a avanzar en centímetros.                                                                                                            | **io.github.jokoframework.report.printer.EscPrinter** |
| **horizontalPositionCm**(float centimeters, Object param) | Avanza la posición horizontal de impresión de forma absoluta a x centímetros desde el margen izquierdo e imprime un valor. El espacio avanzado no empuja a los demas caracteres de la línea | **centimeters**: longitud a avanzar en centímetros.<br/>**param**: El valor a imprimir en la posición indicada                                                 | **io.github.jokoframework.report.printer.EscPrinter** |
| **tab**(int tabs)                                         | Avanza la posición horizontal de impresión a x tabulaciones desde el margen izquierdo. El espacio avanzado afecta a los demas caracteres de la línea empujandolos                           | **tabs**: número de tabulaciones.                                                                                                                              | **io.github.jokoframework.report.printer.EscPrinter** |
| **tab**(int tabs, Object param)                           | Avanza la posición horizontal de impresión a x tabulaciones desde el margen izquierdo e imprime un valor. El espacio avanzado afecta a los demas caracteres de la línea empujandolos        | **tabs**: número de tabulaciones.<br/>**param**: El valor a imprimir en la posición indicada                                                                   | **io.github.jokoframework.report.printer.EscPrinter** |
| **vtab**(int tabs)                                        | Avanza la posición vertical de impresión a x tabulaciones desde el margen superior.                                                                                                         | **tabs**: número de tabulaciones.                                                                                                                              | **io.github.jokoframework.report.printer.EscPrinter** |
| **vtab**(int tabs, Object param)                          | Avanza la posición horizontal de impresión a x tabulaciones desde el margen superior e imprime un valor.                                                                                    | **tabs**: número de tabulaciones.<br/>**param**: El valor a imprimir en la posición indicada                                                                   | **io.github.jokoframework.report.printer.EscPrinter** |
| **space**(int spaces)                                     | Agrega x caraceres de espacio desde el margen izquierdo. Afecta a los demas caracteres de la línea empujandolos                                                                             | **spaces**: cantidad de caracteres de espacio a agregar.                                                                                                       | **io.github.jokoframework.report.printer.EscPrinter** |
| **space**(int spaces, Object param)                       | Agrega x caraceres de espacio desde el margen izquierdo e imprime un valor.Afecta a los demas caracteres de la línea empujandolos                                                           | **spaces**: cantidad de caracteres de espacio a agregar.<br/>**param**: El valor a imprimir en la posición indicada                                            | **io.github.jokoframework.report.printer.EscPrinter** |
| **point**(int points)                                     | Agrega x caraceres de punto desde el margen izquierdo. Afecta a los demas caracteres de la línea empujandolos                                                                               | **points**: cantidad de caracteres de punto a agregar.                                                                                                         | **io.github.jokoframework.report.printer.EscPrinter** |

La clase **EscPrinter** está disponible dentro de los templates de reportes en la variable **$Escp**

### COMPONENTE JokoReport
La clase **JokoReport** es un componente para Spring que facilita el trabajo con servidores cups y permite construir instancias
de la clase **JokoReporter** de manera sencilla:

Para uilizarlo con Spring Boot podemos extender la clase de auto configuración **JokoReportAutoConfig** o escanear el paquete correspondiente:
```
@Configuration
public class ReportConfig extends JokoReportAutoConfig {
}
```

```
@Configuration
@ComponentScan("io.github.jokoframework.report.component")
public class ReportConfig {
}
```
#### Configuración de servidor CUPS
Podemos configurar a que cups server se debe apuntar utilizando los siguientes properties:

| Property                     | Descripción                                                                         | Valor por defecto | Valores aceptados    |
|------------------------------|-------------------------------------------------------------------------------------|-------------------|----------------------|
| joko.report.cups.server.url  | Permite configurar la ip del servidor cups.                                         | **localhost**     | **dominio o ip**     |
| joko.report.cups.server.port | Permite configurar el puerto donde está expuesto el servicio de cups en el servidor | **631**           | **un número entero** |

#### MÉTODOS
A continuación ofrecemos un listado de los métodos principales con una descripción de su funcionamiento:

| Método                                                                              | Descripción                                                                                                                                                                   | Parámetros                                                                                                                                                                                                                                                                                                                            | Retorna                                                   |
|-------------------------------------------------------------------------------------|-------------------------------------------------------------------------------------------------------------------------------------------------------------------------------|---------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------|-----------------------------------------------------------|
| **findAllPrintersFromServer**()                                                     | Devuelve todas las impresoras configuradas en el servidor cups configurado mediante las propiedades **joko.report.cups.server.url**.<br/> y **joko.report.cups.server.port**  | --                                                                                                                                                                                                                                                                                                                                    | List of **org.cups4j.CupsPrinter**                        |
| **findPrinterByNameFromServer**(String name)                                        | Devuelve una impresora por nombre si esa se encuentra configurada en el servidor cups configurado                                                                             | **name**: Nombre de la impresora en el servidor.                                                                                                                                                                                                                                                                                      | **org.cups4j.CupsPrinter**                                |
| **findPrinterByNameFromOS**(String name)                                            | Devuelve una impresora por nombre si esa se encuentra configurada en el sisema operativo local                                                                                | **name**: Nombre de la impresora.                                                                                                                                                                                                                                                                                                     | **javax.print.PrintService**                              |
| **newJokoReporter**(String templatePath, Object params)                             | Construye una instancia de la clase **JokoReporter** e inicializa un contexto de Velocity                                                                                     | **reportTemplatePath**: Url al archivo **.vm** de velocity que contiene el diseño del reporte<br/>Ej: '/reports/invoice.vm'<br/>**params**: Objeto con los parámetros que se pasará al conexto de Velociy con los valores a ser utilizados dentro del reporte                                                                         | **io.github.jokoframework.report.JokoReporter**           |
| **newJokoReporter**(String templatePath, Object params, ESCPrinter escPrinter)      | Construye una instancia de la clase **JokoReporter** e inicializa un contexto de Velocity                                                                                     | **reportTemplatePath**: Url al archivo **.vm** de velocity que contiene el diseño del reporte<br/>Ej: '/reports/invoice.vm'<br/>**params**: Objeto con los parámetros que se pasará al conexto de Velociy con los valores a ser utilizados dentro del reporte<br/>**escPrinter**: una instancia de la clase **ESCPrinter**            | **io.github.jokoframework.report.JokoReporter**           |
| **newJokoReporter**(ESCPrinter escPrinter)                                          | Construye una instancia de la clase **JokoReporter** e inicializa un contexto de Velocity                                                                                     | **escPrinter**: una instancia de la clase **ESCPrinter**                                                                                                                                                                                                                                                                              | **io.github.jokoframework.report.JokoReporter**           |
| **newJokoReporter**()                                                               | Construye una instancia de la clase **JokoReporter** e inicializa un contexto de Velocity                                                                                     | --                                                                                                                                                                                                                                                                                                                                    | **io.github.jokoframework.report.JokoReporter**           |
| **newEscPrinter**()                                                                 | Construye una instancia de la clase **EscPrinter**.                                                                                                                           | --                                                                                                                                                                                                                                                                                                                                    | **io.github.jokoframework.report.printer.EscPrinter**     |
| **printOnMatrixPrinter**(CupsPrinter printer, String templatePath, Object params)   | Procesa el archivo **.vm** generando el reporte con caracetéres ESC/P2 y genera un trabajo de impresión                                                                       | **printer**: una instancia de la clase **CupsPrinter**<br/>**reportTemplatePath**: Url al archivo **.vm** de velocity que contiene el diseño del reporte<br/>Ej: '/reports/invoice.vm'<br/>**params**: Objeto con los parámetros que se pasará al conexto de Velociy con los valores a ser utilizados dentro del reporte              | **void**                                                  |
| **printOnMatrixPrinter**(CupsPrinter printer, JokoReporter jokoReporter)            | Procesa el archivo **.vm** generando el reporte con caracetéres ESC/P2 y genera un trabajo de impresión                                                                       | **printer**: una instancia de la clase **CupsPrinter**<br/>**jokoReporter**: una instancia de la clase **JokoReporter**                                                                                                                                                                                                               | **void**                                                  |
| **printAsPDF**(CupsPrinter printer, String templatePath, Object params)             | Procesa el archivo **.vm** generando el reporte correspondiente como pdf y genera un trabajo de impresión                                                                     | **printer**: una instancia de la clase **CupsPrinter**<br/>**reportTemplatePath**: Url al archivo **.vm** de velocity que contiene el diseño del reporte<br/>Ej: '/reports/invoice.vm'<br/>**params**: Objeto con los parámetros que se pasará al conexto de Velociy con los valores a ser utilizados dentro del reporte              | **void**                                                  |
| **getPDFAsByte**(String templatePath, Object params)                                | Genera un pdf a partir de un String html y lo convierte a byte array                                                                                                          | **reportTemplatePath**: Url al archivo **.vm** de velocity que contiene el diseño del reporte<br/>Ej: '/reports/invoice.vm'<br/>**params**: Objeto con los parámetros que se pasará al conexto de Velociy con los valores a ser utilizados dentro del reporte                                                                         | **byte[]**                                                |
| **getPDFAsResponseEntity**(String templatePath, Object params, String fileName)     | Genera un pdf a partir de un String html y construye un response entity para usarlo directamente en un controlador Spring                                                     | **reportTemplatePath**: Url al archivo **.vm** de velocity que contiene el diseño del reporte<br/>Ej: '/reports/invoice.vm'<br/>**params**: Objeto con los parámetros que se pasará al conexto de Velociy con los valores a ser utilizados dentro del reporte<br/>**fileName**: Nombre del archivo para el encabezado en la respuesta | **org.springframework.http.ResponseEntity** of **byte[]** |


## Compilando desde los fuentes
La libreria puede ser generada como JAR para java 8 y 11 utilizando tareas gradle
prefabricadas:

### JAVA 8
Para construir el jar para java 8 podemos utilizar la siguiente tarea:

```
./gradlew generateJava8Artifact
```

Esto generará un archivo jar para jdk 8 bajo la carpeta **build/libs** con el sufijo
**-jdk8**. Por ejemplo si la version es **1.0.0** el jar será generado con
el siguiente nombre:

```
/build/libs/joko-report-1.0.0-jdk8.jar
```

### JAVA 11
Para construir el jar para java 11 podemos utilizar la siguiente tarea:

```
./gradlew generateJava11Artifact
```

Esto generará un archivo jar para jdk 8 bajo la carpeta **build/libs**. Por ejemplo si la version es **1.0.0** el jar será generado con
el siguiente nombre:

```
/build/libs/joko-report-1.0.0.jar
```

## Publicando a repositorios maven

### Publicando a un repositorio local
El jar puede ser publicado directament al repositorio maven local maven utilizando la siguiente tarea:

###### JAVA 8
```
./gradlew publishJava8Local
```
###### JAVA 11
```
./gradlew publishJava11Local
```

>OBS: Publicar directamente a un repositorio maven local con un nombre diferente aun no está soportado

### Publicando a un artifactory
Para publicar a un artifactory necesitamos crear un achivo **gradle.properties** en la raíz del proyecto
**root**, con las siguientes proopiedades:

```
artifactory_contextUrl=https://yourartifactoryurl
artifactory_user=artifactoryuser
artifactory_password=artifactorypassword
```

Luego el jar puede ser publicado al artifactory directamente utilizando las siguientes tareas:

###### JAVA 8
```
./gradlew publishJava8Artifactory
```
###### JAVA 11
```
./gradlew publishJava11Artifactory
```

>OBS: El archivo jar será publicado bajo la carpeta **libs-release-local** en el artifactory.
> Publicar a una carpeta diferente o como snapshot aun no está soportado.
