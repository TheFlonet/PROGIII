package progiii.server.util;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;
import progiii.common.data.Email;
import progiii.common.data.IdHandler;
import progiii.common.util.ClosableLock;
import progiii.common.util.ClosableRes;
import progiii.common.util.StringUtils;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;
import java.util.stream.Stream;

public class DataManager {
    private static final String EXTENSION = "json";
    private static final String DATA_FILENAME = "data.json";
    private final Path dataDir;
    private final Path persistentDataPath;
    private final Gson gson;
    private final Map<String, ClosableLock> lockMap = new HashMap<>();

    public DataManager(Path dataDir) {
        if (Files.isRegularFile(dataDir))
            throw new IllegalArgumentException("Passed path is a file");
        this.dataDir = dataDir;

        GsonBuilder gsonBuilder = new GsonBuilder().setPrettyPrinting();
        gsonBuilder.registerTypeAdapter(IdHandler.class, new IdHandler.Adapter());
        gsonBuilder.registerTypeAdapter(Email.class, new Email.Adapter());
        gson = gsonBuilder.create();
        System.out.println(dataDir);

        if (!Files.isDirectory(dataDir)) {
            try {
                Files.createDirectories(dataDir);
            } catch (IOException e) {
                throw new RuntimeException("Error while creating server data directory");
            }
        }

        persistentDataPath = dataDir.resolve(DATA_FILENAME);
        if (!Files.isRegularFile(persistentDataPath)) {
            try {
                Files.createFile(persistentDataPath);
            } catch (IOException e) {
                throw new RuntimeException("Error while creating server data file", e);
            }
            try (FileWriter fileWriter = new FileWriter(persistentDataPath.toFile());
                 BufferedWriter bufferedWriter = new BufferedWriter(fileWriter);
                 JsonWriter writer = new JsonWriter(bufferedWriter)) {
                gson.toJson(new IdHandler(), IdHandler.class, writer);
            } catch (IOException e) {
                throw new RuntimeException("Error while initializing server data file", e);
            }
        }
    }

    private synchronized ClosableRes getEmailLock(String email) throws IllegalArgumentException {
        ClosableLock emailLock = lockMap.get(StringUtils.cleanEmail(email));
        if (emailLock == null) {
            emailLock = new ClosableLock();
            lockMap.put(email, emailLock);
        }
        return emailLock.lockAsResource();
    }

    public boolean userExists(String email) throws IllegalArgumentException {
        Path path = dataDir.resolve(StringUtils.cleanEmail(email));
        return Files.isDirectory(path);
    }

    public Set<Email> getMissingUserEmails(String email, Set<Integer> busyId) throws Exception {
        Set<Email> newEmails = new HashSet<>();
        try (ClosableRes lock = getEmailLock(email);
             Stream<Path> emailPath = Files.walk(dataDir.resolve(email))) {
            Iterator<Path> paths;
            paths = emailPath.filter(Files::isRegularFile).filter(path -> {
                if (busyId == null || busyId.size() == 0) {
                    return true;
                }
                String emailId = String.valueOf(path.getFileName());
                emailId = emailId.substring(0, emailId.lastIndexOf('.'));
                return !busyId.contains(Integer.parseInt(emailId));
            }).iterator();

            while (paths.hasNext()) {
                try (FileReader fileReader = new FileReader(paths.next().toFile());
                     BufferedReader bufferedReader = new BufferedReader(fileReader);
                     JsonReader reader = new JsonReader(bufferedReader)) {
                    Email readEmail = gson.fromJson(reader, Email.class);
                    newEmails.add(readEmail);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return newEmails;
    }

    public void registerEmail(String email) throws Exception {
        try (ClosableRes emailLock = getEmailLock(email)) {
            if (userExists(email)) {
                throw new IllegalArgumentException(String.format("Email %s already exists", email));
            }
            String safeEmail = StringUtils.cleanEmail(email);
            try {
                Files.createDirectory(dataDir.resolve(safeEmail));
            } catch (IOException e) {
                throw new IOException(String.format("Error while registring %s", safeEmail), e);
            }
        }
    }

    public void deliver(String recipient, Email email) {
        if (!userExists(recipient))
            throw new IllegalArgumentException(recipient + " doesn't exist");
        try (ClosableRes emailLock = getEmailLock(recipient)) {
            File emailPath = dataDir.resolve(recipient).resolve(String.format("%d.%s", email.getId(), EXTENSION)).toFile();
            try (FileWriter fileWriter = new FileWriter(emailPath, false);
                 BufferedWriter bufferedWriter = new BufferedWriter(fileWriter);
                 JsonWriter writer = new JsonWriter(bufferedWriter)) {
                gson.toJson(email, Email.class, writer);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public boolean delete(String owner, int id) {
        if (!userExists(owner))
            throw new IllegalArgumentException(owner + " doesn't exist");
        File toDeletePath = dataDir.resolve(owner).resolve(String.format("%d.%s", id, EXTENSION)).toFile();
        try (ClosableRes lock = getEmailLock(owner)) {
            return toDeletePath.delete();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public synchronized int reserveId() {
        IdHandler handler;
        try (FileReader fileReader = new FileReader(persistentDataPath.toFile());
             BufferedReader bufferedReader = new BufferedReader(fileReader);
             JsonReader reader = new JsonReader(bufferedReader)) {
            handler = gson.fromJson(reader, IdHandler.class);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        int id = handler.getAndIncrement();
        try (FileWriter fileWriter = new FileWriter(persistentDataPath.toFile());
             BufferedWriter bufferedWriter = new BufferedWriter(fileWriter);
             JsonWriter writer = new JsonWriter(bufferedWriter)) {
            gson.toJson(handler, IdHandler.class, writer);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return id;
    }
}
