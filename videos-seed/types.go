package videos_seed

import "go.uber.org/zap"

type Videos struct {
	Id          string `json:"id"`
	Title       string `json:"title"`
	Description string `json:"description"`
	ImageURL    string `json:"imageurl"`
	Url         string `json:"url"`
}

type VideosSeed struct {
	Logger *zap.SugaredLogger
}
