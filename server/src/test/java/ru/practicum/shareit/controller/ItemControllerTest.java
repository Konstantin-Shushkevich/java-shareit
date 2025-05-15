package ru.practicum.shareit.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.item.ItemController;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemDtoSpecified;
import ru.practicum.shareit.item.service.ItemService;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Collections;
import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = ItemController.class)
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class ItemControllerTest {
    @MockBean
    private ItemService itemService;
    private final ObjectMapper mapper;
    private final MockMvc mvc;

    private ItemDtoSpecified itemDtoSpecified;
    private ItemDto itemDto;

    @BeforeEach
    public void beforeEach() {
        this.itemDtoSpecified = ItemDtoSpecified.builder()
                .id(1L)
                .name("item")
                .description("description")
                .available(true)
                .comments(Collections.emptyList())
                .build();

        this.itemDto = ItemDto.builder()
                .id(1L)
                .name("item")
                .description("description")
                .available(true)
                .build();
    }

    @Test
    public void shouldGetItemById() throws Exception {
        Mockito
                .when(itemService.findById(anyLong(), anyLong()))
                .thenReturn(itemDtoSpecified);

        mvc.perform(get("/items/1")
                        .header("X-Sharer-User-Id", 1)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(itemDtoSpecified.getId()))
                .andExpect(jsonPath("$.name").value(itemDtoSpecified.getName()))
                .andExpect(jsonPath("$.description").value(itemDtoSpecified.getDescription()));
    }

    @Test
    public void shouldGetItems() throws Exception {
        Mockito
                .when(itemService.findForTheUser(anyLong()))
                .thenReturn(List.of(itemDtoSpecified));

        mvc.perform(get("/items")
                        .header("X-Sharer-User-Id", 1)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.size()").value(1))
                .andExpect(jsonPath("$.[0].id").value(itemDtoSpecified.getId()))
                .andExpect(jsonPath("$.[0].name").value(itemDtoSpecified.getName()))
                .andExpect(jsonPath("$.[0].description").value(itemDtoSpecified.getDescription()));
    }

    @Test
    public void shouldAddItem() throws Exception {
        Mockito
                .when(itemService.create(anyLong(), any(ItemDto.class)))
                .thenReturn(itemDto);

        mvc.perform(post("/items")
                        .content(mapper.writeValueAsString(itemDtoSpecified))
                        .header("X-Sharer-User-Id", 1)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(itemDtoSpecified.getId()))
                .andExpect(jsonPath("$.name").value(itemDtoSpecified.getName()))
                .andExpect(jsonPath("$.description").value(itemDtoSpecified.getDescription()));
    }

    @Test
    public void shouldUpdateItem() throws Exception {
        Mockito
                .when(itemService.update(anyLong(), anyLong(), ArgumentMatchers.any(ItemDto.class)))
                .thenReturn(itemDto);

        mvc.perform(patch("/items/1")
                        .content(mapper.writeValueAsString(itemDtoSpecified))
                        .header("X-Sharer-User-Id", 1)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(itemDtoSpecified.getId()))
                .andExpect(jsonPath("$.name").value(itemDtoSpecified.getName()))
                .andExpect(jsonPath("$.description").value(itemDtoSpecified.getDescription()));
    }

    @Test
    public void shouldSearchItems() throws Exception {
        Mockito
                .when(itemService.search(anyString()))
                .thenReturn(List.of(itemDto));

        mvc.perform(get("/items/search")
                        .header("X-Sharer-User-Id", 1)
                        .param("text", "item")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.size()").value(1))
                .andExpect(jsonPath("$.[0].id").value(itemDtoSpecified.getId()))
                .andExpect(jsonPath("$.[0].name").value(itemDtoSpecified.getName()))
                .andExpect(jsonPath("$.[0].description").value(itemDtoSpecified.getDescription()));
    }

    @Test
    public void shouldAddComment() throws Exception {
        CommentDto commentDto = CommentDto.builder()
                .id(1L)
                .text("text")
                .authorName("userName")
                .created(LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS))
                .build();
        Mockito
                .when(itemService.createComment(anyLong(), anyLong(), ArgumentMatchers.any(CommentDto.class)))
                .thenReturn(commentDto);

        mvc.perform(post("/items/1/comment")
                        .content(mapper.writeValueAsString(commentDto))
                        .header("X-Sharer-User-Id", 1)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(commentDto.getId()))
                .andExpect(jsonPath("$.text").value(commentDto.getText()))
                .andExpect(jsonPath("$.authorName").value(commentDto.getAuthorName()))
                .andExpect(jsonPath("$.created").value(commentDto.getCreated().toString()));
    }
}
