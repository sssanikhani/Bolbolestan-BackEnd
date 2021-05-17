package models.utils;

import io.jsonwebtoken.JwtBuilder;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import models.database.LocalVars;
import models.statics.Constants;

import java.io.IOException;
import java.math.BigInteger;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

public class Utils {

	public static LocalTime convertToLocalTime(String time) {
		LocalTime response;
		String timeStr = time;
		if (!timeStr.contains(":")) {
			timeStr = timeStr + ":00";
		}
		String hour = timeStr.split(":")[0];
		if (hour.length() < 2) timeStr = "0" + timeStr;
		response = LocalTime.parse(timeStr);
		return response;
	}

	public static HashMap<String, Object> sendRequest(
		String method,
		String url,
		HashMap<String, String> params,
		String requestBody
	)
		throws IOException, InterruptedException {
		// An util to send request with "method", "params", "requestBody" to specified
		// "url"
		// Return type is a HashMap with this structure:
		// {
		// "status": <status_code>,
		// "data": <response_body>,
		// }

		if (params != null) {
			ArrayList<String> paramsList = new ArrayList<>();

			for (String param : params.keySet()) {
				paramsList.add(param + "=" + params.get(param));
			}
			if (params.size() > 0) {
				url += "?";
				String parameters = String.join("&", paramsList);
				url += parameters;
			}
		}

		String body = "";
		if (requestBody != null) {
			//            ObjectMapper mapper = new ObjectMapper();
			body = requestBody;
		}

		HttpClient client = HttpClient.newHttpClient();
		URI uri = URI.create(url);
		HttpRequest.Builder builder = HttpRequest.newBuilder().uri(uri);
		HttpRequest request;
		switch (method) {
			case "GET":
				request = builder.GET().build();
				break;
			case "POST":
				request =
					builder
						.POST(HttpRequest.BodyPublishers.ofString(body))
						.header("Content-Type", "application/json")
						.build();
				break;
			case "PUT":
				request =
					builder
						.PUT(HttpRequest.BodyPublishers.ofString(body))
						.header("Content-Type", "application/json")
						.build();
				break;
			case "DELETE":
				request = builder.DELETE().build();
				break;
			default:
				return null;
		}
		HttpResponse<String> response = client.send(
			request,
			HttpResponse.BodyHandlers.ofString()
		);

		String resBody = response.body();
		int resStatus = response.statusCode();

		HashMap<String, Object> res = new HashMap<>();
		res.put("status", resStatus);
		res.put("data", resBody);

		return res;
	}

	public static String createJWT(String id, long ttlMillis) {
		//JWT signature
		SignatureAlgorithm signatureAlgorithm = SignatureAlgorithm.HS256;
		long nowMillis = System.currentTimeMillis();
		Date now = new Date(nowMillis);
		Date dt = new Date(nowMillis + ttlMillis);
		//JWT Claims
		JwtBuilder builder = Jwts
			.builder()
			.setIssuedAt(now)
			.setIssuer(Constants.myDomain)
			.setExpiration(dt)
			.signWith(signatureAlgorithm, LocalVars.secretKey)
			.claim("id", id);

		return builder.compact();
	}

	public static String getSHA(String input) {
		MessageDigest md;
		try {
			md = MessageDigest.getInstance("SHA-256");
		} catch(NoSuchAlgorithmException e) {
			System.out.println(e.getMessage());
			return null;
		}
		
		byte[] hash = md.digest(input.getBytes(StandardCharsets.UTF_8));

		BigInteger number = new BigInteger(1, hash);
		StringBuilder hexString = new StringBuilder(number.toString(16));

		while (hexString.length() < 32) {
			hexString.insert(0, '0');
		}

		return hexString.toString();
	}
}
