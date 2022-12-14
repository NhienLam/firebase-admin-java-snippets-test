package com.company;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.*;
import com.google.firebase.auth.hash.HmacSha256;
import com.google.firebase.auth.multitenancy.ListTenantsPage;
import com.google.firebase.auth.multitenancy.Tenant;
import com.google.firebase.auth.multitenancy.TenantAwareFirebaseAuth;
import com.google.firebase.auth.multitenancy.TenantManager;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Main {

    public static void main(String[] args) throws IOException, FirebaseAuthException {
        final String PATH_TO_SERVICE_ACCOUNT_KEY = "";
        final String DATABASE_URL = "";
        FileInputStream serviceAccount = new FileInputStream(PATH_TO_SERVICE_ACCOUNT_KEY);

        FirebaseOptions options =
            FirebaseOptions.builder()
                .setCredentials(GoogleCredentials.fromStream(serviceAccount))
                .setDatabaseUrl(DATABASE_URL)
                .build();

        FirebaseApp.initializeApp(options);

        String tenantId = "";
        FirebaseAuth auth = FirebaseAuth.getInstance();
        TenantManager tenantManager = auth.getTenantManager();
        TenantAwareFirebaseAuth tenantAuth = tenantManager.getAuthForTenant(tenantId);
        String uid = "";
        String idToken = "";

        System.out.println("----");
        System.out.println("----");
    }

    /***  https://cloud.google.com/identity-platform/docs/multi-tenancy-managing-tenants ***/
    /** Tenant management * */
    /* Getting an existing tenant */
    public static void getTenant(String tenantId) throws FirebaseAuthException {
        // [START get_tenant]
        Tenant tenant = FirebaseAuth.getInstance().getTenantManager().getTenant(tenantId);
        System.out.println("Retrieved tenant: " + tenant.getTenantId());
        // [END get_tenant]
    }

    /* Creating a tenant */
    public static void createTenant() throws FirebaseAuthException {
        // [START create_tenant]
        Tenant.CreateRequest request =
            new Tenant.CreateRequest()
                .setDisplayName("myTenant2")
                .setEmailLinkSignInEnabled(true)
                .setPasswordSignInAllowed(true);
        Tenant tenant = FirebaseAuth.getInstance().getTenantManager().createTenant(request);
        System.out.println("Created tenant: " + tenant.getTenantId());
        // [END create_tenant]
    }

    /* Updating a tenant */
    public static void updateTenant(String tenantId) throws FirebaseAuthException {
        // [START update_tenant]
        Tenant.UpdateRequest request =
            new Tenant.UpdateRequest(tenantId)
                .setDisplayName("updatedName")
                .setPasswordSignInAllowed(false);
        Tenant tenant = FirebaseAuth.getInstance().getTenantManager().updateTenant(request);
        System.out.println("Updated tenant: " + tenant.getTenantId());
        // [END update_tenant]
    }

    /* Deleting a tenant */
    public static void deleteTenant(String tenantId) throws FirebaseAuthException {
        // [START delete_tenant]
        FirebaseAuth.getInstance().getTenantManager().deleteTenant(tenantId);
        // [END delete_tenant]
    }

    /* Listing tenants */
    public static void listTenants() throws FirebaseAuthException {
        // [START list_tenants]
        ListTenantsPage page = FirebaseAuth.getInstance().getTenantManager().listTenants(null);
        for (Tenant tenant : page.iterateAll()) {
            System.out.println("Retrieved tenant: " + tenant.getTenantId());
        }
        // [END list_tenants]
    }

    /** Managing SAML and OIDC providers programmatically * */
    /* Creating a provider */
    public static void createProviderTenant(TenantAwareFirebaseAuth tenantAuth)
        throws FirebaseAuthException {
        // [START create_saml_provider_tenant]
        SamlProviderConfig.CreateRequest request =
            new SamlProviderConfig.CreateRequest()
                .setDisplayName("SAML provider name")
                .setEnabled(true)
                .setProviderId("saml.myProvider")
                .setIdpEntityId("IDP_ENTITY_ID")
                .setSsoUrl("https://example.com/saml/sso/1234/")
                .addX509Certificate("-----BEGIN CERTIFICATE-----\nCERT1...\n-----END CERTIFICATE-----")
                .addX509Certificate("-----BEGIN CERTIFICATE-----\nCERT2...\n-----END CERTIFICATE-----")
                .setRpEntityId("RP_ENTITY_ID")
                .setCallbackUrl("https://project-id.firebaseapp.com/__/auth/handler");
        SamlProviderConfig saml = tenantAuth.createSamlProviderConfig(request);
        System.out.println("Created new SAML provider: " + saml.getProviderId());
        // [END create_saml_provider_tenant]
    }

    /* Modifying a provider */
    public static void updateProviderTenant(TenantAwareFirebaseAuth tenantAuth)
        throws FirebaseAuthException {
        // [START update_saml_provider_tenant]
        SamlProviderConfig.UpdateRequest request =
            new SamlProviderConfig.UpdateRequest("saml.myProvider")
                .addX509Certificate("-----BEGIN CERTIFICATE-----\nCERT2...\n-----END CERTIFICATE-----")
                .addX509Certificate("-----BEGIN CERTIFICATE-----\nCERT3...\n-----END CERTIFICATE-----");
        SamlProviderConfig saml = tenantAuth.updateSamlProviderConfig(request);
        System.out.println("Updated SAML provider: " + saml.getProviderId());
        // [END update_saml_provider_tenant]
    }

    /* Getting a provider */
    public static void getProviderTenant(TenantAwareFirebaseAuth tenantAuth)
        throws FirebaseAuthException {
        // [START get_saml_provider_tenant]
        SamlProviderConfig saml = tenantAuth.getSamlProviderConfig("saml.myProvider");

        // Get display name and whether it is enabled.
        System.out.println(saml.getDisplayName() + " " + saml.isEnabled());
        // [END get_saml_provider_tenant]
    }

    /* Listing providers */
    public static void listProvidersTenant(TenantAwareFirebaseAuth tenantAuth)
        throws FirebaseAuthException {
        // [START list_saml_providers_tenant]
        ListProviderConfigsPage<SamlProviderConfig> page = tenantAuth.listSamlProviderConfigs(null);
        for (SamlProviderConfig saml : page.iterateAll()) {
            System.out.println(saml.getProviderId());
        }
        // [END list_saml_providers_tenant]
    }

    /* Deleting a provider */
    public static void deleteProviderTenant(TenantAwareFirebaseAuth tenantAuth)
        throws FirebaseAuthException {
        // [START delete_saml_provider_tenant]
        tenantAuth.deleteSamlProviderConfig("saml.myProvider");
        // [END delete_saml_provider_tenant]
    }

    /** Managing tenant specific users * */
    /* Getting a user */
    public static void getUserTenant(TenantAwareFirebaseAuth tenantAuth, String uid)
        throws FirebaseAuthException {
        // [START get_user_tenant]
        // Get an auth client from the firebase.App
        UserRecord user = tenantAuth.getUser(uid);
        System.out.println("Successfully fetched user data: " + user.getDisplayName());
        // [END get_user_tenant]
    }

    /* Getting a user by email */
    public static void getUserByEmailTenant(TenantAwareFirebaseAuth tenantAuth, String email)
        throws FirebaseAuthException {
        // [START get_user_by_email_tenant]
        // Get an auth client from the firebase.App
        UserRecord user = tenantAuth.getUserByEmail(email);
        System.out.println("Successfully fetched user data: " + user.getDisplayName());
        // [END get_user_by_email_tenant]
    }

    /* Creating a user */
    public static void createUserTenant(TenantAwareFirebaseAuth tenantAuth)
        throws FirebaseAuthException {
        // [START create_user_tenant]
        UserRecord.CreateRequest request =
            new UserRecord.CreateRequest()
                .setEmail("user123@example.com")
                .setEmailVerified(false)
                .setPhoneNumber("+15555550123")
                .setPassword("secretPassword")
                .setDisplayName("John Doe")
                .setPhotoUrl("http://www.example.com/12345123/photo.png")
                .setDisabled(false);
        UserRecord user = tenantAuth.createUser(request);
        System.out.println("Successfully created user: " + user.getDisplayName());
        // [END create_user_tenant]
    }

    /* Modifying a user */
    public static void updateUserTenant(TenantAwareFirebaseAuth tenantAuth, String uid)
        throws FirebaseAuthException {
        // [START update_user_tenant]
        UserRecord.UpdateRequest request =
            new UserRecord.UpdateRequest(uid)
                .setEmail("user@example.com")
                .setEmailVerified(true)
                .setPhoneNumber("+15555550100")
                .setPassword("newPassword")
                .setDisplayName("John Doe")
                .setPhotoUrl("http://www.example.com/12345678/photo.png")
                .setDisabled(true);
        UserRecord user = tenantAuth.updateUser(request);
        System.out.println("Successfully updated user: " + user.getDisplayName());
        // [END update_user_tenant]
    }

    /* Deleting a user */
    public static void deleteUserTenant(TenantAwareFirebaseAuth tenantAuth, String uid)
        throws FirebaseAuthException {
        // [START delete_user_tenant]
        tenantAuth.deleteUser(uid);

        System.out.println("Successfully deleted user: " + uid);
        // [END delete_user_tenant]
    }

    /* Listing users */
    public static void listUsersTenant(TenantAwareFirebaseAuth tenantAuth)
        throws FirebaseAuthException {
        // [START list_all_users_tenant]
        // Note, behind the scenes, the ListUsersPage retrieves 1000 Users at a time
        // through the API
        ListUsersPage page = tenantAuth.listUsers(null);
        for (ExportedUserRecord user : page.iterateAll()) {
            System.out.println("User: " + user.getUid());
        }

        // Iterating by pages 100 users at a time.
        page = tenantAuth.listUsers(null, 100);
        while (page != null) {
            for (ExportedUserRecord user : page.getValues()) {
                System.out.println("User: " + user.getUid());
            }

            page = page.getNextPage();
        }
        // [END list_all_users_tenant]
    }

    /** Importing users * */
    public static void importWithHmacTenant(TenantAwareFirebaseAuth tenantAuth)
        throws FirebaseAuthException {
        // [START import_with_hmac_tenant]
        List<ImportUserRecord> users = new ArrayList<>();
        users.add(
            ImportUserRecord.builder()
                .setUid("uid1")
                .setEmail("user1@example.com")
                .setPasswordHash("password-hash-1".getBytes())
                .setPasswordSalt("salt1".getBytes())
                .build());
        users.add(
            ImportUserRecord.builder()
                .setUid("uid2")
                .setEmail("user2@example.com")
                .setPasswordHash("password-hash-2".getBytes())
                .setPasswordSalt("salt2".getBytes())
                .build());
        UserImportHash hmacSha256 = HmacSha256.builder().setKey("secret".getBytes()).build();
        UserImportResult result = tenantAuth.importUsers(users, UserImportOptions.withHash(hmacSha256));

        for (ErrorInfo error : result.getErrors()) {
            System.out.println("Failed to import user: " + error.getReason());
        }
        // [END import_with_hmac_tenant]
    }

    /* Users without passwords can also be imported to a specific tenant. */
    public static void importWithoutPasswordTenant(TenantAwareFirebaseAuth tenantAuth)
        throws FirebaseAuthException {
        // [START import_without_password_tenant]
        List<ImportUserRecord> users = new ArrayList<>();
        users.add(
            ImportUserRecord.builder()
                .setUid("some-uid")
                .setDisplayName("John Doe")
                .setEmail("johndoe@acme.com")
                .setPhotoUrl("https://www.example.com/12345678/photo.png")
                .setEmailVerified(true)
                .setPhoneNumber("+11234567890")
                // Set this user as admin.
                .putCustomClaim("admin", true)
                // User with SAML provider.
                .addUserProvider(
                    UserProvider.builder()
                        .setUid("saml-uid")
                        .setEmail("johndoe@acme.com")
                        .setDisplayName("John Doe")
                        .setPhotoUrl("https://www.example.com/12345678/photo.png")
                        .setProviderId("saml.acme")
                        .build())
                .build());

        UserImportResult result = tenantAuth.importUsers(users);

        for (ErrorInfo error : result.getErrors()) {
            System.out.println("Failed to import user: " + error.getReason());
        }
        // [END import_without_password_tenant]
    }

    /** Identity verification * */
    public static void verifyIdTokenTenant(TenantAwareFirebaseAuth tenantAuth, String idToken) {
        try {
            // idToken comes from the client app
            FirebaseToken token = tenantAuth.verifyIdToken(idToken);
            // TenantId on the FirebaseToken should be set to TENANT-ID.
            // Otherwise "tenant-id-mismatch" error thrown.
            System.out.println("Verified ID token from tenant: " + token.getTenantId());
        } catch (FirebaseAuthException e) {
            System.out.println("error verifying ID token: " + e.getMessage());
        }
    }

    /** Managing user sessions * */
    /* The refresh tokens can then be revoked by specifying the uid of that user */
    public static void revokeRefreshTokensTenant(TenantAwareFirebaseAuth tenantAuth, String uid)
        throws FirebaseAuthException {
        // [START revoke_tokens_tenant]
        // Revoke all refresh tokens for a specified user in a specified tenant for whatever reason.
        // Retrieve the timestamp of the revocation, in seconds since the epoch.
        tenantAuth.revokeRefreshTokens(uid);

        // accessing the user's TokenValidAfter
        UserRecord user = tenantAuth.getUser(uid);

        long timestamp = user.getTokensValidAfterTimestamp() / 1000;
        System.out.println("the refresh tokens were revoked at: " + timestamp + " (UTC seconds)");
        // [END revoke_tokens_tenant]
    }

    /* You can verify that an unexpired valid ID token is not revoked by specifying the optional checkRevoked parameter; this checks if a token is revoked after its integrity and authenticity is verified. */
    public static void verifyIdTokenAndCheckRevokedTenant(
        TenantAwareFirebaseAuth tenantAuth, String idToken) {
        // [START verify_id_token_and_check_revoked_tenant]
        // Verify the ID token for a specific tenant while checking if the token is revoked.
        boolean checkRevoked = true;
        try {
            FirebaseToken token = tenantAuth.verifyIdToken(idToken, checkRevoked);
            System.out.println("Verified ID token for: " + token.getUid());
        } catch (FirebaseAuthException e) {
            if ("id-token-revoked".equals(e.getErrorCode())) {
                System.out.println(
                    "Token is revoked. Inform the user to re-authenticate or signOut() the user.");
            } else {
                System.out.println("Token is invalid");
            }
        }
        // [END verify_id_token_and_check_revoked_tenant]
    }

    /** Controlling access with custom claims * */
    /* Set admin privilege on the user corresponding to uid. */
    public static void setCustomUserClaims(String uid, TenantAwareFirebaseAuth tenantAuth)
        throws FirebaseAuthException {
        // [START set_custom_user_claims]
        // Set admin privilege on the user corresponding to uid.
        Map<String, Object> claims = new HashMap<>();
        claims.put("admin", true);
        tenantAuth.setCustomUserClaims(uid, claims);
        // The new custom claims will propagate to the user's ID token the
        // next time a new one is issued.
        // [END set_custom_user_claims]
    }

    /* After verifying the ID token and decoding its payload, the additional custom claims can then be checked to enforce access control. */
    public static void customClaimsVerifyTenant(TenantAwareFirebaseAuth tenantAuth, String idToken)
        throws FirebaseAuthException {
        // [START verify_custom_claims_tenant]
        // Verify the ID token first.
        FirebaseToken token = tenantAuth.verifyIdToken(idToken);
        if (Boolean.TRUE.equals(token.getClaims().get("admin"))) {
            System.out.println("Allow access to requested admin resource.");
        }
        // [END verify_custom_claims_tenant]
    }

    /* Custom claims for an existing user for a specific tenant are also available as a property on the user record. */
    public static void readCustomUserClaims(String uid, TenantAwareFirebaseAuth tenantAuth)
        throws FirebaseAuthException {
        // Lookup the user associated with the specified uid.
        UserRecord user = tenantAuth.getUser(uid);
        System.out.println(user.getCustomClaims().get("admin"));
    }

    /** Generating email action links * */
    public static void generateEmailVerificationLinkTenant(
        TenantAwareFirebaseAuth tenantAuth, String email, String displayName)
        throws FirebaseAuthException {
        // [START email_verification_link_tenant]
        ActionCodeSettings actionCodeSettings =
            ActionCodeSettings.builder()
                // URL you want to redirect back to. The domain (www.example.com) for
                // this URL must be whitelisted in the GCP Console.
                .setUrl("https://www.example.com/checkout?cartId=1234")
                // This must be true for email link sign-in.
                .setHandleCodeInApp(true)
                .setIosBundleId("com.example.ios")
                .setAndroidPackageName("com.example.android")
                .setAndroidInstallApp(true)
                .setAndroidMinimumVersion("12")
                // FDL custom domain.
                .setDynamicLinkDomain("coolapp.page.link")
                .build();

        String link = tenantAuth.generateEmailVerificationLink(email, actionCodeSettings);

        // Construct email verification template, embed the link and send
        // using custom SMTP server.
        sendCustomEmail(email, displayName, link);
        // [END email_verification_link_tenant]
    }

    private static void sendCustomEmail(String email, String displayName, String link) {}

    /*** END OF DOC ***/

    /***  https://cloud.google.com/identity-platform/docs/managing-providers-programmatically ***/
    /** Working with SAML providers * */
    /* Creating a SAML provider configuration */
    public static void createSamlProviderConfig() throws FirebaseAuthException {
        // [START create_saml_provider]
        SamlProviderConfig.CreateRequest request =
            new SamlProviderConfig.CreateRequest()
                .setDisplayName("SAML provider name123")
                .setEnabled(true)
                .setProviderId("saml.myProvider")
                .setIdpEntityId("IDP_ENTITY_ID")
                .setSsoUrl("https://example.com/saml/sso/1234/")
                .addX509Certificate("-----BEGIN CERTIFICATE-----\nCERT1...\n-----END CERTIFICATE-----")
                .addX509Certificate("-----BEGIN CERTIFICATE-----\nCERT2...\n-----END CERTIFICATE-----")
                .setRpEntityId("RP_ENTITY_ID")
                .setCallbackUrl("https://project-id.firebaseapp.com/__/auth/handler");
        SamlProviderConfig saml = FirebaseAuth.getInstance().createSamlProviderConfig(request);
        System.out.println("Created new SAML provider: " + saml.getProviderId());
        // [END create_saml_provider]
    }

    /* Updating a SAML provider configuration */
    public static void updateSamlProviderConfig() throws FirebaseAuthException {
        // [START update_saml_provider]
        SamlProviderConfig.UpdateRequest request =
            new SamlProviderConfig.UpdateRequest("saml.myProvider")
                .addX509Certificate("-----BEGIN CERTIFICATE-----\nCERT2...\n-----END CERTIFICATE-----")
                .addX509Certificate("-----BEGIN CERTIFICATE-----\nCERT3...\n-----END CERTIFICATE-----");
        SamlProviderConfig saml = FirebaseAuth.getInstance().updateSamlProviderConfig(request);
        System.out.println("Updated SAML provider: " + saml.getProviderId());
        // [END update_saml_provider]
    }

    /* Getting a SAML provider configuration */
    public static void getSamlProviderConfig() throws FirebaseAuthException {
        // [START get_saml_provider]
        SamlProviderConfig saml = FirebaseAuth.getInstance().getSamlProviderConfig("saml.myProvider");
        System.out.println(saml.getDisplayName() + ": " + saml.isEnabled());
        // [END get_saml_provider]
    }

    /* Deleting a SAML provider configuration */
    public static void deleteSamlProviderConfig() throws FirebaseAuthException {
        // [START delete_saml_provider]
        FirebaseAuth.getInstance().deleteSamlProviderConfig("saml.myProvider");
        // [END delete_saml_provider]
    }

    /* Listing SAML provider configurations */
    public static void listSamlProviderConfigs() throws FirebaseAuthException {
        // [START list_saml_providers]
        ListProviderConfigsPage<SamlProviderConfig> page =
            FirebaseAuth.getInstance().listSamlProviderConfigs(null);
        for (SamlProviderConfig config : page.iterateAll()) {
            System.out.println(config.getProviderId());
        }
        // [END list_saml_providers]
    }

    /** Working with OIDC providers * */
    /* Creating a OIDC provider configuration */
    public static void createOidcProviderConfig() throws FirebaseAuthException {
        // [START create_oidc_provider]
        OidcProviderConfig.CreateRequest request =
            new OidcProviderConfig.CreateRequest()
                .setDisplayName("OIDC provider name2")
                .setEnabled(true)
                .setProviderId("oidc.myProvider2")
                .setClientId("CLIENT_ID22")
                .setIssuer("https://oidc.com/CLIENT_ID22");
        OidcProviderConfig oidc = FirebaseAuth.getInstance().createOidcProviderConfig(request);
        System.out.println("Created new OIDC provider: " + oidc.getProviderId());
        // [END create_oidc_provider]
    }

    /* Updating a OIDC provider configuration */
    public static void updateOidcProviderConfig() throws FirebaseAuthException {
        // [START update_oidc_provider]
        OidcProviderConfig.UpdateRequest request =
            new OidcProviderConfig.UpdateRequest("oidc.myProvider")
                .setDisplayName("OIDC provider name")
                .setEnabled(true)
                .setClientId("CLIENT_ID")
                .setIssuer("https://oidc.com");
        OidcProviderConfig oidc = FirebaseAuth.getInstance().updateOidcProviderConfig(request);
        System.out.println("Updated OIDC provider: " + oidc.getProviderId());
        // [END update_oidc_provider]
    }

    /* Getting a OIDC provider configuration */
    public static void getOidcProviderConfig() throws FirebaseAuthException {
        // [START get_oidc_provider]
        OidcProviderConfig oidc = FirebaseAuth.getInstance().getOidcProviderConfig("oidc.myProvider");
        System.out.println(oidc.getDisplayName() + ": " + oidc.isEnabled());
        // [END get_oidc_provider]
    }

    /* Deleting a OIDC provider configuration */
    public static void deleteOidcProviderConfig() throws FirebaseAuthException {
        // [START delete_oidc_provider]
        FirebaseAuth.getInstance().deleteOidcProviderConfig("oidc.myProvider");
        // [END delete_oidc_provider]
    }

    /* Listing OIDC provider configurations*/
    public static void listOidcProviderConfigs() throws FirebaseAuthException {
        // [START list_oidc_providers]
        ListProviderConfigsPage<OidcProviderConfig> page =
            FirebaseAuth.getInstance().listOidcProviderConfigs(null);
        for (OidcProviderConfig oidc : page.iterateAll()) {
            System.out.println(oidc.getProviderId());
        }
        // [END list_oidc_providers]
    }

    /*** END OF DOC ***/
}