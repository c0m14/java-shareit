package ru.practicum.shareit.item.repository;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.model.Request;
import ru.practicum.shareit.request.repository.RequestRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Random;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

@DataJpaTest
class ItemRepositoryTest {

    @Autowired
    private ItemRepository itemRepository;
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RequestRepository requestRepository;


    @Test
    void pageableFindByOwnerId_whenInvoked_thenItemsForRequestedOwnerAndPageReturned() {
        User owner = saveRandomUser();
        PageRequest pageRequest = PageRequest.of(1, 1);
        itemRepository.save(Item.builder()
                .name("item_1")
                .description("desc")
                .available(true)
                .owner(owner)
                .build());
        Item item2 = itemRepository.save(Item.builder()
                .name("item2")
                .description("desc")
                .available(true)
                .owner(owner)
                .build());

        Page<Item> items = itemRepository.findByOwnerId(pageRequest, owner.getId());

        assertThat(items.getTotalPages(), equalTo(2));
        assertThat(items.getTotalElements(), equalTo(2L));
        assertEquals(item2, items.getContent().get(0));
    }

    @Test
    void findByOwnerId_whenInvokedWithoutPaging_thenItemsListReturned() {
        User owner = saveRandomUser();
        Item item1 = itemRepository.save(Item.builder()
                .name("item1")
                .description("desc")
                .available(true)
                .owner(owner)
                .build());
        Item item2 = itemRepository.save(Item.builder()
                .name("item2")
                .description("desc")
                .available(true)
                .owner(owner)
                .build());

        List<Item> items = itemRepository.findByOwnerId(owner.getId());

        assertThat(items, hasSize(2));
        assertThat(items.get(0), equalTo(item1));
        assertThat(items.get(1), equalTo(item2));
    }

    @Test
    void searchByText_whenInvoked_thenItemsFoundByTextInNameOrDescriptionCaseInsensitive() {
        User owner = saveRandomUser();
        PageRequest pageRequest = PageRequest.of(0, 3);
        String text = "ITEM_1";
        Item item1 = itemRepository.save(Item.builder()
                .name("item1")
                .description("desc")
                .available(true)
                .owner(owner)
                .build());
        Item item2 = itemRepository.save(Item.builder()
                .name("item2")
                .description("addition to Item_1")
                .available(true)
                .owner(owner)
                .build());
        Item item3 = itemRepository.save(Item.builder()
                .name("item3")
                .description("desc")
                .available(true)
                .owner(owner)
                .build());

        Page<Item> items = itemRepository.searchByText(pageRequest, text);

        List<Item> itemsList = items.getContent();
        assertThat(items.getTotalPages(), equalTo(1));
        assertThat(items.getTotalElements(), equalTo(2L));
        assertEquals(item1, itemsList.get(0));
        assertEquals(item2, itemsList.get(1));
        assertFalse(itemsList.contains(item3));
    }

    @Test
    void findAllByRequest_RequestId_whenInvoked_thenItemsWithRequestFound() {
        User owner = saveRandomUser();
        Request request = saveRandomRequest();
        Item item1 = itemRepository.save(Item.builder()
                .name("item1")
                .description("desc")
                .available(true)
                .owner(owner)
                .request(request)
                .build());
        Item item2 = itemRepository.save(Item.builder()
                .name("item2")
                .description("addition to Item_1")
                .available(true)
                .owner(owner)
                .request(request)
                .build());
        Item item3 = itemRepository.save(Item.builder()
                .name("item3")
                .description("desc")
                .available(true)
                .owner(owner)
                .build());

        List<Item> foundItems = itemRepository.findAllByRequest_RequestId(request.getRequestId());

        assertThat(foundItems, hasSize(2));
        assertThat(foundItems.get(0), equalTo(item1));
        assertThat(foundItems.get(1), equalTo(item2));
        assertThat(foundItems, not(containsInAnyOrder(item3)));
    }

    private User saveRandomUser() {
        return userRepository.save(User.builder()
                .name("name")
                .email(String.format("%s%s@email.ru", "email", new Random(9999L)))
                .build());
    }

    private Request saveRandomRequest() {
        return requestRepository.save(Request.builder()
                .owner(saveRandomUser())
                .description("desc")
                .created(LocalDateTime.now())
                .build());
    }


}