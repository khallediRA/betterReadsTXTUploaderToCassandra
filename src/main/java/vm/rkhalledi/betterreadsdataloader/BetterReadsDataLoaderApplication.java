package vm.rkhalledi.betterreadsdataloader;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.annotation.PostConstruct;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.cassandra.CqlSessionBuilderCustomizer;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.format.annotation.DateTimeFormat;

import connection.DataStaxAstraProperties;
import vm.rkhalledi.betterreadsdataloader.author.Author;
import vm.rkhalledi.betterreadsdataloader.author.AuthorRepository;
import vm.rkhalledi.betterreadsdataloader.author.books.Book;
import vm.rkhalledi.betterreadsdataloader.author.books.BookRepository;

@SpringBootApplication
@EnableConfigurationProperties(DataStaxAstraProperties.class)
public class BetterReadsDataLoaderApplication {

	@Autowired
	private AuthorRepository authorRepository;
	@Autowired
	BookRepository bookRepository;

	@Value("${datadump.location.author}")
	private String authorDumpLocation;

	@Value("${datadump.location.works}")
	private String worksDumpLocation;

	public static void main(String[] args) {

		SpringApplication.run(BetterReadsDataLoaderApplication.class, args);
	}

	private void initAuthors() {

		Path path = Paths.get(authorDumpLocation);
		try {
			Stream<String> lines = Files.lines(path);

			lines.forEach(line -> {

				String jsonString = line.substring(line.indexOf("{"));

				try {
					JSONObject json = new JSONObject(jsonString);

					Author author = new Author();
					author.setName(json.optString("name"));
					author.setPersonalName(json.optString("personal_name"));
					author.setId(json.optString("key").replace("/authors/", ""));

					System.out.println("Saving Author : " + author.getName() + " ...");
					authorRepository.save(author);

				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

			});
		} catch (IOException e) {

			e.printStackTrace();
		}

	}

	private void initWorks() {
		Path path = Paths.get(worksDumpLocation);
		DateTimeFormatter dateFormat = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSSSSS");
		try {
			Stream<String> lines = Files.lines(path);

			lines.forEach(line -> {

				String jsonString = line.substring(line.indexOf("{"));

				try {
					JSONObject json = new JSONObject(jsonString);

					Book book = new Book();

					book.setId(json.getString("key").replace("/works/", ""));
					JSONObject descriptionObj = json.optJSONObject("description");
					if (descriptionObj != null) {
						book.setDescription(descriptionObj.optString("value"));
					} else {
						book.setDescription(json.optString("title"));
					}
					JSONObject createdObj = json.optJSONObject("created");

					if (createdObj != null) {
						String dateStr = createdObj.optString("value");

						book.setPublishedDate(LocalDate.parse(dateStr, dateFormat));

					}
					JSONArray coverJSONARRAY = json.optJSONArray("covers");

					if (coverJSONARRAY != null) {
						List<String> coverIds = new ArrayList<>();
						for (int i = 0; i < coverJSONARRAY.length(); i++) {
							coverIds.add(coverJSONARRAY.getString(i));
						}
						book.setCoverIds(coverIds);

					}

					book.setName(json.optString("title"));
					JSONArray authorJsonArray = json.optJSONArray("authors");
					if (authorJsonArray != null) {
						List<String> authorsIds = new ArrayList<>();
						for (int i = 0; i < authorJsonArray.length(); i++) {
							String authorId = authorJsonArray.getJSONObject(i).getJSONObject("author").getString("key")
									.replace("/authors/", "");
							authorsIds.add(authorId);
						}
						book.setAuthorIds(authorsIds);
						List<String> authorsNames = authorsIds.stream().map(id -> authorRepository.findById(id))
								.map(optionalAuthor -> {
									if (!optionalAuthor.isPresent())
										return "Unknown Author";

									return optionalAuthor.get().getName();
								}).collect(Collectors.toList());
						book.setAuthorNames(authorsNames);
					}
					System.out.println("Saving Data For Book name " + book.getName() + " ...");
					bookRepository.save(book);

				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

			});
		} catch (IOException e) {

			e.printStackTrace();
		}

	}

	@PostConstruct
	public void start() {

		initAuthors();
		initWorks();

	}

	/**
	 * This is necessary to have the Spring Boot app use the Astra secure bundle
	 * to connect to the database
	 */
	@Bean
	public CqlSessionBuilderCustomizer sessionBuilderCustomizer(DataStaxAstraProperties astraProperties) {
		Path bundle = astraProperties.getSecureConnectBundle().toPath();
		return builder -> builder.withCloudSecureConnectBundle(bundle);
	}

}
