package org.sara.api.Tareas;

import org.sara.api.Modelos.TokenModel;
import org.sara.api.interaccion.ConsultarApiExpediente;
import net.serenitybdd.screenplay.Actor;
import net.serenitybdd.screenplay.Task;
import java.util.List;
import static net.serenitybdd.screenplay.Tasks.instrumented;

public class ProcesarExpedientesMasivamente implements Task {

    private final String actionId;
    private final List<String> expedientes;
    private final TokenModel token;

    public ProcesarExpedientesMasivamente(String actionId, List<String> expedientes, TokenModel token) {
        this.actionId = actionId;
        this.expedientes = expedientes;
        this.token = token;
    }

    public static ProcesarExpedientesMasivamente con(String actionId, List<String> expedientes, TokenModel token) {
        return instrumented(ProcesarExpedientesMasivamente.class, actionId, expedientes, token);
    }

    @Override
    public <T extends Actor> void performAs(T actor) {
        // Aquí iteramos sobre las 2000 líneas leídas del TXT
        for (String expedienteActual : expedientes) {
            actor.attemptsTo(
                    ConsultarApiExpediente.con(actionId, expedienteActual, token)
            );
        }
    }
}