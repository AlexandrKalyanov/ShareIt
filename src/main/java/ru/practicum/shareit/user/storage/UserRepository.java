package ru.practicum.shareit.user.storage;


import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.shareit.exceptions.ObjectNotFoundException;
import ru.practicum.shareit.user.model.User;


public interface UserRepository extends JpaRepository<User, Long> {

    default User getUserOrException(Long userId){
        return findById(userId).orElseThrow(() -> new ObjectNotFoundException("User not found"));
    }
}
