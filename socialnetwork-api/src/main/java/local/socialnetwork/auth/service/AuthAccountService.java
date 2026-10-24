package local.socialnetwork.auth.service;

import local.socialnetwork.auth.dto.http.request.DeleteAccountRequest;
import local.socialnetwork.auth.dto.http.request.ChangePasswordRequest;

import java.util.UUID;

/**
 * Changes to an existing, authenticated account: password change and deletion.
 */
public interface AuthAccountService {
    void changePassword(UUID userId, ChangePasswordRequest request);
    void deleteAccount(UUID userId, DeleteAccountRequest request);
}
