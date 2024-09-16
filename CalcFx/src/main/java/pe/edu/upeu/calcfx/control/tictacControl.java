package pe.edu.upeu.calcfx.control;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import org.springframework.stereotype.Component;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;


@Component
public class tictacControl {

    Button[][] tablero;
    boolean turno = true;
    boolean jugando = false;

    int puntajeJugador1 = 0;
    int puntajeJugador2 = 0;

    @FXML
    Button btn00, btn01, btn02, btn10, btn11, btn12, btn20, btn21, btn22, startButton, cancelButton;
    boolean turnoABoolean=true;

    @FXML
    TextField nomIngre1, nomIngre2;

    @FXML
    Label diceturno, diceelpuntajedel1, diceelpuntajedel2;

    @FXML
    TableView<Partida> tablaPuntajes;

    @FXML
    TableColumn<Partida, String> colum1, colum2, colum3, colum4, colum5, colum6;

    private ObservableList<Partida> partidasList = FXCollections.observableArrayList(); // Almacena todas las partidas
    private Partida partidaActual;

    @FXML
    public void initialize() {
        tablero = new Button[][]{
                {btn00, btn01, btn02},
                {btn10, btn11, btn12},
                {btn20, btn21, btn22}
        };
        colum1.setCellValueFactory(new PropertyValueFactory<>("nombrePartido"));
        colum2.setCellValueFactory(new PropertyValueFactory<>("nombreJugador1"));
        colum3.setCellValueFactory(new PropertyValueFactory<>("nombreJugador2"));
        colum4.setCellValueFactory(new PropertyValueFactory<>("ganador"));
        colum5.setCellValueFactory(new PropertyValueFactory<>("puntuacion"));
        colum6.setCellValueFactory(new PropertyValueFactory<>("estado"));

        tablaPuntajes.setItems(partidasList);

        desactivarBotonesTablero();
        actualizarPuntajes();
    }

    @FXML
    void accionButton(ActionEvent e) {
        if (!jugando) return;

        Button b = (Button) e.getSource();

        if (!b.getText().equals("")) {
            return;
        }

        b.setText(turno ? "X" : "O");

        if (verificarGanador()) {
            String ganador = turno ? nomIngre1.getText() : nomIngre2.getText();
            mostrarGanador(ganador);
            deshabilitarTablero();
            actualizarPartida(ganador, 1);
            incrementarPuntaje(ganador);
            jugando = false;

            limpiarTablero();
            startButton.setDisable(false);
            cancelButton.setDisable(true);
        } else if (esEmpate()) {
            mostrarEmpate();
            deshabilitarTablero();
            actualizarPartida("Empate", 0);
            jugando = false;

            limpiarTablero();
            startButton.setDisable(false);
            cancelButton.setDisable(true);
        }

        turno = !turno;
        actualizarTurno();
    }

    @FXML
    void imprimir() {
        for (int i = 0; i < tablero.length; i++) {
            for (int j = 0; j < tablero[i].length; j++) {
                System.out.print(tablero[i][j].getText() + "\t");
            }
            System.out.println();
        }
    }

    @FXML
    void iniciarPartida() {
        if (nomIngre1.getText().isEmpty() || nomIngre2.getText().isEmpty()) {
            mostrarAlerta("Error", "Debe ingresar los nombres de los jugadores");
            return;
        }

        limpiarTablero();
        activarBotonesTablero();
        jugando = true;
        startButton.setDisable(true);
        cancelButton.setDisable(false);

        partidaActual = new Partida("Partida " + (partidasList.size() + 1),
                nomIngre1.getText(), nomIngre2.getText(), "Jugando", 0);
        partidasList.add(partidaActual);
        tablaPuntajes.refresh();

        turno = true;
        actualizarTurno();
    }

    @FXML
    void anularPartida() {
        if (!jugando) return;

        desactivarBotonesTablero();
        startButton.setDisable(false);
        cancelButton.setDisable(true);
        jugando = false;

        if (partidaActual != null) {
            partidaActual.setEstado("Anulado");
            partidaActual.setPuntuacion(0);
            partidaActual = null;
            tablaPuntajes.refresh();
        }
        limpiarTablero();
    }

    private void deshabilitarTablero() {
        for (Button[] fila : tablero) {
            for (Button boton : fila) {
                boton.setDisable(true);
            }
        }
    }

    private boolean verificarGanador() {
        for (int i = 0; i < 3; i++) {
            if (!tablero[i][0].getText().equals("") && tablero[i][0].getText().equals(tablero[i][1].getText()) &&
                    tablero[i][1].getText().equals(tablero[i][2].getText())) {
                return true;
            }
            if (!tablero[0][i].getText().equals("") && tablero[0][i].getText().equals(tablero[1][i].getText()) &&
                    tablero[1][i].getText().equals(tablero[2][i].getText())) {
                return true;
            }
        }
        if (!tablero[0][0].getText().equals("") && tablero[0][0].getText().equals(tablero[1][1].getText()) &&
                tablero[1][1].getText().equals(tablero[2][2].getText())) {
            return true;
        }
        if (!tablero[0][2].getText().equals("") && tablero[0][2].getText().equals(tablero[1][1].getText()) &&
                tablero[1][1].getText().equals(tablero[2][0].getText())) {
            return true;
        }
        return false;
    }

    private boolean esEmpate() {
        for (Button[] fila : tablero) {
            for (Button boton : fila) {
                if (boton.getText().equals("")) {
                    return false;
                }
            }
        }
        return true;
    }

    private void limpiarTablero() {
        for (Button[] fila : tablero) {
            for (Button boton : fila) {
                boton.setText("");
            }
        }
    }

    private void activarBotonesTablero() {
        for (Button[] fila : tablero) {
            for (Button boton : fila) {
                boton.setDisable(false);
            }
        }
    }

    private void desactivarBotonesTablero() {
        for (Button[] fila : tablero) {
            for (Button boton : fila) {
                boton.setDisable(true);
            }
        }
    }

    private void actualizarPartida(String ganador, int puntuacion) {
        if (partidaActual != null) {
            partidaActual.setGanador(ganador);
            partidaActual.setPuntuacion(puntuacion);
            partidaActual.setEstado("Terminado");
            tablaPuntajes.refresh();
        }
    }

    private void incrementarPuntaje(String ganador) {
        if (ganador.equals(nomIngre1.getText())) {
            puntajeJugador1++;
        } else if (ganador.equals(nomIngre2.getText())) {
            puntajeJugador2++;
        }
        actualizarPuntajes();
    }

    private void actualizarTurno() {
        String jugadorEnTurno = turno ? nomIngre1.getText() : nomIngre2.getText();
        diceturno.setText("Turno de: " + jugadorEnTurno);
    }

    private void actualizarPuntajes() {
        diceelpuntajedel1.setText( puntajeJugador1 + " victorias");
        diceelpuntajedel2.setText( puntajeJugador2 + " victorias");
    }

    private void mostrarGanador(String ganador) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Ganador");
        alert.setHeaderText(null);
        alert.setContentText("¡" + ganador + " ha ganado!");
        alert.showAndWait();
    }

    private void mostrarEmpate() {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Empate");
        alert.setHeaderText(null);
        alert.setContentText("¡Es un empate!");
        alert.showAndWait();
    }

    private void mostrarAlerta(String titulo, String mensaje) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(titulo);
        alert.setHeaderText(null);
        alert.setContentText(mensaje);
        alert.showAndWait();
    }

    public static class Partida {
        private String nombrePartido;
        private String nombreJugador1;
        private String nombreJugador2;
        private String ganador;
        private int puntuacion;
        private String estado;

        public Partida(String nombrePartido, String nombreJugador1, String nombreJugador2, String estado, int puntuacion) {
            this.nombrePartido = nombrePartido;
            this.nombreJugador1 = nombreJugador1;
            this.nombreJugador2 = nombreJugador2;
            this.ganador = "";
            this.puntuacion = puntuacion;
            this.estado = estado;
        }

        public String getNombrePartido() {
            return nombrePartido;
        }

        public void setNombrePartido(String nombrePartido) {
            this.nombrePartido = nombrePartido;
        }

        public String getNombreJugador1() {
            return nombreJugador1;
        }

        public void setNombreJugador1(String nombreJugador1) {
            this.nombreJugador1 = nombreJugador1;
        }

        public String getNombreJugador2() {
            return nombreJugador2;
        }

        public void setNombreJugador2(String nombreJugador2) {
            this.nombreJugador2 = nombreJugador2;
        }

        public String getGanador() {
            return ganador;
        }

        public void setGanador(String ganador) {
            this.ganador = ganador;
        }

        public int getPuntuacion() {
            return puntuacion;
        }

        public void setPuntuacion(int puntuacion) {
            this.puntuacion = puntuacion;
        }

        public String getEstado() {
            return estado;
        }

        public void setEstado(String estado) {
            this.estado = estado;
        }
    }
}