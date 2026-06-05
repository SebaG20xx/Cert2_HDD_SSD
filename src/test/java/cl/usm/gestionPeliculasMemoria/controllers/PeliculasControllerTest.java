package cl.usm.gestionPeliculasMemoria.controllers;

import cl.usm.gestionPeliculasMemoria.entities.Comentario;
import cl.usm.gestionPeliculasMemoria.entities.Pelicula;
import cl.usm.gestionPeliculasMemoria.services.PeliculasService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PeliculasControllerTest {

    @Mock
    private PeliculasService peliculasService;

    @InjectMocks
    private PeliculasController peliculasController;

    private Pelicula pelicula;

    @BeforeEach
    void setUp() {
        pelicula = new Pelicula();
        pelicula.setId("1");
        pelicula.setTitulo("Matrix");
        pelicula.setDirector("Wachowski");
    }

    @Test
    @DisplayName("GET /peliculas sin query: retorna 200 con todas las peliculas")
    void getAll_sinQuery_retornaOk() {
        List<Pelicula> peliculas = Arrays.asList(pelicula);
        when(peliculasService.getAll()).thenReturn(peliculas);

        ResponseEntity<List<Pelicula>> response = peliculasController.getAll(null);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(peliculas, response.getBody());
        verify(peliculasService).getAll();
    }

    @Test
    @DisplayName("GET /peliculas con query: retorna 200 con el resultado del filtro")
    void getAll_conQuery_retornaFiltradas() {
        List<Pelicula> filtradas = Arrays.asList(pelicula);
        when(peliculasService.filter("matrix")).thenReturn(filtradas);

        ResponseEntity<List<Pelicula>> response = peliculasController.getAll("matrix");

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(filtradas, response.getBody());
        verify(peliculasService).filter("matrix");
        verify(peliculasService, never()).getAll();
    }

    @Test
    @DisplayName("GET /peliculas: retorna 500 cuando el servicio lanza una excepcion")
    void getAll_servicioFalla_retorna500() {
        when(peliculasService.getAll()).thenThrow(new RuntimeException("error interno"));

        ResponseEntity<List<Pelicula>> response = peliculasController.getAll(null);

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
    }

    @Test
    @DisplayName("POST /peliculas: retorna 200 cuando se crea la pelicula")
    void createPelicula_exito_retornaOk() {
        when(peliculasService.createPelicula(pelicula)).thenReturn(pelicula);

        ResponseEntity<?> response = peliculasController.createPelicula(pelicula);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(pelicula, response.getBody());
        verify(peliculasService).createPelicula(pelicula);
    }

    @Test
    @DisplayName("POST /peliculas: retorna 500 cuando el servicio devuelve null")
    void createPelicula_servicioDevuelveNull_retorna500() {
        when(peliculasService.createPelicula(pelicula)).thenReturn(null);

        ResponseEntity<?> response = peliculasController.createPelicula(pelicula);

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
    }

    @Test
    @DisplayName("GET /peliculas/{id}: retorna 200 cuando la pelicula existe")
    void findById_existente_retornaOk() {
        when(peliculasService.findById("1")).thenReturn(pelicula);

        ResponseEntity<Pelicula> response = peliculasController.findById("1");

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(pelicula, response.getBody());
    }

    @Test
    @DisplayName("GET /peliculas/{id}: retorna 404 cuando la pelicula no existe")
    void findById_inexistente_retorna404() {
        when(peliculasService.findById("999")).thenReturn(null);

        ResponseEntity<Pelicula> response = peliculasController.findById("999");

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }

    @Test
    @DisplayName("GET /peliculas/{id}: retorna 500 cuando el servicio lanza una excepcion")
    void findById_servicioFalla_retorna500() {
        when(peliculasService.findById(anyString())).thenThrow(new RuntimeException("error interno"));

        ResponseEntity<Pelicula> response = peliculasController.findById("1");

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
    }

    @Test
    @DisplayName("GET /peliculas/{id}/comentarios: retorna 200 con los comentarios de la pelicula")
    void getComentarios_existente_retornaOk() {
        Comentario comentario = new Comentario("usuario1", "Excelente pelicula");
        pelicula.setComentarios(new Comentario[]{comentario});
        when(peliculasService.findById("1")).thenReturn(pelicula);

        ResponseEntity<?> response = peliculasController.getComentarios("1");

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertArrayEquals(pelicula.getComentarios(), (Comentario[]) response.getBody());
    }

    @Test
    @DisplayName("GET /peliculas/{id}/comentarios: retorna 404 cuando la pelicula no existe")
    void getComentarios_inexistente_retorna404() {
        when(peliculasService.findById("999")).thenReturn(null);

        ResponseEntity<?> response = peliculasController.getComentarios("999");

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }

    @Test
    @DisplayName("GET /peliculas/{id}/comentarios: retorna 500 cuando el servicio lanza una excepcion")
    void getComentarios_servicioFalla_retorna500() {
        when(peliculasService.findById(anyString())).thenThrow(new RuntimeException("error interno"));

        ResponseEntity<?> response = peliculasController.getComentarios("1");

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
    }
}
