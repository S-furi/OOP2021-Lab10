package it.unibo.oop.lab.lambda.ex02;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.OptionalDouble;
import java.util.Set;
import java.util.function.ToDoubleFunction;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 *
 */
public final class MusicGroupImpl implements MusicGroup {

    private final Map<String, Integer> albums = new HashMap<>();
    private final Set<Song> songs = new HashSet<>();

    @Override
    public void addAlbum(final String albumName, final int year) {
        this.albums.put(albumName, year);
    }

    @Override
    public void addSong(final String songName, final Optional<String> albumName, final double duration) {
        if (albumName.isPresent() && !this.albums.containsKey(albumName.get())) {
            throw new IllegalArgumentException("invalid album name");
        }
        this.songs.add(new MusicGroupImpl.Song(songName, albumName, duration));
    }

    @Override
    public Stream<String> orderedSongNames() {
        return this.songs.stream().map(t -> t.getSongName()).sorted();
    }

    @Override
    public Stream<String> albumNames() {
        final List<String> names = new ArrayList<>(this.albums.size());
        this.albums.forEach((k, v) -> {
            names.add(k);
        });
        return names.stream();
    }

    @Override
    public Stream<String> albumInYear(final int year) {
        final List<String> alb = new ArrayList<>(this.albums.size());
        this.albums.forEach((k, v) -> {
            if (v == year) {
                alb.add(k);
            }
        });
        return alb.stream();
    }

    @Override
    public int countSongs(final String albumName) {
        int count = (int) this.songs.stream().filter(s -> s.getAlbumName().isPresent())
                .filter(s -> s.getAlbumName().get().equals(albumName)).count();
        return count;
    }

    @Override
    public int countSongsInNoAlbum() {
        int count = (int) this.songs.stream().filter(s -> s.getAlbumName().isEmpty()).count();
        return count;
    }

    @Override
    public OptionalDouble averageDurationOfSongs(final String albumName) {
        List<Song> list = this.songs.stream()
        .filter(s -> s.getAlbumName().isPresent())
        .filter(s -> s.getAlbumName().get().equals(albumName))
        .collect(Collectors.toList());

        Double res = 0.0;
        for (Song song : list) {
            res += song.duration;
        }
        res = res / list.size();

        return OptionalDouble.of(res);
    }

    @Override
    public Optional<String> longestSong() {

        Optional<String> max = this.songs.stream()
                .max(Comparator.comparingDouble(t -> t.getDuration()))
                .map(t -> t.getSongName());

        return max;
    }

    @Override
    public Optional<String> longestAlbum() {
        String max = this.albums
                .entrySet()
                .stream()
                .max((e1, e2) -> Integer.compare(e1.getValue(), e2.getValue()))
                .get()
                .getKey();
        return Optional.of(max);
    }

    private static final class Song {

        private final String songName;
        private final Optional<String> albumName;
        private final double duration;
        private int hash;

        Song(final String name, final Optional<String> album, final double len) {
            super();
            this.songName = name;
            this.albumName = album;
            this.duration = len;
        }

        public String getSongName() {
            return songName;
        }

        public Optional<String> getAlbumName() {
            return albumName;
        }

        public double getDuration() {
            return duration;
        }

        @Override
        public int hashCode() {
            if (hash == 0) {
                hash = songName.hashCode() ^ albumName.hashCode() ^ Double.hashCode(duration);
            }
            return hash;
        }

        @Override
        public boolean equals(final Object obj) {
            if (obj instanceof Song) {
                final Song other = (Song) obj;
                return albumName.equals(other.albumName) && songName.equals(other.songName)
                        && duration == other.duration;
            }
            return false;
        }

        @Override
        public String toString() {
            return "Song [songName=" + songName + ", albumName=" + albumName + ", duration=" + duration + "]";
        }

    }

}
