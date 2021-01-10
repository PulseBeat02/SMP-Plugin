package com.github.pulsebeat02.command.music;

import java.io.File;

public class VideoResource {
	
	private final File video;
	private final File sound;
	
	public VideoResource(File v, File s) {
		this.video = v;
		this.sound = s;
	}

	public File getVideo() {
		return video;
	}

	public File getSound() {
		return sound;
	}

}
