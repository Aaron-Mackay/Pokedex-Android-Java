# Pokedex-Android-Java
Pokedex app showing the first (best) 151 Pokemon. Signed APK can be downloaded [here](https://github.com/Aaron-Mackay/Pokedex-Android-Java/raw/master/Pok%C3%A9dexAM.apk "Signed APK for installation").

## Features
* **Pokemon List** - RecyclerView of all pokemon in the original dex, with credit to the API of https://pokeapi.co/
* **Caching** - To improve user experience and allow offline use, data from each pokemon is stored locally once retrieved
* **Database** - Retrieved data is stored in a RoomDB for faster access
* **Search** - Search through pokemon by name, either narrowing down list or retrieving if an exact match
* **Filter** - Filter through list based on type combinations
