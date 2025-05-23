package foodsave.com.foodsave.service;

import foodsave.com.foodsave.exception.FileStorageException;
import foodsave.com.foodsave.exception.UserNotFoundException;
import foodsave.com.foodsave.model.User;
import foodsave.com.foodsave.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class ProfileService {
    private final UserRepository userRepository;
    
    @Value("${app.avatar.upload-dir:uploads/avatars}")
    private String avatarUploadDir;
    
    @Value("${app.avatar.max-size:5242880}")
    private long maxFileSize;
    
    private static final String[] ALLOWED_CONTENT_TYPES = {
        "image/jpeg",
        "image/png",
        "image/gif"
    };

    public Map<String, Object> getCurrentUserProfile() {
        User user = getCurrentUser();
        log.debug("Getting profile for user: {}", user.getEmail());
        
        Map<String, Object> profile = new HashMap<>();

        // Basic info
        profile.put("firstName", user.getFirstName());
        profile.put("lastName", user.getLastName());
        profile.put("email", user.getEmail());
        profile.put("phone", user.getPhone());
        profile.put("bio", user.getBio());

        // Address info
        profile.put("country", user.getCountry());
        profile.put("city", user.getCity());
        profile.put("postalCode", user.getPostalCode());
        profile.put("taxId", user.getTaxId());

        // Social links
        profile.put("facebook", user.getFacebookLink());
        profile.put("twitter", user.getTwitterLink());
        profile.put("linkedin", user.getLinkedinLink());
        profile.put("instagram", user.getInstagramLink());

        // Avatar
        profile.put("avatar", user.getAvatarUrl());

        return profile;
    }

    public Map<String, Object> updateProfileInfo(Map<String, String> profileData) {
        validateProfileData(profileData);
        User user = getCurrentUser();
        log.info("Updating profile info for user: {}", user.getEmail());

        user.setFirstName(profileData.get("firstName"));
        user.setLastName(profileData.get("lastName"));
        user.setPhone(profileData.get("phone"));
        user.setBio(profileData.get("bio"));

        userRepository.save(user);
        return getCurrentUserProfile();
    }

    public Map<String, Object> updateAddress(Map<String, String> addressData) {
        validateAddressData(addressData);
        User user = getCurrentUser();
        log.info("Updating address for user: {}", user.getEmail());

        user.setCountry(addressData.get("country"));
        user.setCity(addressData.get("city"));
        user.setPostalCode(addressData.get("postalCode"));
        user.setTaxId(addressData.get("taxId"));

        userRepository.save(user);
        return getCurrentUserProfile();
    }

    public Map<String, Object> updateSocialLinks(Map<String, String> socialLinks) {
        validateSocialLinks(socialLinks);
        User user = getCurrentUser();
        log.info("Updating social links for user: {}", user.getEmail());

        user.setFacebookLink(socialLinks.get("facebook"));
        user.setTwitterLink(socialLinks.get("twitter"));
        user.setLinkedinLink(socialLinks.get("linkedin"));
        user.setInstagramLink(socialLinks.get("instagram"));

        userRepository.save(user);
        return getCurrentUserProfile();
    }

    public Map<String, Object> updateAvatar(MultipartFile file) {
        validateAvatarFile(file);
        User user = getCurrentUser();
        log.info("Updating avatar for user: {}", user.getEmail());

        try {
            // Create upload directory if it doesn't exist
            Path uploadDir = Paths.get(avatarUploadDir);
            if (!Files.exists(uploadDir)) {
                Files.createDirectories(uploadDir);
            }

            // Delete old avatar if exists
            if (user.getAvatarUrl() != null) {
                deleteAvatarFile(user.getAvatarUrl());
            }

            // Generate unique filename
            String filename = generateUniqueFilename(file);
            Path filePath = uploadDir.resolve(filename);

            // Save file
            Files.copy(file.getInputStream(), filePath);

            // Update user avatar URL
            user.setAvatarUrl("/uploads/avatars/" + filename);
            userRepository.save(user);

            return getCurrentUserProfile();
        } catch (IOException e) {
            log.error("Failed to upload avatar for user: {}", user.getEmail(), e);
            throw new FileStorageException("Failed to upload avatar", e);
        }
    }

    public void deleteAvatar() {
        User user = getCurrentUser();
        log.info("Deleting avatar for user: {}", user.getEmail());

        if (user.getAvatarUrl() != null) {
            deleteAvatarFile(user.getAvatarUrl());
        }

        user.setAvatarUrl(null);
        userRepository.save(user);
    }

    private User getCurrentUser() {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new UserNotFoundException("User not found"));
    }

    private void validateProfileData(Map<String, String> profileData) {
        if (profileData == null) {
            throw new IllegalArgumentException("Profile data cannot be null");
        }
        if (!StringUtils.hasText(profileData.get("firstName"))) {
            throw new IllegalArgumentException("First name is required");
        }
        if (!StringUtils.hasText(profileData.get("lastName"))) {
            throw new IllegalArgumentException("Last name is required");
        }
    }

    private void validateAddressData(Map<String, String> addressData) {
        if (addressData == null) {
            throw new IllegalArgumentException("Address data cannot be null");
        }
    }

    private void validateSocialLinks(Map<String, String> socialLinks) {
        if (socialLinks == null) {
            throw new IllegalArgumentException("Social links cannot be null");
        }
    }

    private void validateAvatarFile(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw new IllegalArgumentException("Avatar file cannot be empty");
        }
        if (file.getSize() > maxFileSize) {
            throw new IllegalArgumentException("File size exceeds maximum limit");
        }
        String contentType = file.getContentType();
        if (contentType == null || !Arrays.asList(ALLOWED_CONTENT_TYPES).contains(contentType)) {
            throw new IllegalArgumentException("Invalid file type. Allowed types: JPEG, PNG, GIF");
        }
    }

    private String generateUniqueFilename(MultipartFile file) {
        String originalFilename = StringUtils.cleanPath(file.getOriginalFilename());
        String extension = originalFilename.substring(originalFilename.lastIndexOf("."));
        return UUID.randomUUID().toString() + extension;
    }

    private void deleteAvatarFile(String avatarUrl) {
        try {
            Path avatarPath = Paths.get(avatarUploadDir, avatarUrl);
            Files.deleteIfExists(avatarPath);
        } catch (IOException e) {
            log.error("Failed to delete avatar file: {}", avatarUrl, e);
            throw new FileStorageException("Failed to delete avatar file", e);
        }
    }

    public User getUserById(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found"));
    }

    public User updateUser(Long id, User updatedUser) {
        User existingUser = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found"));

        // Обновляем только разрешенные поля
        existingUser.setFirstName(updatedUser.getFirstName());
        existingUser.setLastName(updatedUser.getLastName());
        existingUser.setPhone(updatedUser.getPhone());
        existingUser.setBio(updatedUser.getBio());
        existingUser.setCountry(updatedUser.getCountry());
        existingUser.setCity(updatedUser.getCity());
        existingUser.setPostalCode(updatedUser.getPostalCode());
        existingUser.setTaxId(updatedUser.getTaxId());
        existingUser.setFacebookLink(updatedUser.getFacebookLink());
        existingUser.setTwitterLink(updatedUser.getTwitterLink());
        existingUser.setLinkedinLink(updatedUser.getLinkedinLink());
        existingUser.setInstagramLink(updatedUser.getInstagramLink());

        return userRepository.save(existingUser);
    }

    public User updateAvatar(Long id, String avatarUrl) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found"));
        user.setAvatarUrl(avatarUrl);
        return userRepository.save(user);
    }
}