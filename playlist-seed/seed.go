package playlist_seed

import (
	"context"
	"encoding/json"
	"github.com/go-redis/redis/v8"
	"io/ioutil"
	"os"
)

func (playlistSeed *PlaylistSeed) SeedPlaylistsData(client *redis.Client, ctx context.Context) {
	playlistsJson, err := playlistSeed.getPlaylistsFromJson()
	if err != nil {
		playlistSeed.Logger.Errorw("Error while reading playlists from JSON", err)
	}
	playlistSeed.Logger.Infow("Successfully read the data from JSON file", playlistsJson)
	err = client.Set(ctx, "playlists", playlistsJson, 0).Err()
	if err != nil {
		playlistSeed.Logger.Errorw("Error while Updating Redis database", err)
		return
	}
}

func (playlistSeed *PlaylistSeed) getPlaylistsFromJson() ([]byte, error) {
	// Open our jsonFile
	jsonFile, err := os.Open("playlist-seed/playlists.json")
	// if we os.Open returns an error then handle it
	if err != nil {
		playlistSeed.Logger.Errorw("Error while opening the playlists JSON file", err)
		return []byte{}, err
	}
	playlistSeed.Logger.Infow("Successfully opened the JSON file")
	// defer the closing of our jsonFile so that we can parse it later on
	defer func(jsonFile *os.File) {
		err := jsonFile.Close()
		if err != nil {
			playlistSeed.Logger.Errorw("Error while closing the playlists JSON file", err)
		}
	}(jsonFile)

	byteValue, _ := ioutil.ReadAll(jsonFile)

	var playlists []Playlist

	err = json.Unmarshal(byteValue, &playlists)
	if err != nil {
		playlistSeed.Logger.Errorw("Error while closing the unmarshalling the JSON file", err)
		return []byte{}, err
	}
	return byteValue, nil
}
