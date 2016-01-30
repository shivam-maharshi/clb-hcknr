package snip.pojo;

public class YTVideoInfo {

	private String videoId;
	private String channel;
	private String title;

	public YTVideoInfo(String videoId, String channel, String title) {
		this.videoId = videoId;
		this.channel = channel;
		this.title = title;
	}

	public String getChannel() {
		return channel;
	}

	public void setChannel(String channel) {
		this.channel = channel;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getVideoId() {
		return videoId;
	}

	public void setVideoId(String videoId) {
		this.videoId = videoId;
	}

}
