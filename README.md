# Cloud Development: The Weather App

The purpose of these apps is to provide a basic application that operates "locally".  Your job is to store the relevant data in the cloud.

The Weather App consists of four pages:

1) A facebook login page.
2) A favorites list page.
3) A search page.
4) A details page.

You can add a city to the favorites.  Search for the city, then click on it to bring up the details.  Finally, click the star in the top-right corner to add it to the favorites.  Similarly, you can remove an entry from the favorites by clicking on the city in the favorites list, then clicking the star to remove from the favorites.

Underneath, there is a StorageService (which is represented by an interface or protocol) defined that stores data locally.  It is hooked into the entire system using whatever mechanism is normal for the platform.  For instance, in Android, it is a class called FilePreferencesService that writes to shared preferences and is hooked in using dependency injection with Koin.

## Your Job

Your first job is to ensure that you can compile and run the application "as is".  This will ensure that your environment is correctly set up for the tasks ahead.

Write a backend that loads and stores the favorites list in the Azure cloud.

* Your backend must support multiple users, as defined by the Facebook Access Token.
* Any secrets used must be stored securely.
* You must be able to report on how many distinct users are using the service.
* You must be able to report on:
  * How many unique daily / weekly / monthly users you have.
  * How many times a typical user changes the list.
* You must be able to capture errors in the network requests for later diagnosis.

Deploying your backend:

* Your backend must be capable of being deployed automatically.
* Your backend must support repeated deployments without affecting the service.

**SPECIAL ADDITION**

If you are a member of the SDK team, you are not allowed to use "tribal knowledge".  You must use the available documentation, blogs, samples, etc.

## Test Users

The following test users are available.  See Adrian for the password.

|User ID        |Email Address                  |
|---------------|-------------------------------|
|100749868064594|tqpdqraiep_1573921333@tfbnw.net|
|101217064683622|xzcattfhsa_1573921329@tfbnw.net|
|105375930929741|elbngyqapq_1573921334@tfbnw.net|
|106560850809995|asbtdqhuro_1573921332@tfbnw.net|

## Available apps

The following apps have been completed:

* Android - Kotlin
  * Uses Android Architecture Components, Koin for dependency injection, and Dark Sky API for getting the weather.
