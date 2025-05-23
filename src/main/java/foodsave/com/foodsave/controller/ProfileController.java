package foodsave.com.foodsave.controller;

import foodsave.com.foodsave.exception.FileStorageException;
import foodsave.com.foodsave.exception.UserNotFoundException;
import foodsave.com.foodsave.model.User;
import foodsave.com.foodsave.service.ProfileService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/api/profile")
@RequiredArgsConstructor
@Tag(name = "Profile", description = "Profile management APIs")
public class ProfileController {
    private final ProfileService profileService;

    @Operation(summary = "Get user profile", description = "Retrieves the profile information of the currently authenticated user")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Profile retrieved successfully"),
        @ApiResponse(responseCode = "401", description = "User not authenticated"),
        @ApiResponse(responseCode = "404", description = "User not found")
    })
    @GetMapping
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Map<String, Object>> getProfile() {
        try {
            return ResponseEntity.ok(profileService.getCurrentUserProfile());
        } catch (UserNotFoundException e) {
            log.error("User not found while getting profile", e);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }

    @Operation(summary = "Update profile info", description = "Updates the basic information of the user's profile")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Profile updated successfully"),
        @ApiResponse(responseCode = "400", description = "Invalid input data"),
        @ApiResponse(responseCode = "401", description = "User not authenticated"),
        @ApiResponse(responseCode = "404", description = "User not found")
    })
    @PutMapping("/info")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Map<String, Object>> updateProfileInfo(
            @Parameter(description = "Profile data to update") @RequestBody Map<String, String> profileData) {
        try {
            return ResponseEntity.ok(profileService.updateProfileInfo(profileData));
        } catch (IllegalArgumentException e) {
            log.error("Invalid profile data", e);
            return ResponseEntity.badRequest().build();
        } catch (UserNotFoundException e) {
            log.error("User not found while updating profile", e);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }

    @Operation(summary = "Update address", description = "Updates the address information of the user's profile")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Address updated successfully"),
        @ApiResponse(responseCode = "400", description = "Invalid input data"),
        @ApiResponse(responseCode = "401", description = "User not authenticated"),
        @ApiResponse(responseCode = "404", description = "User not found")
    })
    @PutMapping("/address")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Map<String, Object>> updateAddress(
            @Parameter(description = "Address data to update") @RequestBody Map<String, String> addressData) {
        try {
            return ResponseEntity.ok(profileService.updateAddress(addressData));
        } catch (IllegalArgumentException e) {
            log.error("Invalid address data", e);
            return ResponseEntity.badRequest().build();
        } catch (UserNotFoundException e) {
            log.error("User not found while updating address", e);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }

    @Operation(summary = "Update social links", description = "Updates the social media links of the user's profile")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Social links updated successfully"),
        @ApiResponse(responseCode = "400", description = "Invalid input data"),
        @ApiResponse(responseCode = "401", description = "User not authenticated"),
        @ApiResponse(responseCode = "404", description = "User not found")
    })
    @PutMapping("/social")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Map<String, Object>> updateSocialLinks(
            @Parameter(description = "Social links to update") @RequestBody Map<String, String> socialLinks) {
        try {
            return ResponseEntity.ok(profileService.updateSocialLinks(socialLinks));
        } catch (IllegalArgumentException e) {
            log.error("Invalid social links data", e);
            return ResponseEntity.badRequest().build();
        } catch (UserNotFoundException e) {
            log.error("User not found while updating social links", e);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }

    @Operation(summary = "Update avatar", description = "Uploads and updates the user's profile avatar")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Avatar updated successfully"),
        @ApiResponse(responseCode = "400", description = "Invalid file or file too large"),
        @ApiResponse(responseCode = "401", description = "User not authenticated"),
        @ApiResponse(responseCode = "404", description = "User not found"),
        @ApiResponse(responseCode = "500", description = "File storage error")
    })
    @PostMapping("/avatar")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Map<String, Object>> updateAvatar(
            @Parameter(description = "Avatar image file (JPEG, PNG, GIF)") @RequestParam("file") MultipartFile file) {
        try {
            return ResponseEntity.ok(profileService.updateAvatar(file));
        } catch (IllegalArgumentException e) {
            log.error("Invalid avatar file", e);
            return ResponseEntity.badRequest().build();
        } catch (FileStorageException e) {
            log.error("Failed to store avatar file", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        } catch (UserNotFoundException e) {
            log.error("User not found while updating avatar", e);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }

    @Operation(summary = "Delete avatar", description = "Removes the user's profile avatar")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Avatar deleted successfully"),
        @ApiResponse(responseCode = "401", description = "User not authenticated"),
        @ApiResponse(responseCode = "404", description = "User not found"),
        @ApiResponse(responseCode = "500", description = "File storage error")
    })
    @DeleteMapping("/avatar")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Void> deleteAvatar() {
        try {
            profileService.deleteAvatar();
            return ResponseEntity.ok().build();
        } catch (FileStorageException e) {
            log.error("Failed to delete avatar file", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        } catch (UserNotFoundException e) {
            log.error("User not found while deleting avatar", e);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }
} 