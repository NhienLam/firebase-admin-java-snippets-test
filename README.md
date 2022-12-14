# firebase-admin-java-snippets-test
Steps for using this repository to test code snippets in
https://github.com/firebase/firebase-admin-java/blob/7b991238067137818907847826513bbd62a8f68a/src/test/java/com/google/firebase/snippets/FirebaseAuthSnippets.java.

1.  Clone this repository

1.  [Install Admin Java SDK](https://firebase.google.com/docs/admin/setup#add-sdk)
    to the project

    For InteliJ:

    `File > Project Structure > Library > '+' button > From Maven > Paste the
    Maven dependency > Apply`

    [Video shows how to install Admin Java SDK in InteliJ](https://www.youtube.com/watch?v=zdJ-Kp73GA8)

1.  Download your Firebase project's service account key

    `In Firebase Console > Project settings > Service accounts > Generate new
    private key`

1.  Open the project in your IDE and go to

    /firebase-admin-java-snippets-test/src/com/company/Main.java 

1.  Set `PATH_TO_SERVICE_ACCOUNT_KEY` to the path to your generated service
    account key (line 23)

    Optional: Put your `tenantId`, `uid`, and `idToken` (line 35-40)

1.  Set `DATABASE_URL` to your Firebase project's database URL (line 24)

    `In Firebase Console > Project settings > General > Find "databaseURL" in
    Config`

1.  Add new functions that you want to test

1.  Call functions in the main() method and verify whether the functions work at Google Cloud console
