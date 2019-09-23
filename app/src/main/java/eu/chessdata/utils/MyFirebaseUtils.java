package eu.chessdata.utils;

import android.util.Log;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.CountDownLatch;
import java.util.stream.Collectors;

import eu.chessdata.chesspairing.model.ChesspairingGame;
import eu.chessdata.chesspairing.model.ChesspairingPlayer;
import eu.chessdata.chesspairing.model.ChesspairingRound;
import eu.chessdata.chesspairing.model.ChesspairingTournament;
import eu.chessdata.model.DefaultClub;
import eu.chessdata.model.Game;
import eu.chessdata.model.Player;
import eu.chessdata.model.RankedPlayer;
import eu.chessdata.model.Tournament;
import eu.chessdata.model.User;
import eu.chessdata.ui.MainActivity;

/**
 * Created by Bogdan Oloeriu on 5/31/2016.
 */
public class MyFirebaseUtils {
    private static final String tag = Constants.LOG_TAG;

    /**
     * it collects the entire required data from firebase in order to build the current state
     * of the tournament
     *
     * @param clubKey
     * @param tournamentKey
     * @return
     */
    public static ChesspairingTournament buildChessPairingTournament(String clubKey, String tournamentKey) {
        final ChesspairingTournament chesspairingTournament = new ChesspairingTournament();
        final CountDownLatch latch1 = new CountDownLatch(1);
        //get the general description
        String tournamentLoc = Constants.LOCATION_TOURNAMENT
                .replace(Constants.CLUB_KEY, clubKey)
                .replace(Constants.TOURNAMENT_KEY, tournamentKey);
        DatabaseReference tournamentRef = FirebaseDatabase.getInstance().getReference(tournamentLoc);
        tournamentRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Tournament tournament = dataSnapshot.getValue(Tournament.class);
                if (tournament != null) {
                    chesspairingTournament.setName(tournament.getName());
                    chesspairingTournament.setDescription(tournament.getDescription());
                    chesspairingTournament.setTotalRounds(tournament.getTotalRounds());
                    chesspairingTournament.setTotalRounds(tournament.getTotalRounds());
                }
                tournamentRef.removeEventListener(this);
                latch1.countDown();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.e(tag, databaseError.getMessage());
                latch1.countDown();
            }
        });
        try {
            latch1.await();
        } catch (InterruptedException e) {
            Log.e(tag, "tournamentDetailsError: " + e.getMessage());
        }

        //populate players
        List<Player> players = getTournamentPlayers(tournamentKey);

        //initial order
        List<RankedPlayer> rankedPlayers = getTournamentInitialOrder(tournamentKey);
        Map<String, Integer> initialOrder = new HashMap<>();
        boolean weHaveInitialOrder = false;
        if (rankedPlayers.size() > 0) {
            weHaveInitialOrder = true;
            int i = 1;
            for (RankedPlayer player : rankedPlayers) {
                initialOrder.put(player.getPlayerKey(), i++);
            }
        }

        List<ChesspairingPlayer> chesspairingPlayers = new ArrayList<>();
        int i = 0;
        int k = -1;
        for (Player player : players) {
            i++;//set the player order as the natural one collected from firebase
            ChesspairingPlayer chesspairingPlayer = MyChesspairingUtils.scanPlayer(player);
            if (weHaveInitialOrder) {
                if (initialOrder.containsKey(chesspairingPlayer.getPlayerKey())) {
                    chesspairingPlayer.setInitialOrderId(
                            initialOrder.get(chesspairingPlayer.getPlayerKey()));
                } else {
                    k++;
                    int order = k + initialOrder.size();
                    chesspairingPlayer.setInitialOrderId(k + initialOrder.size());
                }

            } else {
                chesspairingPlayer.setInitialOrderId(i);
            }
            chesspairingPlayers.add(chesspairingPlayer);
        }

        Comparator<ChesspairingPlayer> comparator = (p1, p2)
                -> p1.getInitialOrderId() - p2.getInitialOrderId();
        chesspairingPlayers.sort(comparator);

        chesspairingTournament.setPlayers(chesspairingPlayers);

        //populate the rounds
        chesspairingTournament.setRounds(getTournamentRounds(tournamentKey));
        return chesspairingTournament;
    }

    public static List<ChesspairingRound> getTournamentRounds(String tournamentKey) {
        final List<ChesspairingRound> chesspairingRounds = new ArrayList<>();
        final CountDownLatch latch = new CountDownLatch(1);
        String sectionNotRequired = "/" + Constants.ROUND_NUMBER + "/" + Constants.ROUND_ABSENT_PLAYERS;
        //get the rounds data
        String roundsLoc = Constants.LOCATION_ROUND_ABSENT_PLAYERS
                .replace(sectionNotRequired, "")
                .replace(Constants.TOURNAMENT_KEY, tournamentKey);
        DatabaseReference roundsRef = FirebaseDatabase.getInstance().getReference(roundsLoc);
        roundsRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Iterator<DataSnapshot> roundIterator = dataSnapshot.getChildren().iterator();
                while (roundIterator.hasNext()) {
                    DataSnapshot snapshot = (DataSnapshot) roundIterator.next();
                    ChesspairingRound chesspairingRound = new ChesspairingRound();
                    //get the round number
                    String roundNumber = snapshot.getKey();
                    chesspairingRound.setRoundNumber(Integer.valueOf(roundNumber));

                    List<Player> absentPlayers = new ArrayList<Player>();
                    List<Game> games = new ArrayList<Game>();

                    //get the absentPlayers
                    if (snapshot.hasChild(Constants.ROUND_ABSENT_PLAYERS)) {
                        DataSnapshot playersSnapshot = snapshot.child(Constants.ROUND_ABSENT_PLAYERS);
                        Iterator<DataSnapshot> playersIterator = playersSnapshot.getChildren().iterator();
                        while (playersIterator.hasNext()) {
                            DataSnapshot playerSnapshot = (DataSnapshot) playersIterator.next();
                            Player player = playerSnapshot.getValue(Player.class);
                            absentPlayers.add(player);
                        }
                    }
                    //List<ChesspairingPlayer> absentChesspairingPlayers = new ArrayList<ChesspairingPlayer>();
                    for (Player player : absentPlayers) {
                        ChesspairingPlayer chesspairingPlayer = MyChesspairingUtils.scanPlayer(player);
                        chesspairingPlayer.setPresent(false);
                        chesspairingRound.addAbsentPlayer(chesspairingPlayer);
                    }

                    Log.i(tag, "Time to decode game");
                    //get the games
                    if (snapshot.hasChild(Constants.GAMES)) {
                        DataSnapshot gamesSnapshot = snapshot.child(Constants.GAMES);
                        Iterator<DataSnapshot> gamesIterator = gamesSnapshot.getChildren().iterator();
                        List<ChesspairingGame> chesspairingGames = new ArrayList<ChesspairingGame>();
                        List<ChesspairingPlayer> presentPlayers = new ArrayList<ChesspairingPlayer>();
                        while (gamesIterator.hasNext()) {
                            DataSnapshot gameSnapshot = (DataSnapshot) gamesIterator.next();
                            Game game = gameSnapshot.getValue(Game.class);
                            ChesspairingGame chesspairingGame = MyChesspairingUtils.scanGame(game);
                            chesspairingGames.add(chesspairingGame);

                            presentPlayers.add(chesspairingGame.getWhitePlayer());
                            if (chesspairingGame.getBlackPlayer() != null) {
                                presentPlayers.add(chesspairingGame.getBlackPlayer());
                            }
                        }
                        chesspairingRound.setGames(chesspairingGames);
                        chesspairingRound.setPresentPlayers(presentPlayers);
                    }

                    //add the round
                    chesspairingRounds.add(chesspairingRound);
                }

                latch.countDown();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                latch.countDown();
            }
        });
        //wait for the thread to finish computation
        try {
            latch.await();
        } catch (InterruptedException e) {
            Log.e(tag, "tournamentDetailsError: " + e.getMessage());
        }
        return chesspairingRounds;
    }

    public static List<Player> getClubPlayers(String clubKey) {
        final CountDownLatch latch = new CountDownLatch(1);
        final List<Player> playerList = new ArrayList<>();
        String playerLocation = Constants.LOCATION_CLUB_PLAYERS
                .replace(Constants.CLUB_KEY, clubKey);

        DatabaseReference playerReference = FirebaseDatabase.getInstance()
                .getReference(playerLocation);
        playerReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Iterator<DataSnapshot> it = dataSnapshot.getChildren().iterator();
                while (it.hasNext()) {
                    DataSnapshot snapshot = (DataSnapshot) it.next();
                    Player player = snapshot.getValue(Player.class);
                    playerList.add(player);
                }
                latch.countDown();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.e(tag, databaseError.getMessage());
                latch.countDown();
            }
        });
        try {
            latch.await();
        } catch (InterruptedException e) {
            Log.e(tag, "getTournamentPlayers: " + e.getMessage());
        }
        return playerList;
    }

    public static Optional<Player> getClubPlayer(String clubKey, String playerKey) {
        Optional<Player> returnValue[] = new Optional[1];
        returnValue[0] = Optional.empty();
        final CountDownLatch latch = new CountDownLatch(1);
        final List<Player> playerList = new ArrayList<>();
        String playerLocation = Constants.LOCATION_CLUB_PLAYERS
                .replace(Constants.CLUB_KEY, clubKey)
                + "/" + playerKey;

        DatabaseReference playerReference = FirebaseDatabase.getInstance()
                .getReference(playerLocation);
        playerReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Player player = dataSnapshot.getValue(Player.class);
                if (null != player) {
                    returnValue[0] = Optional.of(player);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.e(tag, databaseError.getMessage());
                latch.countDown();
            }
        });
        try {
            latch.await();
        } catch (InterruptedException e) {
            Log.e(tag, "getTournamentPlayers: " + e.getMessage());
        }
        return returnValue[0];
    }

    public static List<Player> getTournamentPlayers(String tournamentKey) {
        final List<Player> playerList = new ArrayList<>();
        final CountDownLatch latch = new CountDownLatch(1);
        String playersLoc = Constants.LOCATION_TOURNAMENT_PLAYERS
                .replace(Constants.TOURNAMENT_KEY, tournamentKey);
        DatabaseReference playersRef = FirebaseDatabase.getInstance().getReference(playersLoc);
        playersRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Iterator<DataSnapshot> it = dataSnapshot.getChildren().iterator();
                while (it.hasNext()) {
                    DataSnapshot snapshot = (DataSnapshot) it.next();
                    Player player = snapshot.getValue(Player.class);
                    playerList.add(player);
                }
                latch.countDown();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.e(tag, databaseError.getMessage());
                latch.countDown();
            }
        });
        try {
            latch.await();
        } catch (InterruptedException e) {
            Log.e(tag, "getTournamentPlayers: " + e.getMessage());
        }
        return playerList;
    }


    public static List<RankedPlayer> getTournamentInitialOrder(String tournamentKey) {
        final List<RankedPlayer> playerList = new ArrayList<>();
        final CountDownLatch latch = new CountDownLatch(1);
        String playersLoc = Constants.LOCATION_TOURNAMENT_INITIAL_ORDER
                .replace(Constants.TOURNAMENT_KEY, tournamentKey);
        DatabaseReference playersRef = FirebaseDatabase.getInstance().getReference(playersLoc);
        playersRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Iterator<DataSnapshot> it = dataSnapshot.getChildren().iterator();
                while (it.hasNext()) {
                    DataSnapshot snapshot = (DataSnapshot) it.next();
                    RankedPlayer player = snapshot.getValue(RankedPlayer.class);
                    playerList.add(player);
                }
                latch.countDown();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.e(tag, databaseError.getMessage());
                latch.countDown();
            }
        });

        try {
            latch.await();
        } catch (InterruptedException e) {
            Log.e(tag, "getTournamentPlayers: " + e.getMessage());
        }

        Comparator<RankedPlayer> comparator = new Comparator<RankedPlayer>() {
            @Override
            public int compare(RankedPlayer pa, RankedPlayer pb) {
                return pa.getTournamentInitialOrder() - pb.getTournamentInitialOrder();
            }
        };
        playerList.sort(comparator);

        return playerList;
    }


    public static void setDefaultClub(DefaultClub defaultManagedClub) {
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();

        String defaultClubLocation = Constants.LOCATION_DEFAULT_CLUB
                .replace(Constants.USER_KEY, uid);
        DatabaseReference managedClubRef = database.getReference(defaultClubLocation);
        managedClubRef.setValue(defaultManagedClub);
    }

    public static void getDefaultClub(final OnOneTimeResultsListener listener, final MainActivity.ACTION action) {
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
        String defaultClubLocation = Constants.LOCATION_DEFAULT_CLUB
                .replace(Constants.USER_KEY, uid);
        DatabaseReference defaultClubRef = database.getReference(defaultClubLocation);
        defaultClubRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                DefaultClub defaultClub = dataSnapshot.getValue(DefaultClub.class);
                Log.d(tag, "default Club found");
                listener.onClubValue(defaultClub, action);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    public static void locateDefaultClub(final OnDefaultClubLocated listener) {
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
        String defaultClubLocation = Constants.LOCATION_DEFAULT_CLUB
                .replace(Constants.USER_KEY, uid);
        DatabaseReference defaultClubRef = database.getReference(defaultClubLocation);
        defaultClubRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                DefaultClub defaultClub = dataSnapshot.getValue(DefaultClub.class);
                if (defaultClub != null) {
                    Log.d(tag, "default Club located");
                    listener.onDefaultClubLocated(defaultClub);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }


    /**
     * Identifies if current user is manager for a specific club by clubKey and then notifies the
     * registered listener only for positive results.
     *
     * @param listener
     * @param action
     */
    public static void isManagerForClubKey(final String clubKey, final OnOneTimeResultsListener listener, final MainActivity.ACTION action) {
        final FirebaseDatabase database = FirebaseDatabase.getInstance();
        final String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
        String managerLoc = Constants.LOCATION_CLUB_MANAGERS
                .replace(Constants.CLUB_KEY, clubKey)
                .replace(Constants.MANAGER_KEY, uid);
        DatabaseReference managerRef = database.getReference(managerLoc);
        managerRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                User manager = dataSnapshot.getValue(User.class);
                if (manager != null) {
                    DefaultClub defaultClub = new DefaultClub();
                    defaultClub.setClubKey(clubKey);
                    Map<String, String> values = new HashMap<String, String>();
                    values.put(Constants.CLUB_KEY, clubKey);
                    listener.onUserIsClubManager(values, action);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.e(tag, "databaseError: " + databaseError.getMessage());
            }
        });
    }

    public static void isManagerForDefaultClub(final OnOneTimeResultsListener listener, final MainActivity.ACTION action) {
        final FirebaseDatabase database = FirebaseDatabase.getInstance();
        final String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
        String defaultClubLocation = Constants.LOCATION_DEFAULT_CLUB
                .replace(Constants.USER_KEY, uid);
        DatabaseReference defaultClubRef = database.getReference(defaultClubLocation);
        defaultClubRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                final DefaultClub defaultClub = dataSnapshot.getValue(DefaultClub.class);
                if (defaultClub != null) {
                    String clubKey = defaultClub.getClubKey();
                    String managersLocation = Constants.LOCATION_CLUB_MANAGERS
                            .replace(Constants.CLUB_KEY, clubKey)
                            .replace(Constants.MANAGER_KEY, uid);
                    DatabaseReference managerRef = database.getReference(managersLocation);
                    managerRef.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            User manager = dataSnapshot.getValue(User.class);
                            if (manager != null) {
                                String clubKey = defaultClub.getClubKey();
                                Map<String, String> values = new HashMap<String, String>();
                                values.put(Constants.CLUB_KEY, clubKey);
                                listener.onUserIsClubManager(values, action);
                            }
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    public static void updateTournamentReversedOrder(String clubKey, String tournamentKey) {
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        String tournamentLocation = Constants.LOCATION_TOURNAMENTS
                .replace(Constants.CLUB_KEY, clubKey) + "/" + tournamentKey;

        final DatabaseReference tournamentRef = database.getReference(tournamentLocation);
        tournamentRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Tournament tournament = dataSnapshot.getValue(Tournament.class);
                if (tournament != null) {
                    long timeStamp = tournament.dateCreatedGetLong();
                    long reversedDateCreated = 0 - timeStamp;
                    tournamentRef.child("reversedDateCreated").setValue(reversedDateCreated);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }

    public static void isCurrentUserAdmin(String clubKey, final OnUserIsAdmin onUserIsAdmin) {
        String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
        String clubManagerLoc = Constants.LOCATION_CLUB_MANAGERS
                .replace(Constants.CLUB_KEY, clubKey)
                .replace(Constants.MANAGER_KEY, uid);
        DatabaseReference managerRef = FirebaseDatabase.getInstance().getReference(clubManagerLoc);
        managerRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                boolean manager = false;
                if (dataSnapshot.getValue() != null) {
                    manager = true;
                }
                onUserIsAdmin.onUserIsAdmin(manager);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.e(tag, databaseError.getMessage());
                onUserIsAdmin.onUserIsAdmin(false);
            }
        });
    }

    /**
     * It checks using the clubKey if the user is a manager
     *
     * @param clubKey
     * @return
     */
    public static boolean isCurrentUserAdmin(String clubKey) {
        final Boolean managers[] = {false};//first boolean holds the result
        String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
        String clubManagerLoc = Constants.LOCATION_CLUB_MANAGERS
                .replace(Constants.CLUB_KEY, clubKey)
                .replace(Constants.MANAGER_KEY, uid);
        final CountDownLatch latch = new CountDownLatch(1);
        DatabaseReference managerRef = FirebaseDatabase.getInstance().getReference(clubManagerLoc);
        managerRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                if (dataSnapshot.getValue() != null) {
                    managers[0] = true;
                    latch.countDown();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.e(tag, databaseError.getMessage());
                latch.countDown();
            }
        });
        try {
            latch.await();
        } catch (InterruptedException e) {
            Log.e(tag, "isCurrentUserAdmin " + e.getMessage());
        }
        return managers[0];
    }


    public static void persistNewGames(String clubKey, String tournamentKey, ChesspairingRound round) {
        int firstTableNumber = MyFirebaseUtils.getFirstTableNumber(clubKey, tournamentKey);
        List<Player> tempList = MyFirebaseUtils.getTournamentPlayers(tournamentKey);
        Map<String, Player> playerMap = new HashMap<>();
        for (Player player : tempList) {
            playerMap.put(player.getPlayerKey(), player);
        }

        //copy the games data
        List<ChesspairingGame> chesspairingGames = round.getGames();
        List<Game> games = new ArrayList<>();
        for (ChesspairingGame chesspairingGame : chesspairingGames) {
            Game game = new Game();
            game.setTableNumber(chesspairingGame.getTableNumber());
            game.setActualNumber(chesspairingGame.getTableNumber() + firstTableNumber - 1);
            game.setWhitePlayer(playerMap.get(chesspairingGame.getWhitePlayer().getPlayerKey()));
            if (chesspairingGame.getBlackPlayer() != null) {
                //white player ad black player are present
                game.setBlackPlayer(playerMap.get(chesspairingGame.getBlackPlayer().getPlayerKey()));
            } else {
                game.setResult(4);
            }
            games.add(game);
        }
        String roundNumber = String.valueOf(round.getRoundNumber());
        String gamesLoc = Constants.LOCATION_ROUND_GAMES
                .replace(Constants.TOURNAMENT_KEY, tournamentKey)
                .replace(Constants.ROUND_NUMBER, roundNumber);
        DatabaseReference allGamesRef = FirebaseDatabase.getInstance().getReference(gamesLoc);
        for (Game gameItem : games) {
            DatabaseReference gameRef = allGamesRef.getRef().child(String.valueOf(gameItem.getTableNumber()));
            gameRef.setValue(gameItem);
        }
    }

    private static int getFirstTableNumber(String clubKey, String tournamentKey) {
        final int numbers[] = {1};//first number holds the result
        final CountDownLatch latch = new CountDownLatch(1);

        String tournamentLoc = Constants.LOCATION_TOURNAMENTS
                .replace(Constants.CLUB_KEY, clubKey)
                .replace(Constants.TOURNAMENT_KEY, tournamentKey);
        DatabaseReference tournamentRef = FirebaseDatabase.getInstance().getReference(tournamentLoc);
        tournamentRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.getValue() != null) {
                    Tournament tournament = dataSnapshot.getValue(Tournament.class);
                    numbers[0] = tournament.getFirstTableNumber();
                }
                latch.countDown();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.e(tag, "getFirstTableNumber: " + databaseError.getMessage());
                latch.countDown();
            }
        });
        try {
            latch.await();
        } catch (InterruptedException e) {
            Log.e(tag, "getFirstTableNumber " + e.getMessage());
        }
        return numbers[0];
    }

    public static void persistDefaultStandings(String tournamentKey, int roundNumber, List<ChesspairingPlayer> standings) {
        List<Player> players = new ArrayList<>();
        for (ChesspairingPlayer item : standings) {
            Player player = new Player(item.getName(), "nomail@mail.com", "noClubKey", "noClubName", item.getElo(), 0);
            players.add(player);
        }

        List<RankedPlayer> rankedPlayers = new ArrayList<>();
        int i = 0;
        for (ChesspairingPlayer item : standings) {
            i++;
            RankedPlayer player = new RankedPlayer();
            player.setPlayerKey(item.getPlayerKey());
            player.setTournamentKey(tournamentKey);
            player.setRankNumber(i);
            player.setPlayerName(item.getName());
            rankedPlayers.add(player);
        }


        for (RankedPlayer player : rankedPlayers) {
            i = player.getRankNumber();
            String standingsLocation = Constants.LOCATION_STANDINGS
                    .replace(Constants.TOURNAMENT_KEY, tournamentKey)
                    .replace(Constants.ROUND_NUMBER, String.valueOf(roundNumber))
                    .replace(Constants.CATEGORY_NUMBER, "0")
                    .replace(Constants.STANDING_NUMBER, String.valueOf(i));
            DatabaseReference standingsReference = FirebaseDatabase.getInstance().getReference(standingsLocation);
            standingsReference.setValue(player);
        }
    }

    public static void updateTournamentInitialOrder(String clubKey,
                                                    String tournamentKey,
                                                    String playerKey,
                                                    String updateOrderString,
                                                    ChesspairingTournament tournament) {

        //compute new order
        int updateValue = Integer.valueOf(updateOrderString);

        List<ChesspairingPlayer> initialList = tournament.getPlayers().stream()
                .sorted(Comparator.comparingInt(ChesspairingPlayer::getInitialOrderId))
                .collect(Collectors.toList());

        Optional<ChesspairingPlayer> optionalPlayer = initialList.stream()
                .filter(player -> playerKey.equals(player.getPlayerKey()))
                .findFirst();

        List<ChesspairingPlayer> newList = new ArrayList<>();
        int order = 0;
        //set the order
        for (ChesspairingPlayer player : initialList) {
            order++;
            if (order == updateValue) {
                newList.add(optionalPlayer.get());
            } else {
                if (!playerKey.equals(player.getPlayerKey())
                ) {
                    newList.add(player);
                }
            }
        }

        //fix the values
        order = 0;
        for (ChesspairingPlayer player : newList) {
            order++;
            player.setInitialOrderId(order);
        }


        //build RankedPlayer list
        List<RankedPlayer> rankedPlayers = new ArrayList<>();
        newList.forEach(chesspairingPlayer -> rankedPlayers.add(
                new RankedPlayer(chesspairingPlayer, tournamentKey, clubKey)
        ));

        //persist players
        rankedPlayers.forEach(player -> {
            String tournamentOrderLocation = Constants.LOCATION_TOURNAMENT_PLAYER_INITIAL_ORDER
                    .replace(Constants.TOURNAMENT_KEY, tournamentKey)
                    .replace(Constants.PLAYER_KEY, player.getPlayerKey());
            DatabaseReference initialOrderReference = FirebaseDatabase.getInstance()
                    .getReference(tournamentOrderLocation);
            initialOrderReference.setValue(player);
        });

    }

    public static void refreshTournamentInitialOrder(String clubKey,
                                                     String tournamentKey,
                                                     String userKey,
                                                     ChesspairingTournament tournament) {

        //initial order
        List<RankedPlayer> rankedPlayers = getTournamentInitialOrder(tournamentKey);
        Map<String, RankedPlayer> orderedMap = new LinkedHashMap<>();

        int rank = 1;
        if (rankedPlayers.size() > 0) {

            for (RankedPlayer player : rankedPlayers) {
                player.setTournamentInitialOrder(rank++);
                orderedMap.put(player.getPlayerKey(), player);
            }
        }


        for (ChesspairingPlayer player : tournament.getPlayers()) {
            String playerKey = player.getPlayerKey();
            if (!orderedMap.containsKey(playerKey)) {
                RankedPlayer rankedPlayer = new RankedPlayer(player, tournamentKey, clubKey);
                rankedPlayer.setTournamentInitialOrder(rank++);
                orderedMap.put(rankedPlayer.getPlayerKey(), rankedPlayer);
            }
        }

        //delete initial order
        String initialOrder = Constants.LOCATION_TOURNAMENT_PLAYER_INITIAL_ORDER
                .replace(Constants.TOURNAMENT_KEY, tournamentKey)
                .replace("/" + Constants.PLAYER_KEY, "");
        DatabaseReference initialOrderReference = FirebaseDatabase.getInstance()
                .getReference(initialOrder);
        initialOrderReference.removeValue();

        //set new order

        for (RankedPlayer rankedPlayer : orderedMap.values()) {
            String tournamentOrderLocation = Constants.LOCATION_TOURNAMENT_PLAYER_INITIAL_ORDER
                    .replace(Constants.TOURNAMENT_KEY, tournamentKey)
                    .replace(Constants.PLAYER_KEY, rankedPlayer.getPlayerKey());
            DatabaseReference updatedOrderReference = FirebaseDatabase.getInstance()
                    .getReference(tournamentOrderLocation);
            updatedOrderReference.setValue(rankedPlayer);
        }


        Log.d(tag, "End of eu.chessdata.utils.MyFirebaseUtils.refreshTournamentInitialOrder");
    }


    public interface OnOneTimeResultsListener {
        public void onClubValue(DefaultClub defaultClub, MainActivity.ACTION action);

        public void onClubValue(DefaultClub defaultClub);

        public void onUserIsClubManager(Map<String, String> dataMap, MainActivity.ACTION action);
    }

    public interface OnUserIsAdmin {
        public void onUserIsAdmin(boolean isAdmin);
    }

    public interface OnDefaultClubLocated {
        public void onDefaultClubLocated(DefaultClub defaultClub);
    }
}
