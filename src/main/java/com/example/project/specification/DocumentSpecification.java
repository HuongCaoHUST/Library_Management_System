package com.example.project.specification;

import com.example.project.model.Document;
import org.springframework.data.jpa.domain.Specification;

public class DocumentSpecification {

    public static Specification<Document> hasTitle(String title) {
        return (root, query, cb) ->
                title == null || title.isEmpty()
                        ? cb.conjunction()
                        : cb.like(cb.lower(root.get("title")), "%" + title.toLowerCase() + "%");
    }

    public static Specification<Document> hasAuthor(String author) {
        return (root, query, cb) ->
                author == null || author.isEmpty()
                        ? cb.conjunction()
                        : cb.like(cb.lower(root.get("author")), "%" + author.toLowerCase() + "%");
    }

    public static Specification<Document> hasPublisher(String publisher) {
        return (root, query, cb) ->
                publisher == null || publisher.isEmpty()
                        ? cb.conjunction()
                        : cb.like(cb.lower(root.get("publisher")), "%" + publisher.toLowerCase() + "%");
    }

    public static Specification<Document> hasPublicationYear(Integer publicationYear) {
        return (root, query, cb) ->
                publicationYear == null
                        ? cb.conjunction()
                        : cb.equal(root.get("publicationYear"), publicationYear);
    }
}
