package org.sara.api.interaccion;

import org.sara.api.Modelos.TokenModel;
import org.sara.api.Util.ApiExecutionReportUtil;
import org.sara.api.Util.Post;
import io.restassured.http.ContentType;
import net.serenitybdd.rest.SerenityRest;
import lombok.AllArgsConstructor;
import net.serenitybdd.screenplay.Actor;
import net.serenitybdd.screenplay.Interaction;

import static net.serenitybdd.screenplay.Tasks.instrumented;
@AllArgsConstructor
public class ConsultarApiExpediente implements Interaction {

    private final String actionId;
    private final String expediente;
    private final TokenModel token;
    @Override
    public <T extends Actor> void performAs(T actor) {
// 2. Armar el body JSON dinámicamente usando String.format
        String bodyPeticion = String.format("{\n" +
                "    \"type\": \"filter\",\n" +
                "    \"action_id\": \"%s\",\n" +
                "    \"evaluate_conditions\": false,\n" +
                "    \"search_filter\": {\n" +
                "        \"filter\": [\n" +
                "            {\n" +
                "                \"operator\": \"in\",\n" +
                "                \"value\": \"%s\",\n" + // Aquí se inyecta el expediente
                "                \"type\": \"AND\",\n" +
                "                \"field\": \"cases.external_code\"\n" +
                "            }\n" +
                "        ],\n" +
                "        \"limit\": 0,\n" +
                "        \"offset\": 0\n" +
                "    },\n" +
                "    \"separator\": \",\"\n" +
                "}", actionId, expediente);

        // 3. Ejecutar la petición POST con todos los Headers y el Body
        actor.attemptsTo(
                Post.to("/action-jobs") // Reemplaza por el endpoint real
                        .with(request -> request
                                .contentType(ContentType.JSON)
                                .header("x-client-fingerprint", "2a4b759a58af9d3c1dcd970b9fba4c7d055b7ffcc30adcbc7cfd0be554d6ed68")
                                .header("x-tenant-id", "fcfe80af-4e8f-4665-b02e-57d61272eaf1")
                                // Agregamos el token a la cabecera (Valida si tu API usa "Authorization" o "Bearer")
                                .header("Authorization", token.getToken())
                                .body(bodyPeticion)
                        )
        );

        int statusCode = SerenityRest.lastResponse().statusCode();
        String responseBody = SerenityRest.lastResponse().asString();
        ApiExecutionReportUtil.record(expediente, actionId, statusCode, responseBody);
    }





    public static ConsultarApiExpediente con(String actionId, String expediente, TokenModel token) {
        return instrumented(ConsultarApiExpediente.class, actionId, expediente, token);
    }
}
