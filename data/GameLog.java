package data;

/**
 * The server should be able to log game state. I believe it is possible to design the system
 * such that nobody can cheat, and nobody has to trust anyone (almost), but the cost is that clients
 * need to have the game logic. If the log is done right then the correctness can be verified
 * by running it through the logic again, at each client.
 * 
 * To get untrusted random numbers, all players send a random number to the server. The total
 * random value is hash(value1 | value2 | value3 ...) and can be verified later.
 *  
 * Hard part: can one guarantee that the server does not peek on cards it gives out, and tells someone?
 * Not easy - server must not have information about what card it is giving out.  
 * 
 * @author mahogny
 *
 */
public class GameLog
	{

	}
