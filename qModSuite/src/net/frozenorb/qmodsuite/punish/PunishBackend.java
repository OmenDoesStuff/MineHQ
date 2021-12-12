package net.frozenorb.qmodsuite.punish;

import com.mongodb.BasicDBObject;
import com.mongodb.BasicDBObjectBuilder;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;
import com.mongodb.MongoClient;
import com.mongodb.MongoCredential;
import com.mongodb.ServerAddress;
import com.mongodb.MongoClientOptions.Builder;
import com.mongodb.client.model.DBCollectionUpdateOptions;
import net.frozenorb.bridge.utils.GeneralCallback;
import net.frozenorb.qlib.utils.Messages;
import net.frozenorb.qmodsuite.punish.utils.PunishmentBooleanCallback;
import net.frozenorb.qmodsuite.punish.utils.PunishmentTypes;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

public class PunishBackend {
    private Set<Punishment> punishments = new HashSet();
    MongoClient mongoClient;
    DB mongoDatabase;
    DBCollection punishmentsCollection;

    public PunishBackend() {
    }

    public boolean insertPunishment(Punishment punishment) {
        this.getPunishments().add(punishment);
        this.updatePunishment(punishment.getUuid(), punishment.getPunishID(), punishment);
        return true;
    }

    public boolean removePunishment(UUID target, UUID punishmentID) {
        if (this.getPunishmentOfById(target, punishmentID) != null) {
            this.getPunishments().remove(this.getPunishmentOfById(target, punishmentID));
            DBCursor cursor = this.punishmentsCollection.find(new BasicDBObject("punishmentID", punishmentID.toString()));
            DBObject one = cursor.one();
            if (one != null) {
                this.punishmentsCollection.remove(one);
            }

            return true;
        } else {
            return false;
        }
    }

    public boolean updatePunishment(UUID target, UUID punishmentID, Punishment punishment) {
        if (this.getPunishmentOfById(target, punishmentID) != null) {
            Punishment s = this.getPunishmentOfById(target, punishmentID);
            this.getPunishments().remove(s);
            punishment.setPunishID(s.getPunishID());
            punishment.setUuid(s.getUuid());
            punishment.setPunishmentTypes(s.getPunishmentTypes());
            punishment.setExecutor(s.getExecutor());
            punishment.setTime(s.getTime());
            this.getPunishments().add(punishment);
        }

        DBObject punishmentObject = (new BasicDBObjectBuilder()).add("punishmentID", punishmentID.toString()).add("uuid", punishment.getUuid().toString()).add("executor", punishment.getExecutor() != null ? punishment.getExecutor().toString() : "").add("pardonedBy", punishment.getPardonedBy() != null ? punishment.getPardonedBy().toString() : "").add("type", punishment.getPunishmentTypes().toString()).add("reason", punishment.getReason()).add("pardonedReason", punishment.getPardonedReason() == null ? "" : punishment.getPardonedReason()).add("time", punishment.getTime()).add("duration", punishment.getDuration()).add("pardonedAt", punishment.getPardonedAt()).add("pardoned", punishment.isPardoned()).add("playerIP", punishment.getIP()).get();
        this.punishmentsCollection.update(new BasicDBObject("punishmentID", punishment.getPunishID().toString()), punishmentObject, (new DBCollectionUpdateOptions()).upsert(true));
        return false;
    }

    public void initializeBackend(GeneralCallback<Boolean> callback) {
        if (!Messages.isMongoAuth()) {
            this.mongoClient = new MongoClient(Messages.getMongoIP(), (new Builder()).connectTimeout(2000).build());
        } else {
            List<ServerAddress> seeds = new ArrayList();
            seeds.add(new ServerAddress(Messages.getMongoIP()));
            List<MongoCredential> credentials = new ArrayList();
            credentials.add(MongoCredential.createScramSha1Credential(Messages.getMongoUser(), "admin", Messages.getMongoPassword().toCharArray()));
            this.mongoClient = new MongoClient(seeds, credentials, (new Builder()).connectTimeout(2000).build());
        }

        this.mongoDatabase = this.mongoClient.getDB("qModSuite");
        this.punishmentsCollection = this.mongoDatabase.getCollection("punishments");
        callback.call(true);
    }

    public boolean loadFromDatabase() {
        DBCursor cursor = this.punishmentsCollection.find();
        cursor.forEach((dbObject) -> {
            this.parsePunishment(dbObject, (callback, result) -> {
                if (callback) {
                    this.punishments.add(result);
                } else {
                    System.out.println("Failed to parse " + dbObject.get("_id"));
                }

            });
        });
        return true;
    }

    public void shutdown() {
        this.mongoClient.close();
    }

    public void parsePunishment(DBObject object, PunishmentBooleanCallback callback) {
        try {
            UUID punishmentID = UUID.fromString((String)object.get("punishmentID"));
            UUID uuid = UUID.fromString((String)object.get("uuid"));
            UUID executor = ((String)object.get("executor")).equalsIgnoreCase("") ? null : UUID.fromString((String)object.get("executor"));
            UUID pardonedBy = ((String)object.get("pardonedBy")).equalsIgnoreCase("") ? null : UUID.fromString((String)object.get("pardonedBy"));
            PunishmentTypes punishmentType = PunishmentTypes.valueOf(((String)object.get("type")).toUpperCase());
            String reason = (String)object.get("reason");
            String pardonedReason = (String)object.get("pardonedReason");
            String playerIP = object.containsField("playerIP") ? (String)object.get("playerIP") : "None";
            long time = (Long)object.get("time");
            long duration = (Long)object.get("duration");
            long pardonedAt = (Long)object.get("pardonedAt");
            boolean pardoned = (Boolean)object.get("pardoned");
            callback.call(true, new Punishment(punishmentID, uuid, executor, pardonedBy, punishmentType, reason, pardonedReason, time, duration, pardonedAt, pardoned, playerIP));
        } catch (Exception var18) {
            var18.printStackTrace();
            callback.call(false, (Punishment)null);
        }
    }

    public Punishment getPunishmentOfById(UUID target, UUID punishmentID) {
        return (Punishment)this.getPunishments().stream().filter((punishment) -> {
            return punishment.getPunishID().toString().equals(punishmentID.toString()) && punishment.getUuid().toString().equals(target.toString());
        }).findFirst().orElse(null);
    }

    public Set<Punishment> getPunishmentsOf(UUID uuid) {
        return this.getPunishments().stream().filter((punishment) -> {
            return punishment.getUuid().toString().equals(uuid.toString());
        }).collect(Collectors.toSet());
    }

    public boolean isPunishedByTypes(UUID uuid, PunishmentTypes punishmentType) {
        return this.getPunishmentsByTypes(uuid, punishmentType).size() > 0;
    }

    public boolean isCurrentlyPunishedGloballyOrCurrent(UUID uuid, PunishmentTypes... punishmentType) {
        return this.getCurrentPunishmentsGloballyOrCurrent(uuid, punishmentType).size() > 0;
    }

    public boolean isCurrentlyIPPunishedGloballyOrCurrent(String ip, PunishmentTypes... punishmentType) {
        if (ip != null && !ip.equalsIgnoreCase("None") && !ip.equalsIgnoreCase("")) {
            return this.getCurrentIPPunishmentsGloballyOrCurrent(ip, punishmentType).size() > 0;
        } else {
            return false;
        }
    }

    public Set<Punishment> getCurrentPunishmentsGloballyOrCurrent(UUID uuid, PunishmentTypes... punishmentType) {
        return this.getPunishments().stream().filter((punishment) -> {
            return punishment.isActive() && punishment.getUuid() == uuid && Arrays.asList(punishmentType).contains(punishment.getPunishmentTypes());
        }).collect(Collectors.toSet());
    }

    public Set<Punishment> getCurrentIPPunishmentsGloballyOrCurrent(String ip, PunishmentTypes... punishmentType) {
        return this.getPunishments().stream().filter((punishment) -> {
            return punishment.isActive() && punishment.getIP().equalsIgnoreCase(ip) && Arrays.asList(punishmentType).contains(punishment.getPunishmentTypes());
        }).collect(Collectors.toSet());
    }

    public boolean isCurrentlyPunishedByTypes(UUID uuid, PunishmentTypes... punishmentType) {
        return this.getActivePunishmentsByTypes(uuid, punishmentType).size() > 0;
    }

    public boolean isCurrentlyIPPunishedByTypes(String ip, PunishmentTypes... punishmentType) {
        if (ip != null && !ip.equalsIgnoreCase("None") && !ip.equalsIgnoreCase("")) {
            return this.getActiveIPPunishmentsByTypes(ip, punishmentType).size() > 0;
        } else {
            return false;
        }
    }

    public Set<Punishment> getPunishmentsByTypes(UUID uuid, PunishmentTypes... punishmentType) {
        return this.getPunishments().stream().filter((punishment) -> {
            return punishment.getUuid().toString().equals(uuid.toString()) && Arrays.asList(punishmentType).contains(punishment.getPunishmentTypes());
        }).collect(Collectors.toSet());
    }

    public Set<Punishment> getIPPunishmentsByType(String ip, PunishmentTypes punishmentType) {
        return (ip != null && !ip.equalsIgnoreCase("None") && !ip.equalsIgnoreCase("") ? this.getPunishments().stream().filter((punishment) -> {
            return punishment.getIP().equalsIgnoreCase(ip) && punishment.getPunishmentTypes() == punishmentType;
        }).collect(Collectors.toSet()) : new HashSet());
    }

    public Set<Punishment> getActivePunishmentsByTypes(UUID uuid, PunishmentTypes... punishmentType) {
        return this.getPunishments().stream().filter((punishment) -> {
            return punishment.isActive() && punishment.getUuid().toString().equals(uuid.toString()) && Arrays.asList(punishmentType).contains(punishment.getPunishmentTypes());
        }).collect(Collectors.toSet());
    }

    public Set<Punishment> getActiveIPPunishmentsByTypes(String ip, PunishmentTypes... punishmentType) {
        return (ip != null && !ip.equalsIgnoreCase("None") && !ip.equalsIgnoreCase("") ? this.getPunishments().stream().filter((punishment) -> {
            return punishment.isActive() && punishment.getIP().equalsIgnoreCase(ip) && Arrays.asList(punishmentType).contains(punishment.getPunishmentTypes());
        }).collect(Collectors.toSet()) : new HashSet());
    }

    public Set<Punishment> getPunishments() {
        return this.punishments;
    }

    public MongoClient getMongoClient() {
        return this.mongoClient;
    }

    public DB getMongoDatabase() {
        return this.mongoDatabase;
    }

    public DBCollection getPunishmentsCollection() {
        return this.punishmentsCollection;
    }
}
