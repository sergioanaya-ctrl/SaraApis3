package interaccion;

import Util.Post;
import builders.CreateSessionBuilder;
import io.restassured.http.ContentType;
import net.serenitybdd.core.Serenity;
import net.serenitybdd.rest.SerenityRest;
import net.serenitybdd.screenplay.Actor;
import net.serenitybdd.screenplay.Interaction;

import static net.serenitybdd.screenplay.Tasks.instrumented;

public class login implements Interaction {
    @Override
    public <T extends Actor> void performAs(T actor) {
        // Realiza el intento de login y guarda el token en la sesión
        actor.attemptsTo(Post.to("/auth-cognito")
                .with(requestSpecification -> requestSpecification
                        .contentType(ContentType.JSON)
                        .header("x-client-fingerprint", "2a4b759a58af9d3c1dcd970b9fba4c7d055b7ffcc30adcbc7cfd0be554d6ed68")
                        .header("x-tenant-id","fcfe80af-4e8f-4665-b02e-57d61272eaf1")
                        .body(CreateSessionBuilder.getToken())));

        String id_token = extraerToken();
        Serenity.setSessionVariable("id_token").to(id_token);
    }

    public static login data() {
        return instrumented(login.class);
    }

    private String extraerToken() {
        // Intenta obtener el token de la respuesta
        try {
            String tokenSession = SerenityRest.lastResponse().body().jsonPath().getString("data.id_token");
            System.out.println(tokenSession);
            return tokenSession;
        } catch (Exception e) {
            // Manejo de errores al extraer el token
            System.err.println("Error al extraer el token: " + e.getMessage());
            throw new RuntimeException("No se pudo obtener el token de sesión.");
        }
    }

    public static String generateToken() {
        // Recupera el token de la sesión
        return Serenity.sessionVariableCalled("id_token");
    }
}


