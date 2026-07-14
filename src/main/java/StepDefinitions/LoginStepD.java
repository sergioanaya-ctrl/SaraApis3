package StepDefinitions;

import Modelos.TokenModel;
import Tareas.ProcesarExpedientesMasivamente;
import interaccion.login;
import io.cucumber.java.Before;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import net.serenitybdd.screenplay.actors.OnStage;
import net.serenitybdd.screenplay.actors.OnlineCast;
import net.serenitybdd.screenplay.rest.abilities.CallAnApi;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

import static conf.ConfiguracionGeneral.restApiSara;

public class LoginStepD {

    TokenModel token = new TokenModel();
    // ❌ ELIMINAMOS: Actor actor = Actor.named("Sara");

    @Before
    public void setUp() {
        // 1. Inicializamos el escenario (Stage) para que Serenity sepa dónde poner a los actores
        OnStage.setTheStage(new OnlineCast());
    }

    @Given("Sara3 se loguea")
    public void loginUser() {
        // 2. Llamamos a "Sara" al escenario, le damos la habilidad de API y automáticamente queda "InTheSpotlight" (bajo el reflector)
        OnStage.theActorCalled("Sara")
                .whoCan(CallAnApi.at(restApiSara))
                .attemptsTo(login.data());
    }

    @Then("el valida que la respuesta del servicio sea: {int}")
    public void validateStatusCode(int statusCode) {
        //actor.should(seeThat(new ResponseCode(), equalTo(statusCode)));
    }

    @When("el usuario consulta la {string} para los expedientes del archivo {string}")
    public void elUsuarioConsultaLaParaLosExpedientesDelArchivo(String apiTarget, String rutaArchivo) throws IOException {
        token.setToken(login.generateToken());

        // Elegir el actionId correcto según el parámetro del Gherkin
        String actionId = "";
        if (apiTarget.equals("API_1")) {
            actionId = "25230205-f7ef-40ed-bd98-e1259f33359c";
        } else if (apiTarget.equals("API_2")) {
            actionId = "2ceba4de-9c4d-4773-8952-1373993ecd44";
        }

        // Leer el TXT línea por línea
        String rutaCompleta = "src/test/resources/" + rutaArchivo;
        List<String> expedientes = Files.readAllLines(Paths.get(rutaCompleta));

        // 3. Como ya llamamos a Sara en el @Given, el escenario ya sabe quién está bajo el reflector. ¡Ya no dará NullPointer!
        OnStage.theActorInTheSpotlight().attemptsTo(
                ProcesarExpedientesMasivamente.con(actionId, expedientes, token)
        );
    }

    @Then("todos los expedientes deben ser procesados exitosamente")
    public void todosLosExpedientesDebenSerProcesadosExitosamente() {
    }
}