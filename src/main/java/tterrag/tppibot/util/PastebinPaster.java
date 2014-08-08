package tterrag.tppibot.util;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import com.google.gson.JsonParser;

public class PastebinPaster
{
    public String pasteData(CharSequence data)
    {
        URL url;
        HttpURLConnection connection = null;
        try
        {
            String location = "http://pastebin.kde.org/api/json/create";
            url = new URL(location);

            connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("POST");
            connection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
            connection.setRequestProperty("Content-Length", "" + Integer.toString(location.getBytes().length) + 100000);
            connection.setRequestProperty("Content-Language", "en-US");

            connection.setUseCaches(true);
            connection.setDoInput(true);
            connection.setDoOutput(true);

            // Send request
            DataOutputStream wr = new DataOutputStream(connection.getOutputStream());
            wr.writeBytes(location);

            StringBuilder sb = new StringBuilder(data.length()+32);
            sb.append("&language=text&data=").append(data);

            wr.writeBytes(sb.toString());

            wr.flush();
            wr.close();

            // Get Response
            InputStream is = connection.getInputStream();
            BufferedReader rd = new BufferedReader(new InputStreamReader(is));
            String line;
            StringBuffer response = new StringBuffer();
            while ((line = rd.readLine()) != null)
            {
                response.append(line);
                response.append('\n');
            }
            rd.close();
            
            return "http://pastebin.kde.org/" + new JsonParser().parse(response.toString()).getAsJsonObject().get("result").getAsJsonObject().get("id").getAsString();
        }
        catch (Exception e)
        {
            e.printStackTrace();
            return null;

        }
        finally
        {
            if (connection != null)
            {
                connection.disconnect();
            }
        }
    }
}
