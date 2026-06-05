package cl.usm.gestionPeliculasMemoria.services;

import cl.usm.gestionPeliculasMemoria.entities.Pelicula;
import cl.usm.gestionPeliculasMemoria.repositories.PeliculasRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PeliculasServiceImplTest {

    @Mock
    private PeliculasRepository peliculasRepository;

    @InjectMocks
    private PeliculasServiceImpl peliculasService;

    private Pelicula pelicula;

    @BeforeEach
    void setUp() {
        pelicula = new Pelicula();
        pelicula.setId("1");
        pelicula.setTitulo("Matrix");
        pelicula.setDirector("Wachowski");
    }

    @Test
    @DisplayName("createPelicula: asigna tokenDescarga, delega en el repositorio y retorna la pelicula")
    void createPelicula_exito_asignaTokenYDelega() {
        when(peliculasRepository.insert(any(Pelicula.class))).thenReturn(pelicula);

        Pelicula resultado = peliculasService.createPelicula(pelicula);

        assertNotNull(resultado);
        assertNotNull(resultado.getTokenDescarga());
        assertEquals(10, resultado.getTokenDescarga().length());
        verify(peliculasRepository, times(1)).insert(pelicula);
    }

    @Test
    @DisplayName("createPelicula: retorna null cuando el repositorio lanza una excepcion")
    void createPelicula_repositorioFalla_retornaNull() {
        when(peliculasRepository.insert(any(Pelicula.class)))
                .thenThrow(new IllegalArgumentException("ID duplicado"));

        Pelicula resultado = peliculasService.createPelicula(pelicula);

        assertNull(resultado);
        verify(peliculasRepository).insert(pelicula);
    }

    @Test
    @DisplayName("getAll: delega en el repositorio y retorna la lista completa")
    void getAll_retornaTodasLasPeliculas() {
        List<Pelicula> peliculas = Arrays.asList(pelicula, new Pelicula());
        when(peliculasRepository.findAll()).thenReturn(peliculas);

        List<Pelicula> resultado = peliculasService.getAll();

        assertEquals(2, resultado.size());
        assertEquals(peliculas, resultado);
        verify(peliculasRepository).findAll();
    }

    @Test
    @DisplayName("findById: delega en el repositorio y retorna la pelicula encontrada")
    void findById_existente_retornaPelicula() {
        when(peliculasRepository.findById("1")).thenReturn(pelicula);

        Pelicula resultado = peliculasService.findById("1");

        assertNotNull(resultado);
        assertEquals("Matrix", resultado.getTitulo());
        verify(peliculasRepository).findById("1");
    }

    @Test
    @DisplayName("findById: retorna null cuando el repositorio no encuentra la pelicula")
    void findById_inexistente_retornaNull() {
        when(peliculasRepository.findById(anyString())).thenReturn(null);

        Pelicula resultado = peliculasService.findById("999");

        assertNull(resultado);
    }

    @Test
    @DisplayName("filter: encuentra coincidencias por titulo (case-insensitive)")
    void filter_coincidePorTitulo_retornaCoincidencias() {
        Pelicula inception = new Pelicula();
        inception.setId("2");
        inception.setTitulo("Inception");
        inception.setDirector("Nolan");
        when(peliculasRepository.findAll()).thenReturn(Arrays.asList(pelicula, inception));

        List<Pelicula> resultado = peliculasService.filter("matrix");

        assertEquals(1, resultado.size());
        assertEquals("Matrix", resultado.get(0).getTitulo());
    }

    @Test
    @DisplayName("filter: encuentra coincidencias por ID (case-insensitive)")
    void filter_coincidePorId_retornaCoincidencias() {
        Pelicula otra = new Pelicula();
        otra.setId("ABC");
        otra.setTitulo("Otra");
        otra.setDirector("Director");
        when(peliculasRepository.findAll()).thenReturn(Arrays.asList(pelicula, otra));

        List<Pelicula> resultado = peliculasService.filter("abc");

        assertEquals(1, resultado.size());
        assertEquals("ABC", resultado.get(0).getId());
    }

    @Test
    @DisplayName("filter: retorna lista vacia cuando no hay coincidencias")
    void filter_sinCoincidencias_retornaListaVacia() {
        when(peliculasRepository.findAll()).thenReturn(Arrays.asList(pelicula));

        List<Pelicula> resultado = peliculasService.filter("zzz");

        assertTrue(resultado.isEmpty());
    }
}
