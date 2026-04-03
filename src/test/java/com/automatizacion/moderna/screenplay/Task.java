package com.automatizacion.moderna.screenplay;

/**
 * Interfaz marcadora que extiende {@link Performable}.
 * <p>
 * Toda clase que implemente {@code Task} se ve OBLIGADA por el compilador
 * a sobreescribir el método {@link #performAs(com.automatizacion.moderna.actors.Actor)},
 * replicando el comportamiento del patrón Screenplay de Serenity BDD.
 * </p>
 *
 * <pre>
 * Ejemplo de uso:
 *
 *   public class MiTarea implements Task {
 *
 *       public static MiTarea ahora() {
 *           return new MiTarea();
 *       }
 *
 *       {@literal @}Override
 *       public void performAs(Actor actor) {
 *           // lógica de la tarea
 *       }
 *   }
 * </pre>
 */
public interface Task extends Performable {
    // Interfaz marcadora: hereda performAs(Actor actor) de Performable.
    // Al implementar Task, el compilador exige sobreescribir performAs.
}

