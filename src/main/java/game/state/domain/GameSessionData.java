package game.state.domain;

import game.domain.personaje.Personaje;
import game.dungeon.model.Dungeon;
import game.dungeon.theme.DungeonThemeFactory;
import game.items.model.ContainerItem;

/**
 * Encapsula el estado compartido de la sesión del juego.
 * Proporciona acceso a datos que múltiples estados de dominio necesitan.
 * 
 * Esta clase permite que los estados de dominio sean independientes de
 * InteractiveGame y sean fácilmente reutilizables en otros contextos.
 */
public class GameSessionData {
    
    private Personaje heroe;
    private Dungeon mazmorra;
    private DungeonThemeFactory temaActual;
    private ContainerItem inventario;
    private int salaActual;
    private int enemigosDerrota;
    private int oroAcumulado;
    private boolean defensaHeroeActiva;
    private int turnosVenenoHeroe;
    private int danioVenenoHeroe;
    
    // Constructor vacío para inicialización gradual
    public GameSessionData() {
    }
    
    // Getters y Setters
    
    public Personaje getHeroe() {
        return heroe;
    }
    
    public void setHeroe(Personaje heroe) {
        this.heroe = heroe;
    }
    
    public Dungeon getMazmorra() {
        return mazmorra;
    }
    
    public void setMazmorra(Dungeon mazmorra) {
        this.mazmorra = mazmorra;
    }
    
    public DungeonThemeFactory getTemaActual() {
        return temaActual;
    }
    
    public void setTemaActual(DungeonThemeFactory temaActual) {
        this.temaActual = temaActual;
    }
    
    public ContainerItem getInventario() {
        return inventario;
    }
    
    public void setInventario(ContainerItem inventario) {
        this.inventario = inventario;
    }
    
    public int getSalaActual() {
        return salaActual;
    }
    
    public void setSalaActual(int salaActual) {
        this.salaActual = salaActual;
    }
    
    public int getEnemigosDerrota() {
        return enemigosDerrota;
    }
    
    public void setEnemigosDerrota(int enemigosDerrota) {
        this.enemigosDerrota = enemigosDerrota;
    }
    
    public int getOroAcumulado() {
        return oroAcumulado;
    }
    
    public void setOroAcumulado(int oroAcumulado) {
        this.oroAcumulado = oroAcumulado;
    }
    
    public boolean isDefensaHeroeActiva() {
        return defensaHeroeActiva;
    }
    
    public void setDefensaHeroeActiva(boolean defensaHeroeActiva) {
        this.defensaHeroeActiva = defensaHeroeActiva;
    }
    
    public int getTurnosVenenoHeroe() {
        return turnosVenenoHeroe;
    }
    
    public void setTurnosVenenoHeroe(int turnosVenenoHeroe) {
        this.turnosVenenoHeroe = turnosVenenoHeroe;
    }
    
    public int getDanioVenenoHeroe() {
        return danioVenenoHeroe;
    }
    
    public void setDanioVenenoHeroe(int danioVenenoHeroe) {
        this.danioVenenoHeroe = danioVenenoHeroe;
    }
    
    /**
     * Reinicia todos los valores para una nueva partida
     */
    public void reiniciarParaNuevaPartida() {
        this.salaActual = 0;
        this.enemigosDerrota = 0;
        this.oroAcumulado = 0;
        this.turnosVenenoHeroe = 0;
        this.danioVenenoHeroe = 0;
        this.defensaHeroeActiva = false;
    }
}
