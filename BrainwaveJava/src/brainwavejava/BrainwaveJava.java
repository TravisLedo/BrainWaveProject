/*
 * This Java program will create a continuous graph from the Brainwaves generated by the Arduino over serial
 */
package brainwavejava;
import com.fazecast.jSerialComm.*;
import javafx.event.ActionEvent;
import javafx.geometry.Rectangle2D;
import javafx.scene.Group;
import javafx.scene.SceneAntialiasing;
import javafx.scene.control.Button;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Screen;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.ComboBox;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.stage.Stage;
import java.text.SimpleDateFormat;
import java.util.Date;
import javafx.geometry.Insets;
import javafx.geometry.Pos;

public class BrainwaveJava extends Application {

    final int MaxPoints = 10; //max number of readings on screen before removing oldest one from graphs
    // this is used to display time in HH:mm:ss format
    final SimpleDateFormat simpleDateFormat = new SimpleDateFormat("HH:mm:ss");
    XYChart.Series<String, Number> seriesAttention; //holds all the plot points for attention
    XYChart.Series<String, Number> seriesMeditation; //holds all the plot points for Meditation

    //variable holding sizes of entire program window
    private static double WIDTH;
    private static double HEIGHT;

    Group rootMain; 

    //creating a list of serial ports available
    ObservableList<String> serialItems;
    ComboBox serialComboBox;
    SerialPort[] availableSerialPorts;
    SerialPort selectedPort;

    //used for concatingnating serial readings
    String mergedText = "";


    @Override
    public void start(Stage primaryStage) throws Exception {

        Rectangle2D primaryScreenBounds = Screen.getPrimary().getVisualBounds(); //use to get the users resolution to match the window size

        //set Stage boundaries to visible bounds of the main screen
        WIDTH = primaryScreenBounds.getWidth();
        HEIGHT = primaryScreenBounds.getHeight();

        //creating attention graph
        final CategoryAxis xAxis = new CategoryAxis(); // using time for X
        final NumberAxis yAxis = new NumberAxis();
        yAxis.setAutoRanging(false);

        xAxis.setLabel("Time/s");
        xAxis.setAnimated(false); // remove axis animations 
        yAxis.setLabel("Value");
        yAxis.setAnimated(false); // remove axis animations

        //creating the line chart with two axis created above
        final LineChart<String, Number> LineChart = new LineChart<>(xAxis, yAxis);
        LineChart.setTitle("Brainwaves");
        LineChart.setAnimated(false); // disable animations

        //defining a seriesAttention to display data
        seriesAttention = new XYChart.Series<>();
        seriesAttention.setName("Attention");

        //defining a seriesAttention to display data
        seriesMeditation = new XYChart.Series<>();
        seriesMeditation.setName("Meditation");

        // add seriesAttention to chart
        LineChart.getData().add(seriesAttention);
        LineChart.getData().add(seriesMeditation);

        LineChart.setMinHeight(HEIGHT * 0.8);
        LineChart.setMinWidth(WIDTH * .98);

        primaryStage.setX(primaryScreenBounds.getMinX());
        primaryStage.setY(primaryScreenBounds.getMinY());
        primaryStage.setWidth(WIDTH);
        primaryStage.setHeight(HEIGHT);

        serialItems = FXCollections.observableArrayList(); //items for dropdown of serial ports
        serialComboBox = new ComboBox(serialItems); //drop down menu for serial ports
        serialComboBox.setPromptText("Select Serial Port");

        Button scanButton = new Button("Scan");
        scanButton.setOnAction((ActionEvent e) -> { 
            Scan(serialComboBox.getSelectionModel().getSelectedIndex());
        });

        //creating/adjustiing layouts
        VBox vbox = new VBox();
        HBox graphsHbox = new HBox(LineChart);
        HBox serialHbox = new HBox(serialComboBox, scanButton);
        vbox.getChildren().add(graphsHbox);
        vbox.getChildren().add(serialHbox);
        graphsHbox.setAlignment(Pos.CENTER);
        serialHbox.setAlignment(Pos.CENTER);
        vbox.setAlignment(Pos.CENTER);
        vbox.setPadding(new Insets(50, 0, 0, 0));

        // scene setup
        rootMain = new Group(vbox);
        Scene scene = new Scene(rootMain, WIDTH, HEIGHT, true, SceneAntialiasing.BALANCED);
        primaryStage.setScene(scene);
        primaryStage.setTitle("Brainwave Project");
        primaryStage.show();

        loadSerialPorts(); //initial port load

    } //end of start method

    public static void main(String[] args) {
        launch(args);
    }

    public void PlotAPoint(int attentionX, int meditationX) //render a point for both lines
    {
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                Date now = new Date();
                //plot attention point
                seriesAttention.getData().add(new XYChart.Data<>(simpleDateFormat.format(now), attentionX));
                if (seriesAttention.getData().size() > MaxPoints) //remove last value if graph too long
                {
                    seriesAttention.getData().remove(0);
                }
                //plot meditation point
                seriesMeditation.getData().add(new XYChart.Data<>(simpleDateFormat.format(now), meditationX));
                if (seriesMeditation.getData().size() > MaxPoints) //remove last value if graph too long
                {
                    seriesMeditation.getData().remove(0);
                }

            }
        });

    }

    public void loadSerialPorts() { //run at startup
        availableSerialPorts = SerialPort.getCommPorts(); //Get array of all ports on system. 
        for (SerialPort availableSerialPort : availableSerialPorts) {
            //  System.out.println(availableSerialPorts[i].getSystemPortName());
            serialItems.add(availableSerialPort.getSystemPortName());
        }
    }

    public void Scan(int index) {
        if (index == -1) {
            // user did not select port
        } else {
            selectedPort = availableSerialPorts[index];
            selectedPort.openPort();
            selectedPort.setBaudRate(57600);
            selectedPort.addDataListener(new SerialPortDataListener() {
                @Override
                public int getListeningEvents() {
                    return SerialPort.LISTENING_EVENT_DATA_AVAILABLE;
                }

                @Override
                public void serialEvent(SerialPortEvent event) {

                    if (event.getEventType() != SerialPort.LISTENING_EVENT_DATA_AVAILABLE) {
                        return;
                    }

                    byte[] newData = new byte[selectedPort.bytesAvailable()];
                    int byteSize = selectedPort.readBytes(newData, newData.length);
                    String text = new String(newData);
                    
                    //  System.out.println("New Text: " + text);
                    if (text.contains(".*[a-z].*")) {
                        text = "";
                    }

                    //will print only once we get entire line of text 
                    if (mergedText.length() < 12) {
                        mergedText = mergedText + text;
                        if (mergedText.length() >= 12) {
                            System.out.print(mergedText); //for visual reference
                            String[] values = mergedText.split(",");
                            //parse data
                            int signalQuality = Integer.parseInt(values[0].replaceAll("[^0-9]", "").trim());
                            int attentionValue = Integer.parseInt(values[1].replaceAll("[^0-9]", "").trim());
                            int meditationValue = Integer.parseInt(values[2].replaceAll("[^0-9]", "").trim());

                            if (attentionValue > 100) {
                                attentionValue = 100;
                            }
                            if (meditationValue > 100) {
                                meditationValue = 100;
                            }

                            //plot point only if there are no noises
                            if (signalQuality == 0) {
                                PlotAPoint(attentionValue, meditationValue);
                            } else {
                                System.out.println("Poor signal, skipped point");
                            }

                            String signalString = "1";
                            byte[] signalByte = signalString.getBytes();
                            selectedPort.writeBytes(signalByte, signalByte.length);

                            mergedText = new String();
                            //   mergedByteSize = 0;
                        }

                    } else {
   
                        System.out.print(mergedText); //for visual reference
                        String[] values = mergedText.split(",");
                        //parse data
                        int signalQuality = Integer.parseInt(values[0].replaceAll("[^0-9]", "").trim());
                        int attentionValue = Integer.parseInt(values[1].replaceAll("[^0-9]", "").trim());
                        int meditationValue = Integer.parseInt(values[2].replaceAll("[^0-9]", "").trim());

                        if (attentionValue > 100) {
                            attentionValue = 100;
                        }
                        if (meditationValue > 100) {
                            meditationValue = 100;
                        }
                        //plot point only if there are no noises
                        if (signalQuality == 0) {
                            PlotAPoint(attentionValue, meditationValue);
                        } else {
                            System.out.println("Poor signal, skipped point");
                        }
                        
                        String signalString = "0";
                        byte[] signalByte = signalString.getBytes();
                        selectedPort.writeBytes(signalByte, signalByte.length);
                        mergedText = new String();
                        //  mergedByteSize = 0;
                    }

                }

            });

        }

    }

    @Override
    public void stop() throws Exception {
        super.stop();
    }

}
