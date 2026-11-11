# Implement a ticket from its step-by-step guide

**Approved on:** pending (review in the Pull Request that adds this file)
**Approved by:** pending
**Used for:** implementing the main logic of a ticket with an AI assistant, giving it the step-by-step guide of the
ticket as the prompt. The guide is written beforehand (see prompt 03) and does not contain the solution; the tests
of the ticket check the result.

The prompt below is kept **verbatim, in its original language (Spanish)**, because it is the exact text that was
given to the AI. Translating it would change the prompt that was used.

## Prompt

````markdown
# Guía PG-22 — Obtener un vídeo (`GET /api/videos/{id}`)

Guía para implementar el ticket PG-22. Se entiende sin más contexto que este documento y **no contiene la
solución**: explica qué hacer, en qué orden, con qué herramientas y cómo comprobarlo.

---

## 0. Contexto en 2 minutos

**Qué hay ya hecho (PG-18 y PG-20):**
- La tabla `video` en PostgreSQL (`entity/Video.java`) y su repositorio (`repository/VideoRepository.java`).
- Al arrancar, `VideoLoaderService` lee los `.json` del videoGrabber y guarda los vídeos.
- `GET /api/videos?page=0&size=12` devuelve la lista paginada para la home.
- `InvalidPaginationException` responde **400** cuando la página es inválida. Vas a hacer algo parecido para el 404.

**Qué pide PG-22 (Jira):**
1. `GET /api/videos/{id}` devuelve **título, descripción, URL del vídeo, URL de la miniatura, autor y tags**.
2. Si el id no existe → **404 Not Found**.

**Decisión ya tomada (Product Owner):** todavía no existe la entidad `User` (llega en PG-25). De momento el autor es
un texto, `authorName`, dentro de `Video`, y se rellena con el campo `"user"` del JSON del videoGrabber (el nombre
del canal):

```json
{
    "id": 0,
    "title": "Learning Java in 30 seconds",
    "user": "Code Channel",
    "meta": { "description": "...", "tags": ["java", "programming"] }
}
```

**Qué está ya preparado (no lo tienes que hacer):**

| Pieza                                   | Qué es                                                                       |
|-----------------------------------------|------------------------------------------------------------------------------|
| Campo `authorName` en `Video`           | Con su `getAuthorName()` y `setAuthorName(...)`                               |
| `dto/VideoDetailResponse.java`          | Lo que devuelve el detalle: `id`, `title`, `description`, `author`, `tags`, `videoUrl`, `thumbnailUrl`, `duration`. Se crea con su constructor de 8 parámetros, en ese orden. |
| Tests                                   | Dicen exactamente qué tiene que hacer tu código (ver sección 6)               |

---

## 1. Preparar el entorno

```bash
git fetch origin
git checkout feature/PG-22-get-one-video
git pull
```

En Jira, mueve **PG-22** a *En curso*.

> La rama **no compila** hasta que hagas los pasos 1 a 3: los tests usan una clase y un método que todavía no
> existen. Es normal.

Estilo de código: `.claude/skills/protube-coding-style/SKILL.md` (si no está en tu rama, en GitHub en
`chore/PG-13-ai-config`). Sobre todo la sección de `Optional`.

---

## 2. Paso 1 — `VideoNotFoundException`

**Fichero nuevo:** `backend/src/main/java/com/tecnocampus/LS2/protube_back/services/VideoNotFoundException.java`

Una excepción que, cuando sale de un controlador, Spring convierte en una respuesta **404**.

- Hereda de `RuntimeException` (así no obliga a poner `throws` en cada método).
- Lleva la anotación `@ResponseStatus(HttpStatus.NOT_FOUND)`.
- Su constructor recibe el **id** del vídeo (`Long`) y pasa un mensaje claro a `super(...)`, por ejemplo
  `"Video not found: " + id`. Los tests la crean así: `new VideoNotFoundException(99L)`.

**Modelo:** abre `InvalidPaginationException.java` (PG-20). Es lo mismo con otro estado HTTP y otro constructor.

Documentación: [`@ResponseStatus`](https://docs.spring.io/spring-framework/reference/web/webmvc/mvc-controller/ann-exceptionhandler.html)

---

## 3. Paso 2 — `VideoService.getVideoById`

**Fichero existente:** `services/VideoService.java`. Añade un método público nuevo (no toques `getVideos`):

```java
public VideoDetailResponse getVideoById(Long id)
```

**Qué tiene que hacer, en orden:**
1. Pedir el vídeo al repositorio con `findById(id)`. Devuelve un `Optional<Video>`: una "caja" que puede tener un
   vídeo dentro o estar vacía.
2. Si está vacía → lanzar `VideoNotFoundException` con ese id.
3. Si tiene el vídeo → sacarlo y crear un `VideoDetailResponse` con:
   - `videoUrl` = `"/media/"` + nombre del fichero de vídeo (p. ej. `"/media/1.mp4"`)
   - `thumbnailUrl` = `"/media/"` + nombre del fichero de la miniatura
   - `author` = `authorName` del vídeo
   - el resto de campos tal cual (`id`, `title`, `description`, `tags`, `duration`)
4. Devolverlo.

**Cómo usar `Optional`** (forma recomendada en el estilo del proyecto, ejemplo genérico):
```java
Optional<Product> foundProduct = productRepository.findById(productId);
if (foundProduct.isEmpty()) {
    throw new ProductNotFoundException(productId);
}
Product product = foundProduct.get();
```
Documentación: [`Optional`](https://docs.oracle.com/en/java/javase/21/docs/api/java.base/java/util/Optional.html)

---

## 4. Paso 3 — El endpoint en `VideosController`

**Fichero existente:** `controller/VideosController.java`. Añade un método nuevo (no toques el de la lista):

- Responde a `GET /api/videos/{id}` → anotación `@GetMapping("/{id}")`. El `/api/videos` ya lo pone el
  `@RequestMapping` de la clase.
- Recibe el id de la URL con `@PathVariable Long id`.
- Llama a `videoService.getVideoById(id)` y devuelve `ResponseEntity.ok(...)` con el resultado.
- **No hace falta ningún `if` para el 404**: si el servicio lanza `VideoNotFoundException`, Spring responde 404 solo.

**Ejemplo genérico** — [`@PathVariable`](https://docs.spring.io/spring-framework/reference/web/webmvc/mvc-controller/ann-methods/requestmapping.html#mvc-ann-requestmapping-uri-templates)
```java
@GetMapping("/{productId}")
public ResponseEntity<ProductResponse> getProduct(@PathVariable Long productId) {
    return ResponseEntity.ok(productService.getProductById(productId));
}
```

---

## 5. Paso 4 — Guardar el autor al cargar los vídeos

**Fichero existente:** `services/VideoLoaderService.java`, método `createVideo`.

Donde se rellenan los campos del `Video`, añade el autor con el campo `"user"` de la **raíz** del JSON (no de
`meta`). Ya tienes un método auxiliar que lee un texto obligatorio del JSON: úsalo igual que para el título.

> **Ojo con tu base de datos local:** el cargador solo funciona si la tabla está vacía (regla de PG-20). Los vídeos
> que ya cargaste antes no tienen autor. Para recargarlos, borra la base de datos local:
> `docker compose down -v` (borra el volumen de PostgreSQL) y luego `docker compose up -d db`.

---

## 6. Comprobar que todo funciona

**Tests** (igual que la CI):
```bash
cd backend
./mvnw clean verify -Pcoverage
```
Debe terminar en `BUILD SUCCESS` con **24 tests** y 0 fallos.

| Test                                    | Qué comprueba                                        |
|-----------------------------------------|------------------------------------------------------|
| `VideoServiceTest`                      | Devuelve todos los datos del vídeo; lanza la excepción si no existe |
| `VideoLoaderServiceTest`                | Guarda el canal del JSON como autor                  |
| `VideosControllerWebTest`               | La URL real `/api/videos/1` responde 200 con el JSON y `/api/videos/99` responde 404 |

**Probarlo de verdad:**
```bash
docker compose up -d db
cd backend
./mvnw spring-boot:run
```
- http://localhost:8080/api/videos/1 → JSON con `title`, `description`, `author`, `tags`, `videoUrl`, `thumbnailUrl`.
- http://localhost:8080/api/videos/999999 → error 404.
- Abre `http://localhost:8080` + el `videoUrl` → el vídeo se reproduce.

Antes del commit, repasa el checklist del estilo de código.

---

## 7. Commit y Pull Request

Si se ha usado IA para escribir alguna parte, el commit lo indica con `AI-assisted: yes` y `Prompt:` (regla del
README del proyecto).

```bash
git add backend/src/main
git commit
```

Mensaje de ejemplo:
```
feat(videos): get one video by id

- GET /api/videos/{id} returns title, description, author, tags,
  video URL and thumbnail URL.
- VideoNotFoundException answers 404 when the id does not exist.
- The loader saves the channel of the JSON as the author.

Refs: PG-22
```

```bash
git push
```

PR con **base `develop`** (después de los de PG-18 y PG-20):
https://github.com/FerranSalvatella/grup99-LS2-FerranSalvatella/compare/develop...feature/PG-22-get-one-video

En la descripción del PR enlaza `docs/ai-usage/PG-22-human-part.md` (cómo se ha repartido el ticket). Mueve PG-22 a
*En revisión* en Jira.

---

## 8. Preguntas de examen (prepáralas)

1. ¿Cómo llega una petición a `/api/videos/7` hasta `getVideoById(7)`?
2. ¿Cómo se convierte la excepción en un 404? ¿Qué pasaría si fuera una `Exception` normal sin `@ResponseStatus`?
3. ¿Qué es `Optional` y por qué `findById` lo devuelve en lugar de `null`?
4. ¿Por qué el detalle usa `VideoDetailResponse` y la lista `VideoResponse`?
5. ¿Por qué `authorName` es provisional y qué cambiará en PG-25?
````

## Notes

* First used in PG-22 (get one video by id). The generated code was reviewed and checked with
  `./mvnw clean verify -Pcoverage` (24 tests, all green) before committing.
* For a new ticket, replace the guide with the guide of that ticket.
