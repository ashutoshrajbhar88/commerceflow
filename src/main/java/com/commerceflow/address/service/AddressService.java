package com.commerceflow.address.service;

import com.commerceflow.address.Address;
import com.commerceflow.address.dto.AddressRequest;
import com.commerceflow.address.dto.AddressResponse;
import com.commerceflow.address.repository.AddressRepository;
import com.commerceflow.exception.ResourceNotFoundException;
import com.commerceflow.user.User;
import com.commerceflow.user.Role;
import com.commerceflow.user.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AddressService {

    private final AddressRepository addressRepository;
    private final UserRepository userRepository;

    public AddressService(
            AddressRepository addressRepository,
            UserRepository userRepository
    ) {
        this.addressRepository = addressRepository;
        this.userRepository = userRepository;
    }

    // CREATE ADDRESS
    public AddressResponse createAddress(
            Long userId,
            AddressRequest request
    ) {

        User user = userRepository.findById(userId)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "User not found with id: " + userId
                        )
                );

        Address address = new Address();

        address.setFullName(request.getFullName());
        address.setPhoneNumber(request.getPhoneNumber());
        address.setAddressLine1(request.getAddressLine1());
        address.setAddressLine2(request.getAddressLine2());
        address.setCity(request.getCity());
        address.setState(request.getState());
        address.setCountry(request.getCountry());
        address.setPostalCode(request.getPostalCode());

        address.setUser(user);

        // If this is the user's first address,
        // or user explicitly sets it as default
        List<Address> existingAddresses =
                addressRepository.findByUserId(userId);

        boolean shouldBeDefault =
                existingAddresses.isEmpty()
                        || Boolean.TRUE.equals(request.getIsDefault());

        if (shouldBeDefault) {
            removeDefaultAddress(userId);
        }

        address.setIsDefault(shouldBeDefault);

        Address savedAddress =
                addressRepository.save(address);

        return mapToResponse(savedAddress);
    }


    // GET ALL USER ADDRESSES
    public List<AddressResponse> getUserAddresses(Long userId) {

        return addressRepository
                .findByUserId(userId)
                .stream()
                .map(this::mapToResponse)
                .toList();
    }


    // GET ADDRESS BY ID
    public AddressResponse getAddressById(
            Long userId,
            Role role,
            Long addressId
    ) {

        Address address =
                addressRepository.findById(addressId)
                        .orElseThrow(() ->
                                new ResourceNotFoundException(
                                        "Address not found with id: "
                                                + addressId
                                )
                        );

        // ADMIN can view any address
        if (role == Role.ADMIN) {
            return mapToResponse(address);
        }

        // CUSTOMER can view only their own address
        if (!address.getUser()
                .getId()
                .equals(userId)) {

            throw new ResourceNotFoundException(
                    "Address does not belong to this user"
            );
        }

        return mapToResponse(address);
    }


    // UPDATE ADDRESS
    public AddressResponse updateAddress(
            Long userId,
            Long addressId,
            AddressRequest request
    ) {

        Address address = getUserAddress(
                userId,
                addressId
        );

        address.setFullName(request.getFullName());
        address.setPhoneNumber(request.getPhoneNumber());
        address.setAddressLine1(request.getAddressLine1());
        address.setAddressLine2(request.getAddressLine2());
        address.setCity(request.getCity());
        address.setState(request.getState());
        address.setCountry(request.getCountry());
        address.setPostalCode(request.getPostalCode());

        if (Boolean.TRUE.equals(request.getIsDefault())) {

            removeDefaultAddress(userId);

            address.setIsDefault(true);
        }

        Address updatedAddress =
                addressRepository.save(address);

        return mapToResponse(updatedAddress);
    }


    // SET DEFAULT ADDRESS
    public AddressResponse setDefaultAddress(
            Long userId,
            Long addressId
    ) {

        Address address = getUserAddress(
                userId,
                addressId
        );

        removeDefaultAddress(userId);

        address.setIsDefault(true);

        Address updatedAddress =
                addressRepository.save(address);

        return mapToResponse(updatedAddress);
    }


    // DELETE ADDRESS
    public void deleteAddress(
            Long userId,
            Long addressId
    ) {

        Address address = getUserAddress(
                userId,
                addressId
        );

        boolean wasDefault =
                Boolean.TRUE.equals(
                        address.getIsDefault()
                );

        addressRepository.delete(address);

        // If deleted address was default,
        // make another address default
        if (wasDefault) {

            List<Address> remainingAddresses =
                    addressRepository.findByUserId(userId);

            if (!remainingAddresses.isEmpty()) {

                Address newDefault =
                        remainingAddresses.get(0);

                newDefault.setIsDefault(true);

                addressRepository.save(newDefault);
            }
        }
    }


    // REMOVE CURRENT DEFAULT ADDRESS
    private void removeDefaultAddress(Long userId) {

        List<Address> defaultAddresses =
                addressRepository
                        .findByUserIdAndIsDefaultTrue(userId);

        for (Address address : defaultAddresses) {

            address.setIsDefault(false);

            addressRepository.save(address);
        }
    }


    private Address getUserAddress(
            Long userId,
            Long addressId
    ) {

        Address address =
                addressRepository.findById(addressId)
                        .orElseThrow(() ->
                                new ResourceNotFoundException(
                                        "Address not found with id: "
                                                + addressId
                                )
                        );

        System.out.println("ADDRESS OWNER ID: "
                + address.getUser().getId());

        System.out.println("CURRENT USER ID: "
                + userId);

        if (!address.getUser()
                .getId()
                .equals(userId)) {

            throw new ResourceNotFoundException(
                    "Address does not belong to this user"
            );
        }

        return address;
    }


    // ENTITY → RESPONSE
    private AddressResponse mapToResponse(
            Address address
    ) {

        AddressResponse response =
                new AddressResponse();

        response.setId(address.getId());
        response.setFullName(address.getFullName());
        response.setPhoneNumber(address.getPhoneNumber());
        response.setAddressLine1(address.getAddressLine1());
        response.setAddressLine2(address.getAddressLine2());
        response.setCity(address.getCity());
        response.setState(address.getState());
        response.setCountry(address.getCountry());
        response.setPostalCode(address.getPostalCode());
        response.setIsDefault(address.getIsDefault());
        response.setCreatedAt(address.getCreatedAt());
        response.setUpdatedAt(address.getUpdatedAt());

        return response;
    }
}