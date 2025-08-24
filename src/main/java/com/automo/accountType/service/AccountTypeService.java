package com.automo.accountType.service;

import com.automo.accountType.dto.AccountTypeDto;
import com.automo.accountType.entity.AccountType;
import com.automo.accountType.response.AccountTypeResponse;

import java.util.List;

public interface AccountTypeService {

    AccountTypeResponse createAccountType(AccountTypeDto accountTypeDto);

    AccountTypeResponse updateAccountType(Long id, AccountTypeDto accountTypeDto);

    List<AccountTypeResponse> getAllAccountTypes();

    AccountType getAccountTypeById(Long id);

    AccountTypeResponse getAccountTypeByIdResponse(Long id);

    void deleteAccountType(Long id);
} 