FROM golang:1.19-alpine as build
RUN apk add --no-cache git

WORKDIR /src

COPY . /src/
RUN go mod tidy


RUN go build -o app main.go

FROM alpine:3.12

WORKDIR /app
RUN mkdir /videos-seed && mkdir playlist-seed
COPY --from=build /src/app ./app
COPY --from=build /src/videos-seed/videos.json ./videos-seed/videos.json
COPY --from=build /src/playlist-seed/playlists.json ./playlist-seed/playlists.json
CMD ["./app"]