package pl.codecity.main.request;

import java.io.Serializable;

public class CommentDeleteRequest implements Serializable {

	private long id;

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}
}

