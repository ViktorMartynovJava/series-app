package com.seriesapp.service;


import org.junit.jupiter.api.Test;
import com.seriesapp.repository.UserRepository;
import com.seriesapp.entity.User;
import com.seriesapp.dto.RegisterDto;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.mockito.Mockito.*;
import static org.assertj.core.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    UserRepository userRepository;

    @Mock
    PasswordEncoder passwordEncoder;

    @InjectMocks
    UserService userService;

    RegisterDto dto = new RegisterDto("ivan", "ivan@mail.ru", "0000", "0000");

    @Test
    void registerSuccess() {
        when(userRepository.existsByUsername("ivan")).thenReturn(false);
        when(userRepository.existsByEmail("ivan@mail.ru")).thenReturn(false);
        when(passwordEncoder.encode("0000")).thenReturn("encoded");
        when(userRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        User result = userService.register(dto);

        assertThat(result.getPassword()).isEqualTo("encoded");
        assertThat(result.getRole()).isEqualTo(User.Role.USER);
        verify(userRepository, times(1)).save(any(User.class));
    }

    @Test
    void registerWhenUserNameAlreadyExists() {
        when(userRepository.existsByUsername("ivan")).thenReturn(true);

        assertThatThrownBy(() -> userService.register(dto))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Имя пользователя уже занято");

        verify(userRepository, never()).save(any());
    }

    @Test
    void registerWhenEmailAlreadyExists() {
        when(userRepository.existsByEmail("ivan@mail.ru")).thenReturn(true);

        assertThatThrownBy(()-> userService.register(dto))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Email уже используется");

        verify(userRepository,never()).save(any());
    }

    @Test
    void registerWhenPasswordNotCorrect() {
        RegisterDto dto = new RegisterDto("ivan", "ivan@mail.ru", "0000", "1111");

        when(userRepository.existsByUsername("ivan")).thenReturn(false);
        when(userRepository.existsByEmail("ivan@mail.ru")).thenReturn(false);

        assertThatThrownBy(()-> userService.register(dto))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Пароли не совпадают");

        verify(userRepository,never()).save(any());
    }

    @Test
    void findByUsername() {
        User user = User.builder()
                .username("ivan")
                .email("ivan@mail.ru")
                .build();

        when(userRepository.findByUsername("ivan")).thenReturn(Optional.of(user));

        User result = userService.findByUsername("ivan");

        assertThat(result.getUsername()).isEqualTo("ivan");
    }

    @Test
    void findByEmptyUsername() {
        when(userRepository.findByUsername("bill")).thenReturn(Optional.empty());

        assertThatThrownBy(()-> userService.findByUsername("bill"))
                .isInstanceOf(RuntimeException.class)
                .hasMessage("Пользователь не найден");

    }

    @Test
    void toggleFavorite() {
    }
}