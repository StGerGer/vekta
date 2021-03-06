package vekta.display;

public abstract class StyledDisplay implements Display {
	private DisplayStyle style = new DisplayStyle();

	private boolean custom;

	public StyledDisplay() {
	}

	public DisplayStyle getStyle() {
		return style;
	}

	public void setStyle(DisplayStyle style) {
		this.style = style;
	}

	public DisplayStyle customize() {
		// Derive custom style from parent
		if(!custom) {
			custom = true;
			style = style.derive();
		}
		return style;
	}
}
