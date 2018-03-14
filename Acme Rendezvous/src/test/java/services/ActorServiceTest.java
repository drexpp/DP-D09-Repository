package services;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.annotation.Transactional;

import utilities.AbstractTest;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations={"classpath:spring/junit.xml"})
@Transactional
public class ActorServiceTest extends AbstractTest {
	
	@Autowired
	private ActorService actorService;
	
@Test
public void diverFindByPrincipal(){
	Object testingData[][]= {
			{"user1",null}, 
			{"admin",null},
			{"manager1",null},
			{"null",IllegalArgumentException.class}};	// Si el usuario a encontrar no est� determinado, deber� devolver un error
	for (int i = 0; i < testingData.length;i++){
		templateFindByPrincipal((String) testingData[i][0], (Class<?>) testingData[i][1]); 
	
	}
}

protected void templateFindByPrincipal(String username, Class<?> expected){
	Class<?> caught;
	caught = null;
	try{
		authenticate(username);
		actorService.findByPrincipal();
		unauthenticate();
	} catch(Throwable oops){
		caught = oops.getClass();
	}
	checkExceptions(expected, caught);
}




}
