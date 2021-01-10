package com.github.pulsebeat02.command.music;

import com.github.kiulian.downloader.YoutubeDownloader;
import com.github.kiulian.downloader.YoutubeException;
import com.github.kiulian.downloader.model.VideoDetails;
import com.github.kiulian.downloader.model.YoutubeVideo;
import com.github.pulsebeat02.HTTPServer;
import com.github.pulsebeat02.SMPPlugin;
import it.sauronsoftware.jave.AudioAttributes;
import it.sauronsoftware.jave.Encoder;
import it.sauronsoftware.jave.EncodingAttributes;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

public class MusicTrackPlayer {

	private final SMPPlugin plugin;
	private VideoDetails details;

	public MusicTrackPlayer(final SMPPlugin plugin) {
		this.plugin = plugin;
	}

	public void stopMusic(final CommandSender sender) {
		HTTPServer server = plugin.getHTTPServer();
		if (server == null) {
			sender.sendMessage(plugin.formatMessage(ChatColor.RED + "Track not playing!"));
			return;
		}
		server.terminate();
		for (Player p : Bukkit.getOnlinePlayers()) {
			p.stopSound("audio");
		}
		sender.sendMessage(plugin.formatMessage(org.bukkit.ChatColor.RED + "Current track stopped"));
	}

	@Deprecated
	public void playMusic(final String url) throws Exception {
		File[] files = getFiles(url);
		createEmptyZipFile(new VideoResource(files[0], files[1]));
		HTTPServer server = plugin.getHTTPServer();
        if (server == null) {
			server = new HTTPServer(plugin, 1334, details);
			server.start();
		} else {
			server.terminate();
			server = new HTTPServer(plugin, 1334, details);
			server.start();
		}
		String ip = "http://" + plugin.getServer().getIp() + ":" + 1334 + "/resourcepack.zip";
		for (Player player : Bukkit.getOnlinePlayers()) {
			player.sendMessage(ChatColor.AQUA + "Sending Resourcepack...");
			player.setResourcePack(ip);
		}
	}

	private File[] getFiles(final String url) throws Exception {
		File video = downloadVideo(url);
		assert video != null;
		File sound = new File(video.getParentFile() + "/audio.ogg");
		AudioAttributes audio = new AudioAttributes();
		audio.setCodec("libvorbis");
		audio.setBitRate(160000);
		audio.setChannels(2);
		audio.setSamplingRate(44100);
		EncodingAttributes attrs = new EncodingAttributes();
		attrs.setFormat("ogg");
		attrs.setAudioAttributes(audio);
		Encoder encoder = new Encoder();
		encoder.encode(video, sound, attrs);
		return new File[] { video, sound };
	}

	private File downloadVideo(final String url) throws IOException, YoutubeException {
		YoutubeDownloader downloader = new YoutubeDownloader();
		String ID = getVideoId(url);
		if (ID != null) {
			YoutubeVideo video = downloader.getVideo(ID);
			details = video.details();
			for (Player p : Bukkit.getOnlinePlayers()) {
				p.sendMessage(ChatColor.GOLD + "=====================================");
				p.sendMessage(ChatColor.RED + "Now Playing: " + ChatColor.AQUA + details.title());
				p.sendMessage(ChatColor.RED + "Author: " + ChatColor.AQUA + details.author());
				p.sendMessage(ChatColor.RED + "Rating: " + ChatColor.AQUA + details.averageRating());
				p.sendMessage(ChatColor.RED + "Description: " + ChatColor.AQUA + details.description());
				p.sendMessage(ChatColor.GOLD + "=====================================");
			}
			File outputDir = new File(plugin.getDataFolder().getAbsolutePath());
			return video.download(video.videoWithAudioFormats().get(0), outputDir, "video", true);
		} else {
			return null;
		}
	}

	private String getVideoId(final String url) {
		String pattern = "(?<=youtu.be/|watch\\?v=|/videos/|embed)[^#]*";
		Pattern compiledPattern = Pattern.compile(pattern);
		Matcher matcher = compiledPattern.matcher(url);
		if (matcher.find()) {
			return matcher.group();
		} else {
			System.out.println("Invalid Youtube URL Found!");
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
		byte[] soundJSON = ("{\r\n" + "   \"minecraftvideo\":{\r\n" + "      \"sounds\":[\r\n"
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

}
