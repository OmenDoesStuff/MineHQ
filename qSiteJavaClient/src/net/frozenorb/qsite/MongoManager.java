package net.frozenorb.qsite;

import com.mongodb.*;
import com.mongodb.client.model.DBCollectionUpdateOptions;
import lombok.Getter;
import net.frozenorb.qlib.utils.GeneralCallback;
import net.frozenorb.qlib.utils.Messages;
import net.frozenorb.qsite.backend.SiteUser;
import org.bukkit.Bukkit;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.*;

public class MongoManager {

    @Getter private MongoClient mongoClient;
    @Getter private DB database;
    @Getter private DBCollection siteCollection;

    public MongoManager() {
        if (!Messages.isMongoAuth()) {
            String link = "mongodb://" + Messages.getMongoIP() + ":" + Messages.getMongoPort();
            mongoClient = new MongoClient(new MongoClientURI(link));
        } else {
            List<ServerAddress> seeds = new ArrayList<>();
            seeds.add(new ServerAddress(Messages.getMongoIP()));
            List<MongoCredential> credentials = new ArrayList<>();
            credentials.add(
                    MongoCredential.createScramSha1Credential(
                            Messages.getMongoUser(),
                            "admin",
                            Messages.getMongoPassword().toCharArray()
                    )
            );
            mongoClient = new MongoClient( seeds, credentials );
        }

        try {
            database = mongoClient.getDB("Protocol");
            siteCollection = database.getCollection("accounts");
        } catch (Exception ex) {
            System.err.println("[cSite] §cFailed to initialize backend.");
            System.err.println(ex.getClass().getSimpleName() + " - " + ex.getMessage());
        }
    }


    public void getSiteUsesrInDB(GeneralCallback<Set<UUID>> callback) {
        Set<UUID> profiles = new HashSet<>();

        DBCursor cursor = siteCollection.find();
        cursor.forEach(dbObject-> profiles.add(UUID.fromString(dbObject.get("uuid").toString())));
        callback.call(profiles);
    }


    public void removeProfile(UUID uuid, GeneralCallback<Boolean> callback) {
        new BukkitRunnable() {
            @Override
            public void run() {

                siteCollection.remove(new BasicDBObject("uuid", uuid.toString()));

                callback.call(true);
            }
        }.runTaskAsynchronously(QSite.getInstance());
    }

    public DBObject getProfileObj(String email) {
        DBObject query = new BasicDBObject("username", email);

        if(!email.contains("@")) {
            query = new BasicDBObject("ign", email);
        }
        DBCursor cursor = siteCollection.find(query);
        return cursor.one();
    }

    public DBObject getProfileObj(UUID uuid) {
        DBObject query = new BasicDBObject("uuid", uuid);
        DBCursor cursor = siteCollection.find(query);
        return cursor.one();
    }


    public void loadProfile(String email, GeneralCallback<SiteUser> callback) {
        new BukkitRunnable() {
            @Override
            public void run() {
                SiteUser profile = new SiteUser(email);

                DBObject query = new BasicDBObject("username", email);
                DBCursor cursor = siteCollection.find(query);
                DBObject storedProfile = cursor.one();
                if (storedProfile == null) {
                    callback.call(null);
                    return;
                }
                if(storedProfile.get("ign") != null) profile.setIGN(storedProfile.get("ign").toString());
                if(storedProfile.get("registered") != null)profile.setRegistered(Boolean.valueOf(storedProfile.get("registered").toString()));
                if(storedProfile.get("uuid") != null) profile.setUuid(UUID.fromString(storedProfile.get("uuid").toString()));
                callback.call(profile);
            }
        }.runTaskAsynchronously(QSite.getInstance());
    }

    public SiteUser loadProfileByUsername(String username) {

                DBObject query = getProfileObj(username);
                DBCursor cursor = siteCollection.find(query);
                DBObject storedProfile = cursor.one();
                if (storedProfile == null) {
                    return null;
                }
                SiteUser siteUser = new SiteUser(storedProfile.get("username").toString());

                if(storedProfile.get("ign") != null) siteUser.setIGN(storedProfile.get("ign").toString());
                if(storedProfile.get("registered") != null)siteUser.setRegistered(Boolean.valueOf(storedProfile.get("registered").toString()));
                if(storedProfile.get("uuid") != null) siteUser.setUuid(UUID.fromString(storedProfile.get("uuid").toString()));
                return siteUser;
    }

    public SiteUser loadProfileByUUID(UUID uuid) {

        DBObject query = new BasicDBObject("uuid", uuid.toString());
        DBCursor cursor = siteCollection.find(query);
        DBObject storedProfile = cursor.one();
        if (storedProfile == null) {
            return null;
        }
        Bukkit.broadcastMessage(Bukkit.getPlayer(uuid).getName() + " " + storedProfile.get("username").toString());
        SiteUser siteUser = new SiteUser(storedProfile.get("username").toString());

        if(storedProfile.get("ign") != null) siteUser.setIGN(storedProfile.get("ign").toString());
        if(storedProfile.get("uuid") != null) siteUser.setUuid(UUID.fromString(storedProfile.get("uuid").toString()));
        if(storedProfile.get("registered") != null)siteUser.setRegistered(Boolean.valueOf(storedProfile.get("registered").toString()));
        return siteUser;
    }

    public void saveProfile(SiteUser profile, GeneralCallback<Boolean> callback) {
        new BukkitRunnable() {
            @Override
            public void run() {
                DBObject rankObject = new BasicDBObjectBuilder()
                        .add("username", profile.getUsername())
                        .add("salt", profile.getSalt())
                        .add("hash", profile.getHash())
                        .add("ign", profile.getIGN())
                        .add("uuid", profile.getUuid().toString())
                        .add("registered", profile.isRegistered())
                        .add("__v", 0)
                        .get();
                siteCollection.update(new BasicDBObject("username", profile.getUsername()), rankObject, new DBCollectionUpdateOptions().upsert(true));
                callback.call(true);
            }
        }.runTaskAsynchronously(QSite.getInstance());
    }


}
