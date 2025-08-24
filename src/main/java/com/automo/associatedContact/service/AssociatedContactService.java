package com.automo.associatedContact.service;

import com.automo.associatedContact.dto.AssociatedContactDto;
import com.automo.associatedContact.entity.AssociatedContact;
import com.automo.associatedContact.response.AssociatedContactResponse;

import java.util.List;

public interface AssociatedContactService {

    AssociatedContactResponse createAssociatedContact(AssociatedContactDto associatedContactDto);

    AssociatedContactResponse updateAssociatedContact(Long id, AssociatedContactDto associatedContactDto);

    List<AssociatedContactResponse> getAllAssociatedContacts();

    AssociatedContact getAssociatedContactById(Long id);

    AssociatedContactResponse getAssociatedContactByIdResponse(Long id);

    List<AssociatedContactResponse> getAssociatedContactsByIdentifier(Long identifierId);

    List<AssociatedContactResponse> getAssociatedContactsByState(Long stateId);

    void deleteAssociatedContact(Long id);
} 