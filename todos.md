# Speak Friend Todos

- Make accounts stored in kotlin AccountList object (accounts property) when unlocked. Account list use this if AccountList.unlocked, and after editing or deleting, update accounts object.
- AccountsList object add unlock(key) and lock(key), with locked and unlocked properties to check status.
- Make backup and import scenes with list of accounts. Can check accounts to back up / import, or click account to view diff in modal.
- Use CurrentUser.apiKey based on pass, to encrypt accounts to be backed up, and to decrypt accounts on import. Require this the first time an interaction with the API is required, and store in CurrentUser.apiPass.
- Fix going back to account list from Cloud scene, missing accounts.
- Build the ApiResponse.errors in Api instead of in the controller.
- Separate AccountController into controllers for each scene.
