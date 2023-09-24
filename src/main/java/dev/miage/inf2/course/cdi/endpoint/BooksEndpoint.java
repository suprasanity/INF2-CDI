package dev.miage.inf2.course.cdi.endpoint;

import dev.miage.inf2.course.cdi.domain.BookShop;
import dev.miage.inf2.course.cdi.model.Book;
import dev.miage.inf2.course.cdi.model.Customer;
import info.schnatterer.mobynamesgenerator.MobyNamesGenerator;
import io.quarkus.qute.CheckedTemplate;
import io.quarkus.qute.TemplateInstance;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.context.Dependent;
import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

import java.net.URI;
import java.net.URISyntaxException;
import java.net.URLEncoder;
import java.nio.charset.Charset;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

@Dependent
@Path("book")
public class BooksEndpoint {


    @Inject
    BookShop bookShop;

    @CheckedTemplate
    public static class Templates {
        public static native TemplateInstance booklist(Collection<Book> books);

        public static native TemplateInstance formNew();
    }

    @Path("all")
    @GET
    @Produces(MediaType.TEXT_HTML)
    public TemplateInstance getBooks() {
        return Templates.booklist(bookShop.getAllItems());
    }

    @Path("{isbn}")
    @GET
    @Produces(MediaType.TEXT_HTML)
    public TemplateInstance getBook(@PathParam("isbn") String isbn) {
        Optional<Book> book = bookShop.getAllItems().stream().filter(b -> b.isbn().equals(isbn)).findAny();
        if (book.isEmpty()) {
            throw new WebApplicationException(404);
        } else {
            return Templates.booklist(List.of(book.get()));
        }
    }

    @Path("{isbn}")
    @DELETE
    @Produces(MediaType.TEXT_HTML)
    public TemplateInstance buybook(@PathParam("isbn") String isbn) {
        bookShop.sell(new Customer(MobyNamesGenerator.getRandomName(), MobyNamesGenerator.getRandomName(), "toto@miage.dev", "+3395387845",12),isbn);
        return Templates.booklist(bookShop.getAllItems());
    }

    @Path("form-new")
    @GET
    @Produces(MediaType.TEXT_HTML)
    public TemplateInstance newBookForm() {
        return Templates.formNew();
    }

    @POST
    @Produces(MediaType.TEXT_HTML)
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    public Response newbook(@FormParam("author") String author, @FormParam("title") String title, @FormParam("isbn") String isbn) throws URISyntaxException {
        Book book = new Book(author, title, isbn);
        bookShop.stock(book);
        return Response.seeOther(new URI("/book/all")).build();
    }
}
