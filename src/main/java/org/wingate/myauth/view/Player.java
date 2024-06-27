/*
 * Copyright (C) 2024 util2
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.wingate.myauth.view;

import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Composite;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.RoundRectangle2D;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ShortBuffer;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.SourceDataLine;
import javax.swing.ImageIcon;
import javax.swing.JPanel;
import org.bytedeco.javacv.FFmpegFrameGrabber;
import org.bytedeco.javacv.Frame;
import org.bytedeco.javacv.Java2DFrameConverter;
import org.json.JSONArray;
import org.json.JSONObject;
import org.wingate.myauth.Viewer;

/**
 *
 * @author util2
 */
public class Player extends JPanel {
    
    private String root = null;
    
    private final Controller cPlay;
    private final Controller cPause;
    private final Controller cStop;
    
    private final Viewer viewer;
    
    private BufferedImage videoImage;
    private String media = null;
    
    private PlayerState playerState;
    private long mediaTotalTime = -1L;
    private long mediaCurrentTime = -1L;

    private volatile Thread playThread;

    public Player(Viewer viewer) {
        this.viewer = viewer;
        
        videoImage = null;
        playerState = PlayerState.NoStatus;

        root = new File("").getAbsolutePath();
        System.out.println(root);
        
        // Remplit les menus
        populate();
        
        cPlay = new Controller(32, 32);
        cPlay.setImageIcon(new ImageIcon(getClass().getResource("/images/32_timer_stuffs play dark.png")));
        
        cPause = new Controller(32, 32);
        cPause.setImageIcon(new ImageIcon(getClass().getResource("/images/32_timer_stuffs pause dark.png")));        
        
        cStop = new Controller(32, 32);
        cStop.setImageIcon(new ImageIcon(getClass().getResource("/images/32_timer_stuffs stop dark.png")));
        
        
        
        addMouseListener(new MouseAdapter(){
            @Override
            public void mouseClicked(MouseEvent e) {
                super.mouseClicked(e);
                
                if(playerState != PlayerState.Play && playerState != PlayerState.Pause
                        && e.getButton() == MouseEvent.BUTTON1){
                    for(Menu mnu : viewer.getSubMenus()){
                        if(mnu.getMenuImage() != null){
                            Rectangle r = new Rectangle(           // Rectangle area
                                    mnu.getMenuImageLocation(),
                                    mnu.getMenuImageDimension()
                            );
                            if(r.contains(e.getPoint())){
                                // Launch video
                                media = mnu.getVideo();
                                play();
                            }
                        }else if(mnu.getBackgroundImage() != null){
                            Rectangle r = new Rectangle(           // Rectangle area
                                    mnu.getBackgroundImage().getWidth(null),
                                    mnu.getBackgroundImage().getHeight(null)
                            );
                            if(r.contains(e.getPoint())){
                                // Do nothing
                            }
                        }                        
                    }
                }
                
                if(playerState != PlayerState.NoStatus && e.getButton() == MouseEvent.BUTTON1){
                    if(cPlay.getRectangle().contains(e.getPoint())){
                        play();
                    }else if(cPause.getRectangle().contains(e.getPoint())){
                        pause();
                    }else if(cStop.getRectangle().contains(e.getPoint())){
                        stop();
                    }
                }
            }
        });
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        
        Graphics2D g2d = (Graphics2D)g;
        
        if(videoImage != null){// && playerState == PlayerState.Play
            int w = videoImage.getWidth();
            int h = videoImage.getHeight();
            g2d.drawImage(videoImage, 0, 0, getWidth(), getHeight(), 0, 0, w, h, null);
            
            Composite oldComposite = g2d.getComposite();
            g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, .3f));
            
            double percent = (double)mediaCurrentTime / (double)mediaTotalTime;
            RoundRectangle2D rIn = new RoundRectangle2D.Double(
                    10, getHeight() - 20,
                    (getWidth() - 20) * percent, 6,
                    3, 3
            );
            g2d.setColor(Color.white);
            g2d.fill(rIn);
            
            RoundRectangle2D rOut = new RoundRectangle2D.Double(
                    10, getHeight() - 20,
                    getWidth() - 20, 6,
                    3, 3
            );
            g2d.setColor(Color.white);
            g2d.draw(rOut);            
            
            cPlay.setLocation(new Point(10, getHeight() - 20 - 2 - 32));
            g2d.drawImage(
                    cPlay.getImageIcon().getImage(),
                    cPlay.getLocation().x,
                    cPlay.getLocation().y,
                    null
            );
            
            cPause.setLocation(new Point(10 + 2 + 32, getHeight() - 20 - 2 - 32));
            g2d.drawImage(
                    cPause.getImageIcon().getImage(),
                    cPause.getLocation().x,
                    cPause.getLocation().y,
                    null
            );
            
            cStop.setLocation(new Point(10 + 2 + 32 + 2 + 32, getHeight() - 20 - 2 - 32));
            g2d.drawImage(
                    cStop.getImageIcon().getImage(),
                    cStop.getLocation().x,
                    cStop.getLocation().y,
                    null
            );
            
            g2d.setComposite(oldComposite);
        }else if(viewer.getSubMenus().isEmpty() == false){
            //==================================================================
            // MAIN MENU
            //==================================================================
            Menu mainMenu = null;            
            for(Menu m : viewer.getSubMenus()){
                if(m.getBackgroundImage() != null){
                    mainMenu = m;
                    break;
                }
            }
            if(mainMenu == null) return;
            int w = mainMenu.getBackgroundImage().getWidth(null);
            int h = mainMenu.getBackgroundImage().getHeight(null);
            g2d.drawImage(mainMenu.getBackgroundImage(), 0, 0, getWidth(), getHeight(), 0, 0, w, h, null);
            
            //==================================================================
            // SUB MENU
            //==================================================================
            for(Menu m : viewer.getSubMenus()){
                if(m.getMenuImage()!= null){
                    g2d.drawImage(
                            m.getMenuImage(),
                            m.getMenuImageLocation().x,
                            m.getMenuImageLocation().y,
                            m.getMenuImageDimension().width,
                            m.getMenuImageDimension().height,
                            null
                    );
                }
            }
        }
    }
    
    public void play(){
        play(new Chapter());
    }
    
    public void play(Chapter ch){
        if(playerState == PlayerState.NoStatus || playerState == PlayerState.Stop){
            playerState = PlayerState.Play;
            startPlayback();
        }else if(playerState == PlayerState.Pause){
            playerState = PlayerState.Play;
        }        
    }
    
    public void pause(){
        switch(playerState){
            case Play -> {
                playerState = PlayerState.Pause;
            }
            case Pause -> {
                playerState = PlayerState.Play;
            }
        }
    }
    
    public void stop(){
        if(playerState == PlayerState.Stop){
            playerState = PlayerState.NoStatus; // Avoid use of buttons (triggers) in menu
            mediaTotalTime = -1L;
            mediaCurrentTime = -1L;
            videoImage = null;
            repaint();
        }else{
            playerState = PlayerState.Stop;
            stopPlayback();
        }
    }
    
    @SuppressWarnings("SleepWhileInLoop")
    private void startPlayback(){
        playThread = new Thread(() -> {
            try {
                final FFmpegFrameGrabber grabber = new FFmpegFrameGrabber(media);
                grabber.start();
                mediaTotalTime = grabber.getLengthInTime();
                final PlaybackTimer playbackTimer;
                final SourceDataLine soundLine;
                if (grabber.getAudioChannels() > 0) {
                    final AudioFormat audioFormat = new AudioFormat(grabber.getSampleRate(), 16, grabber.getAudioChannels(), true, true);

                    final DataLine.Info info = new DataLine.Info(SourceDataLine.class, audioFormat);
                    soundLine = (SourceDataLine) AudioSystem.getLine(info);
                    soundLine.open(audioFormat);
                    soundLine.start();
                    playbackTimer = new PlaybackTimer(soundLine);
                } else {
                    soundLine = null;
                    playbackTimer = new PlaybackTimer();
                }

                final Java2DFrameConverter converter = new Java2DFrameConverter();

                final ExecutorService audioExecutor = Executors.newSingleThreadExecutor();
                final ExecutorService imageExecutor = Executors.newSingleThreadExecutor();

                final long maxReadAheadBufferMicros = 1000 * 1000L;

                long lastTimeStamp = -1L;
                while (!Thread.interrupted()) {
                    if(playerState == PlayerState.Play){
                        final Frame frame = grabber.grab();
                        if (frame == null) {
                            break;
                        }                    
                        if (lastTimeStamp < 0) {
                            playbackTimer.start();
                        }
                        lastTimeStamp = frame.timestamp;
                        if(lastTimeStamp > mediaCurrentTime){
                            mediaCurrentTime = lastTimeStamp;
                        }                        
                        if (frame.image != null) {
                            final Frame imageFrame = frame.clone();

                            imageExecutor.submit(() -> {
                                final BufferedImage image1 = converter.convert(imageFrame);
                                final long timeStampDeltaMicros = imageFrame.timestamp - playbackTimer.elapsedMicros();
                                imageFrame.close();
                                if (timeStampDeltaMicros > 0) {
                                    final long delayMillis = timeStampDeltaMicros / 1000L;
                                    try {
                                        Thread.sleep(delayMillis);
                                    } catch (InterruptedException e) {
                                        Thread.currentThread().interrupt();
                                    }
                                }

                                videoImage = image1;
                                repaint();
                            });
                        } else if (frame.samples != null) {
                            if (soundLine == null) {
                                throw new IllegalStateException("Internal error: sound playback not initialized");
                            }
                            final ShortBuffer channelSamplesShortBuffer = (ShortBuffer) frame.samples[0];
                            channelSamplesShortBuffer.rewind();

                            final ByteBuffer outBuffer = ByteBuffer.allocate(channelSamplesShortBuffer.capacity() * 2);

                            for (int i = 0; i < channelSamplesShortBuffer.capacity(); i++) {
                                short val = channelSamplesShortBuffer.get(i);
                                outBuffer.putShort(val);
                            }

                            audioExecutor.submit(() -> {
                                soundLine.write(outBuffer.array(), 0, outBuffer.capacity());
                                outBuffer.clear();
                            });
                        }
                        final long timeStampDeltaMicros = frame.timestamp - playbackTimer.elapsedMicros();
                        if (timeStampDeltaMicros > maxReadAheadBufferMicros) {
                            Thread.sleep((timeStampDeltaMicros - maxReadAheadBufferMicros) / 1000);
                        }
                    }                    
                }

                if (!Thread.interrupted()) {
                    long delay = (lastTimeStamp - playbackTimer.elapsedMicros()) / 1000 +
                            Math.round(1 / grabber.getFrameRate() * 1000);
                    Thread.sleep(Math.max(0, delay));
                }
                grabber.stop();
                grabber.release();
                if (soundLine != null) {
                    soundLine.stop();
                }
                audioExecutor.shutdownNow();
                audioExecutor.awaitTermination(10, TimeUnit.SECONDS);
                imageExecutor.shutdownNow();
                imageExecutor.awaitTermination(10, TimeUnit.SECONDS);
            } catch (IllegalStateException | InterruptedException |
                    FFmpegFrameGrabber.Exception | LineUnavailableException ex) {
                Logger.getLogger(Player.class.getName()).log(Level.SEVERE, null, ex);
            }
        });
        playThread.start();
    }
    
    private void stopPlayback(){
        playThread.interrupt();
    }

    private class PlaybackTimer {
        private Long startTime = -1L;
        private final DataLine soundLine;

        public PlaybackTimer(DataLine soundLine) {
            this.soundLine = soundLine;
        }

        public PlaybackTimer() {
            this.soundLine = null;
        }

        public void start() {
            if (soundLine == null) {
                startTime = System.nanoTime();
            }
        }

        public long elapsedMicros() {
            if (soundLine == null) {
                if (startTime < 0) {
                    throw new IllegalStateException("PlaybackTimer not initialized.");
                }
                return (System.nanoTime() - startTime) / 1000;
            } else {
                return soundLine.getMicrosecondPosition();
            }
        }
    }

    public void setMedia(String media) {
        this.media = media;
    }
    
    /**
     * Remplit les menus
     */
    private void populate(){
        // Main folder
        File folder = new File(root + File.separator + "res");
        if(folder.exists() == false) return;
        
        // Main file
        File jsonRoot = new File(folder, "root.json");
        if(jsonRoot.exists() == false) return;
        
        // Create Ref to help create menu
        final List<Ref> refs = new ArrayList<>(); 
        
        // Parse
        try(FileReader fr = new FileReader(jsonRoot, StandardCharsets.UTF_8);
                BufferedReader br = new BufferedReader(fr);){
            StringBuilder jsonData = new StringBuilder();
            String line;
            while((line = br.readLine()) != null){
                jsonData.append(line).append("\n");
            }
            JSONArray a = new JSONArray(jsonData.toString());
            for(int i=0; i<a.length(); i++){
                JSONObject o = a.getJSONObject(i);
                Ref ref = new Ref();
                for(Map.Entry<String, Object> entry : o.toMap().entrySet()){
                    switch(entry.getKey()){
                        case "ref" -> {
                            if(entry.getValue() instanceof String s){
                                if(s.startsWith("root")) ref.setName("root");
                                if(s.startsWith("sub")) ref.setName("sub");
                                if(s.startsWith("all")) ref.setName("all");
                                if(s.contains("::")){
                                    ref.setSection(s.substring(s.indexOf("-")+1, s.lastIndexOf("::")));
                                    ref.setChapterName(s.substring(s.lastIndexOf("::")+2));
                                }else{
                                    ref.setSection(s.substring(s.indexOf("-")+1));
                                }
                            }
                        }
                        case "img src" -> {
                            if(entry.getValue() instanceof String s){
                                ref.setImgSrc(s);
                            }
                        }
                        case "img x" -> {
                            if(entry.getValue() instanceof Integer v){
                                ref.setX(v);
                            }
                        }
                        case "img y" -> {
                            if(entry.getValue() instanceof Integer v){
                                ref.setY(v);
                            }
                        }
                        case "img w" -> {
                            if(entry.getValue() instanceof Integer v){
                                ref.setW(v);
                            }
                        }
                        case "img h" -> {
                            if(entry.getValue() instanceof Integer v){
                                ref.setH(v);
                            }
                        }
                        case "dest" -> {
                            if(entry.getValue() instanceof String s){
                                if(s.contains("::")){
                                    ref.setDest(s.substring(0, s.lastIndexOf("::")));
                                    ref.setChapterTime(Long.parseLong(s.substring(s.lastIndexOf("::")+2)));
                                }else{
                                    ref.setDest(s);
                                }
                            }
                        }
                        case "playlist" -> {
                            if(entry.getValue() instanceof String s){
                                ref.setPlaylist(s);
                            }
                        }
                    }
                }
                refs.add(ref);
            }
        }catch(IOException ex) {
            Logger.getLogger(Player.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        // Make menu
        for(Ref ref : refs){
            Menu mnu = new Menu();
            switch(ref.getName()){
                case "root" -> {
                    // Root - Main menu
                    String rawSrc = ref.getImgSrc().replace("/", File.separator);
                    String src = folder.getAbsolutePath() + File.separator + rawSrc;
                    mnu.setBackgroundImage(new ImageIcon(src).getImage());
                }
                case "sub" -> {
                    // Sub - Sub menu (clickable small image)                    
                    mnu.setMenuImageLocation(new Point(ref.getX(), ref.getY()));
                    mnu.setMenuImageDimension(new Dimension(ref.getW(), ref.getH()));
                    
                    String rawSrc = ref.getImgSrc().replace("/", File.separator);
                    String src = folder.getAbsolutePath() + File.separator + rawSrc;
                    mnu.setMenuImage(new ImageIcon(src).getImage());
                    
                    String rawVideo = ref.getDest().replace("/", File.separator);
                    String video = folder.getAbsolutePath() + File.separator + rawVideo;
                    mnu.setVideo(video);
                }
            }
            viewer.getSubMenus().add(mnu);
        }
        
        if(viewer.getSubMenus().isEmpty() == false){
            repaint();
        }
        
    }
}
