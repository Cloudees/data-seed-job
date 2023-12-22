package main

import (
	"context"
	"github.com/go-redis/redis/v8"
	playlist "github.com/kubees/data-seed-job/playlist-seed"
	videos "github.com/kubees/data-seed-job/videos-seed"
	"go.uber.org/zap"
	"os"
)

var microservice = os.Getenv("MICROSERVICE")
var redisHost = os.Getenv("REDIS_HOST")
var redisPort = os.Getenv("REDIS_PORT")
var password = os.Getenv("PASSWORD")
var client *redis.Client

func main() {
	r := redis.NewClient(&redis.Options{
		Addr:     redisHost + ":" + redisPort,
		DB:       0,
		Password: password,
	})
	client = r
	ctx := context.Background()
	logger, _ := zap.NewProduction()
	defer logger.Sync()
	sugar := logger.Sugar()
	if microservice == "playlist" {
		sugar.Infow("Seeding Data into playlists database")
		playlistSeed := playlist.PlaylistSeed{
			Logger: sugar,
		}
		playlistSeed.SeedPlaylistsData(client, ctx)
	} else if microservice == "videos" {
		sugar.Infow("Seeding Data into videos database")
		videosSeed := videos.VideosSeed{
			Logger: sugar,
		}
		videosSeed.SeedVideosData(client, ctx)
	}
}
