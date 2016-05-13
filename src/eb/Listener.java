package eb;

@FunctionalInterface
public interface Listener {
	void respondToUpdate(UpdateType updateType);

}
