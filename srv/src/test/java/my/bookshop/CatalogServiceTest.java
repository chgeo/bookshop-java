package my.bookshop;

import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.CoreMatchers.not;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.Arrays;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

import com.sap.cds.ql.Insert;
import com.sap.cds.services.persistence.PersistenceService;

import cds.gen.my.bookshop.Books;
import cds.gen.my.bookshop.Books_;

@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
public class CatalogServiceTest {

	@Autowired
	PersistenceService db;

	@Autowired
	private MockMvc mockMvc;

	@Before
	public void initializeBooks() {
		Books book1 = Books.create();
		book1.setTitle("Wuthering Heights");
		book1.setStock(100);

		Books book2 = Books.create();
		book2.setTitle("Harry Potter and the Sorcerer's Stone");
		book2.setStock(200);

		db.run(Insert.into(Books_.class).entries(Arrays.asList(book1, book2)));
	}

	@Test
	public void testDiscount() throws Exception {
		mockMvc.perform(get("/api/browse/Books"))
		.andExpect(status().isOk())
		.andExpect(jsonPath("$.value[0].title").value(not(containsString("11% discount"))))
		.andExpect(jsonPath("$.value[0].stock").value(100))
		.andExpect(jsonPath("$.value[1].title").value(containsString("11% discount")))
		.andExpect(jsonPath("$.value[1].stock").value(200));
	}

}