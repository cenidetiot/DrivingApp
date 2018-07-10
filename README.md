# Welcome to DrivingApp

***DrivingApp is the smartest app to Mexican smart cities!***

### Discover all the features we offer you!

Our app is available on android mobile devices! Download  [here](https://play.google.com/store/apps/details?id=mx.edu.cenidet.app) now to get started! 

***All our features are designed for you security***

 - **Device Monitoring** : Constant monitoring of the devices that are within one of our institutions or affiliated companies.
 - **Accident Reports** : You can report car accidents to alert other users and improve the experience.
 - **Dynamic maps** : We show you the accidents or unusual situations that occurred near to you
 - **Special Algorithms** :  We develop algorithms to automatically detect unusual situations to keep you alert.

## "**Stop waiting. Start building**"

### Download this project 

    git clone https://github.com/cenidetiot/DrivingApp.git
### Open this project in Android Studio
Minimum SDK Version 16

###  Configure your services 

**Configure your own Orion**
Edit the file : *ngsi/src/main/assets/config.properties*

    http.host=http://130.206.113.226 // Change it for your Orion host 
    http.port=1026 // Change it form your Orion port
    http.apiversion=v2 // Change if for your Orion api version
   
   **Configure your smartsecurity service**
   Edit the file: *cenidetsdk/src/main/java/mx/edu/cenidet/cenidetsdk/utilities/ConfigServer.java*

    // Change it for your Service 
    http_host("https://smartsecurity-webservice.herokuapp.com")



**Configure your Firebase Cloud Messaging project**
If you want to recive our notifications using our algorithms to send the notificatios to the users, you don't need to configure anything.

But if you need to implement your own notifications, check [this guide](#) to configure them.


See all the documentation [here](#).

### Join us, we &hearts; new friends!
