package com.asg.common.services.controller;

import com.asg.common.lib.security.util.UserContext;
import com.asg.common.services.dto.FavoriteMenuRequest;
import com.asg.common.services.entity.FavoriteMenuEntity;
import com.asg.common.services.service.FavoriteMenuService;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.extern.slf4j.Slf4j;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.Objects;

import static com.asg.common.lib.dto.response.ApiResponse.internalServerError;
import static com.asg.common.lib.dto.response.ApiResponse.success;


@RestController
@RequestMapping("/v1/users")
@SecurityRequirement(name = "bearerAuth")
@Slf4j
@Validated
public class UserController {
    @Autowired
    private FavoriteMenuService favoriteMenuService;

    @GetMapping("/favorite-menu/favoriteList")
    public ResponseEntity<?> getFavoriteMenuList() {
        try {
            String processedUserId = Objects.requireNonNull(UserContext.getUserId()).toUpperCase();

            List<FavoriteMenuEntity> favorites = favoriteMenuService.getFavoriteList(UserContext.getUserPoid(), processedUserId);
            return success("success", favorites);
        } catch (Exception e) {
            return internalServerError("Error fetching favorite menus: " + e.getMessage());
        }
    }

    @GetMapping("/favorite-menu/unAssignedFavoriteList")
    public ResponseEntity<?> getFavoriteMenusAvailable(@RequestParam(required = false) String search, @ParameterObject Pageable pageable) {
        try {
            String processedUserId = Objects.requireNonNull(UserContext.getUserId()).toUpperCase();
            Map<String, Object> favorites = favoriteMenuService.getUnassignedFavList(UserContext.getUserPoid(), processedUserId, search, pageable);
            return success("success", favorites);
        } catch (Exception e) {
            return internalServerError("Error fetching favorite menus: " + e.getMessage());
        }
    }

    @PostMapping("/favorite-menu/add")
    public ResponseEntity<?> addFavoriteMenu(@RequestBody FavoriteMenuRequest request) {
        try {
            if (request == null || request.getUserId() == null ||
                    request.getMenuGroup() == null || request.getSelectedDocIds() == null) {
                throw new RuntimeException("Missing or invalid fields: userId, userPoid, menuGroup, or selectedDocIds");
            }
            String result = favoriteMenuService.addFavoriteMenu(request);
            return success("Favorite menu added successfully", result);
        } catch (Exception e) {
            return internalServerError(e.getMessage());
        }
    }

    @PostMapping("/favorite-menu/remove")
    public ResponseEntity<?> removeFavoriteMenus(@RequestBody FavoriteMenuRequest request) {

        try {
            if (request == null || request.getUserId() == null ||
                    request.getMenuGroup() == null || request.getSelectedDocIds() == null) {
                throw new RuntimeException("Missing or invalid fields: userId, userPoid, menuGroup, or selectedDocIds");
            }
            String result = favoriteMenuService.removeFavoriteMenus(request.getUserId(), request.getUserPoid(), request.getMenuGroup(), request.getSelectedDocIds());
            return success("Favorite menu removed successfully", result);
        } catch (Exception e) {
            return internalServerError(e.getMessage());
        }
    }
}
