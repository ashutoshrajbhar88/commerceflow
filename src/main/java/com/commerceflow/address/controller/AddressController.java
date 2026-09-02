package com.commerceflow.address.controller;

import com.commerceflow.address.dto.AddressRequest;
import com.commerceflow.address.dto.AddressResponse;
import com.commerceflow.address.service.AddressService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import com.commerceflow.user.User;
import com.commerceflow.user.Role;

import java.util.List;

@RestController
@RequestMapping("/api/v1/addresses")
public class AddressController {

    private final AddressService addressService;

    public AddressController(
            AddressService addressService
    ) {
        this.addressService = addressService;
    }

    @PostMapping
    public ResponseEntity<AddressResponse> createAddress(
            @RequestBody AddressRequest request,
            Authentication authentication
    ) {

        Long userId = getCurrentUserId(authentication);

        AddressResponse response =
                addressService.createAddress(
                        userId,
                        request
                );

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(response);
    }


    @GetMapping
    public ResponseEntity<List<AddressResponse>> getMyAddresses(
            Authentication authentication
    ) {

        Long userId = getCurrentUserId(authentication);

        return ResponseEntity.ok(
                addressService.getUserAddresses(userId)
        );
    }


    @GetMapping("/{addressId}")
    public ResponseEntity<AddressResponse> getAddressById(
            @PathVariable Long addressId,
            Authentication authentication
    ) {

        User currentUser =
                (User) authentication.getPrincipal();

        return ResponseEntity.ok(
                addressService.getAddressById(
                        currentUser.getId(),
                        currentUser.getRole(),
                        addressId
                )
        );
    }


    @PutMapping("/{addressId}")
    public ResponseEntity<AddressResponse> updateAddress(
            @PathVariable Long addressId,
            @RequestBody AddressRequest request,
            Authentication authentication
    ) {

        Long userId = getCurrentUserId(authentication);

        return ResponseEntity.ok(
                addressService.updateAddress(
                        userId,
                        addressId,
                        request
                )
        );
    }


    @PutMapping("/{addressId}/default")
    public ResponseEntity<AddressResponse> setDefaultAddress(
            @PathVariable Long addressId,
            Authentication authentication
    ) {

        Long userId = getCurrentUserId(authentication);

        System.out.println("========== SET DEFAULT ==========");
        System.out.println("CURRENT USER ID: " + userId);
        System.out.println("ADDRESS ID: " + addressId);

        return ResponseEntity.ok(
                addressService.setDefaultAddress(
                        userId,
                        addressId
                )
        );
    }


    @DeleteMapping("/{addressId}")
    public ResponseEntity<Void> deleteAddress(
            @PathVariable Long addressId,
            Authentication authentication
    ) {

        Long userId = getCurrentUserId(authentication);

        addressService.deleteAddress(
                userId,
                addressId
        );

        return ResponseEntity.noContent().build();
    }


    private Long getCurrentUserId(
            Authentication authentication
    ) {

        User user = (User) authentication.getPrincipal();

        return user.getId();
    }
}