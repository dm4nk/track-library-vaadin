package com.dm4nk.track_library_vaadin.bootstrap;

import com.dm4nk.track_library_vaadin.converters.ByteArrayToWrappedByteArray;
import com.dm4nk.track_library_vaadin.domain.Track;
import com.dm4nk.track_library_vaadin.repositiry.GenreRepository;
import com.dm4nk.track_library_vaadin.repositiry.TrackRepository;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalTime;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

/**
 * Class meant to load data t database
 */
@Component
public class DataLoader implements ApplicationListener<ContextRefreshedEvent> {
    private final TrackRepository trackRepository;
    private final GenreRepository genreRepository;

    public DataLoader(TrackRepository trackRepository, GenreRepository genreRepository) {
        this.trackRepository = trackRepository;
        this.genreRepository = genreRepository;
    }

    /**
     * Loads data every time application restarts
     */
    @Override
    public void onApplicationEvent(ContextRefreshedEvent event) {
        try {
            trackRepository.saveAll(getTracks());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private List<Track> getTracks() throws IOException {

        Track track1 = Track.builder()
                .name("Montero")
                .author("Lil Nas X")
                .album("Montero")
                .duration(LocalTime.of(0, 2, 18))
                .genre(genreRepository.findByName("rap").orElse(null))
                .build();

        Track track2 = Track.builder()
                .name("Космос")
                .author("Лизер")
                .album("Молодость, Ч.1")
                .duration(LocalTime.of(0, 3, 40))
                .genre(genreRepository.findByName("русская").orElse(null))
                .build();

        Track track3 = Track.builder()
                .name("Revolution")
                .author("The Score")
                .album("Atlas")
                .duration(LocalTime.of(0, 3, 52))
                .genre(genreRepository.findByName("pop").orElse(null))
                .build();

        Track track4 = Track.builder()
                .name("deiveway")
                .author("Lil Peep")
                .album("crybaby")
                .duration(LocalTime.of(0, 2, 39))
                .genre(genreRepository.findByName("rap").orElse(null))
                .build();

        String filePath = "src/main/resources/static/tracks/Score.wav";

        byte[] data = Files.readAllBytes(Paths.get(filePath));

        Track track5 = Track.builder()
                .name("Revenge")
                .author("XXXTENTACION")
                .album("Revenge")
                .duration(LocalTime.of(0, 2, 1))
                .genre(genreRepository.findByName("rap").orElse(null))
                .track(ByteArrayToWrappedByteArray.convert(data))
                .build();


        return new LinkedList<>(Arrays.asList(track1, track2, track3, track4, track5));
    }
}
