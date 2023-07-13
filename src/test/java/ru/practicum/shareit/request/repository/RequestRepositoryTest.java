package ru.practicum.shareit.request.repository;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import ru.practicum.shareit.request.model.Request;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Random;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasSize;

@DataJpaTest
class RequestRepositoryTest {

    @Autowired
    RequestRepository requestRepository;
    @Autowired
    UserRepository userRepository;

    @Test
    void testFindAllByOwnerId() {
        User owner = saveRandomUser();
        Sort sort = Sort.by(Sort.Direction.DESC, "created");
        Request request_1 = requestRepository.save(Request.builder()
                .created(LocalDateTime.now().minusHours(2))
                .description("request_1 desc")
                .owner(owner)
                .build());
        Request request_2 = requestRepository.save(Request.builder()
                .created(LocalDateTime.now().minusHours(1))
                .description("request_2 desc")
                .owner(owner)
                .build());

        List<Request> requests = requestRepository.findAllByOwnerId(owner.getId(), sort);

        assertThat(requests, hasSize(2));
        assertThat(requests.get(0), equalTo(request_2));
        assertThat(requests.get(1), equalTo(request_1));
    }

    @Test
    void testFindAll() {
        requestRepository.deleteAll();
        User owner = saveRandomUser();
        int page = 2;
        int size = 1;
        Sort sort = Sort.by(Sort.Direction.DESC, "created");
        PageRequest pageRequest = PageRequest.of(page, size, sort);
        Request request_1 = requestRepository.save(Request.builder()
                .created(LocalDateTime.now().minusHours(3))
                .description("request_1 desc")
                .owner(owner)
                .build());
        Request request_2 = requestRepository.save(Request.builder()
                .created(LocalDateTime.now().minusHours(2))
                .description("request_2 desc")
                .owner(owner)
                .build());
        Request request_3 = requestRepository.save(Request.builder()
                .created(LocalDateTime.now().minusHours(1))
                .description("request_1 desc")
                .owner(owner)
                .build());

        Page<Request> result = requestRepository.findAll(pageRequest);

        assertThat(result.getTotalElements(), equalTo(3L));
        assertThat(result.getTotalPages(), equalTo(3));
        assertThat(result.getContent().get(0), equalTo(request_1));
    }

    private User saveRandomUser() {
        return userRepository.save(User.builder()
                .name("name")
                .email(String.format("%s%s@email.ru", "email", new Random(9999L)))
                .build());
    }
}