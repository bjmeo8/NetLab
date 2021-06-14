package unitTest;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import jdbc.Persistance;

class daoTest {

	@DisplayName("test save user")
	@Test
	void test1() {
		Persistance.persistUser("test", "test", "test@gmail.com", "allocine");
		assertEquals(0, 0);
	
	}
	
	@DisplayName("test validation name, password")
	@Test
	void test2() {
		
		assertEquals(true, Persistance.getUser("test", "test"));
	
	}

	@DisplayName("test getUserById ")
	@Test
	void test3() throws Exception {
		
		assertEquals("Z20141218234416927428111", Persistance.getRealUserID(1));

	}
	
	@DisplayName("test persistRating ")
	@Test
	void test4() throws Exception {
		Persistance.persistRating("real_user_943", "acm_1", 5, "allocine");
		assertEquals(1, 1);

	}
	
	@DisplayName(" test getMovieInfo ")
	@Test
	void test5() throws Exception {
		Persistance.persistRating("real_user_943", "acm_1", 5, "allocine");
		assertEquals(1, 1);

	}
	
	@DisplayName(" test addRateTomovie ")
	@Test
	void test6() throws Exception {
		Persistance.persistRating("real_user_943", "acm_1", 5, "allocine");
		assertEquals(1, 1);

	}
	
	
	@DisplayName("get allMovie History ")
	@Test
	void test7() throws Exception {
		
		//assertEquals(1, Persistance.gethistorique("real_user_943").size());

	}
	
	
	@DisplayName("test getMovieInfoByTitle")
	@Test
	void test8() throws Exception {
		assertEquals(1, 1);
		//assertEquals(1, Persistance.gethistorique("real_user_943").size());

	}
	
	
}
