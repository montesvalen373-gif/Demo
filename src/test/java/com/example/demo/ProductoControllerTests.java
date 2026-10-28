package com.example.demo;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@AutoConfigureMockMvc
@SpringBootTest
class ProductoControllerTests {

	@Autowired
	private MockMvc mockMvc;

	@Autowired
	private ObjectMapper objectMapper;

	@Test
	void createRejectsInvalidProduct() throws Exception {
		mockMvc.perform(post("/api/productos")
						.contentType(MediaType.APPLICATION_JSON)
						.content("""
								{"nombre":"Producto inválido","precio":-1,"costo":100}
								"""))
				.andExpect(status().isBadRequest());
	}

	@Test
	void replaceRejectsProductOutsideCostRange() throws Exception {
		String id = firstProductId();

		mockMvc.perform(put("/api/productos/{id}", id)
						.contentType(MediaType.APPLICATION_JSON)
						.content("""
								{"nombre":"Producto inválido","precio":100,"costo":1000001}
								"""))
				.andExpect(status().isBadRequest());
	}

	@Test
	void patchRejectsInvalidProductWithoutChangingIt() throws Exception {
		String id = firstProductId();
		String originalResponse = mockMvc.perform(get("/api/productos/{id}", id))
				.andExpect(status().isOk())
				.andReturn()
				.getResponse()
				.getContentAsString();
		double precioOriginal = objectMapper.readTree(originalResponse).get("precio").asDouble();

		mockMvc.perform(patch("/api/productos/{id}", id)
						.contentType(MediaType.APPLICATION_JSON)
						.content("""
								{"precio":-1}
								"""))
				.andExpect(status().isBadRequest());

		mockMvc.perform(get("/api/productos/{id}", id))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.precio").value(precioOriginal));
	}

	private String firstProductId() throws Exception {
		String response = mockMvc.perform(get("/api/productos"))
				.andExpect(status().isOk())
				.andReturn()
				.getResponse()
				.getContentAsString();
		return objectMapper.readTree(response).get(0).get("id").asText();
	}
}
