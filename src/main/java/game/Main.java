package game;

import game.combat.engine.MotorCombate;
import game.domain.personaje.EnemigoBasico;
import game.domain.personaje.Guerrero;
import game.domain.personaje.Personaje;

public class Main {
	public static void main(String[] args) {
		Guerrero guerrero = new Guerrero("Heroe", 50, 10);
		EnemigoBasico enemigo = new EnemigoBasico("Goblin", 40, 8);
		MotorCombate motorCombate = new MotorCombate(guerrero, enemigo);

		Personaje ganador = motorCombate.iniciar();
		System.out.println("Ganador: " + ganador.getNombre());
	}
}
