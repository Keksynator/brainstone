package mods.brainstone.logicgates;

public class Pin {
	public static final Pin NullPin = new Pin('\0', false, false, false,
			PinState.NotExisting);
	public static final Pin MovableNullPin = new Pin('\0', true, false, false,
			PinState.NotExisting);

	public final char Name;
	public final boolean Movable;
	public final boolean Output;
	public final boolean Inverted;
	public PinState State;

	public Pin(char name) {
		this(name, true);
	}

	public Pin(char name, boolean movable) {
		this(name, movable, false);
	}

	public Pin(char name, boolean movable, boolean output) {
		this(name, movable, output, false);
	}

	public Pin(char name, boolean movable, boolean output, boolean inverted) {
		this(name, movable, output, inverted, PinState.NotConnected);
	}

	public Pin(char name, boolean movable, boolean output, boolean inverted,
			PinState state) {
		Name = name;
		Movable = movable;
		Output = output;
		Inverted = inverted;
		State = state;
	}
}
