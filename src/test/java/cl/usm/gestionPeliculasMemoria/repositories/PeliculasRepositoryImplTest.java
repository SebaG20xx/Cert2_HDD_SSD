package cl.usm.gestionPeliculasMemoria.repositories;

import cl.usm.gestionPeliculasMemoria.entities.Pelicula;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Pruebas unitarias de la capa de Persistencia (Repositorio en memoria).
 * No se usan mocks: se prueba directamente la implementacion sobre el ArrayList interno.
 */
class PeliculasRepositoryImplTest {

    private PeliculasRepositoryImpl repository;

    @BeforeEach
    void setUp() {
        // Cada prueba parte con un repositorio limpio y aislado.
        repository = new PeliculasRepositoryImpl();
    }

    private Pelicula nuevaPelicula(String id, String titulo, String director) {
        Pelicula pelicula = new Pelicula();
        pelicula.setId(id);
        pelicula.setTitulo(titulo);
        pelicula.setDirector(director);
        return pelicula;
    }

    @Test
    @DisplayName("insert: guarda la pelicula y la deja disponible en findAll")
    void insert_peliculaValida_seGuardaYRetorna() {
        Pelicula pelicula = nuevaPelicula("1", "Matrix", "Wachowski");

        Pelicula guardada = repository.insert(pelicula);

        assertNotNull(guardada);
        assertSame(pelicula, guardada);
        assertEquals(1, repository.findAll().size());
        assertEquals("Matrix", repository.findAll().get(0).getTitulo());
    }

    @Test
    @DisplayName("insert: lanza IllegalArgumentException cuando el ID es nulo")
    void insert_idNulo_lanzaExcepcion() {
        Pelicula sinId = nuevaPelicula(null, "Sin ID", "Director");

        IllegalArgumentException ex = assertThrows(
                IllegalArgumentException.class,
                () -> repository.insert(sinId));

        assertEquals("El ID de la pelicula no puede ser nulo", ex.getMessage());
        assertTrue(repository.findAll().isEmpty());
    }

    @Test
    @DisplayName("insert: lanza IllegalArgumentException cuando el ID ya existe (case-insensitive)")
    void insert_idDuplicado_lanzaExcepcion() {
        repository.insert(nuevaPelicula("ABC", "Original", "Director"));
        Pelicula duplicada = nuevaPelicula("abc", "Otra", "Otro Director");

        IllegalArgumentException ex = assertThrows(
                IllegalArgumentException.class,
                () -> repository.insert(duplicada));

        assertEquals("La pelicula con ID abc ya existe", ex.getMessage());
        // Solo debe quedar la pelicula original, no la duplicada.
        assertEquals(1, repository.findAll().size());
    }

    @Test
    @DisplayName("findAll: retorna lista vacia cuando no hay peliculas")
    void findAll_sinDatos_retornaListaVacia() {
        List<Pelicula> resultado = repository.findAll();

        assertNotNull(resultado);
        assertTrue(resultado.isEmpty());
    }

    @Test
    @DisplayName("findAll: retorna todas las peliculas insertadas")
    void findAll_conDatos_retornaTodas() {
        repository.insert(nuevaPelicula("1", "Matrix", "Wachowski"));
        repository.insert(nuevaPelicula("2", "Inception", "Nolan"));

        List<Pelicula> resultado = repository.findAll();

        assertEquals(2, resultado.size());
    }

    @Test
    @DisplayName("findAll: retorna una copia defensiva (modificarla no afecta el storage)")
    void findAll_esCopiaDefensiva() {
        repository.insert(nuevaPelicula("1", "Matrix", "Wachowski"));

        List<Pelicula> copia = repository.findAll();
        copia.clear();

        // El storage interno debe permanecer intacto.
        assertEquals(1, repository.findAll().size());
    }

    @Test
    @DisplayName("findById: retorna la pelicula cuando el ID existe")
    void findById_existente_retornaPelicula() {
        repository.insert(nuevaPelicula("1", "Matrix", "Wachowski"));

        Pelicula encontrada = repository.findById("1");

        assertNotNull(encontrada);
        assertEquals("Matrix", encontrada.getTitulo());
    }

    @Test
    @DisplayName("findById: la busqueda por ID es case-insensitive")
    void findById_caseInsensitive_retornaPelicula() {
        repository.insert(nuevaPelicula("ABC", "Matrix", "Wachowski"));

        Pelicula encontrada = repository.findById("abc");

        assertNotNull(encontrada);
        assertEquals("ABC", encontrada.getId());
    }

    @Test
    @DisplayName("findById: retorna null cuando el ID no existe")
    void findById_inexistente_retornaNull() {
        repository.insert(nuevaPelicula("1", "Matrix", "Wachowski"));

        Pelicula encontrada = repository.findById("999");

        assertNull(encontrada);
    }

    @Test
    @DisplayName("findById: retorna null cuando el ID es nulo")
    void findById_idNulo_retornaNull() {
        Pelicula encontrada = repository.findById(null);

        assertNull(encontrada);
    }
}
