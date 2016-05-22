package eb.eventhandling;

@FunctionalInterface
public interface Listener {
	void respondToUpdate(Update update);

}
