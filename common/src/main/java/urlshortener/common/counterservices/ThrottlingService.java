package urlshortener.common.ThrottlingService;

import redis.clients.jedis.Jedis;

@Endpoint
public class ThrottlingService{
	
	
	
	@ResponsePayload
	public boolean getServerIP (){
		Jedis jedis = new Jedis ("localhost");
		System.out.println("Connection to server sucessful");
		
		//https://redis.io/commands/incr
		
		
		return true;
	}
}