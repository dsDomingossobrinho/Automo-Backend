package com.automo.accountType.service;

import com.automo.accountType.dto.AccountTypeDto;
import com.automo.accountType.entity.AccountType;
import com.automo.accountType.repository.AccountTypeRepository;
import com.automo.accountType.response.AccountTypeResponse;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AccountTypeServiceImpl implements AccountTypeService {

    private final AccountTypeRepository accountTypeRepository;

    @Override
    public AccountTypeResponse createAccountType(AccountTypeDto accountTypeDto) {
        AccountType accountType = new AccountType();
        accountType.setType(accountTypeDto.type());
        accountType.setDescription(accountTypeDto.description());
        
        AccountType savedAccountType = accountTypeRepository.save(accountType);
        return mapToResponse(savedAccountType);
    }

    @Override
    public AccountTypeResponse updateAccountType(Long id, AccountTypeDto accountTypeDto) {
        AccountType accountType = this.getAccountTypeById(id);
        
        accountType.setType(accountTypeDto.type());
        accountType.setDescription(accountTypeDto.description());
        
        AccountType updatedAccountType = accountTypeRepository.save(accountType);
        return mapToResponse(updatedAccountType);
    }

    @Override
    public List<AccountTypeResponse> getAllAccountTypes() {
        return accountTypeRepository.findAll().stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Override
    public AccountType getAccountTypeById(Long id) {
        return accountTypeRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("AccountType with ID " + id + " not found"));
    }

    @Override
    public AccountTypeResponse getAccountTypeByIdResponse(Long id) {
        AccountType accountType = this.getAccountTypeById(id);
        return mapToResponse(accountType);
    }

    @Override
    public void deleteAccountType(Long id) {
        if (!accountTypeRepository.existsById(id)) {
            throw new EntityNotFoundException("AccountType with ID " + id + " not found");
        }
        accountTypeRepository.deleteById(id);
    }

    private AccountTypeResponse mapToResponse(AccountType accountType) {
        return new AccountTypeResponse(
                accountType.getId(),
                accountType.getType(),
                accountType.getDescription(),
                accountType.getCreatedAt(),
                accountType.getUpdatedAt()
        );
    }

    @Override
    public AccountType findById(Long id) {
        return accountTypeRepository.findById(id)
                .orElseThrow(() -> new jakarta.persistence.EntityNotFoundException("AccountType with ID " + id + " not found"));
    }

    @Override
    public AccountType findByIdAndStateId(Long id, Long stateId) {
        AccountType entity = accountTypeRepository.findById(id)
                .orElseThrow(() -> new jakarta.persistence.EntityNotFoundException("AccountType with ID " + id + " not found"));
        
        // For entities without state relationship, return the entity regardless of stateId
        return entity;
    }
} 