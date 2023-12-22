package videos_seed

import (
	"context"
	"encoding/json"
	"github.com/go-redis/redis/v8"
	"io/ioutil"
	"os"
)

func (videosSeed *VideosSeed) SeedVideosData(client *redis.Client, ctx context.Context) {
	videos, err := videosSeed.getVideosFromJson()
	if err != nil {
		videosSeed.Logger.Errorw("Error while trying to get videos from JSON", err)
		return
	}
	videosSeed.Logger.Infow("Videos fetched successfully from the JSON", videos)
	for _, video := range videos {
		videoJson, err := json.Marshal(video)
		if err != nil {
			videosSeed.Logger.Errorw("Error while marshalling video to JSON", err)
			return
		}
		err = client.Set(ctx, video.Id, videoJson, 0).Err()
		if err != nil {
			videosSeed.Logger.Errorw("Error while Updating database with video", err)
			return
		}
	}
}

func (videosSeed *VideosSeed) getVideosFromJson() ([]Videos, error) {
	// Open our jsonFile
	jsonFile, err := os.Open("videos-seed/videos.json")
	// if we os.Open returns an error then handle it
	if err != nil {
		videosSeed.Logger.Errorw("Error while opening the videos json file", err)
		return []Videos{}, err
	}
	videosSeed.Logger.Infow("Successfully Opened videos.json")
	// defer the closing of our jsonFile so that we can parse it later on
	defer func(jsonFile *os.File) {
		err := jsonFile.Close()
		if err != nil {
			videosSeed.Logger.Errorw("Error while closing the videos json file", err)
		}
	}(jsonFile)

	byteValue, _ := ioutil.ReadAll(jsonFile)

	var videos []Videos

	err = json.Unmarshal(byteValue, &videos)
	if err != nil {
		videosSeed.Logger.Errorw("Error while unmarshalling the json file", err)
		return []Videos{}, err
	}
	return videos, nil
}
