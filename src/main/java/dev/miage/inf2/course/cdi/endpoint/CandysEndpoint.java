package dev.miage.inf2.course.cdi.endpoint;

import dev.miage.inf2.course.cdi.domain.BookShop;
import dev.miage.inf2.course.cdi.domain.CandyShop;
import dev.miage.inf2.course.cdi.model.Book;
import dev.miage.inf2.course.cdi.model.Candy;
import dev.miage.inf2.course.cdi.model.Customer;
import info.schnatterer.mobynamesgenerator.MobyNamesGenerator;
import io.quarkus.qute.CheckedTemplate;
import io.quarkus.qute.TemplateInstance;
import jakarta.enterprise.context.Dependent;
import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

@Dependent
@Path("candy")
public class CandysEndpoint {


    @Inject
    CandyShop candyShop;

    @CheckedTemplate
    public static class Templates {
        public static native TemplateInstance candylist(Collection<Candy> candys);

        public static native TemplateInstance formNew();

    }

    @Path("all")
    @GET
    @Produces(MediaType.TEXT_HTML)
    public TemplateInstance getCandys() {
        return Templates.candylist(candyShop.getAllItems());
    }

    @Path("{id}")
    @GET
    @Produces(MediaType.TEXT_HTML)
    public TemplateInstance getCandy(@PathParam("id") String id) {
        Optional<Candy> candy = candyShop.getAllItems().stream().filter(b -> b.id().equals(id)).findAny();
        if (candy.isEmpty()) {
            throw new WebApplicationException(404);
        } else {
            return Templates.candylist(List.of(candy.get()));
        }
    }

    @Path("{id}")
    @DELETE
    @Produces(MediaType.TEXT_HTML)
    public TemplateInstance buycandy(@PathParam("id") String id) {
        candyShop.sell(new Customer(MobyNamesGenerator.getRandomName(), MobyNamesGenerator.getRandomName(), "toto@miage.dev", "+3395387845",12),id);
        return Templates.candylist(candyShop.getAllItems());
    }

    @Path("form-new")
    @GET
    @Produces(MediaType.TEXT_HTML)
    public TemplateInstance newCandyForm() {
        return Templates.formNew();
    }

    @POST
    @Produces(MediaType.TEXT_HTML)
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    public Response newcandy(@FormParam("id") String id, @FormParam("flavor") String flavor, @FormParam("weight") int weight) throws URISyntaxException {
        Candy candy = new Candy(id, flavor, weight);
        candyShop.stock(candy);
        return Response.seeOther(new URI("/candy/all")).build();
    }

}
