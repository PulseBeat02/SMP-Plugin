package com.github.pulsebeat02.command.music;

import com.github.kiulian.downloader.YoutubeDownloader;
import com.github.kiulian.downloader.YoutubeException;
import com.github.kiulian.downloader.model.VideoDetails;
import com.github.kiulian.downloader.model.YoutubeVideo;
import com.github.pulsebeat02.HTTPServer;
import com.github.pulsebeat02.SMPPlugin;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerResourcePackStatusEvent;
import ws.schild.jave.AudioAttributes;
import ws.schild.jave.Encoder;
import ws.schild.jave.EncodingAttributes;
import ws.schild.jave.MultimediaObject;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.MessageDigest;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

public class MusicTrackPlayer implements Listener {

    private final SMPPlugin plugin;
    private boolean finished;
    private VideoDetails details;

    public MusicTrackPlayer(final SMPPlugin plugin) {
        this.plugin = plugin;
        plugin.getServer().getPluginManager().registerEvents(this, plugin);
    }

    public void stopMusic(final CommandSender sender) {
        if (plugin.getHTTPServer().isRunning()) {
            plugin.getHTTPServer().terminate();
        }
        plugin.setHttpServer(null);
        for (Player p : Bukkit.getOnlinePlayers()) {
            p.stopSound("smpplugin");
        }
        sender.sendMessage(plugin.formatMessage(org.bukkit.ChatColor.RED + "Current Track Stopped"));
    }

    @Deprecated
    public void loadMusic(final CommandSender sender, final String url) {
        new Thread(() -> {
            File[] files = new File[0];
            try {
                files = getFiles(sender, url);
            } catch (Exception e) {
                e.printStackTrace();
            }
            if (files == null) {
                return;
            }
            try {
                createEmptyZipFile(new VideoResource(files[0], files[1]));
            } catch (IOException e) {
                e.printStackTrace();
            }
            try {
                plugin.setHttpServer(new HTTPServer(plugin, plugin.getPort(), details));
            } catch (IOException e) {
                e.printStackTrace();
            }
            plugin.getHTTPServer().start();
            String ip = "http://" + plugin.getServer().getIp() + ":" + plugin.getPort() + "/resourcepack.zip";
            for (Player player : Bukkit.getOnlinePlayers()) {
                player.sendMessage(ChatColor.AQUA + "Sending Resourcepack...");
                try {
                    byte[] hash = createHash(new File(plugin.getDataFolder().getAbsolutePath() + "/resourcepack.zip"));
                    System.out.println(new String(hash));
                    player.setResourcePack(ip);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();
        finished = true;
    }

    private byte[] createHash(final File file) throws Exception {
        MessageDigest digest = MessageDigest.getInstance("SHA-1");
        InputStream fis = new FileInputStream(file);
        int n = 0;
        byte[] buffer = new byte[8192];
        while (n != -1) {
            n = fis.read(buffer);
            if (n > 0) {
                digest.update(buffer, 0, n);
            }
        }
        return digest.digest();
    }

    public void playMusic() {
        for (Player p : Bukkit.getOnlinePlayers()) {
            p.playSound(p.getLocation(), "smpplugin", 0.6F, 1.0F);
            p.sendMessage(ChatColor.GOLD + "=====================================");
            p.sendMessage(ChatColor.AQUA + "Now Playing: " + ChatColor.LIGHT_PURPLE + details.title());
            p.sendMessage(ChatColor.AQUA + "Author: " + ChatColor.LIGHT_PURPLE + details.author());
            p.sendMessage(ChatColor.AQUA + "Rating: " + ChatColor.LIGHT_PURPLE + details.averageRating());
            p.sendMessage(ChatColor.GOLD + "=====================================");
        }
    }

    private File[] getFiles(final CommandSender sender, final String url) throws Exception {
        File source = downloadVideo(sender, url);
        if (source == null) {
            return null;
        }
        File sound = new File(source.getParentFile() + "/audio.ogg");
        AudioAttributes audio = new AudioAttributes();
        audio.setCodec("libvorbis");
        audio.setBitRate(160000);
        audio.setChannels(2);
        audio.setSamplingRate(44100);
        audio.setVolume(48);
        EncodingAttributes attrs = new EncodingAttributes();
        attrs.setFormat("ogg");
        attrs.setAudioAttributes(audio);
        Encoder encoder = new Encoder();
        encoder.encode(new MultimediaObject(source), sound, attrs);
        return new File[]{source, sound};
    }

    private File downloadVideo(final CommandSender sender, final String url) throws IOException, YoutubeException {
        YoutubeDownloader downloader = new YoutubeDownloader();
        String ID = getVideoId(sender, url);
        if (ID != null) {
            YoutubeVideo video = downloader.getVideo(ID);
            details = video.details();
            for (Player p : Bukkit.getOnlinePlayers()) {
                p.sendMessage(ChatColor.GOLD + "=====================================");
                p.sendMessage(ChatColor.AQUA + "Now Playing: " + ChatColor.LIGHT_PURPLE + details.title());
                p.sendMessage(ChatColor.AQUA + "Author: " + ChatColor.LIGHT_PURPLE + details.author());
                p.sendMessage(ChatColor.AQUA + "Rating: " + ChatColor.LIGHT_PURPLE + details.averageRating());
                p.sendMessage(ChatColor.AQUA + "Description: " + ChatColor.LIGHT_PURPLE + details.description());
                p.sendMessage(ChatColor.GOLD + "=====================================");
            }
            File outputDir = new File(plugin.getDataFolder().getAbsolutePath());
            return video.download(video.videoWithAudioFormats().get(0), outputDir, "video", true);
        } else {
            return null;
        }
    }

    private String getVideoId(final CommandSender sender, final String url) {
        String pattern = "(?<=youtu.be/|watch\\?v=|/videos/|embed)[^#]*";
        Pattern compiledPattern = Pattern.compile(pattern);
        Matcher matcher = compiledPattern.matcher(url);
        if (matcher.find()) {
            return matcher.group();
        } else {
            sender.sendMessage(plugin.formatMessage(ChatColor.RED + "Invalid Youtube URL"));
        }
        return null;
    }

    private void createEmptyZipFile(final VideoResource v) throws IOException {
        ZipOutputStream out = new ZipOutputStream(new FileOutputStream(
                plugin.getDataFolder().getAbsolutePath() + "/resourcepack.zip"));
        byte[] mcmeta = ("{\r\n" + "	\"pack\": {\r\n" + "    \"pack_format\": 6,\r\n"
                + "    \"description\": \"Custom Server Resourcepack for MinecraftVideo\"\r\n" + "  }\r\n" + "}")
                .getBytes();
        ZipEntry config = new ZipEntry("pack.mcmeta");
        out.putNextEntry(config);
        out.write(mcmeta);
        out.closeEntry();
        byte[] soundJSON = ("{\r\n" + "   \"smpplugin\":{\r\n" + "      \"sounds\":[\r\n"
                + "         \"audio\"\r\n" + "      ]\r\n" + "   }\r\n" + "}").getBytes();
        ZipEntry sound = new ZipEntry("assets/minecraft/sounds.json");
        out.putNextEntry(sound);
        out.write(soundJSON);
        out.closeEntry();
        ZipEntry soundFile = new ZipEntry("assets/minecraft/sounds/audio.ogg");
        out.putNextEntry(soundFile);
        out.write(Files.readAllBytes(Paths.get(v.getSound().getAbsolutePath())));
        out.closeEntry();
        out.close();
    }

    public VideoDetails getDetails() {
        return details;
    }

    public boolean finishedLoading() {
        return finished;
    }

}
