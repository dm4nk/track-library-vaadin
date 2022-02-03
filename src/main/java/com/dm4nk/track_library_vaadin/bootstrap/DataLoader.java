package com.dm4nk.track_library_vaadin.bootstrap;

import com.dm4nk.track_library_vaadin.converters.ByteArrayToWrappedByteArray;
import com.dm4nk.track_library_vaadin.domain.Album;
import com.dm4nk.track_library_vaadin.domain.Author;
import com.dm4nk.track_library_vaadin.domain.Genre;
import com.dm4nk.track_library_vaadin.domain.Track;
import com.dm4nk.track_library_vaadin.service.AlbumService;
import com.dm4nk.track_library_vaadin.service.AuthorService;
import com.dm4nk.track_library_vaadin.service.GenreService;
import com.dm4nk.track_library_vaadin.service.TrackService;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalTime;
import java.util.Arrays;
import java.util.List;

/**
 * Class meant to load data t database
 */
@Component
public class DataLoader implements ApplicationListener<ContextRefreshedEvent> {
    private final TrackService trackService;
    private final GenreService genreService;
    private final AuthorService authorService;
    private final AlbumService albumService;

    public DataLoader(TrackService trackService, GenreService genreService, AuthorService authorService, AlbumService albumService) {
        this.trackService = trackService;
        this.genreService = genreService;
        this.authorService = authorService;
        this.albumService = albumService;
    }

    /**
     * Loads data every time application restarts
     */
    @Override
    public void onApplicationEvent(ContextRefreshedEvent event) {
        try {
            loadData();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void loadData() throws IOException {

        Genre rap = genreService.findByName("rap").orElse(null);
        Genre russian = genreService.findByName("русская").orElse(null);
        Genre pop = genreService.findByName("pop").orElse(null);

        Author LilNasX = authorService.findByName("Lil Nas X").orElse(null);
        Author Lizer = authorService.findByName("Лизер").orElse(null);
        Author TheScore = authorService.findByName("The Score").orElse(null);
        Author LilPeep = authorService.findByName("Lil Peep").orElse(null);
        Author XXXXTENTACION = authorService.findByName("XXXXTENTACION").orElse(null);

        Album Montero = Album.builder().name("Montero").author(LilNasX).build();
        Album Youth = Album.builder().name("Молодость, Ч.1").author(Lizer).build();
        Album Atlas = Album.builder().name("Atlas").author(TheScore).build();
        Album crybaby = Album.builder().name("crybaby").author(LilPeep).build();
        Album Revenge = Album.builder().name("Revenge").author(XXXXTENTACION).build();
        Album NotAngel = Album.builder().name("Не Ангел").author(Lizer).build();

        LilNasX.getAlbums().add(Montero);
        Lizer.getAlbums().addAll(List.of(Youth, NotAngel));
        TheScore.getAlbums().add(Atlas);
        LilPeep.getAlbums().add(crybaby);
        XXXXTENTACION.getAlbums().add(Revenge);

        Montero = albumService.save(Montero);
        Youth = albumService.save(Youth);
        Atlas = albumService.save(Atlas);
        crybaby = albumService.save(crybaby);
        Revenge = albumService.save(Revenge);
        NotAngel = albumService.save(NotAngel);

        String filePathMontero = "src/main/resources/static/tracks/MONTERO.wav";
        byte[] dataMontero = Files.readAllBytes(Paths.get(filePathMontero));
        Track track1 = Track.builder()
                .name("Montero")
                .author(LilNasX)
                .album(Montero)
                .duration(LocalTime.of(0, 2, 18))
                .genre(rap)
                .track(ByteArrayToWrappedByteArray.convert(dataMontero))
                .build();

        String filePathCosmos = "src/main/resources/static/tracks/Космос.wav";
        byte[] dataCosmos = Files.readAllBytes(Paths.get(filePathCosmos));
        Track track2 = Track.builder()
                .name("Космос")
                .author(Lizer)
                .album(Youth)
                .duration(LocalTime.of(0, 3, 40))
                .genre(russian)
                .track(ByteArrayToWrappedByteArray.convert(dataCosmos))
                .build();

        String filePathRevolution = "src/main/resources/static/tracks/Score.wav";
        byte[] dataRevolution = Files.readAllBytes(Paths.get(filePathRevolution));
        Track track3 = Track.builder()
                .name("Revolution")
                .author(TheScore)
                .album(Atlas)
                .duration(LocalTime.of(0, 3, 52))
                .genre(pop)
                .track(ByteArrayToWrappedByteArray.convert(dataRevolution))
                .build();

        String filePathDriveway = "src/main/resources/static/tracks/driveway.wav";
        byte[] dataDriveway = Files.readAllBytes(Paths.get(filePathDriveway));
        Track track4 = Track.builder()
                .name("deiveway")
                .author(LilPeep)
                .album(crybaby)
                .duration(LocalTime.of(0, 2, 39))
                .genre(rap)
                .track(ByteArrayToWrappedByteArray.convert(dataDriveway))
                .build();

        String filePathRevenge = "src/main/resources/static/tracks/Score.wav";
        byte[] dataRevenge = Files.readAllBytes(Paths.get(filePathRevenge));
        Track track5 = Track.builder()
                .name("Revenge")
                .author(XXXXTENTACION)
                .album(Revenge)
                .duration(LocalTime.of(0, 2, 1))
                .genre(rap)
                .track(ByteArrayToWrappedByteArray.convert(dataRevenge))
                .build();

        /////////////////////new/////////////////////////////
        String filePathToYoung = "src/main/resources/static/tracks/Молодым.wav";
        byte[] dataToYoung = Files.readAllBytes(Paths.get(filePathToYoung));
        Track ToYoung = Track.builder()
                .name("Молодым")
                .author(Lizer)
                .album(NotAngel)
                .duration(LocalTime.of(0, 3, 29))
                .genre(russian)
                .track(ByteArrayToWrappedByteArray.convert(dataToYoung))
                .build();

        String filePathBankroll = "src/main/resources/static/tracks/Бэнкролл.wav";
        byte[] dataBankroll = Files.readAllBytes(Paths.get(filePathBankroll));
        Track Bankroll = Track.builder()
                .name("Бэнкролл")
                .author(Lizer)
                .album(NotAngel)
                .duration(LocalTime.of(0, 4, 58))
                .genre(russian)
                .track(ByteArrayToWrappedByteArray.convert(dataBankroll))
                .build();

        String filePathKindness = "src/main/resources/static/tracks/Добро.wav";
        byte[] dataKindness = Files.readAllBytes(Paths.get(filePathKindness));
        Track Kindness = Track.builder()
                .name("Добро")
                .author(Lizer)
                .album(Youth)
                .duration(LocalTime.of(0, 4, 8))
                .genre(russian)
                .track(ByteArrayToWrappedByteArray.convert(dataKindness))
                .build();

        String filePathFastGrowth = "src/main/resources/static/tracks/Быстро повзрослел.wav";
        byte[] dataFastGrowth = Files.readAllBytes(Paths.get(filePathFastGrowth));
        Track FastGrowth = Track.builder()
                .name("Быстро повзрослел")
                .author(Lizer)
                .album(Youth)
                .duration(LocalTime.of(0, 2, 48))
                .genre(russian)
                .track(ByteArrayToWrappedByteArray.convert(dataFastGrowth))
                .build();

        LilNasX.getTracks().add(track1);
        Lizer.getTracks().addAll(List.of(track2, ToYoung, Bankroll, Kindness, FastGrowth));
        TheScore.getTracks().add(track3);
        LilPeep.getTracks().add(track4);
        XXXXTENTACION.getTracks().add(track5);

        pop.getTracks().add(track3);
        rap.getTracks().addAll(Arrays.asList(track1, track4, track5));
        russian.getTracks().addAll(List.of(track2, ToYoung, Bankroll, Kindness, FastGrowth));

        trackService.save(track1);
        trackService.save(track2);
        trackService.save(track3);
        trackService.save(track4);
        trackService.save(track5);
        trackService.save(ToYoung);
        trackService.save(Bankroll);
        trackService.save(Kindness);
        trackService.save(FastGrowth);
    }
}
