package com.capstone.store;
import com.capstone.store.Repository.UserRepository;
import com.capstone.store.constants.Constants;
import com.capstone.store.dto.UserRequest;
import com.capstone.store.model.User;
import com.capstone.store.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.bcrypt.BCrypt;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import java.util.Optional;
import static org.mockito.Mockito.when;




@ExtendWith(MockitoExtension.class)
public class UserServiceTest {
    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserService userService;

    private User user;
    private UserRequest userRequest;

    @BeforeEach
    void setUp(){
        userRequest = new UserRequest();
        userRequest.setEmail("Ksah@gmail.com");
        userRequest.setPassword("Ksah@123");

        String hashedPassword = BCrypt.hashpw("Ksah@123",BCrypt.gensalt());
        user=new User("Ksah@gmail.com",hashedPassword);
        user.setId(1L);
    }


    @Test
    void registerUser_Success(){
        when(userRepository.findByEmail(userRequest.getEmail())).thenReturn(Optional.empty());
        assertDoesNotThrow(() -> userService.registerUser(userRequest));
        verify(userRepository, times(1)).save(any(User.class));
    }

    @Test
    void loginUser_Success(){
        when(userRepository.findByEmail(userRequest.getEmail())).thenReturn(Optional.of(user));
        User loggedInUser = userService.loginUser(userRequest);

        assertNotNull(loggedInUser);
        assertEquals(user.getEmail(), loggedInUser.getEmail());
    }

    @Test
    void getUserIdByEmail_Success(){
        when(userRepository.findByEmail(userRequest.getEmail())).thenReturn(Optional.of(user));
        Long userId = userService.getUserIdByEmail(userRequest.getEmail());
        assertEquals(1L,userId);
    }


    @Test
    void testExceptions() {
        assertException(Constants.USER_ALREADY_EXISTS, () -> {
            when(userRepository.findByEmail(userRequest.getEmail())).thenReturn(Optional.of(user));
            userService.registerUser(userRequest);
        });

        assertException(Constants.INVALID_REGISTRATION_REQUEST, () -> {
            UserRequest invalidRequest = new UserRequest();
            invalidRequest.setEmail("");
            invalidRequest.setPassword("");
            userService.registerUser(invalidRequest);
        });

        assertException(Constants.INVALID_USER_CREDENTIALS, () -> {
            when(userRepository.findByEmail(userRequest.getEmail())).thenReturn(Optional.empty());
            userService.loginUser(userRequest);
        });

        assertException(Constants.INVALID_USER_CREDENTIALS, () -> {
            userRequest.setPassword("wrongpassword");
            when(userRepository.findByEmail(userRequest.getEmail())).thenReturn(Optional.of(user));
            userService.loginUser(userRequest);
        });

        assertException(Constants.INVALID_EMAIL, () -> {
            when(userRepository.findByEmail(userRequest.getEmail())).thenReturn(Optional.empty());
            userService.getUserIdByEmail(userRequest.getEmail());
        });
    }

    private void assertException(String expectedMessage, Runnable action) {
        Exception exception = assertThrows(IllegalArgumentException.class, action::run);
        assertEquals(expectedMessage, exception.getMessage());
    }
}


