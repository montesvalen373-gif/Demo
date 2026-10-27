package com.example.demo;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.hasSize;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@AutoConfigureMockMvc
@SpringBootTest
class DemoApplicationTests {

	@Autowired
	private MockMvc mockMvc;

	@Autowired
	private ObjectMapper objectMapper;

	@Test
	void usuariosApiSupportsCrudOperations() throws Exception {
		String usuarioJson = """
				{"nombre":"Ana Torres","email":"ana@example.com","edad":28}
				""";
		JsonNode usuariosIniciales = objectMapper.readTree(mockMvc.perform(get("/api/usuarios"))
				.andExpect(status().isOk())
				.andReturn()
				.getResponse()
				.getContentAsString());
		int cantidadInicial = usuariosIniciales.size();

		MvcResult createResult = mockMvc.perform(post("/api/usuarios")
						.contentType(MediaType.APPLICATION_JSON)
						.content(usuarioJson))
				.andExpect(status().isCreated())
				.andExpect(jsonPath("$.nombre").value("Ana Torres"))
				.andReturn();
		String response = createResult.getResponse().getContentAsString();
		String id = objectMapper.readTree(response)
				.get("id")
				.asText();

		mockMvc.perform(get("/api/usuarios"))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$", hasSize(cantidadInicial + 1)));
		mockMvc.perform(get("/api/usuarios/buscar").param("nombre", "ana"))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$[0].id").value(id));
		mockMvc.perform(get("/api/usuarios/cabecera").header("User-Agent", "api-test"))
				.andExpect(status().isOk())
				.andExpect(header().string("Content-Type", containsString("text/plain")));
		mockMvc.perform(get("/api/usuarios/buscar").param("nombre", " "))
				.andExpect(status().isBadRequest());
		mockMvc.perform(get("/api/usuarios/{id}", id))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.email").value("ana@example.com"));

		mockMvc.perform(put("/api/usuarios/{id}", id)
						.contentType(MediaType.APPLICATION_JSON)
						.content("""
								{"nombre":"Ana T.","email":"ana.t@example.com","edad":29}
								"""))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.id").value(id))
				.andExpect(jsonPath("$.nombre").value("Ana T."));
		mockMvc.perform(patch("/api/usuarios/{id}", id)
						.contentType(MediaType.APPLICATION_JSON)
						.content("""
								{"edad":30}
								"""))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.edad").value(30));

		mockMvc.perform(delete("/api/usuarios/{id}", id))
				.andExpect(status().isNoContent());
		mockMvc.perform(get("/api/usuarios/{id}", id))
				.andExpect(status().isNotFound());
	}

	@Test
	void createIgnoresClientSuppliedId() throws Exception {
		String usuarioOriginal = mockMvc.perform(get("/api/usuarios"))
				.andExpect(status().isOk())
				.andReturn()
				.getResponse()
				.getContentAsString();
		JsonNode usuarioExistente = objectMapper.readTree(usuarioOriginal).get(0);
		String idExistente = usuarioExistente
				.get("id")
				.asText();
		String nombreExistente = usuarioExistente
				.get("nombre")
				.asText();

		String response = mockMvc.perform(post("/api/usuarios")
						.contentType(MediaType.APPLICATION_JSON)
						.content("""
								{"id":"%s","nombre":"Nuevo Usuario","email":"nuevo@example.com","edad":20}
								""".formatted(idExistente)))
				.andExpect(status().isCreated())
				.andReturn()
				.getResponse()
				.getContentAsString();
		String idCreado = objectMapper.readTree(response)
				.get("id")
				.asText();

		assertNotEquals(idExistente, idCreado);
		mockMvc.perform(get("/api/usuarios/{id}", idExistente))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.nombre").value(nombreExistente));
	}

	@Test
	void invalidPatchReturnsBadRequestWithoutChangingUser() throws Exception {
		String response = mockMvc.perform(get("/api/usuarios"))
				.andExpect(status().isOk())
				.andReturn()
				.getResponse()
				.getContentAsString();
		JsonNode primerUsuario = objectMapper.readTree(response).get(0);
		String id = primerUsuario
				.get("id")
				.asText();
		String nombreOriginal = primerUsuario
				.get("nombre")
				.asText();

		mockMvc.perform(patch("/api/usuarios/{id}", id)
						.contentType(MediaType.APPLICATION_JSON)
						.content("""
								{"nombre":"Nombre temporal","campoDesconocido":true}
								"""))
				.andExpect(status().isBadRequest());
		mockMvc.perform(get("/api/usuarios/{id}", id))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.nombre").value(nombreOriginal));
		mockMvc.perform(patch("/api/usuarios/{id}", id)
						.contentType(MediaType.APPLICATION_JSON)
						.content("""
								{"edad":"no es un numero"}
								"""))
				.andExpect(status().isBadRequest());
		mockMvc.perform(patch("/api/usuarios/{id}", id)
						.contentType(MediaType.APPLICATION_JSON)
						.content("""
								{"nombre":"Nombre temporal","edad":-1}
								"""))
				.andExpect(status().isBadRequest());
		mockMvc.perform(patch("/api/usuarios/{id}", id)
						.contentType(MediaType.APPLICATION_JSON)
						.content("""
								{"edad":1.5}
								"""))
				.andExpect(status().isBadRequest());
		mockMvc.perform(patch("/api/usuarios/{id}", id)
						.contentType(MediaType.APPLICATION_JSON)
						.content("{}"))
				.andExpect(status().isBadRequest());
		mockMvc.perform(get("/api/usuarios/{id}", id))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.nombre").value(nombreOriginal));
	}

	@Test
	void createAndReplaceRejectInvalidUserData() throws Exception {
		mockMvc.perform(post("/api/usuarios")
						.contentType(MediaType.APPLICATION_JSON)
						.content("""
								{"nombre":" ","email":"correo-invalido","edad":-1}
								"""))
				.andExpect(status().isBadRequest());

		String response = mockMvc.perform(get("/api/usuarios"))
				.andExpect(status().isOk())
				.andReturn()
				.getResponse()
				.getContentAsString();
		String id = objectMapper.readTree(response).get(0).get("id").asText();

		mockMvc.perform(put("/api/usuarios/{id}", id)
						.contentType(MediaType.APPLICATION_JSON)
						.content("""
								{"nombre":"Nombre inválido","email":"no-es-email","edad":151}
								"""))
				.andExpect(status().isBadRequest());
		mockMvc.perform(get("/api/usuarios/{id}", id))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.email").value(objectMapper.readTree(response).get(0).get("email").asText()));
	}

	@Test
	void apiPreservesSpanishCharacters() throws Exception {
		mockMvc.perform(get("/api/usuarios/buscar").param("nombre", "Juan"))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$[0].nombre").value("Juan Pérez"));
	}
}
