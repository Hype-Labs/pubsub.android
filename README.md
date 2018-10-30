![alt tag](https://hypelabs.io/static/img/NQMAnSZ.jpg)
![alt tag](https://hypelabs.io/static/img/logo200x.png)

[Hype](https://hypelabs.io/?r=10) is an SDK for cross-platform peer-to-peer communication with mesh networking. Hype works even without Internet access, connecting devices via other communication channels such as Bluetooth, Wi-Fi direct, and Infrastructural Wi-Fi.

The Hype SDK has been designed by [Hype Labs](http://hypelabs.io/?r=10). It is currently available for multiple platforms.

You can [start using Hype](http://hypelabs.io/?r=10) today.


## Project

This project is a decentralized peer-to-peer publisher-subscriber system powered by the HypeSDK, for the Android platform. Due to its decentralized nature this system is more robust than traditional publisher-subscriber systems that usually requires a centralized broker to serve as intermediary between publishers and subscribers. You can find out a detailed description of the system in this [article](https://medium.com/@hypelabs.io). You can also see a demonstration video of the system [here](https://www.youtube.com/watch?v=2fMwe3q1NYc&t=2s).



## Setup

This demo does not work out of the box. The following are the necessary steps to configure it:

 1. [Download](https://hypelabs.io/downloads/?r=10) the SDK binary for Android
 2. Extract it, and drag it to the *hype* named folder inside the project root folder
 3. Access the [apps](https://hypelabs.io/apps/?r=10) page and create a new app
 4. Name the app and press "Create new app"
 5. Go to the app settings
 6. Copy the identifier under `App ID`
 7. With the project open on Android Studio, in the file `HpsConstants.java`, find the line that reads `static public final String APP_IDENTIFIER = "{{app_identifier))";`
 8. Replace `{{app_identifier}}` by the App ID you just copied
 9. Go back to the app settings
 10. This time, copy the `Access Token`, under the Authorization group
 11. Open the same file, `HpsConstants.java`, find the line that reads `static public final String ACCESS_TOKEN = "{{access_token))";`
 12. Replace `{{access_token}}` by the token you just copied instead

You should be ready to go! 

Please note that the app can **ONLY be run on physical hardware devices**. Running on *emulators will not work* due to APIs related to certain features being unsupported.

If you run into trouble, feel free to reach out to our [community](https://hypelabs.io/community/?r=10) or browse our other [support](https://hypelabs.io/support/?r=10) options. Also keep in mind our project templates on the [apps](https://hypelabs.io/apps/?r=10) page for demos working out of the box.

## Other platforms

Besides Android, this project is available for the following platforms:

- [iOS](https://github.com/Hype-Labs/pubsub.ios) <br>
- [Linux](https://github.com/Hype-Labs/pubsub.linux)

## Demonstration

Click [here](https://www.youtube.com/watch?v=2fMwe3q1NYc) to see a demonstration video, on the iOS platform.

## License

This project is MIT-licensed.

## Follow us

Follow us on [twitter](http://www.twitter.com/hypelabstech) and [facebook](http://www.facebook.com/hypelabs.io). We promise to keep you up to date!

## App Icon Credits

<div>Icons made by <a href="http://www.freepik.com" title="Freepik">Freepik</a> from <a href="https://www.flaticon.com/" title="Flaticon">www.flaticon.com</a> is licensed by <a href="http://creativecommons.org/licenses/by/3.0/" title="Creative Commons BY 3.0" target="_blank">CC 3.0 BY</a></div>
