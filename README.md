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
- El usuario se encuentra autenticado y todos los requests son válidos desde un punto de vista de seguridad, por lo tanto los endpoints reciben como parámetro el id del entrenador sin validaciones extra.
- La configuración del equipo activo nunca va a exceder un número razonable de pokemons, haciendo innecesario paginar los resultados del GET.


# Analisis y decisiones tomadas


## Proyecto multimódulo
Para mantener separadas las responsabilidades y facilitar la mantenibilidad y extensibilidad del proyecto, decidí separar el cliente proporcionado de la aplicación.


De esta forma, el cliente encargado de comunicarse con PokeAPI queda aislado de la lógica de negocio y de la API de la aplicación. Esto permite modificar o reemplazar el cliente de PokeAPI sin afectar directamente al resto de la aplicación, además de mantener una separación clara entre integración externa y lógica propia.

## POST /pokemon
Servicio que da de alta un pokemon asociado a un ID de entrenador.
- Cada entrenador tiene un límite máximo de pokemons determinado por las variables de ambiente ubicadas en el archivo applications.properties del módulo pokemon-service.
- Para evitar sobrepasar este límite cada entrenador lleva el conteo de los pokemons actuales en su equipo y pc_box, el cual se verifica antes de insertar un nuevo registro (y luego se updatean los contadores).
- Al existir la posibilidad de requests concurrentes el metodo de creacion de pokemons se hace transaccional (atomizando el update del contador y la creación de un nuevo registro pokemon) y se agrega un lock en la tabla de entrenadores, impidiendo que dos o más ejecuciones concurrentes consuman el mismo valor de contador.



