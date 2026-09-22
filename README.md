# Guía rápida de ejecución


Este proyecto es un multimódulo de Spring Boot con dos módulos principales:


- `pokemon-service`: aplicación principal
- `pokeapi-reactor`: librería cliente para consumir la API de PokeAPI


## Requisitos


- Java 17+
- Maven 3.9+ o usar el wrapper incluido en la raíz (`mvnw` / `mvnw.cmd`)


## 1) Compilar e instalar dependencias


Desde la raíz del proyecto:


### En Linux/macOS


```bash
./mvnw clean install
```


### En Windows


```powershell
mvnw.cmd clean install
```


Este comando compila ambos módulos y deja las dependencias listas para ejecutar la aplicación.


## 2) Ejecutar la aplicación


### Opción A: ejecutar con Maven desde el módulo principal


```bash
cd pokemon-service
../mvnw spring-boot:run
```


### Opción B: ejecutar el JAR generado


```bash
cd pokemon-service
java -jar target/pokemon-service-0.0.1-SNAPSHOT.jar
```


La aplicación queda disponible en:


http://localhost:8080


## 3) Base de datos H2


La app usa una base de datos en memoria H2.


- URL de consola: http://localhost:8080/h2-console
- JDBC URL: `jdbc:h2:mem:testdb`
- Usuario: `sa`
- Contraseña: vacío


---
# Postman Collection
La carpeta postman/ contiene ejemplos listos para importar.


[collection.json](postman/postman_collection.json)
---
# Assumptions
- El usuario se encuentra autenticado y todos los requests son válidos desde un punto de vista de seguridad, por lo tanto los endpoints reciben como parámetro el id del entrenador/pokemon sin validaciones extra.
- La conexion del cliente y el servidor es estable, por lo tanto no va a reintentar crear pokemon. (Caso contrario habria que evaluar agregar una key de idempotencia)


# Analisis y decisiones tomadas


## Proyecto multimódulo
Para mantener separadas las responsabilidades y facilitar la mantenibilidad y extensibilidad del proyecto, decidí separar el cliente proporcionado de la aplicación.


De esta forma, el cliente encargado de comunicarse con PokeAPI queda aislado de la lógica de negocio y de la API de la aplicación. Esto permite modificar o reemplazar el cliente de PokeAPI sin afectar directamente al resto de la aplicación, además de mantener una separación clara entre integración externa y lógica propia.

## Cache
El cliente provisto cuenta con una configuracion para habilitar el cacheado de los requests, si bien esto genera un ligero overhead en el primer request, los siguientes requests permiten ahorrar request a la API y mejorar la latencia.

## POST /pokemon
Da de alta un pokemon asociado a un ID de entrenador.
- Cada entrenador tiene un límite máximo de pokemons determinado por las variables de ambiente ubicadas en el archivo applications.properties del módulo pokemon-service.
- Para evitar sobrepasar este límite cada entrenador lleva el conteo de los pokemons actuales en su equipo y PC_BOX, el cual se verifica antes de insertar un nuevo registro (y luego se updatean los contadores).
- Al existir la posibilidad de requests concurrentes el metodo de creacion de pokemons se hace transaccional (atomizando el update del contador y la creación de un nuevo registro pokemon) y se agrega un lock en la tabla de entrenadores, impidiendo que dos o más ejecuciones concurrentes consuman el mismo valor de contador.

## GET /pokemon

Devuelve el detalle de un pokemon, utiliza el mismo DTO usado en la creación de pokemons.

## GET /trainers/{id}/pokemon?type=TEAM/PC_BOX&pageSize=10&pageNumber=1

Devuelve un listado paginado de pokemons, se puede configurar el tamaño de la página y el número a través de query param. Combina datos guardados de los pokemon con datos de la API.

Dependiendo del tipo utilizado devuelve los pokemon del equipo o del PC_BOX

## PATCH /pokemon/{id}/move

Mueve el pokemon al PC_BOX o al team, respetando los límites predeterminados.

## PATCH /pokemon/{id}/evolve

Evoluciona al pokemon deseado, recibe el nombre de la evolución destino.
- Revisa que tanto el nombre de la especie y su relación con el pokemon elegido sean válidos.
- Updatea la habilidad del pokemon con la primer habilidad encontrada o ninguna en caso de no tener.
