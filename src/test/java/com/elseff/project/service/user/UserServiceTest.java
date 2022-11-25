package com.elseff.project.service.user;

import com.elseff.project.dto.user.UserAllFieldsCanBeNullDto;
import com.elseff.project.dto.user.UserAllFieldsDto;
import com.elseff.project.dto.user.UserDto;
import com.elseff.project.entity.User;
import com.elseff.project.exception.IdLessThanZeroException;
import com.elseff.project.exception.user.UserNotFoundException;
import com.elseff.project.repository.UserRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.modelmapper.ModelMapper;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.willDoNothing;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

class UserServiceTest {

    @InjectMocks
    private UserService service;

    @Mock
    private UserRepository repository;

    @Mock
    private ModelMapper modelMapper;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void getAllUsers() {
        given(repository.findAll()).willReturn(Arrays.asList(
                new User(),
                new User(),
                new User()
        ));

        List<UserDto> allUsers = service.getAllUsers();

        int expectedListSize = 3;
        int actualListSize = allUsers.size();

        Assertions.assertEquals(expectedListSize, actualListSize);

        verify(repository, times(1)).findAll();
    }

    @Test
    void getUserById() {
        User userFromDb = new User();

        given(repository.existsById(anyLong())).willReturn(true);
        given(repository.findById(anyLong())).willReturn(Optional.of(userFromDb));
        given(modelMapper.map(userFromDb, UserAllFieldsDto.class)).willReturn(new UserAllFieldsDto());

        UserAllFieldsDto user = service.getUserById(anyLong());

        Assertions.assertNotNull(user);

        verify(repository, times(1)).existsById(anyLong());
        verify(repository, times(1)).findById(anyLong());
    }

    @Test
    void getUserById_When_Id_Less_Than_Zero() {
        IdLessThanZeroException idLessThanZeroException = Assertions.assertThrows(IdLessThanZeroException.class,
                () -> service.getUserById(-1L));

        String expectedMessage = "id must be a greater than 0";
        String actualMessage = idLessThanZeroException.getMessage();

        Assertions.assertTrue(actualMessage.contains(expectedMessage));
    }

    @Test
    void getUserById_When_User_Does_Not_Exists() {
        given(repository.existsById(anyLong())).willReturn(false);

        UserNotFoundException exception = Assertions.assertThrows(UserNotFoundException.class, () ->
                service.getUserById(5L));

        String expectedMessage = "could not find user 5";
        String actualMessage = exception.getMessage();

        Assertions.assertTrue(actualMessage.contains(expectedMessage));

        verify(repository, times(1)).existsById(anyLong());
    }

    @Test
    void deleteUser_Id_Less_Than_Zero() {
        IdLessThanZeroException idLessThanZeroException = Assertions.assertThrows(IdLessThanZeroException.class,
                () -> service.deleteUser(-1L));

        String expectedMessage = "id must be a greater than 0";
        String actualMessage = idLessThanZeroException.getMessage();

        Assertions.assertTrue(actualMessage.contains(expectedMessage));
    }

    @Test
    void deleteUser_If_User_Not_Exists() {
        given(repository.existsById(anyLong())).willReturn(false);

        UserNotFoundException exception = Assertions.assertThrows(UserNotFoundException.class, () ->
                service.deleteUser(5L));

        String expectedMessage = "could not find user 5";
        String actualMessage = exception.getMessage();

        Assertions.assertTrue(actualMessage.contains(expectedMessage));

        verify(repository, times(1)).existsById(anyLong());
    }

    @Test
    void deleteUser() {
        given(repository.existsById(anyLong())).willReturn(true);
        willDoNothing().given(repository).deleteById(anyLong());

        service.deleteUser(anyLong());

        verify(repository, times(1)).existsById(anyLong());
        verify(repository, times(1)).deleteById(anyLong());
    }

    @Test
    void updateUser() {
        User userFromDb = new User();
        userFromDb.setFirstName("test");
        UserAllFieldsCanBeNullDto userAllFieldsCanBeNullDto = new UserAllFieldsCanBeNullDto();
        userAllFieldsCanBeNullDto.setFirstName("test1");
        UserAllFieldsDto userAllFieldsDto = new UserAllFieldsDto();
        userAllFieldsDto.setFirstName("test1");

        given(repository.getById(anyLong())).willReturn(userFromDb);
        given(repository.save(any(User.class))).willReturn(userFromDb);
        given(modelMapper.map(userFromDb, UserAllFieldsDto.class)).willReturn(userAllFieldsDto);

        UserAllFieldsDto updatedUser = service.updateUser(1L, userAllFieldsCanBeNullDto);

        String expectedFirstName = "test1";
        String actualFirstName = updatedUser.getFirstName();

        Assertions.assertEquals(expectedFirstName, actualFirstName);

        verify(repository, times(1)).getById(anyLong());
        verify(repository, times(1)).save(any(User.class));
        verify(modelMapper, times(1)).map(any(), any());
    }
}