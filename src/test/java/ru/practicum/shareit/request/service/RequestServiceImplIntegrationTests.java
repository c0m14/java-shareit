package ru.practicum.shareit.request.service;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.request.dto.CreationRequestDto;
import ru.practicum.shareit.request.dto.RequestDto;
import ru.practicum.shareit.request.dto.RequestNoItemsDto;
import ru.practicum.shareit.request.model.Request;
import ru.practicum.shareit.request.repository.RequestRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Random;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasSize;

@SpringBootTest
class RequestServiceImplIntegrationTests {
    @Autowired
    private RequestService requestService;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private RequestRepository requestRepository;
    @Autowired
    private ItemRepository itemRepository;

    @Test
    void add_whenInvoked_thenRequestSavedInDB() {
        User user = saveRandomUser();
        CreationRequestDto creationRequestDto = new CreationRequestDto();
        creationRequestDto.setDescription("desc");

        Long requestId = requestService.add(user.getId(), creationRequestDto).getId();

        Request savedRequest = requestRepository.findById(requestId).get();
        assertThat(savedRequest.getDescription(), equalTo(creationRequestDto.getDescription()));
        assertThat(savedRequest.getOwner().getId(), equalTo(user.getId()));
    }

    @Test
    void add_whenInvoked_thenRequestNoItemsDtoReturned() {
        User user = saveRandomUser();
        CreationRequestDto creationRequestDto = new CreationRequestDto();
        creationRequestDto.setDescription("desc");

        RequestNoItemsDto requestNoItemsDto = requestService.add(user.getId(), creationRequestDto);

        assertThat(requestNoItemsDto.getDescription(), equalTo(creationRequestDto.getDescription()));
    }

    @Test
    void getAllUserItemRequests_whenInvoked_thenUserRequestsDtoReturnedWithItemsSortedByCreationDesc() {
        User user = saveRandomUser();
        Request request_1 = requestRepository.save(Request.builder()
                .description("request_1")
                .owner(user)
                .created(LocalDateTime.now().minusHours(2))
                .build());
        Request request_2 = requestRepository.save(Request.builder()
                .description("request_2")
                .owner(user)
                .created(LocalDateTime.now().minusHours(1))
                .build());
        saveRandomItemWithRequest(request_1);
        saveRandomItemWithRequest(request_1);

        List<RequestDto> foundRequests = requestService.getAllUserItemRequests(user.getId());

        assertThat(foundRequests.get(0).getId(), equalTo(request_2.getRequestId()));
        assertThat(foundRequests.get(1).getId(), equalTo(request_1.getRequestId()));
        assertThat(foundRequests.get(1).getItems(), hasSize(2));
    }

    @Test
    @Transactional
    void getAllOtherUsersRequests_whenInvoked_thenOtherUsersRequestsWithItemsReturned() {
        //clear context
        requestRepository.deleteAll();
        User requestedUser = saveRandomUser();
        User otherUser = saveRandomUser();
        requestRepository.save(Request.builder()
                .description("request_1")
                .owner(requestedUser)
                .created(LocalDateTime.now().minusHours(2))
                .build());
        Request request_2 = requestRepository.save(Request.builder()
                .description("request_2")
                .owner(otherUser)
                .created(LocalDateTime.now().minusHours(1))
                .build());
        saveRandomItemWithRequest(request_2);
        saveRandomItemWithRequest(request_2);

        List<RequestDto> foundRequests =
                requestService.getAllOtherUsersRequests(requestedUser.getId(), 0, 5);

        assertThat(foundRequests, hasSize(1));
        assertThat(foundRequests.get(0).getId(), equalTo(request_2.getRequestId()));
        assertThat(foundRequests.get(0).getItems(), hasSize(2));
    }

    @Test
    void getById_whenInvoked_thenRequestDtoWithItemsReturned() {
        User requestedUser = saveRandomUser();
        Request request = requestRepository.save(Request.builder()
                .description("request_1")
                .owner(requestedUser)
                .created(LocalDateTime.now().minusHours(2))
                .build());
        saveRandomItemWithRequest(request);
        saveRandomItemWithRequest(request);

        RequestDto returnedRequest = requestService.getById(requestedUser.getId(), request.getRequestId());

        assertThat(returnedRequest.getId(), equalTo(request.getRequestId()));
        assertThat(returnedRequest.getDescription(), equalTo(request.getDescription()));
        assertThat(returnedRequest.getCreated(), equalTo(request.getCreated()));
        assertThat(returnedRequest.getItems(), hasSize(2));
    }

    private User saveRandomUser() {
        return userRepository.save(User.builder()
                .name("name")
                .email(String.format("%s%s@email.ru", "email", new Random(9999L)))
                .build());
    }

    private Item saveRandomItemWithRequest(Request request) {
        return itemRepository.save(Item.builder()
                .name("name")
                .description("desc")
                .available(true)
                .owner(saveRandomUser())
                .request(request)
                .build());
    }
}