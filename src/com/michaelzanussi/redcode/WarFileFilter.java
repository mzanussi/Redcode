package com.michaelzanussi.redcode;

import java.io.File;
import java.util.Hashtable;
import javax.swing.filechooser.FileFilter;
import java.util.Dictionary;
import java.util.Enumeration;

/**
 * Courtesy of Sun! 
 * 
 * @author <a href="mailto:iosdevx@gmail.com">Michael Zanussi</a>
 * @version 1.0 (10 May 2004) 
 */
public class WarFileFilter extends FileFilter {

	private Dictionary<Object, Object> filters = null;
	private String description = null;
	private String fullDescription = null;
	private boolean useExtensionsInDescription = true;

	/**
	 * Creates a file filter. If no filters are added, then all files are
	 * accepted.
	 *
	 * @see #addExtension
	 */
	public WarFileFilter() {
		this.filters = new Hashtable<Object, Object>();
	}

	/* (non-Javadoc)
	 * @see javax.swing.filechooser.FileFilter#accept(java.io.File)
	 */
	public boolean accept(File f) {
		if (f != null) {
			if (f.isDirectory()) {
				return true;
			}
			String extension = getExtension(f);
			if (extension != null && filters.get(getExtension(f)) != null) {
				return true;
			}
		}
		return false;
	}

	/**
	 * Return the extension portion of the file's name .
	 *
	 * @see #getExtension
	 * @see FileFilter#accept
	 */
	public String getExtension(File f) {
		if (f != null) {
			String filename = f.getName();
			int i = filename.lastIndexOf('.');
			if (i > 0 && i < filename.length() - 1) {
				return filename.substring(i + 1).toLowerCase();
			}
		}
		return null;
	}

	/**
	 * Adds a file type "dot" extension to filter against.
	 *
	 * For example: the following code will create a filter that filters out all
	 * files except those that end in ".jpg" and ".tif":
	 *
	 * ExampleFileFilter filter = new ExampleFileFilter();
	 * filter.addExtension("jpg"); filter.addExtension("tif");
	 *
	 * Note that the "." before the extension is not needed and will be ignored.
	 */
	public void addExtension(String extension) {
		if (filters == null) {
			filters = new Hashtable<Object, Object>(5);
		}
		filters.put(extension.toLowerCase(), this);
		fullDescription = null;
	}

	/* (non-Javadoc)
	 * @see javax.swing.filechooser.FileFilter#getDescription()
	 */
	public String getDescription() {
		if (fullDescription == null) {
			if (description == null || isExtensionListInDescription()) {
				fullDescription = description == null ? "(" : description + " (";
				// build the description from the extension list
				Enumeration<Object> extensions = filters.keys();
				if (extensions != null) {
					fullDescription += "." + (String) extensions.nextElement();
					while (extensions.hasMoreElements()) {
						fullDescription += ", ." + (String) extensions.nextElement();
					}
				}
				fullDescription += ")";
			} else {
				fullDescription = description;
			}
		}
		return fullDescription;
	}

	/**
	 * Sets the human readable description of this filter. For example:
	 * filter.setDescription("Gif and JPG Images");
	 *
	 * @see setDescription
	 * @see setExtensionListInDescription
	 * @see isExtensionListInDescription
	 */
	public void setDescription(String description) {
		this.description = description;
		fullDescription = null;
	}

	/**
	 * Determines whether the extension list (.jpg, .gif, etc) should show up in
	 * the human readable description.
	 *
	 * Only relevant if a description was provided in the constructor or using
	 * setDescription();
	 *
	 * @see getDescription
	 * @see setDescription
	 * @see isExtensionListInDescription
	 */
	public void setExtensionListInDescription(boolean b) {
		useExtensionsInDescription = b;
		fullDescription = null;
	}

	/**
	 * Returns whether the extension list (.jpg, .gif, etc) should show up in
	 * the human readable description.
	 *
	 * Only relevant if a description was provided in the constructor or using
	 * setDescription();
	 *
	 * @see getDescription
	 * @see setDescription
	 * @see setExtensionListInDescription
	 */
	public boolean isExtensionListInDescription() {
		return useExtensionsInDescription;
	}
	
}
