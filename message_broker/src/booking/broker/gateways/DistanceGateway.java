package booking.broker.gateways;

import booking.client.model.Address;
import org.glassfish.jersey.client.ClientConfig;

import javax.json.Json;
import javax.json.JsonArray;
import javax.json.JsonObject;
import javax.json.JsonReader;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Invocation;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriBuilder;
import java.io.StringReader;
import java.net.URI;

public class DistanceGateway {
    //connect to google maps distance matrix api
    ClientConfig config = new ClientConfig();
    Client client = ClientBuilder.newClient(config);
    URI baseURI = UriBuilder.fromUri("https://maps.googleapis.com/maps/api/distancematrix").build();
    WebTarget serviceTarget = client.target(baseURI);

    public String getTransferDistance(String destinationAirport, Address transferAddress){
        //https://maps.googleapis.com/maps/api/distancematrix/json?units=metric&origins=Washington,DC&destinations=New+York+City,NY&key=YOUR_API_KEY
        //api key
        String key = "AIzaSyCdFKLrfe4q8_Z3r43BVnreXTriTJ7YPyE";
        WebTarget methodTarget = serviceTarget.path("json")
                .queryParam("units","metric")
                .queryParam("origins", destinationAirport)
                .queryParam("destinations", transferAddress.toString())
                .queryParam("key", key);
        Invocation.Builder requestBuilder = methodTarget.request().accept(MediaType.APPLICATION_JSON_TYPE);
        Response response = requestBuilder.get();
        String distance = response.readEntity(String.class);
        System.out.println("The distance is " + distance);
        return distance;
    }

    public static void main(String[] args){
        DistanceGateway gateway = new DistanceGateway();
        String destinationAirport = "Heathrow";
        String street = "Portman Square";
        int number = 30;
        String city = "London";
        Address transferAddress = new Address(street, number, city);
        String result = gateway.getTransferDistance(destinationAirport, transferAddress);
        JsonReader jsonReader = Json.createReader(new StringReader(result));
        JsonObject resultMatrix = jsonReader.readObject();
        jsonReader.close();
        resultMatrix.get("rows");
        JsonArray arr = resultMatrix.getJsonArray("rows");
        int disValue = arr.getJsonObject(0).getJsonArray("elements").getJsonObject(0).getJsonObject("distance").getInt("value");
        double distance = disValue / 1000;
        System.out.println(distance);
    }

}
