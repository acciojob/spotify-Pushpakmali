package com.driver;

import java.util.*;

import org.springframework.stereotype.Service;

@Service
public class SpotifyService {

    //Auto-wire will not work in this case, no need to change this and add autowire

    SpotifyRepository spotifyRepository = new SpotifyRepository();

    public User createUser(String name, String mobile){
        User user = spotifyRepository.createUser(name, mobile);
        return user;
    }

    public Artist createArtist(String name) {
        Artist artist = spotifyRepository.createArtist(name);
        return artist;
    }

    public Album createAlbum(String title, String artistName) {
        Album album = spotifyRepository.createAlbum(title, artistName);
        return album;
    }

    public Song createSong(String title, String albumName, int length) throws Exception {
        Optional<Album> albumOp = this.getAlbumByName(albumName);
        if(albumOp.isEmpty()){
            throw new Exception("Album does not exist");
        }
        Song song = spotifyRepository.createSong(title, albumName, length);
        return song;
    }

    public Playlist createPlaylistOnLength(String mobile, String title, int length) throws Exception {
        Playlist playlist = spotifyRepository.createPlaylistOnLength(mobile, title, length);
        return playlist;
    }

    public Playlist createPlaylistOnName(String mobile, String title, List<String> songTitles) throws Exception {
        Playlist playlist = spotifyRepository.createPlaylistOnName(mobile,title, songTitles);
        return playlist;
    }

    public Playlist findPlaylist(String mobile, String playlistTitle) throws Exception {
        Playlist playlist = spotifyRepository.findPlaylist(mobile, playlistTitle);
        return playlist;
    }

    public Song likeSong(String mobile, String songTitle) throws Exception {
        return spotifyRepository.likeSong(mobile, songTitle);
    }

    public String mostPopularArtist() {
        return spotifyRepository.mostPopularArtist();
    }

    public String mostPopularSong() {
        return spotifyRepository.mostPopularSong();
    }

    public Optional<Album> getAlbumByName(String albumName) {
        return spotifyRepository.getAlbum(albumName);
    }
}
