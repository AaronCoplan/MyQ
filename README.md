### MyQ

MyQ is an Android application that enables multiple people to contribute to a shared queue of music, which plays on one person's phone. While the Spotify App itself only allows the person playing music to modify the queue, our app provides the ability for multiple people to contribute.

The host creates the queue and shares the unique join code with the people wishing to join. Each person may use the search functionality to look up songs to add to the queue. The songs in the queue are played in order on the host's device and play, pause, and skip buttons are provided.

The app uses the Spotify Web API for searches and the Spotify SDK / Spotify App Remote for playing the music on the host device. Additionally, the app uses Firebase for its authentication and real time database.

### Requirements to Build from Source

1. `google-services.json` file in the `app/` directory.  This can be generated from the Firebase website by following [this guide][1]
2. `spotify.xml` file under the `values/` folder within the `app/` directory tree.  You will first need to generate a client id by creating a Spotify App within their [developer portal][2].  This file needs to contain two string resources, as follows:

```xml
<string name="spotify_client_id" translatable="false">YOUR_CLIENT_ID_HERE</string>
<string name="spotify_redirect_uri" translatable="false">yourschema://yourcallback</string>
```

[1]: https://support.google.com/firebase/answer/7015592?hl=en
[2]: https://developer.spotify.com/dashboard/
