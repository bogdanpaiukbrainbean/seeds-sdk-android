package ly.count.android.sdk.inappmessaging;

public enum ClickType {
	INAPP, BROWSER;

	public static ClickType getValue(final String value) {
		for (final ClickType clickType : ClickType.values())
			if (clickType.name().equalsIgnoreCase(value))
				return clickType;
		return null;
	}
}