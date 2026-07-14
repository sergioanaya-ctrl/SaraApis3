Feature: Loguerse y enviar los resflow

  @login
  Scenario: Ejecutar API 1 y API 2 para múltiples expedientes
    Given Sara3 se loguea
    When el usuario consulta la "API_1" para los expedientes del archivo "Datos/expedientes.txt"
    And el usuario consulta la "API_2" para los expedientes del archivo "Datos/expedientes.txt"
    Then todos los expedientes deben ser procesados exitosamente
