package com.fdmgroup.documentuploader.model.document;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Lob;
import javax.persistence.SequenceGenerator;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import java.io.Serializable;
import java.util.Arrays;

/**
 * Encapsulates information related to a document/file uploaded from a client to
 * the database.
 * 
 * @author Noah Anderson
 *
 */
@ApiModel(description = "Represents a document which has been uploaded to an account.")
@Entity
public class Document implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 5454232325451663784L;

	@ApiModelProperty(notes = "Unique identifier of the Document.",
			example = "1", required = true)
	@Id
	@Column(name = "document_id")
	@GeneratedValue(generator = "document_gen", strategy = GenerationType.SEQUENCE)
	@SequenceGenerator(name = "document_gen", sequenceName = "document_seq", allocationSize = 1)
	private long id;

	@ApiModelProperty(notes = "The content of the Document",
		required = true)
	@NotEmpty(message = "{document.content.not-empty}")
	@Lob
	@Column(nullable = false, updatable = false)
	private byte[] content;

	@ApiModelProperty(notes = "The name of the Document.",
		example = "wordDoc", required = true)
	@NotBlank(message = "{document.name.not-empty}")
	@Column(nullable = false, updatable = false)
	private String name;

	@ApiModelProperty(notes = "The extension of the Document.",
		example = ".docx", required = true)
	@NotBlank(message = "{document.extension.not-empty}")
	@Column(nullable = false, updatable = false)
	private String extension;

	public Document() {
	}

	public Document(@NotEmpty(message = "{document.content.not-empty}") byte[] content,
			@NotBlank(message = "{document.name.not-empty}") String name,
			@NotBlank(message = "{document.extension.not-empty}") String extension) {
		this.content = content;
		this.name = name;
		this.extension = extension;
	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public byte[] getContent() {
		return content;
	}

	public void setContent(byte[] content) {
		this.content = content;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getExtension() {
		return extension;
	}

	public void setExtension(String extension) {
		this.extension = extension;
	}

	@Override
	public String toString() {
		return "Document [id=" + id + ", content=" + Arrays.toString(content) + ", name=" + name + ", extension="
				+ extension + "]";
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + Arrays.hashCode(content);
		result = prime * result + ((extension == null) ? 0 : extension.hashCode());
		result = prime * result + (int) (id ^ (id >>> 32));
		result = prime * result + ((name == null) ? 0 : name.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Document other = (Document) obj;
		if (!Arrays.equals(content, other.content))
			return false;
		if (extension == null) {
			if (other.extension != null)
				return false;
		} else if (!extension.equals(other.extension))
			return false;
		if (id != other.id)
			return false;
		if (name == null) {
			if (other.name != null)
				return false;
		} else if (!name.equals(other.name))
			return false;
		return true;
	}

}
