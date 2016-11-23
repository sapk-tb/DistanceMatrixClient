package eu.telecom_bretagne.distanceMatrixClient;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import eu.telecom_bretagne.distanceMatrixClient.jaxb.DistanceMatrixResponse;
import eu.telecom_bretagne.distanceMatrixClient.jaxb.Element;
import java.io.IOException;
import java.io.StringReader;
import java.net.MalformedURLException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.xml.bind.JAXBContext;

import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;

/**
 * Classe facade pour l'accès au Web Service REST Google Distance Matrix
 * API.<br/>
 * Voir documentation :
 * https://developers.google.com/maps/documentation/distancematrix/<br/>
 *
 * La requête au Web Service se fait en passant des paramètres à l'URL, exemple
 * d'appels :
 * <ul>
 * <li><b>JSON</b> :
 * https://maps.googleapis.com/maps/api/distancematrix/json?origins=Brest&destinations=Paris&mode=bicycling&language=fr-FR</li>
 * <li><b>XML</b> :
 * https://maps.googleapis.com/maps/api/distancematrix/xml?origins=Brest&destinations=Paris&mode=bicycling&language=fr-FR</li>
 * </ul>
 *
 * La requête possède deux paramètres obligatoires :
 * <ul>
 * <li><b>origins</b> : une ou plusieurs origines (texte ou lat/long) séparées
 * par le caractère '|'.</li>
 * <li><b>destinations</b> : une ou plusieurs origines (texte ou lat/long)
 * séparées par le caractère '|'.</li>
 * </ul>
 * Note : on ne prendra en compte qu'<u>UNE SEULE</u> origine et <u>UNE
 * SEULE</u> destination.<br/><br/>
 *
 * De manière optionnelle, la requête peut comporter les éléments suivants :
 * <ul>
 * <li><b>mode</b> : moyen de transport utilisé à choisir parmi "driving" (par
 * défaut), "walking" ou "bicycling".</li>
 * <li><b>language</b> : langue du résultat (anglais par défaut) : non utilisé
 * ici.</li>
 * <li><b>avoid</b> : restrictions sur le trajet (routes à péage ou non) : non
 * utilisé ici.</li>
 * <li><b>unit</b> : unité de distance à choisir parmi metric (par défaut) ou
 * imperial : non utilisé ici.</li>
 * <li><b>departure_time</b> : pour la prise en compte des conditions de
 * circulation dans le calcul du temps de trajet : non utilisé ici.</li>
 * </ul>
 *
 * @author Philippe TANGUY
 */
public class DistanceMatrixFacade {
    //-----------------------------------------------------------------------------

    public enum OutputType {
        xml, json
    }

    public enum ModeTransport {
        driving, walking, bicycling
    }
    //-----------------------------------------------------------------------------
    //private String        distanceMatrixBaseURL = "http://74.125.71.95/maps/api/distancematrix/";
    private String distanceMatrixBaseURL = "http://maps.googleapis.com/maps/api/distancematrix/";
    private String origin;
    private String destination;
    private ModeTransport modeTransport = ModeTransport.driving; // driving, choix par défaut
    private OutputType outputType = OutputType.xml;        // XML, choix par défaut
    //-----------------------------------------------------------------------------

    /*
   * Méthodes assurant la mise à jour des informations à partir de l'IHM.
     */
    public void setOrigin(String origin) {
        this.origin = origin;
    }

    public void setDestination(String destination) {
        this.destination = destination;
    }

    public void setOutputType(OutputType outputType) {
        this.outputType = outputType;
    }

    public void setModeTransport(ModeTransport modeTransport) {
        this.modeTransport = modeTransport;
    }
    //-----------------------------------------------------------------------------

    /**
     * Construction de l'URL d'invocation du Web Service à partir des
     * informations (origine, destination, etc.) stockées dans la classe
     * DistanceMatrixFacade.
     *
     * @return La représentation textuelle de l'URL.
     */
    private String getEntireURL() {
        String entireURL = distanceMatrixBaseURL + outputType + "?origins=" + origin + "&destinations=" + destination + "&mode=" + modeTransport;
        return entireURL;
    }
    //-----------------------------------------------------------------------------

    /**
     * Réalisation de la connexion HTTP au Web Service. Le résultat (XML ou
     * JSON) dépend du format désiré.
     *
     * @return La source de la réponse.
     * @throws MalformedURLException
     * @throws IOException
     */
    private String getResponseText() throws IOException {
        try {
            Request r = new Request("GET", getEntireURL(), "");
            String result = r.send();
            return result;
        } catch (Exception ex) {
            Logger.getLogger(DistanceMatrixFacade.class.getName()).log(Level.SEVERE, null, ex);
        }
        return "";
    }
    //-----------------------------------------------------------------------------

    /**
     * Impsired from http://blog.xebia.fr/2011/03/17/jaxb-le-parsing-xml-objet/
     *
     * @param <T>
     * @param data
     * @param clazz
     * @return
     * @throws JAXBException
     */
    private <T> T parse(String data, Class<T> clazz) throws JAXBException {
        Unmarshaller unmarshaller = JAXBContext.newInstance(clazz).createUnmarshaller();
        return clazz.cast(unmarshaller.unmarshal(new StringReader(data)));
    }

    /**
     * Renvoie dans un tableau de chaînes de caractères la réponse suite à un
     * appel quel que soit le format de sortie : JSON ou XML. Contenu du tableau
     * :
     * <ul>
     * <li>0 : origin_addresses</li>
     * <li>1 : destination_addresses</li>
     * <li>2 : top level status</li>
     * <li>3 : Element-level status</li>
     * <li>4 : distance (text)</li>
     * <li>5 : distance (value)</li>
     * <li>6 : duration (text)</li>
     * <li>7 : duration (value)</li>
     * <li>8 : la source de la réponse</li>
     * <li>9 : l'URL d'invocation du Web Service</li>
     * </ul>
     *
     * @return un tableau de chaînes de caractères.
     * @throws MalformedURLException
     * @throws IOException
     * @throws JAXBException
     */
    public String[] getResponse() throws IOException, JAXBException {
        String[] response = new String[10];

        // Étape 1 : on invoque le Web Service distant.
        String source = getResponseText();

        // Étape 2 : on traite les données suivant son format (JSON ou XML).
        switch (outputType) {
            case xml:
                DistanceMatrixResponse objXML = parse(source, DistanceMatrixResponse.class);
                response[0] = objXML.getOriginAddress();
                response[1] = objXML.getDestinationAddress();
                response[2] = objXML.getStatus();
                Element elXML = objXML.getRow().getElement();
                response[3] = elXML.getStatus();
                response[4] = elXML.getDistance().getText();
                response[5] = elXML.getDistance().getValue() + "";
                response[6] = elXML.getDuration().getText();
                response[7] = elXML.getDuration().getValue() + "";
                break;
            case json:
                //Not present here JSONObject obj = JSONObject(source);
                ObjectMapper mapper = new ObjectMapper(); //JSON
                JsonNode rootNode = mapper.readTree(source);
                response[0] = rootNode.get("origin_addresses").get(0).asText();
                response[1] = rootNode.get("destination_addresses").get(0).asText();
                response[2] = rootNode.get("status").asText();

                ArrayNode rowsNode = (ArrayNode) rootNode.get("rows");
                JsonNode elem = rowsNode.get(0).get("elements").get(0);
                response[3] = elem.get("status").asText();
                response[4] = elem.get("distance").get("text").asText();
                response[5] = elem.get("distance").get("value").asText();
                response[6] = elem.get("duration").get("text").asText();
                response[7] = elem.get("duration").get("value").asText();
                /*
                response[4] = elJSON.getDistance().getText();
                response[5] = elJSON.getDistance().getValue() + "";
                response[6] = elJSON.getDuration().getText();
                response[7] = elJSON.getDuration().getValue() + "";
*/
            // ### À compléter ###
        }
        // Source du résultat.
        response[8] = source;
        // URL d'appel
        response[9] = getEntireURL();

        return response;
    }
    //-----------------------------------------------------------------------------
}
