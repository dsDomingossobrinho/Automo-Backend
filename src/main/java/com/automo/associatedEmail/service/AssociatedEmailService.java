package com.automo.associatedEmail.service;

import com.automo.associatedEmail.dto.AssociatedEmailDto;
import com.automo.associatedEmail.entity.AssociatedEmail;
import com.automo.associatedEmail.response.AssociatedEmailResponse;

import java.util.List;

public interface AssociatedEmailService {

    AssociatedEmailResponse createAssociatedEmail(AssociatedEmailDto associatedEmailDto);

    AssociatedEmailResponse updateAssociatedEmail(Long id, AssociatedEmailDto associatedEmailDto);

    List<AssociatedEmailResponse> getAllAssociatedEmails();

    AssociatedEmail getAssociatedEmailById(Long id);

    AssociatedEmailResponse getAssociatedEmailByIdResponse(Long id);

    List<AssociatedEmailResponse> getAssociatedEmailsByIdentifier(Long identifierId);

    List<AssociatedEmailResponse> getAssociatedEmailsByState(Long stateId);

    AssociatedEmailResponse getAssociatedEmailByEmail(String email);

    void deleteAssociatedEmail(Long id);
} 