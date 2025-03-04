package ru.itmentor.spring.boot_security.demo.service;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.itmentor.spring.boot_security.demo.model.User;
import ru.itmentor.spring.boot_security.demo.repository.UserRepository;

import java.util.List;
import java.util.Optional;

@Service
public class UserService implements UserDetailsService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }


    @Transactional
    public List<User> findAll() {
        return userRepository.findAll();
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Optional<User> user = userRepository.findByUserName(username);
        return user.orElseThrow(() -> new UsernameNotFoundException("User not found"));
    }


    public User create(User user) {
        if (userRepository.findByEmail(user.getEmail()).isPresent()) {
            throw new IllegalStateException("User with this email already exists");
        }
        user.setPassword(passwordEncoder.encode(user.getPassword())); // Шифруем пароль
        return userRepository.save(user);
    }

    public void delete(Long id) {
        if (!userRepository.existsById(id)) {
            throw new IllegalStateException("User not found");
        }
        userRepository.deleteById(id);
    }

    public void update(Long id, String userName, String email, String password) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new IllegalStateException("User with such ID " + id + " does not exist"));

        if (userName != null && !userName.isBlank()) {
            user.setUserName(userName);
        }

        if (email != null && !email.equals(user.getEmail())) {
            userRepository.findByEmail(email).ifPresent(existingUser -> {
                throw new IllegalStateException("User with this email already exists");
            });
            user.setEmail(email);
        }

        if (password != null && !password.isBlank()) {
            user.setPassword(password);
        }

        userRepository.save(user);
    }
    public User getById(Long id) {
        Optional<User> optionalUser = userRepository.findById(id);
        if (optionalUser.isEmpty()) {
            checkIfIdExists(id);
        }
        return optionalUser.get();
    }

    private void checkIfEmailExists(String email) {
        if (userRepository.findByEmail(email).isPresent()) {
            throw new IllegalStateException("User with this email address already exists");
        }
    }

    private void checkIfIdExists(Long id) {
        if (userRepository.findById(id).isEmpty()) {
            throw new IllegalStateException("User with such ID " + id + " does not exist");
        }
    }


}
