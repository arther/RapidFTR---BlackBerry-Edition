package com.rapidftr.model;

import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Vector;

import net.rim.device.api.util.Persistable;

import com.rapidftr.form.FormFieldAction;
import com.rapidftr.form.Forms;
import com.rapidftr.utilities.FileUtility;
import com.rapidftr.utilities.HttpUtility;
import com.rapidftr.utilities.RandomStringGenerator;
import com.rapidftr.utilities.StringUtility;
import com.sun.me.web.request.Arg;
import com.sun.me.web.request.Part;
import com.sun.me.web.request.PostData;

public class Child implements Persistable {

	public static final String CREATED_AT_KEY = "created_at";
	public static final String LAST_UPDATED_KEY = "last_updated_at";
	private static final String UNIQUE_IDENTIFIER = "unique_identifier";
	public static final String FLAG_MESSAGE_KEY = "flag_message";
	public static final String FLAGGED_KEY = "flag";

	private final Hashtable data;
	private final Hashtable changedFields;

	private ChildStatus childStatus;
	public static final String NAME = "name";

	public Child(String creationDate) {
		changedFields = new Hashtable();
		data = new Hashtable();
		put("_id", RandomStringGenerator.generate(32));
		put(CREATED_AT_KEY, creationDate);
		childStatus = ChildStatus.NEW;
	}

	public String toString() {
		return (String) data.get("name");
	}

	public PostData getPostData() {

		Vector parts = new Vector();
		Enumeration keyList = isNewChild() ? data.keys() : getChangedFields();

		while (keyList.hasMoreElements()) {
			Object key = keyList.nextElement();
			Object value = data.get(key);

			if (key.equals("histories")) {
				continue;
			}

			if (key.equals("current_photo_key")) {
				if (!StringUtility.isBlank(String.valueOf(value))) {
					parts.addElement(multiPart(value, "photo",
							HttpUtility.HEADER_CONTENT_TYPE_IMAGE));
				}
				continue;
			}

			if (key.equals("recorded_audio")) {
				if (value != null
						&& !StringUtility.isBlank(String.valueOf(value))) {
					parts.addElement(multiPart(value, "audio",
							HttpUtility.HEADER_CONTENT_TYPE_AUDIO));
				}
				continue;
			}

			Arg[] headers = new Arg[1];
			headers[0] = new Arg("Content-Disposition",
					"form-data; name=\"child[" + key + "]\"");
			Part part = new Part(value.toString().getBytes(), headers);
			parts.addElement(part);
		}

		Part[] anArray = new Part[parts.size()];
		parts.copyInto(anArray);
		String boundary = "abced";

		PostData postData = new PostData(anArray, boundary);
		return postData;
	}

	private Enumeration getChangedFields() {
		return changedFields.keys();
	}

	private Part multiPart(Object value, String paramName, Arg headerContentType) {
		Arg[] headers = new Arg[2];
		headers[0] = new Arg("Content-Disposition", "form-data; name=\"child["
				+ paramName + "]\"");
		headers[1] = headerContentType;
		byte[] imageData = FileUtility.getByteArray(value.toString());

		return new Part(imageData, headers);
	}

	public void setField(String name, Object value) {
		if (!isNewChild()) {
			String oldValue = getField(name);

			if (oldValue != null && !oldValue.equals(value)
					&& !name.equals("_id")) {
				changedFields.put(name, value);
			}
		}
		put(name, value);

	}

	private void put(String name, Object value) {
		if (value != null) {
			data.put(name, value);
		}
	}

	public String getField(String key) {
		return (String) data.get(key);
	}

	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((data == null) ? 0 : data.get("_id").hashCode());
		return result;
	}

	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (getClass() != obj.getClass()) {
			return false;
		}
		Child other = (Child) obj;
		if (data == null) {
			if (other.data != null) {
				return false;
			}
		} else {
			if (data.get("_id") != null && other.data.get("_id") != null
					&& data.get("_id").equals((other.data.get("_id")))) {
				return true;
			}
			if (data.get(UNIQUE_IDENTIFIER) != null
					&& other.data.get(UNIQUE_IDENTIFIER) != null
					&& data.get(UNIQUE_IDENTIFIER).equals(
							(other.data.get(UNIQUE_IDENTIFIER)))) {
				return true;
			}
		}

		return false;
	}

	public void updateField(String name) {
		if (!data.containsKey(name)) {
			put(name, "");
		}

	}

	public static Child create(Forms forms, String currentFormattedDateTime) {
		Child child = new Child(currentFormattedDateTime);
		child.update(forms, currentFormattedDateTime);
		return child;
	}

	public void update(Forms forms, String updatedDate) {
		forms.forEachField(new FormFieldAction() {
			public void execute(com.rapidftr.form.FormField field) {
				setField(field.getName(), field.getValue());
			}
		});
		if (isUpdated()) {
			childStatus = ChildStatus.UPDATED;
		}
		setField(Child.LAST_UPDATED_KEY, updatedDate);
	}

	// Art
	public void setPrimaryPhoto(String imagePath, String updatedDate) {
		addImageToChild(getField("current_photo_key"), updatedDate);
		setField("current_photo_key", imagePath);
		changedFields.put("current_photo_key", imagePath);
		if (isUpdated()) {
			childStatus = ChildStatus.UPDATED;
		}
		setField(Child.LAST_UPDATED_KEY, updatedDate);
	}

	//

	public ChildHistories getHistory() {
		return new ChildHistories(getField("histories"));
	}

	public boolean hasChangesByOtherThan(final String username) {
		ChildHistories histories = getHistory();
		final boolean[] result = { false };
		histories.forEachHistory(new HistoryAction() {
			public void execute(ChildHistoryItem historyItem) {
				result[0] = !(historyItem.getUsername().equals(username));
			}
		});
		return result[0];
	}

	public boolean isNewChild() {
		return getField(UNIQUE_IDENTIFIER) == null;
	}

	public boolean isUpdated() {
		return changedFields.size() > 0;
	}

	public ChildStatus childStatus() {
		return childStatus;
	}

	public void syncSuccess() {
		changedFields.clear();
		if ("true".equals(getField(FLAGGED_KEY))) {
			childStatus = ChildStatus.FLAGGED;
			return;
		}
		childStatus = ChildStatus.SYNCED;
	}

	public void syncFailed(String message) {
		ChildStatus status = ChildStatus.SYNC_FAILED;
		status.setSyncError(message);
		childStatus = status;
	}

	public boolean isSyncFailed() {
		return childStatus.equals(ChildStatus.SYNC_FAILED);
	}

	public boolean matches(String queryString) {
		String id = getField(UNIQUE_IDENTIFIER);
		if (id == null) {
			id = "";
		}

		String name = getField("name");
		if (name == null) {
			name = "";
		}

		return ((!"".equals(queryString) && (id.equalsIgnoreCase(queryString))) || (!""
				.equals(queryString) && name.toString().toLowerCase()
				.indexOf(queryString) != -1));
	}

	public String getCreatedBy() {
		return getField("created_by");
	}

	public String getImageLocation() {
		return getField("current_photo_key");
	}

	public void setUniqueIdentifier(String uniqueId) {
		setField(UNIQUE_IDENTIFIER, uniqueId);
	}

	public void setId(String id) {
		setField("_id", id);
	}

	public boolean hasPhoto() {
		return !StringUtility.isBlank(getField("current_photo_key"));
	}

	public void setPhotoKey(String value) {
		setField("current_photo_key", value);
	}

	public void setPhotoKeyWithoutUpdate(String value) {
		put("current_photo_key", value);
	}

	public String getPhotoKey() {
		return getField("current_photo_key");
	}

	public void flagRecord(String flagReason) {
		childStatus = ChildStatus.FLAGGED;
		put(FLAGGED_KEY, "true");
		put(FLAG_MESSAGE_KEY, flagReason);
		changedFields.put(FLAGGED_KEY, "true");
		changedFields.put(FLAG_MESSAGE_KEY, flagReason);
	}

	public String flaggedByUserName() {
		final String[] username = { null };
		ChildHistories histories = getHistory();
		histories.forEachHistory(new HistoryAction() {
			public void execute(ChildHistoryItem historyItem) {
				if (historyItem.getChangedFieldsNames().contains("flag")) {
					username[0] = historyItem.getUsername();
				}
			}
		});
		return username[0];
	}

	public String flagInformation() {
		return getField(FLAG_MESSAGE_KEY);
	}

	public String[] getImageLocations() {
		String imagesLocation = getField("photo_keys");
		if (imagesLocation == null || imagesLocation.equals("")) {
			return null;
		}

		return fetchImagesLocation(imagesLocation);
	}

	private String[] fetchImagesLocation(String imagesLocation) {
		String fileNames[];
		imagesLocation = imagesLocation.replace('[', ' ');
		imagesLocation = imagesLocation.replace(']', ' ');
		imagesLocation = imagesLocation.replace('"', ' ');
		imagesLocation = imagesLocation.trim();

		fileNames = split(imagesLocation, " , ");
		for (int i = 0; i < fileNames.length; i++) {
			fileNames[i] = fileNames[i].trim();
		}
		return fileNames;
	}

	private static String[] split(String strString, String strDelimiter) {
		String[] strArray;
		int iOccurrences = 0;
		int iIndexOfInnerString = 0;
		int iIndexOfDelimiter = 0;
		int iCounter = 0;

		if (strString == null) {
			throw new IllegalArgumentException("Input string cannot be null.");
		}

		if (strDelimiter.length() <= 0 || strDelimiter == null) {
			throw new IllegalArgumentException(
					"Delimeter cannot be null or empty.");
		}

		if (strString.startsWith(strDelimiter)) {
			strString = strString.substring(strDelimiter.length());
		}

		if (!strString.endsWith(strDelimiter)) {
			strString += strDelimiter;
		}

		while ((iIndexOfDelimiter = strString.indexOf(strDelimiter,
				iIndexOfInnerString)) != -1) {
			iOccurrences += 1;
			iIndexOfInnerString = iIndexOfDelimiter + strDelimiter.length();
		}

		strArray = new String[iOccurrences];

		iIndexOfInnerString = 0;
		iIndexOfDelimiter = 0;

		while ((iIndexOfDelimiter = strString.indexOf(strDelimiter,
				iIndexOfInnerString)) != -1) {

			strArray[iCounter] = strString.substring(iIndexOfInnerString,
					iIndexOfDelimiter);

			iIndexOfInnerString = iIndexOfDelimiter + strDelimiter.length();

			iCounter += 1;
		}

		return strArray;
	}

	public void addImageToChild(String imageLocation, String updatedDate) {
		if (imageLocation == null || imageLocation == "") {
			return;
		}
		String imageLocations = getField("photo_keys");

		imageLocation = imageLocation.replace('.', '/');
		String[] tempImageLocations = split(imageLocation, "/");
		imageLocation = tempImageLocations[tempImageLocations.length - 2];
		if (imageLocations != null && !"".equals(imageLocations)) {
			if (imageLocations.indexOf(imageLocation) == -1) {
				imageLocations = imageLocations.replace(']', ' ');
				imageLocations = imageLocations.trim();
				imageLocations += ",\"" + imageLocation + "\"]";
				setField("photo_keys", imageLocations);
			}
		} else {
			setField("photo_keys", "[\"" + imageLocation + "\"]");
		}

		changedFields.put("photo_keys", imageLocation);
		if (isUpdated()) {
			if (childStatus != ChildStatus.NEW)
				childStatus = ChildStatus.UPDATED;
		}
		setField(Child.LAST_UPDATED_KEY, updatedDate);
	}
}
