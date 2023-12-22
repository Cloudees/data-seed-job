package playlist_seed

import "go.uber.org/zap"

type Playlist struct {
	Id     string   `json:"id"`
	Name   string   `json:"name"`
	Videos []Videos `json:"videos"`
}

type Videos struct {
	Id string `json:"id"`
}

type PlaylistSeed struct {
	Logger *zap.SugaredLogger
}
