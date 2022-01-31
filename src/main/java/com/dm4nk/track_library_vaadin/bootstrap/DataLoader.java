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
import java.util.ArrayList;
import java.util.Arrays;
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

        String filePathMontero = "src/main/resources/static/tracks/MONTERO.wav";
        byte[] dataMontero = Files.readAllBytes(Paths.get(filePathMontero));
        Track track1 = Track.builder()
                .name("Montero")
                .author("Lil Nas X")
                .album("Montero")
                .duration(LocalTime.of(0, 2, 18))
                .genre(genreRepository.findByName("rap").orElse(null))
                .track(ByteArrayToWrappedByteArray.convert(dataMontero))
                .build();

        String filePathCosmos = "src/main/resources/static/tracks/Космос.wav";
        byte[] dataCosmos = Files.readAllBytes(Paths.get(filePathCosmos));
        Track track2 = Track.builder()
                .name("Космос")
                .author("Лизер")
                .album("Молодость, Ч.1")
                .duration(LocalTime.of(0, 3, 40))
                .genre(genreRepository.findByName("русская").orElse(null))
                .track(ByteArrayToWrappedByteArray.convert(dataCosmos))
                .build();

        String filePathRevolution = "src/main/resources/static/tracks/Score.wav";
        byte[] dataRevolution = Files.readAllBytes(Paths.get(filePathRevolution));
        Track track3 = Track.builder()
                .name("Revolution")
                .author("The Score")
                .album("Atlas")
                .duration(LocalTime.of(0, 3, 52))
                .genre(genreRepository.findByName("pop").orElse(null))
                .track(ByteArrayToWrappedByteArray.convert(dataRevolution))
                .build();

        String filePathDriveway = "src/main/resources/static/tracks/driveway.wav";
        byte[] dataDriveway = Files.readAllBytes(Paths.get(filePathDriveway));
        Track track4 = Track.builder()
                .name("deiveway")
                .author("Lil Peep")
                .album("crybaby")
                .duration(LocalTime.of(0, 2, 39))
                .genre(genreRepository.findByName("rap").orElse(null))
                .track(ByteArrayToWrappedByteArray.convert(dataDriveway))
                .build();

        String filePathRevenge = "src/main/resources/static/tracks/Score.wav";
        byte[] dataRevenge = Files.readAllBytes(Paths.get(filePathRevenge));
        Track track5 = Track.builder()
                .name("Revenge")
                .author("XXXTENTACION")
                .album("Revenge")
                .duration(LocalTime.of(0, 2, 1))
                .genre(genreRepository.findByName("rap").orElse(null))
                .track(ByteArrayToWrappedByteArray.convert(dataRevenge))
                .build();

        return new ArrayList<>(Arrays.asList(track1, track2, track3, track4, track5));
    }
}
