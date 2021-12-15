package vm.rkhalledi.betterreadsdataloader.author.books;

import java.time.LocalDate;
import java.util.List;

import org.springframework.data.annotation.Id;
import org.springframework.data.cassandra.core.cql.PrimaryKeyType;
import org.springframework.data.cassandra.core.mapping.CassandraType;
import org.springframework.data.cassandra.core.mapping.Column;
import org.springframework.data.cassandra.core.mapping.PrimaryKeyColumn;
import org.springframework.data.cassandra.core.mapping.Table;
import org.springframework.data.cassandra.core.mapping.CassandraType.Name;

@Table("book_by_id")
public class Book {

    @Id
    @PrimaryKeyColumn(name = "book_id", type = PrimaryKeyType.PARTITIONED, ordinal = 0)
    private String id;

    @Column("book_name")
    @CassandraType(type = Name.TEXT)
    private String name;

    @Column("published_date")
    @CassandraType(type = Name.DATE)
    private LocalDate publishedDate;

    @Column("book_description")
    @CassandraType(type = Name.TEXT)
    private String description;

    @Column("cover_ids")
    @CassandraType(type = Name.LIST, typeArguments = Name.TEXT)
    private List<String> coverIds;

    @Column("author_names")
    @CassandraType(type = Name.LIST, typeArguments = Name.TEXT)
    private List<String> authorNames;

    @Column("author_id")
    @CassandraType(type = Name.LIST, typeArguments = Name.TEXT)
    private List<String> authorIds;

    /**
     * @return String return the id
     */
    public String getId() {
        return id;
    }

    /**
     * @param id the id to set
     */
    public void setId(String id) {
        this.id = id;
    }

    /**
     * @return String return the name
     */
    public String getName() {
        return name;
    }

    /**
     * @param name the name to set
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * @return LocalDate return the publishedDate
     */
    public LocalDate getPublishedDate() {
        return publishedDate;
    }

    /**
     * @param publishedDate the publishedDate to set
     */
    public void setPublishedDate(LocalDate publishedDate) {
        this.publishedDate = publishedDate;
    }

    /**
     * @return String return the description
     */
    public String getDescription() {
        return description;
    }

    /**
     * @param description the description to set
     */
    public void setDescription(String description) {
        this.description = description;
    }

    /**
     * @return List<String> return the coverIds
     */
    public List<String> getCoverIds() {
        return coverIds;
    }

    /**
     * @param coverIds the coverIds to set
     */
    public void setCoverIds(List<String> coverIds) {
        this.coverIds = coverIds;
    }

    /**
     * @return List<String> return the authorNames
     */
    public List<String> getAuthorNames() {
        return authorNames;
    }

    /**
     * @param authorNames the authorNames to set
     */
    public void setAuthorNames(List<String> authorNames) {
        this.authorNames = authorNames;
    }

    /**
     * @return List<String> return the authorIds
     */
    public List<String> getAuthorIds() {
        return authorIds;
    }

    /**
     * @param authorIds the authorIds to set
     */
    public void setAuthorIds(List<String> authorIds) {
        this.authorIds = authorIds;
    }

}
