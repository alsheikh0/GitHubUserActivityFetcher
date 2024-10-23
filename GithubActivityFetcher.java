import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class GithubActivityFetcher {
    private static final String GITHUB_API_URL = "https://api.github.com/users/";

    public static void main(String args[]) {
        if (args.length == 0) {
            System.out.println("Invalid username");
            return;
        }

        String username = args[0];

        try {
            fetchGithubUserActivity(username);
        } catch (Exception e) {
            System.out.println(" an errror occured:" + e.getMessage());
        }
    }

    public static void fetchGithubUserActivity(String username) throws Exception {

        String apiUrl = GITHUB_API_URL + username + "/events.";

        URL url = new URL(apiUrl);

        HttpURLConnection connection = (HttpURLConnection) url.openConnection();

        connection.setRequestMethod("GET");
        connection.setRequestProperty("User-Agent", "Java-GitHubActivityFetcher");

        int responseCode = connection.getResponseCode();

        if (responseCode == 200) {
            BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));

            String inputLine;

            StringBuilder response = new StringBuilder();

            while ((inputLine = in.readLine()) != null) {
                response.append(inputLine);
            }
            in.close();
            String jsonResponse = response.toString();
            parseAndDisplayEvents(jsonResponse);
        } else if (responseCode == 404) {
            System.out.println("user not found");
        } else {
            System.out.println("Failed to fetch activity. Response code: " + responseCode);
        }

    }

    public static void parseAndDisplayEvents(String jsonResponse) {
        int eventCount = 0;
        int index = 0;

        while (eventCount < 5 && (index = jsonResponse.indexOf("\"type\"", index)) != -1) {
            int typeStart = jsonResponse.indexOf(":", index) + 2;
            int typeEnd = jsonResponse.indexOf("\"", typeStart);

            String eventType = jsonResponse.substring(typeStart, typeEnd);

            int repoStart = jsonResponse.indexOf("\"name\"", typeEnd) + 8;
            int repoEnd = jsonResponse.indexOf("\"", repoStart);

            String repoName = jsonResponse.substring(repoStart, repoEnd);
            System.out.println("Event Type: " + eventType + ", Repository: " + repoName);

            eventCount++;

            index = repoEnd;
        }

        if (eventCount == 0) {
            System.out.println("No recent activity found for this user. ");
        }

    }

}