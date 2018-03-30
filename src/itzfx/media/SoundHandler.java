/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package itzfx.media;

import java.net.URISyntaxException;
import java.net.URL;
import java.util.EnumMap;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;

/**
 *
 * @author prem
 */
public class SoundHandler implements AutoCloseable {

    private final EnumMap<Sounds, MediaPlayer> soundPlayers;

    public SoundHandler() {
        soundPlayers = new EnumMap<>(Sounds.class);
        soundPlayers.put(Sounds.AUTON_OVER, generateMediaPlayer(retrieveSoundFile("Pause.wav")));
        soundPlayers.put(Sounds.END, generateMediaPlayer(retrieveSoundFile("Stop.wav")));
        soundPlayers.put(Sounds.START, generateMediaPlayer(retrieveSoundFile("Start.wav")));
        soundPlayers.put(Sounds.WARNING, generateMediaPlayer(retrieveSoundFile("Warning.wav")));
    }
    
    private URL retrieveSoundFile(String fileName) {
        return SoundHandler.class.getResource("/itzfx/media/sound/" + fileName);
    }

    private MediaPlayer generateMediaPlayer(URL url) {
        try {
            MediaPlayer mp = new MediaPlayer(new Media(url.toURI().toString()));
            mp.setOnEndOfMedia(() -> {
                mp.seek(mp.getStartTime());
            });
            return mp;
        } catch (URISyntaxException ex) {
            return null;
        }
    }
    
    public void play(Sounds sound) {
        soundPlayers.get(sound).play();
    }
    
    @Override
    public void close() {
        soundPlayers.values().forEach(MediaPlayer::dispose);
    }
}
