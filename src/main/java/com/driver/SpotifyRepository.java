package com.driver;

import java.util.*;

import org.springframework.stereotype.Repository;

@Repository
public class SpotifyRepository {
    public HashMap<Artist, List<Album>> artistAlbumMap;
    public HashMap<Album, List<Song>> albumSongMap;
    public HashMap<Playlist, List<Song>> playlistSongMap;
    public HashMap<Playlist, List<User>> playlistListenerMap;
    public HashMap<User, Playlist> creatorPlaylistMap;
    public HashMap<User, List<Playlist>> userPlaylistMap;
    public HashMap<Song, List<User>> songLikeMap;

    public List<User> users;
    public List<Song> songs;
    public List<Playlist> playlists;
    public List<Album> albums;
    public List<Artist> artists;

    public SpotifyRepository(){
        //To avoid hitting apis multiple times, initialize all the hashmaps here with some dummy data
        artistAlbumMap = new HashMap<>();
        albumSongMap = new HashMap<>();
        playlistSongMap = new HashMap<>();
        playlistListenerMap = new HashMap<>();
        creatorPlaylistMap = new HashMap<>();
        userPlaylistMap = new HashMap<>();
        songLikeMap = new HashMap<>();

        users = new ArrayList<>();
        songs = new ArrayList<>();
        playlists = new ArrayList<>();
        albums = new ArrayList<>();
        artists = new ArrayList<>();
    }

    public User createUser(String name, String mobile) {
        User user = new User(name, mobile);
        users.add(user);
        return user;
    }

    public Artist createArtist(String name) {
        Artist artist = new Artist(name);
        artists.add(artist);
        return artist;
    }

    public Album createAlbum(String title, String artistName) {
        if(!artists.contains(artistName)){
            this.createArtist(artistName);
        }
        Artist artist = this.getArtistByName(artistName);

        Album album = new Album(title);
        albums.add(album);

        List<Album> prevAlbums = artistAlbumMap.getOrDefault(artist, new ArrayList<>());
        prevAlbums.add(album);
        artistAlbumMap.put(artist, prevAlbums);

        return album;
    }

    private Artist getArtistByName(String artistName) {
        for(Artist artist : this.artists){
            if(artist.getName().equals(artistName)){
                return artist;
            }
        }
        return null;
    }

    public Song createSong(String title, String albumName, int length) throws Exception{
        Song song = new Song(title, length);
        songs.add(song);

        Album album = this.getAlbum(albumName).get();
        List<Song> songs = albumSongMap.getOrDefault(album, new ArrayList<>());
        songs.add(song);
        albumSongMap.put(album, songs);

        return song;
    }

    public Playlist createPlaylistOnLength(String mobile, String title, int length) throws Exception {
        Optional<User> userOptional = this.getUserBymobile(mobile);
        if(userOptional.isEmpty()){
            throw new Exception("User does not exist");
        }

        User user = userOptional.get();
        Playlist playlist = new Playlist(title);
        playlists.add(playlist);
        creatorPlaylistMap.put(user, playlist);

        List<Song> songList = playlistSongMap.getOrDefault(playlist, new ArrayList<>());
        for(Song song : songs){
            if(song.getLength() == length){
                songList.add(song);
            }
        }
        playlistSongMap.put(playlist, songList);

        List<User> users = playlistListenerMap.getOrDefault(playlist,new ArrayList<>());
        users.add(user);
        playlistListenerMap.put(playlist, users);

        List<Playlist> playlistList = userPlaylistMap.getOrDefault(user, new ArrayList<>());
        playlistList.add(playlist);
        userPlaylistMap.put(user, playlistList);

        return playlist;
    }

    public Playlist createPlaylistOnName(String mobile, String title, List<String> songTitles) throws Exception {
        Optional<User> userOptional = this.getUserBymobile(mobile);
        if(userOptional.isEmpty()){
            throw new Exception("User does not exist");
        }
        User user = userOptional.get();
        Playlist playlist = new Playlist(title);
        playlists.add(playlist);
        creatorPlaylistMap.put(user, playlist);

        List<Song> songList = playlistSongMap.getOrDefault(playlist, new ArrayList<>());
        for(String songTitle : songTitles){
            Optional<Song> songOp = this.getSongByTitle(songTitle);
            if(songOp.isPresent()){
                songList.add(songOp.get());
            }
        }
        playlistSongMap.put(playlist, songList);

        List<User> userList = playlistListenerMap.getOrDefault(playlist, new ArrayList<>());
        userList.add(user);
        playlistListenerMap.put(playlist, userList);

        List<Playlist> playlistList = userPlaylistMap.getOrDefault(playlist, new ArrayList<>());
        playlistList.add(playlist);
        userPlaylistMap.put(user, playlistList);

        return playlist;
    }

    public Optional<Song> getSongByTitle(String songTitle) {
        for(Song song : songs){
            if(song.getTitle().equals(songTitle)){
                return Optional.of(song);
            }
        }
        return Optional.empty();
    }

    public Playlist findPlaylist(String mobile, String playlistTitle) throws Exception {
        Optional<User> userOp = this.getUserBymobile(mobile);
        if(userOp.isEmpty()){
            throw new Exception("User does not exist");
        }

        Optional<Playlist> playlistOp = this.getPlaylistByTitle(playlistTitle);
        if(playlistOp.isEmpty()){
            throw new Exception("Playlist does not exist");
        }

        User user = userOp.get();
        if(creatorPlaylistMap.containsKey(user)){
            if(creatorPlaylistMap.get(user).getTitle().equals(playlistTitle)){
                return playlistOp.get();
            }
        }

        if(playlistListenerMap.containsKey(playlistOp.get())){
            List<User> userList = playlistListenerMap.get(playlistOp.get());
            for(User listner : users){
                if(listner.getMobile().equals(mobile)){
                    return playlistOp.get();
                }
            }
        }

        List<User> userList = playlistListenerMap.getOrDefault(playlistOp.get(), new ArrayList<>());
        userList.add(userOp.get());
        playlistListenerMap.put(playlistOp.get(), userList);

        List<Playlist> playlistList = userPlaylistMap.getOrDefault(userOp.get(), new ArrayList<>());
        playlistList.add(playlistOp.get());
        userPlaylistMap.put(userOp.get(), playlistList);

        return playlistOp.get();

    }

    public Song likeSong(String mobile, String songTitle) throws Exception {
        User user = null;
        for(User user1:users){
            if(user1.getMobile()==mobile){
                user=user1;
                break;
            }
        }
        if(user==null){
            throw new Exception("User does not exist");
        }

        Song song = null;
        for(Song song1:songs){
            if(song1.getTitle()==songTitle){
                song=song1;
                break;
            }
        }

        if (song==null) {
            throw new Exception("Song does not exist");
        }

        if(songLikeMap.containsKey(song)){
            List<User> list = songLikeMap.get(song);
            if(list.contains(user)) {
                return song;
            }
            else{
                int likes = song.getLikes()+1;
                song.setLikes(likes);
                list.add(user);
                songLikeMap.put(song, list);

                Album album=null;
                for(Album album1:albumSongMap.keySet()) {
                    List<Song> songList = albumSongMap.get(album1);
                    if(songList.contains(song)) {
                        album = album1;
                        break;
                    }
                }
                Artist artist = null;
                for(Artist artist1:artistAlbumMap.keySet()){
                    List<Album> albumList = artistAlbumMap.get(artist1);
                    if (albumList.contains(album)){
                        artist = artist1;
                        break;
                    }
                }
                int likes1 = artist.getLikes() +1;
                artist.setLikes(likes1);
                artists.add(artist);
                return song;
            }
        }
        else {
            int likes = song.getLikes() + 1;
            song.setLikes(likes);
            List<User> list = new ArrayList<>();
            list.add(user);
            songLikeMap.put(song, list);

            Album album = null;
            for (Album album1 : albumSongMap.keySet()) {
                List<Song> songList = albumSongMap.get(album1);
                if (songList.contains(song)) {
                    album = album1;
                    break;
                }
            }

            Artist artist = null;
            for (Artist artist1 : artistAlbumMap.keySet()) {
                List<Album> albumList = artistAlbumMap.get(artist1);
                if (albumList.contains(album)) {
                    artist = artist1;
                    break;
                }
            }
            int likes1 = artist.getLikes() + 1;
            artist.setLikes(likes1);
            artists.add(artist);

            return song;
        }
    }

    public String mostPopularArtist() {
        int max = 0;
        Artist artist1=null;

        for(Artist artist:artists){
            if(artist.getLikes()>=max){
                artist1=artist;
                max = artist.getLikes();
            }
        }
        if(artist1==null) {
            return null;
        }
        else {
            return artist1.getName();
        }
    }

    public String mostPopularSong() {
        int max = 0;
        Song song = null;

        for (Song song1 : songLikeMap.keySet()) {
            if (song1.getLikes() >= max) {
                song = song1;
                max = song1.getLikes();
            }
        }

        if(song == null){
            return null;
        }
        else{
            return song.getTitle();
        }
    }

    public Optional<Album> getAlbum(String albumName) {
        for(Album album : albums){
            if(album.getTitle().equals(albumName)){
                return Optional.of(album);
            }
        }
        return Optional.empty();
    }

    public Optional<User> getUserBymobile(String mobile) {
        for(User user : users){
            if(user.getMobile().equals(mobile)){
                return Optional.of(user);
            }
        }
        return Optional.empty();
    }

    public Optional<Playlist> getPlaylistByTitle(String playlistTitle) {
        for(Playlist playlist : playlists){
            if(playlist.getTitle().equals(playlistTitle)){
                return Optional.of(playlist);
            }
        }
        return Optional.empty();
    }
}
