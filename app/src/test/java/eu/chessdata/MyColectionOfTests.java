package eu.chessdata;

import com.google.gson.Gson;

import org.junit.Assert;
import org.junit.Test;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;

import eu.chessdata.chesspairing.Tools;
import eu.chessdata.chesspairing.model.ChesspairingTournament;

import static org.junit.Assert.assertEquals;

/**
 * To work on unit tests, switch the Test Artifact in the Build Variants view.
 */
public class MyColectionOfTests {
    @Test
    public void addition_isCorrect() throws Exception {
        assertEquals(4, 2 + 2);
    }

    @Test
    public void integrateChessParing() throws Exception{
        InputStream inputStream = MyColectionOfTests.class
                .getResourceAsStream("/myColectionOfTests/tournament1.json");
        Reader reader = new InputStreamReader(inputStream, "UTF-8");
        Gson gson = Tools.getGson();

        // line that loads from file
        ChesspairingTournament tournament = gson.fromJson(reader, ChesspairingTournament.class);

        // simple line that tests that wee have the correct data
        Assert.assertTrue("Not the expected data", tournament.getName().equals("Tournament 1"));

        assertEquals(6,3+3);
    }

    @Test
    public void howToDecodeListOfObjectsFromJson()throws Exception{
        assertEquals(6,3+3);
    }
}