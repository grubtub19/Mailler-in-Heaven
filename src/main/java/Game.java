public class Game {

    private static Game game = new Game();
    TimeManager time_manager;
    ImageManager image_manager;
    AudioManager audio_manager;
    PrintManager print_manager;

    GunFactory gun_factory;



    private Game() {
        time_manager = new TimeManager();
        image_manager = new ImageManager();
        audio_manager = new AudioManager();
        print_manager = new PrintManager();

        gun_factory = new GunFactory();

    }

    public static Game get() {
        return game;
    }
}
