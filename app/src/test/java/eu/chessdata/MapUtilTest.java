package eu.chessdata;

import org.junit.Test;

import java.text.Collator;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import eu.chessdata.model.Player;
import eu.chessdata.utils.MapUtil;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;


/**
 * Created by Bogdan Oloeriu on 6/12/2016.
 */
public class MapUtilTest {

    @Test
    public void testSortPlayers() throws Exception {
        Random random = new Random(System.currentTimeMillis());
        Map<String, Player> testMap = new HashMap<>();
        for (int i = 0; i <= 5; i++) {
            Player player = new Player(String.valueOf(random.nextInt()), "email", String.valueOf(i), "name" + i);

            testMap.put("key" + i, player);
        }
        testMap = MapUtil.sortByValue(testMap);
        String previousName = null;
        for (Map.Entry<String, Player> entry : testMap.entrySet()) {

            assertNotNull(entry.getValue());
            String name = entry.getValue().getName();
            if (previousName != null) {
                Collator collator = Collator.getInstance();
                assertTrue(collator.compare(previousName,name)<=0);
            }
        }
    }


}
