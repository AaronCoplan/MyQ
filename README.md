### Requirements to Build from Source

1. `google-services.json` file in the `app/` directory.  This can be generated from the Firebase website by following [this guide][1]
2. `spotify.xml` file under the `values/` folder within the `app/` directory tree.  You will first need to generate a client id by creating a Spotify App within their [developer portal][2].  This file needs to contain two string resources, as follows:

```<string name="spotify_client_id" translatable="false">YOUR_CLIENT_ID_HERE</string>
<string name="spotify_redirect_uri" translatable="false">yourschema://yourcallback</string>```

[1]: https://support.google.com/firebase/answer/7015592?hl=en
[2]: https://developer.spotify.com/dashboard/
